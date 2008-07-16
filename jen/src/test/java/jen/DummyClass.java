/* DummyClass.java - Testmodel
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:29 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

/** 
 * Testmodel
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:29 $ 
 */
public class DummyClass
{
  private static final String DUMMY_FIELD = "dummy";
  
  static final Constructor VOID_CTOR; 
  static final Constructor ARGS_CTOR;
  static final Constructor THROWS_CTOR; 
  static final Constructor PRIVATE_CTOR;

  static final Method VOID_METHOD; 
  static final Method ARGS_METHOD;
  static final Method THROWS_METHOD; 
  static final Method PRIVATE_METHOD;
  
  static final Field PRIVATE_REFERENCE_FIELD;
  static final Field PUBLIC_PRIMITIVE_FIELD;
  
  static final List EXPECT_THROWS;  
  static final List EXPECT_ARGS;
  
  static final Type PRIVATE_FIELD_TYPE = Type.getType(Object.class);
  static final Type PUBLIC_FIELD_TYPE = Type.getType(int.class);
  
  static {
    try {
      VOID_METHOD = DummyClass.class.getDeclaredMethod("voidMethod",(Class[])null);
      ARGS_METHOD = DummyClass.class.getDeclaredMethod("argsMethod",new Class[] { String.class, String.class });
      THROWS_METHOD = DummyClass.class.getDeclaredMethod("throwsMethod",(Class[])null);
      PRIVATE_METHOD = DummyClass.class.getDeclaredMethod("privateMethod",(Class[])null);
      
      VOID_CTOR = DummyClass.class.getDeclaredConstructor(new Class[0]);
      ARGS_CTOR = DummyClass.class.getDeclaredConstructor(new Class[] { String.class, String.class });
      THROWS_CTOR = DummyClass.class.getDeclaredConstructor(new Class[] { boolean.class });
      PRIVATE_CTOR = DummyClass.class.getDeclaredConstructor(new Class[] { String.class });
      
      PRIVATE_REFERENCE_FIELD = DummyClass.class.getDeclaredField("refField");
      PUBLIC_PRIMITIVE_FIELD = DummyClass.class.getDeclaredField("primField");
      
      ArrayList<Type> l = new ArrayList<Type>();
      l.add(Type.getType(ClassNotFoundException.class));
      l.add(Type.getType(InstantiationException.class));
      EXPECT_THROWS = Collections.unmodifiableList(l);
      
      l = new ArrayList<Type>();
      l.add(Type.getType(String.class));
      l.add(Type.getType(String.class));
      EXPECT_ARGS = Collections.unmodifiableList(l);
      
    } catch (Exception e) {
      throw(new InternalError("Existent methods cannot be reflected (JVM Error?): "+e));
    }
  }
  
  private static Object refField;
  public int primField;
  
  public DummyClass() { }
  public DummyClass(boolean dummy) throws ClassNotFoundException, InstantiationException { }
  public DummyClass(String dummy, String arg2) { }
  private DummyClass(String dummy) { }
  
  public static void voidMethod() { }  
  public void throwsMethod() throws ClassNotFoundException, InstantiationException { 
    throw new ClassNotFoundException(); 
  }  
  public String argsMethod(String arg1, String arg2) { return ""+arg1+arg2; }
  private String privateMethod() { return null; }
  
  // for reflectInvoke
  protected String protectedMethod() {
    return "TEST";
  }
}
