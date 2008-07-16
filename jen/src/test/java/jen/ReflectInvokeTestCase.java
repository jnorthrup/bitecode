/* ReflectInvokeTestCase.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/23 02:15:16 $
 * Originated: Oct 23, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import static jen.SoftUtils.reflectInvoke;
import static jen.SoftUtils.reflectInvokeRTE;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.converters.JUnitSupport.assertThat;

public class ReflectInvokeTestCase extends TestCase
{
  DummyClass dummy = new DummyClass();
  
  /* ************************* METHODS ********************************* */
  public void testReflectInvokeVoidMethod() throws Exception {
    assertThat(reflectInvoke(dummy,"voidMethod"),isNull());
  }

  public void testReflectInvokeArgsMethod() throws Exception {
    assertThat(reflectInvoke(dummy,"argsMethod",new Object[] { "one", "two" }),eq("onetwo"));
  }

  public void testReflectInvokeThrowsMethod() throws Exception {
    try {
      reflectInvoke(dummy,"throwsMethod");
    } catch (InvocationTargetException e) {
      // Unwrapped the InvocationTargetException?
      assertThat(e.getCause(),instOf(ClassNotFoundException.class));
    }
  }

  public void testReflectInvokeProtectedMethod() throws Exception {
    try {
      reflectInvoke(dummy,"protectedMethod");
    } catch (IllegalAccessException e) {
      // pass
    }
  }
  
  public void testReflectInvokeProtectedMethodWithAccessible() throws Exception {
   assertThat(reflectInvoke(dummy,"protectedMethod",true),eq("TEST"));
  }
  
  public void testReflectInvokePrivateMethod() throws Exception {
    try {
      reflectInvoke(dummy,"privateMethod");
    } catch (Exception e) {
      
      // Is IllegalAccess?
      assertThat(e,instOf(IllegalAccessException.class));
      
    }
  }
    
  public void testReflectInvokePrivateMethodWithAccessible() throws Exception {
    try {
      reflectInvoke(dummy,"privateMethod",true,(Object[])null);
    } catch (Exception e) {
      
      // Should still be IllegalAccess...
      assertThat(e,instOf(IllegalAccessException.class));
      
    }
  }
  
  /* ************** CONSTRUCTORS ************************** */
  static class CtorsTest {
    public boolean b;  
  
    public CtorsTest() { }
    
    private CtorsTest(String s) { }
    
    protected CtorsTest(Boolean b) { 
      this.b = b.booleanValue();
    }
  }
 
  
  public void testReflectInvokeConstructorWithNonClassParamUsesObjectClass() throws Exception {
    CtorsTest ct = new CtorsTest();
    CtorsTest ct2 = (CtorsTest)reflectInvoke(ct,"<init>");
    
    assertThat(ct,instOf(CtorsTest.class));
    assertThat(ct2,instOf(CtorsTest.class));
    assertThat(ct,notSame(ct2));
  }
  
 
  
  public void testReflectInvokePublicConstructor() throws Exception {
    assertThat(reflectInvoke(CtorsTest.class,"<init>"),instOf(CtorsTest.class));
  }
  
    
  public void testReflectInvokePrivateConstructor() throws Exception {
    try {
      reflectInvoke(CtorsTest.class,"<init>","Hello");
    } catch (IllegalAccessException e) {
      // pass
    }
  }
  
  
  public void testReflectInvokePrivateConstructorWithAccessible() throws Exception {
    try {
      // N.B. Eclipse bugs out on this line if you don't specify exact array types
      reflectInvoke(CtorsTest.class,"<init>", true, new Class[] { String.class }, new Object[] { "Hello" });
    } catch (IllegalAccessException e) {
      // Should still be IllegalAccess...
    }
  }
  
  public void testReflectInvokeProtectedConstructor() throws Exception {
    try {
      reflectInvoke(CtorsTest.class,"<init>",Boolean.TRUE);
    } catch (IllegalAccessException e) {      
    }
  }
  
  
  public void testReflectInvokeProtectedConstructorWithAccessible() throws Exception {
    // N.B. Eclipse bugs out on this line if you don't specify exact array types
    assertThat(((CtorsTest)reflectInvoke(CtorsTest.class,"<init>",true,
        new Class[] { Boolean.class }, new Object[] { Boolean.FALSE })).b,isFalse());
  }
  
  public void testReflectInvokeThrowsConstructor() throws Exception {
    try {
      reflectInvoke(dummy,"throwsMethod");
    } catch (Exception e) {
      
      // Unwrapped the InvocationTargetException?
      assertThat(e,instOf(InvocationTargetException.class));
      assertThat(e.getCause(),instOf(ClassNotFoundException.class));
      
    }
  }

  /* **********************************
   * RTE reflectInvoke
   */
  @Deprecated
  public void testReflectInvokeRTEVoidMethod() throws Exception {
    assertThat(reflectInvokeRTE(dummy,"voidMethod"),isNull());
  }

  @Deprecated
  public void testReflectInvokeRTEArgsMethod() throws Exception {
    assertThat(reflectInvokeRTE(dummy,"argsMethod",new Object[] { "one", "two" }),eq("onetwo"));
  }

  @Deprecated
  public void testReflectInvokeRTEThrowsMethod() throws Exception {
    try {
      reflectInvokeRTE(dummy,"throwsMethod");
    } catch (Exception e) {
      
      // Unwrapped the InvocationTargetException?
      assertThat(e,instOf(RuntimeException.class));
      assertThat(e.getCause(),instOf(ClassNotFoundException.class));
      
    }
  }

  @Deprecated
  public void testReflectInvokeRTEPrivateMethod() throws Exception {
    try {
      reflectInvokeRTE(dummy,"privateMethod");
    } catch (Exception e) {
      
      // Is IllegalAccess?
      assertThat(e,instOf(RuntimeException.class));
      assertThat(e.getCause(),instOf(IllegalAccessException.class));
    }
  }

}
