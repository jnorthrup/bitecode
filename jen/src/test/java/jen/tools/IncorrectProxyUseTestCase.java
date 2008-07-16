/* IncorrectProxyUseTestCase.java - Awaiting description
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
 * File version: $Revision: 1.4 $ $Date: 2005/10/30 23:47:46 $
 * Originated: Oct 22, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.util.ArrayList;
import java.util.HashMap;

import jen.NullArgumentException;
import jen.tools.SoftProxyTestCase.InvHandler;
import jen.tools.SoftProxyTestCase.PPInterface;
import jen.tools.SoftProxyTestCase.PPInterfaceTwo;
import junit.framework.TestCase;

import static jen.tools.SoftProxy.createProxy;
import static jen.tools.SoftProxy.createProxyClass;
import static jen.tools.SoftProxy.newProxyInstance;

import static jen.tools.SoftProxy.DEFAULT_INVOKER_STRATEGY;

import static org.roscopeco.juno.converters.JUnitSupport.assertThat;
import static org.roscopeco.juno.Constraints.*;

public class IncorrectProxyUseTestCase extends TestCase
{
  public void testGenerationWithMoreThanOneConcreteClassFails() {
    try {
      createProxy(new Class<?>[] { HashMap.class, ArrayList.class });
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  public void testGenerationWithMoreThanOneNonPublicClassFails() {
    try {
      createProxy(new Class<?>[] { PPInterface.class, PPInterfaceTwo.class });
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  public void testGenerationWithMoreSingleFinalFails() {
    try {
      createProxy(String.class);
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  public void testGenerationWithMultipleIncludingOneFinalFails() {
    try {
      createProxy(new Class<?>[] { PPInterface.class, String.class, HashMap.class });
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  /* ******************************************** */
  
  public void testCreateClassClassesWithNullUsesDefault() {
    assertThat(createProxyClass(null, new Class<?>[] { PPInterface.class }, DEFAULT_INVOKER_STRATEGY).getClassLoader(),
        isEqual(Thread.currentThread().getContextClassLoader()));
  }
  
  // NOTE: Null strategy is just replaced with default strategy 
  
  public void testCreateClassClassWithNullClassFails() {
    try {
      createProxyClass((Class<?>)null);
      fail("Expected NAE");      
    } catch (NullArgumentException e) {
      // PASS
    }
  }
  
  public void testCreateClassClassesWithNullClassesFails() {
    try {
      createProxyClass((Class<?>[])null);
      fail("Expected NAE");
    } catch (NullArgumentException e) {
      // PASS
    }
  }
  
  public void testCreateClassClassesWithEmptyClassesFails() {
    try {
      createProxyClass(new Class<?>[0]);
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      // PASS
    }
  }
  
  /* ****************************************** */  
  
  public void testNewInstanceClassesWithNullInvocationHandlerFails() {
    try {
      newProxyInstance(new Class<?>[] { PPInterface.class }, (Object[])null);
      fail("Expected NAE");
    } catch (NullArgumentException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassWithNullInvocationHandlerFails() {
    try {
      newProxyInstance(PPInterface.class, (Object[])null);
      fail("Expected NAE");
    } catch (ProxyInstantiationException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassesWithNullClassesFails() {
    try {
      newProxyInstance((Class<?>[])null, new InvHandler());
      fail("Expected NAE");
    } catch (NullArgumentException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassWithNullClassFails() {
    try {
      newProxyInstance((Class<?>)null, new InvHandler());      
      fail("Expected NAE");
    } catch (NullArgumentException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassesWithEmptyClassesFails() {
    try {
      newProxyInstance(new Class<?>[0], new InvHandler());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassWithDefaultEmptyArgs() {
    try {
      newProxyInstance(PPInterface.class);
      fail("Expected ProxyInstantiationExceptionr");
    } catch (ProxyInstantiationException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassWithDefaultWrongArgs() {
    try {
      newProxyInstance(PPInterface.class, new Object());
      fail("Expected ProxyInstantiationException");
    } catch (ProxyInstantiationException e) {
      /* PASS */
    }
  }
  
  public void testNewInstanceClassWithDefaultBadConstructor() {
    try {
      newProxyInstance(PPInterface.class, new InvHandler(), new Object(), "String");
      fail("Expected IAE");
    } catch (ProxyInstantiationException e) {
      /* PASS */
      assertThat(e.getCause(),instOf(NoSuchMethodException.class));
    }
  }
}
