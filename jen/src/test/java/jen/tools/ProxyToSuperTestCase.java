/* ProxyToSuperTestCase.java - Awaiting description
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
 * File version: $Revision: 1.4 $ $Date: 2005/10/31 01:50:47 $
 * Originated: Oct 9, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static jen.tools.DefaultInvokerStrategy.invokeSuper;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

import junit.framework.TestCase;

public class ProxyToSuperTestCase extends TestCase
{
  static abstract class ExtendedSuperTestClass {
    public void throwsMethod() throws Exception { throw new IOException(); }
    public void throwsMethod(boolean dummy) throws Exception { throw new RuntimeException(); }
    public abstract int absMethod();
  }
  
  static class SuperInvoker implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return invokeSuper(proxy,method,args);
    }
  }
  
  /* invokeSuper WITH CLASS: Each should act as for the original class. */
  public void testProxyClassSuperInvocationWithSingleClass() throws Exception {
    Class<TestProxyClass> tic = SoftProxy.createProxyClass(TestProxyClass.class);
    TestProxyClass tpc = tic.getConstructor(InvocationHandler.class).newInstance(new SuperInvoker());
    
    assertThat(tpc.basicMethod(),isEqual("TEST METHOD"));
    assertThat(tpc.argsMethod(15,getClass()),isEqual("15 class jen.tools.ProxyToSuperTestCase"));
  }

  /* invokeSuper WITH INTERFACE: Each should return null (abstract methods). */
  public void testProxyClassSuperInvocationWithSingleInterface() throws Exception {
    TestProxyInterface tpc = SoftProxy.newProxyInstance(TestProxyInterface.class, new SuperInvoker());
    
    assertThat(tpc.basicMethod(),isNull());
    assertThat(tpc.anotherArgsMethod("String",getClass()),isNull());
  }
  
  /* invokeSuper WITH INTERFACE, THEN CLASS: iface methods should be null, class should
   * act as the original. 'basicMethod' is declared by both: since interface is first
   * in arg array, this will be treated as an (abstract) interface method, so return null
   * no matter how called. */
  public void testProxyClassSuperInvocationWithInterfaceThenClass() throws Exception {
    TestProxyInterface tpi = 
      (TestProxyInterface)SoftProxy.newProxyInstance(
          new Class[] { TestProxyInterface.class, TestProxyClass.class },new SuperInvoker());
    
    assertThat(tpi.basicMethod(),isNull());
    assertThat(tpi.anotherArgsMethod("String",getClass()),isNull());
    
    TestProxyClass tpc = (TestProxyClass)tpi;
    // actually calls to interface method
    assertThat(tpc.basicMethod(),isNull());
    
    // class method
    assertThat(tpc.argsMethod(15,getClass()),isEqual("15 class jen.tools.ProxyToSuperTestCase"));
  }
  
  /* invokeSuper WITH CLASS, THEN INTERFACE: iface methods should be null, class should
   * act as the original. 'basicMethod' is declared by both: since class is first
   * in arg array, this will be treated as a concrete class method, so returning 
   * the  "TEST METHOD" return from the proxied method. */
  public void testProxyClassSuperInvocationWithClassThenInterface() throws Exception {
    TestProxyInterface tpi = 
      (TestProxyInterface)SoftProxy.newProxyInstance(
          new Class[] { TestProxyClass.class, TestProxyInterface.class },new SuperInvoker());
    
    assertThat(tpi.basicMethod(),isEqual("TEST METHOD"));
    assertThat(tpi.anotherArgsMethod("String",getClass()),isNull());
    
    TestProxyClass tpc = (TestProxyClass)tpi;
    // actually calls to interface method
    assertThat(tpc.basicMethod(),isEqual("TEST METHOD"));
    
    // class method
    assertThat(tpc.argsMethod(15,getClass()),isEqual("15 class jen.tools.ProxyToSuperTestCase"));
  }
  
  /* invokeSuper WITH ABSTRACT METHOD: should return null. */
  public void testProxyClassSuperInvocationWithSingleAbstractClass() throws Exception {
    ExtendedSuperTestClass stc = SoftProxy.newProxyInstance(ExtendedSuperTestClass.class, new SuperInvoker());
    stc.absMethod();
    //assertThat(stc.absMethod(),isNull());    
  }
  
  /* invokeSuper WITH CLASS WITH OVERLOADS: Each should act as for the original class. */
  public void testProxyClassSuperInvocationWithSingleClassWithOverloads() throws Exception {
    ExtendedSuperTestClass stc = SoftProxy.newProxyInstance(ExtendedSuperTestClass.class, new SuperInvoker());
    
    try {
      stc.throwsMethod();
    } catch (IOException e) {
      /* PASS */
    }
    
    try {
      stc.throwsMethod(true);
    } catch (RuntimeException e) {
      /* PASS */
    }
  }
}
