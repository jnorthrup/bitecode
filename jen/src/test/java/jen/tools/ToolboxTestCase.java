/* ToolboxTestCase.java - Awaiting description
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
 * File version: $Revision: 1.8 $ $Date: 2005/10/31 01:50:47 $
 * Originated: Oct 5, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Method;
import java.util.HashMap;

import jen.NullArgumentException;

import org.objectweb.asm.Type;

import junit.framework.TestCase;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

import static jen.tools.Toolbox.*;

public class ToolboxTestCase extends TestCase
{
  private static final Method AM = knownMethod(ToolboxTestCase.class,"aMethod",HashMap.class, int.class);
  private static final Method TM = knownMethod(ToolboxTestCase.class,"anotherMethod",Object.class);
  private static final Method VM = knownMethod(ToolboxTestCase.class,"testMethodDescriptor");
  
  public long aMethod(HashMap arg1, int arg2) {
    return 0;
  }
  
  public String anotherMethod(Object arg1) throws Exception {
    return "";
  }
  
  public void testPrimitiveTypeIdentWithBadArgs() {
    try {
      primitiveClassIdent(null);
      fail("Expected NullArgE");
    } catch (NullArgumentException e) {
      /* PASS */
    }
    
    try {
      primitiveClassIdent(String.class);
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
    
    try {
      primitiveClassIdent(int[].class);
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }

    try {
      primitiveClassIdent(Boolean.class);
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  public void testPrimitiveWrapperClassWithBadArgs() {
    try {
      primitiveWrapperClass('X');
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      /* PASS */
    }
  }
  
  public void testPrimitiveWrapperClassAndClassIdent() {
    assertThat(primitiveWrapperClass(primitiveClassIdent(byte.class)),isEqual(Byte.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(char.class)),isEqual(Character.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(double.class)),isEqual(Double.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(float.class)),isEqual(Float.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(int.class)),isEqual(Integer.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(long.class)),isEqual(Long.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(short.class)),isEqual(Short.class));    
    assertThat(primitiveWrapperClass(primitiveClassIdent(boolean.class)),isEqual(Boolean.class));     
    assertThat(primitiveWrapperClass(primitiveClassIdent(void.class)),isEqual(Void.class));     
  }

  public void testPrimitiveWrapperTypeAndClassIdent() {
    assertThat(primitiveWrapperType(primitiveClassIdent(int.class)),isEqual(Type.getType(Integer.class)));   
    
    // if it does classes okay, one of these is enough
  }
  
  public void testMethodDescriptor() {
    assertThat(namedMethodDescriptor(VM),isEqual("testMethodDescriptor()V"));
    assertThat(namedMethodDescriptor(TM),isEqual("anotherMethod(Ljava/lang/Object;)Ljava/lang/String;"));
    assertThat(namedMethodDescriptor(AM),isEqual("aMethod(Ljava/util/HashMap;I)J"));
  }
  
  public void testMethodSignature() {
    assertThat(methodSignature(VM),isEqual("void testMethodDescriptor()"));
    assertThat(methodSignature(TM),isEqual("String anotherMethod(Object)"));
    assertThat(methodSignature(AM),isEqual("long aMethod(java.util.HashMap, int)"));
  }
  
  public void testKnownMethodWithBadMethod() {
    try {
      knownMethod(Boolean.class,"whereDidAllTheMethodsGo");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertThat(e.getCause(),instOf(NoSuchMethodException.class));
    }        
  }
  
  /* TODO Tests for findMatchingXXXX !!! */
}
