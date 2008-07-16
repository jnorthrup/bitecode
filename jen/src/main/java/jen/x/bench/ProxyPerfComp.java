/* ProxyPerfComp.java - Idea of performance of proxies
 *
 * Copyright (c)2005 Roscopeco Open Technologies & Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * File version: $Revision: 1.2 $ $Date: 2005/10/21 23:11:31 $
 * Originated: Oct 5, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.x.bench;

import jen.tools.*;import static jen.tools.SoftProxy.newProxyInstance;
import org.roscopeco.juno.*;
import static org.roscopeco.juno.Constraints.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/*
 * Just to gauge performance in development. Not accurate at all. <strong>Don't</strong>
 * run this to do any kind of real performance comparison, because it's next to
 * impossible to define any kind of baseline for it, especially in the JVM. Results vary
 * slightly for me, but generally speaking performance is comparable to java.lang.reflect
 * proxies and cglib proxies in terms of the raw invocation (they're all implemented in 
 * similar ways I think).
 * 
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/21 23:11:31 $ 
 */
public class ProxyPerfComp
{
  static final Random rand = new Random();
  
  static final  InvocationHandler cglhandler = new InvocationHandler() {
    public Object invoke(Object proxy, Method m, Object[] args) {
      return "CGLIB_INV_HANDLER";
    }
  };
  
  static final InvocationHandler handler = new PlatformInvHandler();    
  static final InvocationHandler mockh = new MockProxyHandler(
      new MockMethod[] {
          new StubMock("getResult",new Constraint[] { isNull() }, null)
      }
    );
  
  final ProxyInter sci = SoftProxy.newProxyInstance(ProxyInter.class,handler);
  final ProxyInter staticpi = new StaticInter();
  final ProxyInter mockpi = SoftProxy.newProxyInstance(ProxyInter.class,mockh);
  final ProxyInter cglpi = (ProxyInter)
  Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
      new Class[] { ProxyInter.class },handler);
  final ProxyInter cglmocki = (ProxyInter)
  Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
      new Class[] { ProxyInter.class },mockh);
  
  final ProxyClass scc = SoftProxy.newProxyInstance(ProxyClass.class,handler);
  final ProxyClass staticpc = new ProxyClass();
  final ProxyClass mockpc = SoftProxy.newProxyInstance(ProxyClass.class,mockh);
  
  // Already got a small productivity enhancement here =]
  final ProxyInter jri = (ProxyInter)
   newProxyInstance(Thread.currentThread().getContextClassLoader(),
        new Class[] { ProxyInter.class },cglhandler);
  final ProxyInter jrmock = (ProxyInter)
    Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
        new Class[] { ProxyInter.class },mockh);
  
  final String fmt = "   %-25s (%11s) - Total time: %d5ms, avg %fms/test (approx %fms/invocation avg.)\n";
   
  static long testInter(int iters, ProxyInter o) {
    @SuppressWarnings("all")
    String s;
    long start = System.currentTimeMillis();
    for (int i = 0; i < iters; i++) {
      o.getResult();
    }
    return System.currentTimeMillis() - start;
  }
  
  static long testClass(int iters, ProxyClass o) {
    @SuppressWarnings("all")
    String s;
    long start = System.currentTimeMillis();
    for (int i = 0; i < iters; i++) {
      o.getResult();
    }
    return System.currentTimeMillis() - start;
  }
  
  private static void sleepASec() {
    System.out.println("Waiting...");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // well, fine!
    }    
  }
  
  static Object optimizeGuard;
  
  /* **********************************
   * ENTRY POINT
   */
  public static void main(String[] args) {
    // iterations per test
    int iter = 200000;
    try {
      iter = Integer.parseInt(args[0]);
    } catch (Exception e) {
      /* ignore, default */
    }
    
    // number of full tests to run
    int tests = 5;
    try {
      iter = Integer.parseInt(args[1]);
    } catch (Exception e) {
      /* ignore, default */
    }
    
    // tests to run before starting recording results. Allows
    // JIT to get to know the code, stops tests dramatically
    // speeding up toward the end.
    int warmup = 1;
    try {
      iter = Integer.parseInt(args[2]);
    } catch (Exception e) {
      /* ignore, default */
    }
    
    System.out.println("Performance testing with "+tests+" test(s), each with "+iter+" invocation(s) / proxy type.");
    ProxyPerfComp perf = new ProxyPerfComp(iter,tests,warmup);
    perf.runTests(args);
    perf.outputResults();
  }
  
  /* **********************************
   * INSTANCE INIT
   */
  boolean hasrun = false;
  long scit = 0, scct = 0, jrit = 0, staticpit = 0, staticpct = 0, mockpit = 0;
  long mockpct = 0, cglit = 0, cglct = 0, cgmocki = 0, cgmockc = 0, jrmockt = 0;
  
  int iter, tests, warmup;
  
  private ProxyPerfComp(int iter, int tests, int warmup) {
    this.iter = iter;
    this.tests = tests;
    this.warmup = warmup;    
  }
  
  private void reset() {
    scit = 0;
    scct = 0;
    jrit = 0;
    staticpit = 0;
    staticpct = 0;
    mockpit = 0; 
    mockpct = 0;    
    cglit = 0;
    cglct = 0;
    cgmocki = 0;
    cgmockc = 0;
    jrmockt = 0;
  }
  
  public synchronized void runTests(String[] args) {
    hasrun = true;
        
    // ensure linked and initialized    
    optimizeGuard = new StringBuilder().
      append(scc.getResult()).
      append(sci.getResult()).
      append(jri.getResult()).
      append(staticpi.getResult()).
      append(staticpc.getResult()).toString();
    
    int numtests;    

    // warmup
    System.out.println("Running "+warmup+" warmup test(s)...");    
    for (numtests = 0; numtests < warmup; numtests++) {
      oneTest(iter);
    }
    
    System.out.println("Running test(s)...");    
    reset();
    
    for (numtests = 0; numtests < tests; numtests++) {
      oneTest(iter);
    }    
  }
  
  synchronized void outputResults(PrintWriter w) {    
    w.println("Results from "+tests+" runs of "+iter+" invocation(s) each:\n");
    w.println();
    w.println("STATIC INVOCATION: Regular compiled-in method invocation as a baseline:");
    w.printf(fmt,"static invocation","interface",staticpit,(double)staticpit / tests,(double)staticpit / tests / iter);
    w.printf(fmt,"static invocation","class",staticpct,(double)staticpct / tests,(double)staticpct / tests / iter);
    w.println();
    w.println("PROXY WITH return \"INVHANDLER\"; HANDLER: The most basic handler:");
    w.printf(fmt,"java.lang.reflect proxy","interface",jrit,(double)jrit / tests,(double)jrit / tests / iter);
    w.printf(fmt,"CGLib proxy","interface",cglit,(double)cglit / tests,(double)cglit / tests / iter);
    //w.printf(fmt,"CGLib proxy","class",cglct,(double)cglct / tests,(double)cglct / tests / iter);
    w.printf(fmt,"jen.SoftClass proxy","interface",scit,(double)scit / tests,(double)scit / tests / iter);
    w.printf(fmt,"jen.SoftClass proxy","class",scct,(double)scct / tests,(double)scct / tests / iter);
    w.println();
    w.println("PROXY WITH jen.tools.MockProxyHandler; MOCK HANDLER: Const return with a single constraint");
    w.printf(fmt,"java.lang.reflect proxy","interface",jrmockt,(double)jrmockt / tests,(double)jrmockt / tests / iter);
    w.printf(fmt,"CGLib mock proxy","interface",cgmocki,(double)cgmocki / tests,(double)cgmocki / tests / iter);
    //w.printf(fmt,"CGLib mock proxy","class",cgmockc,(double)cgmockc / tests, (double)cgmockc / tests / iter);
    w.printf(fmt,"Jen mock proxy","interface",mockpit,(double)mockpit / tests,(double)mockpit / tests / iter);
    w.printf(fmt,"Jen mock proxy","class",mockpct,(double)mockpct / tests, (double)mockpct / tests / iter);
    
    w.println();
    w.flush();
  }
  
  void outputResults() {
    outputResults(new PrintWriter(System.out));
  }
  
  private void oneTest(int iter) {
    System.gc();
    sleepASec();
    System.out.println("Testing standard (statically linked) invocation (interface) over "+iter+" iterations...");
    staticpit += testInter(iter,staticpi);
    System.gc();
    sleepASec();
    System.out.println("Testing standard (statically linked) invocation (virtual) over "+iter+" iterations...");
    staticpct += testClass(iter,staticpc);
    System.gc();
    sleepASec();
    System.out.println("Testing java.lang.reflect Proxy (interface) over "+iter+" iterations...");
    jrit += testInter(iter,jri);    
    System.gc();    
    sleepASec();
    System.out.println("Testing CGLib Proxy (interface) over "+iter+" iterations...");
    cglit += testInter(iter,cglpi);
    System.gc();    
    sleepASec();
    //System.out.println("Testing CGLib Proxy (class) over "+iter+" iterations...");
    //cglct+= testClass(iter,cglpc);
    //System.gc();
    //sleepASec();
    System.out.println("Testing jen.SoftClass Proxy (interface) over "+iter+" iterations...");
    scit += testInter(iter,sci);
    System.gc();    
    sleepASec();
    System.out.println("Testing jen.SoftClass Proxy (class) over "+iter+" iterations...");
    scct+= testClass(iter,scc);
    System.gc();
    sleepASec();
    System.out.println("Testing java.lang.reflect Mock (interface) over "+iter+" iterations...");
    jrmockt += testInter(iter,jrmock);    
    System.gc();    
    sleepASec();
    System.out.println("Testing CGLib Mock (interface) over "+iter+" iterations...");
    cgmocki += testInter(iter,cglmocki);
    System.gc();    
    sleepASec();
    //System.out.println("Testing CGLib Mock (class) over "+iter+" iterations...");
    //cgmockc += testClass(iter,cglmockc);
    //System.gc();
    //sleepASec();
    System.out.println("Testing jen.SoftClass Mock (interface) over "+iter+" iterations...");
    mockpit += testInter(iter,mockpi);
    System.gc();    
    sleepASec();
    System.out.println("Testing jen.SoftClass Mock (class) over "+iter+" iterations...");
    mockpct += testClass(iter,mockpc);          
  }
}
