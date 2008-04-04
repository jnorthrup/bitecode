/* FrozenAbstractSoftMethodTestCase.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/23 02:15:14 $
 * Originated: Oct 23, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import junit.framework.TestCase;

import static org.objectweb.asm.Opcodes.*;

import static org.roscopeco.juno.converters.JUnitSupport.assertThat;
import static org.roscopeco.juno.Constraints.*;

public class FrozenAbstractSoftMethodTestCase extends TestCase
{
  private MethodNode throws_method;
  
  @SuppressWarnings("unchecked")  
  public FrozenAbstractSoftMethodTestCase() throws Exception {
    ClassReader rdr = new ClassReader(SimpleDummy.class.getName());
    ClassNode n = new ClassNode();
    rdr.accept(n,false);
    throws_method = (MethodNode)n.methods.get(5);
  }
  
  // we use ASMSoftMethod here (it inherits what we're testing from the abstract class)
  
  /* **********************
   *  ABSTRACT SOFT MEMBER
   */
  public void testAbstractMemberModifiersIsAsExpectedWhenFrozen() {
    SoftClass sc = new SoftClass(getClass());
    ASMSoftMethod m = new ASMSoftMethod(sc,throws_method);
    
    assertThat(m.getModifiers(),isEqual(ACC_PUBLIC));
    
    // Can do this now?
    m.setModifiers(ACC_FINAL | ACC_PRIVATE);
    assertThat(m.getModifiers(),isEqual(ACC_FINAL | ACC_PRIVATE));
    
    sc.generateBytecode();
    
    assertThat(m.canModify(),isFalse());
    
    try {
      m.setModifiers(0);
      fail("Expected IllegalState");
    } catch (IllegalStateException e) {
      // PASS
    }
  }
  
  public void testAbstractMemberNameIsAsExpectedWhenFrozen() {
    SoftClass sc = new SoftClass(getClass());
    ASMSoftMethod m = new ASMSoftMethod(sc,throws_method);
    
    assertThat(m.getName(),isEqual("throwsMethod"));
    
    // Can do this now?
    m.setName("knowsMethod");
    assertThat(m.getName(),isEqual("knowsMethod"));
    
    sc.generateBytecode();
    
    assertThat(m.canModify(),isFalse());
    
    try {
      m.setName("throwsMethod");
      fail("Expected IllegalState");
    } catch (IllegalStateException e) {
      // PASS
    }
  }
  
  /* **********************
   *  ABSTRACT SOFT METHOD
   */
  public void testAbstractMethodReturnTypeIsAsExpectedWhenFrozen() {
    SoftClass sc = new SoftClass(getClass());
    ASMSoftMethod m = new ASMSoftMethod(sc,throws_method);
    
    assertThat(m.getReturnType(),isEqual(Type.VOID_TYPE));
    
    // Can do this now?
    m.setReturnType(Type.INT_TYPE);
    assertThat(m.getReturnType(),isEqual(Type.INT_TYPE));
    
    sc.generateBytecode();
    
    assertThat(m.canModify(),isFalse());
    
    try {
      m.setReturnType(Type.INT_TYPE);
      fail("Expected IllegalState");
    } catch (IllegalStateException e) {
      // PASS
    }
  }
  
  public void testAbstractMethodListsAreImmutableWhenFrozen() {
    SoftClass sc = new SoftClass(getClass());
    ASMSoftMethod m = new ASMSoftMethod(sc,throws_method);
    
    sc.generateBytecode();
    
    try {
      m.getArgumentTypes().add(Type.BOOLEAN_TYPE);
      fail("Expected UnsuppOp");
    } catch (UnsupportedOperationException e) {
      // PASS
    }
    
    try {
      m.getThrowsTypes().add(Type.BOOLEAN_TYPE);
      fail("Expected UnsuppOp");
    } catch (UnsupportedOperationException e) {
      // PASS
    }    
  }
}
