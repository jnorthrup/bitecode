/* MultiDispatchProxyTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/30 23:47:46 $
 * Originated: Oct 28, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.util.HashMap;

import jen.NullArgumentException;

import junit.framework.TestCase;

import static jen.SoftUtils.defaultClassLoader;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.converters.JUnitSupport.assertThat;

public class MultiDispatchProxyTestCase extends TestCase
{    
  // two arg
  static final Object[][] callArgs = {
    {new Integer(25), new String("A String")},
    {new Integer(25), new StringBuffer("A String")},
    {new Double(33.3), "A String"},
    {"A string", "Another String"}
  };  
  
  // These returns would all be Object, Object (and Object) without the
  // multi proxy.
  static final Object[] callPreferConcreteRets = {
    "(Integer, String) : Integer, String",
    "(Number, CharSequence) : Integer, StringBuffer",
    "(Number, CharSequence) : Double, String",
    "(String, String) : String, String",
  };
  static final Object[] callNoWeightRets = {
    "(Integer, String) : Integer, String",
    "(Number, CharSequence) : Integer, StringBuffer",
    "(Number, CharSequence) : Double, String",
    "(String, String) : String, String",
  };
  
  static class MyRunnable implements Runnable { public void run() { /* noop */  } }
  
  // three arg
  static final Object[][] callThreeArgs = {
    {"A String",new HashMap(), new Integer(25)},
    {new Thread(), new Thread(), new Long(33L)},    
    {new Double(33.3), "A String", new Integer(18)},
    {new Thread(), new MyRunnable(), new Long(33L)},    
    {"More Strings", new MyRunnable(), new Long(33L)},    
  };
  static final Object[] callThreePreferConcreteRets = {
    "(String, Object, Number) : String, HashMap, Integer",
    "(Thread, Object, Number) : Thread, Thread, Long",
    "(Object, Object, Integer) : Double, String, Integer",
    "(Thread, Object, Number) : Thread, MyRunnable, Long",
    "(String, Object, Number) : String, MyRunnable, Long",
  };
  static final Object[] callThreeNoWeightRets = {
    "(String, Object, Number) : String, HashMap, Integer",
    "(Thread, Object, Number) : Thread, Thread, Long",
    "(Object, Object, Integer) : Double, String, Integer",
    "(Thread, Object, Number) : Thread, MyRunnable, Long",
    "(String, Object, Number) : String, MyRunnable, Long",
  };
  
  final Class<DynaTest> dyna = SoftProxy.createProxyClass(defaultClassLoader(),DynaTest.class,new MultiDispatchStrategy());
  DynaTest d;
  
  public void setUp() throws Exception {
    d = dyna.newInstance();    
  }
  
  public void testProxyWithNullClassesFails() {
    try {
      SoftProxy.createProxyClass(defaultClassLoader(),(Class<?>)null,new MultiDispatchStrategy());
      fail("Expected NAE");
    } catch (NullArgumentException e) {
      /* PASS */
    }    
  }
  
  public void testProxyWithMultipleClassesFails() {
    try {
      SoftProxy.createProxyClass(defaultClassLoader(),new Class[] { DynaTest.class, HashMap.class },new MultiDispatchStrategy());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }    
  }
  
  public void testProxyWithNonConcreteClassFails() {
    try {
      SoftProxy.createProxyClass(defaultClassLoader(),new Class[] { Runnable.class },new MultiDispatchStrategy());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }    
  }
  
  public void testProxyWithNonFinalClassFails() {
    try {
      SoftProxy.createProxyClass(defaultClassLoader(),new Class[] { String.class },new MultiDispatchStrategy());
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }    
  }
  
  public void testCanInstantiateMultiProxy() throws Exception {
    assertThat(d,notNull());
  }
  
  public void testMultiDispatchWorksProperly() throws Exception {
    for (int call = 0; call < callArgs.length; call++) {
      Object o1 = callArgs[call][0];
      Object o2 = callArgs[call][1];
      
      // The compile time binding for this call is Object, Object, Object,
      // but it will be dispatched according to it's runtime type.
      // In fact the compile time binding doesn't matter, since all the
      // methods are proxied to handle the dispatch...
      String r = d.overLoadOne(o1,o2);
      assertThat(r,isEqual(callPreferConcreteRets[call]));
    }
    
    for (int call = 0; call < callThreeArgs.length; call++) {
      Object o1 = callThreeArgs[call][0];
      Object o2 = callThreeArgs[call][1];
      Object o3 = callThreeArgs[call][2];
      
      String r = d.overLoadOne(o1,o2,o3);
      assertThat(r,isEqual(callThreePreferConcreteRets[call]));
    }   
  }  
  
  public void testMultiDispatchWorksWithPrimitiveArgs() {
    assertThat(d.overLoadOne(true,new MyRunnable(),"A string", 145),isEqual("(boolean, Runnable, Object, int) : boolean, MyRunnable, String, int"));        
  }
  
  // TODO more tests 
  //
  //    void methods, null args, inner classes  
}
