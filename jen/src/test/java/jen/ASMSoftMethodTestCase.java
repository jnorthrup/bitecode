/* ASMSoftMethodTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/23 02:15:19 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import junit.framework.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.*;

// THIS ALSO TESTS MOST OF ABSTRACTSOFTMETHOD
public class ASMSoftMethodTestCase extends TestCase
{  
  private MethodNode void_method;
  private MethodNode args_method;
  private MethodNode throws_method;
  private MethodNode private_method;
  
  @SuppressWarnings("unchecked")  
  public ASMSoftMethodTestCase() throws Exception {
    ClassReader rdr = new ClassReader(SimpleDummy.class.getName());
    ClassNode n = new ClassNode();
    rdr.accept(n,-1);
    void_method = (MethodNode)n.methods.get(4);
    throws_method = (MethodNode)n.methods.get(5);
    args_method = (MethodNode)n.methods.get(6);
    private_method = (MethodNode)n.methods.get(7);
  }
  
  public void testInstantiationWithNullsThrowsException() {
    try {
      new ASMSoftMethod(null,null);
      fail("Expected NullArg exception 1");
    } catch (NullArgumentException e) {
      /* pass */
    }
    try {
      new ASMSoftMethod(null,void_method);
      fail("Expected NullArg exception 2");
    } catch (NullArgumentException e) {
      /* pass */
    }
    try {
      new ASMSoftMethod(new SoftClass(getClass()),null);
      fail("Expected NullArg exception 3");
    } catch (NullArgumentException e) {
      /* pass */
    }
  }  
  
  public void testItWorksWithNullMethod() {
    ASMSoftMethod m = new ASMSoftMethod(new SoftClass(getClass()),void_method);
    assertEquals("Wrong modifiers",Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,m.getModifiers());
    assertEquals("Wrong name","voidMethod",m.getName());
    assertEquals("Wrong argument types",new ArrayList(),m.getArgumentTypes());
    assertEquals("Wrong throws types",new ArrayList(),m.getThrowsTypes());
    assertEquals("Wrong return type",Type.VOID_TYPE,m.getReturnType());
  }

  public void testItWorksWithArgsMethod() {
    ASMSoftMethod m = new ASMSoftMethod(new SoftClass(getClass()),args_method);
    assertEquals("Wrong modifiers",Opcodes.ACC_PUBLIC,m.getModifiers());
    assertEquals("Wrong name","argsMethod",m.getName());
    assertEquals("Wrong argument types",DummyClass.EXPECT_ARGS,m.getArgumentTypes());
    assertEquals("Wrong throws types",new ArrayList(),m.getThrowsTypes());
    assertEquals("Wrong return type",Type.getType(String.class),m.getReturnType());
  }

  public void testItWorksWithThrowsMethod() {
    ASMSoftMethod m = new ASMSoftMethod(new SoftClass(getClass()),throws_method);
    assertEquals("Wrong modifiers",Opcodes.ACC_PUBLIC,m.getModifiers());
    assertEquals("Wrong name","throwsMethod",m.getName());
    assertEquals("Wrong argument types",new ArrayList(),m.getArgumentTypes());
    assertEquals("Wrong throws types",DummyClass.EXPECT_THROWS,m.getThrowsTypes());
    assertEquals("Wrong return type",Type.VOID_TYPE,m.getReturnType());
  }

  public void testItWorksWithPrivateMethod() {
    ASMSoftMethod m = new ASMSoftMethod(new SoftClass(getClass()),private_method);
    assertEquals("Wrong modifiers",Opcodes.ACC_PRIVATE,m.getModifiers());
    assertEquals("Wrong name","privateMethod",m.getName());
    assertEquals("Wrong argument types",new ArrayList(),m.getArgumentTypes());
    assertEquals("Wrong throws types",new ArrayList(),m.getThrowsTypes());
    assertEquals("Wrong return type",Type.getType(String.class),m.getReturnType());
  }       
}
