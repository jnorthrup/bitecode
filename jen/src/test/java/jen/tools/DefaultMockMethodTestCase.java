/* DefaultMockMethodTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:42 $
 * Originated: Oct 8, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Method;

import static jen.tools.Toolbox.knownMethod;

import static org.roscopeco.juno.Constraints.isEqual;
import static org.roscopeco.juno.Constraints.isNull;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

import junit.framework.TestCase;

public class DefaultMockMethodTestCase extends TestCase
{
  public boolean boolMethod() { return false; }
  public byte byteMethod() { return 0; }
  public char charMethod() { return 0; }
  public double doubleMethod() { return 0; }
  public float floatMethod() { return 0; }
  public int intMethod() { return 0; }
  public long longMethod() { return 0; }
  public short shortMethod() { return 0; }  
  public String refMethod() { return null; }
  
  final Method boolM = knownMethod(getClass(),"boolMethod");
  final Method byteM = knownMethod(getClass(),"byteMethod");
  final Method charM = knownMethod(getClass(),"charMethod");
  final Method doubleM = knownMethod(getClass(),"doubleMethod");
  final Method floatM = knownMethod(getClass(),"floatMethod");
  final Method intM = knownMethod(getClass(),"intMethod");
  final Method longM = knownMethod(getClass(),"longMethod");
  final Method shortM = knownMethod(getClass(),"shortMethod");
  final Method refM = knownMethod(getClass(),"refMethod");
  final Method voidM = knownMethod(getClass(),"testDefaultInvoker");
  
  final Object[] nullArgs = new Object[0];
  
  public void testDefaultInvoker() throws Throwable {    
    MockMethod mh = new MockProxyHandler(new MockMethod[0]).defaultMock();
    
    assertThat(mh.invocationMatches(this,boolM,nullArgs),isEqual(true));
    assertThat(mh.invocationMatches(null,null,null),isEqual(true));
    
    assertThat(mh.invoke(this,boolM,nullArgs).getClass(),isEqual(Boolean.class));
    assertThat(mh.invoke(this,byteM,nullArgs).getClass(),isEqual(Byte.class));
    assertThat(mh.invoke(this,charM,nullArgs).getClass(),isEqual(Character.class));
    assertThat(mh.invoke(this,doubleM,nullArgs).getClass(),isEqual(Double.class));
    assertThat(mh.invoke(this,floatM,nullArgs).getClass(),isEqual(Float.class));
    assertThat(mh.invoke(this,intM,nullArgs).getClass(),isEqual(Integer.class));
    assertThat(mh.invoke(this,longM,nullArgs).getClass(),isEqual(Long.class));
    assertThat(mh.invoke(this,shortM,nullArgs).getClass(),isEqual(Short.class));
    
    assertThat(mh.invoke(this,refM,nullArgs),isNull());
    assertThat(mh.invoke(this,voidM,nullArgs),isNull());    
  }  
}
