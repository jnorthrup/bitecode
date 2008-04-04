/* DefaultStrategyCtorsTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/20 22:34:42 $
 * Originated: Oct 19, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

import jen.SoftClass;
import jen.tools.SoftProxyTestCase.NullInvHandler;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

import junit.framework.TestCase;

public class DefaultStrategyCtorsTestCase extends TestCase
{
  static class WithPrivateNullCtor {
    final Boolean dummy;
    
    private WithPrivateNullCtor() { this.dummy = true; }    
  }
  
  static class WithNoNullCtor {
    final Boolean dummy;
    
    public WithNoNullCtor(boolean dummy) { this.dummy = dummy; }    
  }
  
  static class WithOneExtraCtor {
    final boolean dummy;
    
    protected WithOneExtraCtor() { dummy = true; } 
    
    public WithOneExtraCtor(boolean dummy) { this.dummy = dummy; }    
  }
  
  static class WithTwoExtraButOnePrivateCtors {
    boolean dummy = true;
    
    WithTwoExtraButOnePrivateCtors() {
      super();
    }
    
    WithTwoExtraButOnePrivateCtors(boolean dummy) {
      this.dummy = dummy;
    }

    private WithTwoExtraButOnePrivateCtors(String dummy) {
      this(dummy != null);
    }
  }
  
  public void testProxyClassWithNullAndOneExtraConstructor() throws Exception {
    Class<WithOneExtraCtor> tic = SoftProxy.createProxyClass(WithOneExtraCtor.class);
    assertThat(tic.getDeclaredConstructors(),exactSize(1));

    // DEFAULT PROXY CTOR
    Constructor<WithOneExtraCtor> ctor;
    WithOneExtraCtor tpc;
    try {
      ctor = tic.getDeclaredConstructor(InvocationHandler.class, String.class);
      fail("Expected NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      /* PASS */
    }
    
    // SUPER CTOR WITH ARG
    ctor = tic.getConstructor(InvocationHandler.class, boolean.class);
    assertThat(ctor.getModifiers() & SoftClass.ACC_PUBLIC,gt(0));    
    tpc = ctor.newInstance(new NullInvHandler(),true);
    assertThat(tpc.dummy,isTrue());
  }
  
  public void testProxyClassWithNullAndTwoExtraOnePrivateConstructors() throws Exception {
    Class<WithTwoExtraButOnePrivateCtors> tic = SoftProxy.createProxyClass(WithTwoExtraButOnePrivateCtors.class);
    assertThat(tic.getDeclaredConstructors(),exactSize(1));
    
    // DEFAULT PROXY CTOR - public access
    Constructor<WithTwoExtraButOnePrivateCtors> ctor = tic.getConstructor(InvocationHandler.class);
    assertThat(ctor.getModifiers() & SoftClass.ACC_PUBLIC,gt(0));    
    WithTwoExtraButOnePrivateCtors tpc = ctor.newInstance(new NullInvHandler());
    assertThat(tpc.dummy,isTrue());
    
    // SUPER CTOR WITH ARG - default access
    try {
      ctor = tic.getDeclaredConstructor(InvocationHandler.class, boolean.class);
      fail("Expected NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      /* PASS */
    }
    
    // SUPER CTOR WITH ARG #2 - private access (not proxied)
    try {
      ctor = tic.getDeclaredConstructor(InvocationHandler.class, String.class);
      fail("Expected NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      /* PASS */
    }
  }
  
  public void testProxyClassWithNoNullConstructor() throws Exception {
    Class<WithNoNullCtor> tic = SoftProxy.createProxyClass(WithNoNullCtor.class);
    assertThat(tic.getDeclaredConstructors(),exactSize(1));

    // DEFAULT PROXY CTOR
    Constructor<WithNoNullCtor> ctor;
    try {
      ctor = tic.getDeclaredConstructor(InvocationHandler.class);
      fail("Expected NoSuchMethodException");
    } catch (NoSuchMethodException e) {
      /* PASS */
    }
    
    // SUPER CTOR WITH ARG
    ctor = tic.getConstructor(InvocationHandler.class, boolean.class);
    assertThat(ctor.getModifiers() & SoftClass.ACC_PUBLIC,gt(0));       
    WithNoNullCtor tpc = ctor.newInstance(new NullInvHandler(),true);
    assertThat(tpc.dummy,isTrue());
  }
   
  public void testProxyClassWithPrivateNullConstructor() throws Exception {
    
    // I don't think there's much else we can do ... ?
    
    try {
      SoftProxy.createProxyClass(WithPrivateNullCtor.class);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
}
