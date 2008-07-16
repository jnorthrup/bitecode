/* SoftProxyTestCase.java - Awaiting description
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
 * File version: $Revision: 1.7 $ $Date: 2005/10/23 02:15:24 $
 * Originated: Oct 3, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

import jen.SoftClass;
import jen.tools.SoftProxy;

import junit.framework.TestCase;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

public class SoftProxyTestCase extends TestCase
{    
  static final HashMap<String,String> FLAG = new HashMap<String,String>();
  
  static class InvHandler implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("equals".equals(method.getName())) {
        return true;
      } else if (("hashCode".equals(method.getName())) || ("primitiveMethod".equals(method.getName()))) {
        return 1980;        
      } else if ("run".equals(method.getName())) {
        // RUNNABLE TEST - NO RETURN, SO ADD FLAG
        FLAG.put("RUN","RAN");
        return null;
      } else return method.getName();
    }    
  }
  
  public void testCanProxyInterfaceSoftClassToClassToInstance() throws Exception {
    SoftClass proxySc = SoftProxy.createProxy(TestProxyInterface.class);
    
    Class proxyClass = proxySc.defineClass();    
    
    @SuppressWarnings("unchecked")    
    TestProxyInterface tif = (TestProxyInterface)proxyClass.getConstructor(InvocationHandler.class).newInstance(new InvHandler());
    
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",15),isEqual("anotherArgsMethod"));   
  }
  
  // GENERICS-ENABLED OVERLOADS FOR SINGLE CLASS / METHOD
  public void testCanProxySingleInterfaceWithGenericsAPIForClass() throws Exception {
    Class<TestProxyInterface> tic = SoftProxy.createProxyClass(TestProxyInterface.class);
    TestProxyInterface tif = tic.getConstructor(InvocationHandler.class).newInstance(new InvHandler());
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",15),isEqual("anotherArgsMethod"));   
    assertThat(tif.primitiveMethod(),isEqual(1980));   
  }

  public void testCanProxySingleInterfaceWithGenericsAPIForInstance() throws Exception {
    TestProxyInterface tif = SoftProxy.newProxyInstance(TestProxyInterface.class, new InvHandler());
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",15),isEqual("anotherArgsMethod"));   
    assertThat(tif.primitiveMethod(),isEqual(1980));   
  }
  
  public void testCanProxyMultipleInterfaceWithoutGenericsForClass() throws Exception {
    Class<?> tic = SoftProxy.createProxyClass(new Class[] { TestProxyInterface.class, Runnable.class });
    TestProxyInterface tif = (TestProxyInterface)tic.getConstructor(InvocationHandler.class).newInstance(new InvHandler());
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",15),isEqual("anotherArgsMethod"));   
    assertThat(tif.primitiveMethod(),isEqual(1980));   

    FLAG.clear();
    ((Runnable)tif).run();
    assertThat(FLAG.toString(),isEqual("{RUN=RAN}"));    
  }

  // NON-GENERICS API (how would we decide which class to use?)
  public void testCanProxyMultipleInterfaceWithoutGenericsForInstance() throws Exception {
    TestProxyInterface tif = (TestProxyInterface)
      SoftProxy.newProxyInstance(new Class[] { TestProxyInterface.class, Runnable.class }, new InvHandler());
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",15),isEqual("anotherArgsMethod"));   
    assertThat(tif.primitiveMethod(),isEqual(1980));   

    FLAG.clear();
    ((Runnable)tif).run();
    assertThat(FLAG.toString(),isEqual("{RUN=RAN}"));    
  }
  
  // GENERICS-ENABLED OVERLOADS FOR SINGLE CLASS / METHOD
  public void testCanProxySingleClassWithGenericsAPIForClass() throws Exception {
    Class<TestProxyClass> tic = SoftProxy.createProxyClass(TestProxyClass.class);
    TestProxyClass tpc = tic.getConstructor(InvocationHandler.class).newInstance(new InvHandler());
    assertThat(tpc.basicMethod(),isEqual("basicMethod"));
    assertThat(tpc.argsMethod(15,getClass()),isEqual("argsMethod"));
  }

  public void testCanProxySingleClassWithGenericsAPIForInstance() throws Exception {
    TestProxyClass tpc = SoftProxy.newProxyInstance(TestProxyClass.class,new InvHandler());
    assertThat(tpc.basicMethod(),isEqual("basicMethod"));
    assertThat(tpc.argsMethod(15,getClass()),isEqual("argsMethod"));
  }
  
  public void testGenerationWithNoNonPublicClassesResultsInSamePackageAsFirstClass() {
    Class c = SoftProxy.createProxyClass(new Class[] { TestProxyInterface.class, Runnable.class });
    assertThat(c.getPackage(),isNull());
  }
  
  static interface PPInterface {
    public String getRet();
  }
  
  static interface PPInterfaceTwo {
    public String getRetTwo();
  }
  
  public void testGenerationWithOneNonPublicClassResultsInSamePackageAsNonPublic() {
    Class c = SoftProxy.createProxyClass(new Class[] { TestProxyInterface.class, Runnable.class, PPInterface.class });
    assertThat(c.getPackage().getName(),isEqual("jen.tools"));
  }
  
  public void testCanProxyMixedInterfacesAndClassesWithoutGenericsForInstance() throws Exception {
    TestProxyInterface tif = (TestProxyInterface)
      SoftProxy.newProxyInstance(new Class[] { TestProxyClass.class, TestProxyInterface.class, Runnable.class }, new InvHandler());
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",15),isEqual("anotherArgsMethod"));
    assertThat(tif.primitiveMethod(),isEqual(1980));   
    
    TestProxyClass tpc = (TestProxyClass)tif;
    assertThat(tpc.basicMethod(),isEqual("basicMethod"));
    assertThat(tpc.argsMethod(15,getClass()),isEqual("argsMethod"));    

    FLAG.clear();
    ((Runnable)tpc).run();
    assertThat(FLAG.toString(),isEqual("{RUN=RAN}"));    
  }
  
  static class NullInvHandler implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return null;
    }
  }
  
  public void testProxyWithNullReturn() {
    TestProxyInterface tif = (TestProxyInterface)
    SoftProxy.newProxyInstance(new Class[] { TestProxyClass.class, TestProxyInterface.class }, new NullInvHandler());
    assertThat(tif.basicMethod(),isNull());
    assertThat(tif.anotherArgsMethod("test",15),isNull());
    assertThat(tif.primitiveMethod(),isEqual(0));   
    
    TestProxyClass tpc = (TestProxyClass)tif;
    assertThat(tpc.basicMethod(),isNull());
    assertThat(tpc.argsMethod(15,getClass()),isNull());      
  }
  
  static class ArgInvHandler implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("anotherArgsMethod".equals(method.getName()) || "basicMethod".equals(method.getName())) {
        StringBuffer buf = new StringBuffer().append(method.getName());
        
        for (Object o : args) {
          buf.append(" ");
          buf.append(o.getClass().getSimpleName());
        }
        
        return buf.toString();
      }
      
      return null;
    }
  }
  
  public void testProxyArgsWorkOkayAfterAllThat() {
    TestProxyInterface tif = (TestProxyInterface)
    SoftProxy.newProxyInstance(new Class[] { TestProxyInterface.class }, new ArgInvHandler());
    assertThat(tif.basicMethod(),isEqual("basicMethod"));
    assertThat(tif.anotherArgsMethod("test",new Object()),isEqual("anotherArgsMethod String Object"));    
  }  
  
  public void testIsProxyClass() {
    Class<TestProxyInterface> tifc = SoftProxy.createProxyClass(TestProxyInterface.class);    
    assertThat(SoftProxy.isProxyClass(tifc),isTrue());
    assertThat(SoftProxy.isProxyClass(getClass()),isFalse());
  }
  
  public void testIsProxy() {
    TestProxyInterface tif = 
      SoftProxy.newProxyInstance(TestProxyInterface.class, new ArgInvHandler());
    
    assertThat(SoftProxy.isProxy(tif),isTrue());
    assertThat(SoftProxy.isProxy(this),isFalse());
  }  
}
