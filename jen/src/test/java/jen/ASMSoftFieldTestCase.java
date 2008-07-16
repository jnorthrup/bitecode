/* JavaSoftConstructorTestCase.java - Awaiting description
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
 * File version: $Revision: 1.4 $ $Date: 2005/10/20 22:34:40 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import org.roscopeco.juno.pre50.JunoTestCase;

/** 
 * Testcase for JavaSoftConstructor. <strong>Note: This testcase doesn't
 * stand alone - it backs up the {@link ASMSoftFieldTestCase} with
 * constructor specific stuff only.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.4 $ $Date: 2005/10/20 22:34:40 $ 
 */
@SuppressWarnings("unchecked")
public class ASMSoftFieldTestCase extends JunoTestCase
{
  static final FieldNode primitive_field;
  static final FieldNode private_field;
  
  static {
    ClassReader rdr = null;
    try {
      rdr = new ClassReader(SimpleDummy.class.getName());
    } catch (Exception e) {
      throw(new RuntimeException(e));
    }
    
    ClassNode n = new ClassNode();
    rdr.accept(n,false);
    private_field = (FieldNode)n.fields.get(0);
    primitive_field = (FieldNode)n.fields.get(1);
  }
  
  public void testInstantiationWithNullsThrowsException() {
    try {
      new ASMSoftField(null,null);
      fail("Expected NullArg exception 1");
    } catch (NullArgumentException e) {
      /* pass */
    }
    try {
      new ASMSoftField(null,primitive_field);
      fail("Expected NullArg exception 2");
    } catch (NullArgumentException e) {
      /* pass */
    }
    try {
      new ASMSoftField(new SoftClass(getClass()),null);
      fail("Expected NullArg exception 3");
    } catch (NullArgumentException e) {
      /* pass */
    }
  }  
  
  public void testItWorksWithPublicField() {
    ASMSoftField f = new ASMSoftField(new SoftClass(getClass()),primitive_field);
    assertThat("Wrong modifiers",new Integer(f.getModifiers()),isEqual(new Integer(Opcodes.ACC_PUBLIC)));
    assertThat("Wrong name",f.getName(),isEqual("primField"));
    assertThat("Wrong field type",f.getType(),isEqual(DummyClass.PUBLIC_FIELD_TYPE));
  }
  
  public void testItWorksWithPrivateField() {
    ASMSoftField f = new ASMSoftField(new SoftClass(getClass()),private_field);
    assertThat("Wrong modifiers",new Integer(f.getModifiers()),isEqual(new Integer(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC)));
    assertThat("Wrong name",f.getName(),isEqual("refField"));
    assertThat("Wrong field type",f.getType(),isEqual(DummyClass.PRIVATE_FIELD_TYPE));
  }
}
