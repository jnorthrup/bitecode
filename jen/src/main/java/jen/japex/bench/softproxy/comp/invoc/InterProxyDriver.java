/* InterProxyDriver.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/21 23:11:43 $
 * Originated: Oct 21, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.japex.bench.softproxy.comp.invoc;

import java.lang.reflect.Proxy;

import jen.tools.SoftProxy;import static jen.tools.SoftProxy.newProxyInstance;
import jen.x.bench.ProxyInter;
import jen.x.bench.StaticInter;

import com.sun.japex.TestCase;

/** 
 * Interface proxy invocation driver.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/21 23:11:43 $ 
 */
public class InterProxyDriver extends AbstractInvocDriver
{
  static final String TYPE_PARAM = "proxy.inter.type";

  /* just as nasty as in class test, but same old excuses... :)
   * 
   * Interfaces are better supported (i.e. theres more than just jen and static
   * options ;) ). The omission of CGLib mocks is deliberate (since the mock 
   * support is implemented as a platform handler it's impossible to test with
   * cglib which has it's own InvocationHandler. 
   */
  private ProxyInter getProxy(String type) {
    String s = type != null ? type.toLowerCase() : "static";    // baseline default
    if ("jen-proxy".equals(s)) {
      return SoftProxy.newProxyInstance(ProxyInter.class,getPlatHandler());
    } else if ("jen-mock".equals(s)) {
      return SoftProxy.newProxyInstance(ProxyInter.class,getMockHandler()); 
    } else if ("plat-proxy".equals(s)) {
      return (ProxyInter)
          Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
              new Class[] { ProxyInter.class },getPlatHandler()); 
    } else if ("plat-mock".equals(s)) {
      return (ProxyInter)
          Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
              new Class[] { ProxyInter.class },getMockHandler()); 
    } else if ("cglib-proxy".equals(s)) {
      return (ProxyInter)
       newProxyInstance(Thread.currentThread().getContextClassLoader(),
          new Class[] { ProxyInter.class },getCglHandler());
    } else if ("static".equals(s)) {  
      // as close an approximation - will call through INVOKEINTERFACE
      return new StaticInter();
    } else {
      throw(new IllegalArgumentException("'" + TYPE_PARAM + "' was not recognised"));
    }
  }
  
  ProxyInter instance;
  
  @Override
  public void prepare(TestCase testCase) {
    super.prepare(testCase);    
    instance = getProxy(testCase.getParam(TYPE_PARAM));
  }

  @Override
  public void run(TestCase testCase) {
    instance.getResult();
  }

  @Override
  public void warmup(TestCase testCase) {
    instance.getResult();
  }
}
