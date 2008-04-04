/* SoftMockTestCase.java - Awaiting description
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
 * File version: $Revision: 1.7 $ $Date: 2005/10/31 01:50:47 $
 * Originated: Oct 6, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import org.roscopeco.juno.Constraint;

import junit.framework.TestCase;

import static jen.tools.StubMock.*;
import static jen.tools.SoftMock.*;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

public class SoftMockTestCase extends TestCase
{
  public void testCanMockInterfaceOnMethodNameAlone() {
    TestProxyInterface tif = SoftMock.newMockInstance(TestProxyInterface.class, new MockMethod[] {
      new StubMock("basicMethod",returnValue("dohteMcisab")),
      new StubMock("anotherArgsMethod",returnValue("dohteMsgrArehtona"))
    });
    
    assertThat(tif.basicMethod(),isEqual("dohteMcisab"));
    assertThat(tif.anotherArgsMethod("booya",new Object()),isEqual("dohteMsgrArehtona"));
  }

  public void testCanMockInterfaceWithArgDispatch() {
    TestProxyInterface tif = SoftMock.newMockInstance(TestProxyInterface.class, new MockMethod[] {
      new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TEST"), notNull() },returnValue("anotherArgsMethod")),
      new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TSET"), notNull() },returnValue("dohteMsgrArehtona")),
      new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TEST"), isNull() },returnValue("anotherArgsMethodWithNull"))
    });
        
    assertThat(tif.anotherArgsMethod("TEST",new Object()),isEqual("anotherArgsMethod"));
    assertThat(tif.anotherArgsMethod("TEST",null),isEqual("anotherArgsMethodWithNull"));
    assertThat(tif.anotherArgsMethod("TSET",new Object()),isEqual("dohteMsgrArehtona"));
    
    // default (not specified)
    assertThat(tif.anotherArgsMethod("TSET",null),isNull());
  }
  
  public void testCanMockInterfaceWithExceptionThrowStub() {
    TestProxyInterface tif = SoftMock.newMockInstance(TestProxyInterface.class, new MockMethod[] {
        // this will hit first if second arg is null
        new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TEST"), isNull() },throwException(new IllegalArgumentException("TEST"))),
        
        // don't care about the second arg, so we could pass null or just omit it.
        new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TEST") },returnValue("OK"))
    });
    
    assertThat(tif.anotherArgsMethod("TEST",new Object()),isEqual("OK"));
    
    try {
      assertThat(tif.anotherArgsMethod("TEST",null),isEqual("anotherArgsMethodWithNull"));
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(),isEqual("TEST"));      
    }
  }

  // ALSO TESTING STATIC HELPERS
  public void testCanMockClassWithArgDispatch() {
    TestProxyClass tpc = SoftMock.newMockInstance(TestProxyClass.class, new MockMethod[] {
      new StubMock("argsMethod",new Constraint[] { eq(11), notNull() },returnValue("WASELEVENNOTNULL")),
      new StubMock("argsMethod",new Constraint[] { ANYTHING, notNull() },returnValue("NOTELEVENNOTNULL")),
      new StubMock("argsMethod",new Constraint[] { gt(0) },returnValue("GTZEROANDANYTHING")),
      new StubMock("argsMethod",returnValue("LASTDITCH"))
    });
    
    assertThat(tpc.argsMethod(11,getClass()),isEqual("WASELEVENNOTNULL"));
    assertThat(tpc.argsMethod(11,null),isEqual("GTZEROANDANYTHING"));
    assertThat(tpc.argsMethod(37,getClass()),isEqual("NOTELEVENNOTNULL"));
    assertThat(tpc.argsMethod(6,getClass()),isEqual("NOTELEVENNOTNULL"));
    assertThat(tpc.argsMethod(6,null),isEqual("GTZEROANDANYTHING"));
    assertThat(tpc.argsMethod(-6,null),isEqual("LASTDITCH"));
  }
  
  public void testCanMockInterfaceWithArgDispatchWithJavaProxies() {
    TestProxyInterface tif = (TestProxyInterface)SoftMock.newMockInstance(new Class<?>[] { TestProxyInterface.class }, new MockMethod[] {
        new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TEST"), notNull() },returnValue("anotherArgsMethod")),
        new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TSET"), notNull() },returnValue("dohteMsgrArehtona")),
        new StubMock("anotherArgsMethod",new Constraint[] { isEqual("TEST"), isNull() },returnValue("anotherArgsMethodWithNull"))
    });
    
    assertThat(tif.anotherArgsMethod("TEST",new Object()),isEqual("anotherArgsMethod"));
    assertThat(tif.anotherArgsMethod("TEST",null),isEqual("anotherArgsMethodWithNull"));
    assertThat(tif.anotherArgsMethod("TSET",new Object()),isEqual("dohteMsgrArehtona"));
    
    // default (not specified)
    assertThat(tif.anotherArgsMethod("TSET",null),isNull());
  }  
  
  public static interface PrimInter {
    public long longMethod();
    public boolean boolMethod();
    public char charMethod();
    public int[] intArrMethod();  // ref
  }
  
  // we don't want NPE with the default invocation handler and primitive methods, which
  // we would get if it blindly returned null. Auto[un]boxing doesn't operate at this
  // level remember...
  public void testCanMockInterfaceWithPrimitiveReturnMethodsBothCustomAndDefault() {
    final int[] theInts = new int[0];
    
    PrimInter tif = SoftMock.newMockInstance(PrimInter.class, new MockMethod[] {
        new StubMock("longMethod",returnValue(21)),
        new StubMock("intArrMethod",returnValue(theInts)),
    });    
    
    assertThat(tif.longMethod(),isEqual(21L));
    assertThat(tif.boolMethod(),isEqual(false));
    assertThat(tif.charMethod(),isEqual(Character.valueOf((char)0)));
    assertThat(tif.intArrMethod(),isEqual(theInts));    
  }  
}
