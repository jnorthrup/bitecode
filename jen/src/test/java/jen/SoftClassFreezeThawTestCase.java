/* SoftClassFreezeThawTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/31 02:09:59 $
 * Originated: Oct 23, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import junit.framework.TestCase;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.converters.JUnitSupport.assertThat;

public class SoftClassFreezeThawTestCase extends TestCase
{
  SoftClass c;
  
  // ONLY BECAUSE WE NEVER DEFINE IT
  public void setUp() {
    c = new SoftClass(SimpleDummy.class);
    c.setName("jen.DummyClassFive05");
  }
  
  public void testFreezeThawRevisitSemantics() throws Exception {
    c.removeSoftMethod(c.getSoftMethod("void <init>(boolean)"));
    c.removeSoftMethod(c.getSoftMethod("String privateMethod()"));
    byte[] b = c.generateBytecode();
    
    assertThat(b,notNull());
    assertThat(c.isFrozen(),isTrue());
    
    try {
      c.setName("SomeNewName");
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      /* PASS */
    }
    
    
    ClassVisitor v = new ClassWriter(-1);

    // can still visit?
    c.accept(v);    
    
    c.thaw();
    
    try {
      c.setName("SomeNewName");
    } catch (IllegalStateException e) {
      fail("Expected IllegalStateException");
    }
    
    // can visit now too? 
    c.accept(v);    
  }
  
  public void testCheckModifyChain() {
    // can do it
    ASMSoftMethod sm = (ASMSoftMethod)c.getSoftMethod("void <init>(boolean)");
    sm.setModifiers(0);
    
    // but if we freeze it
    c.generateBytecode();
    
    // now we can't
    try {
      sm.setModifiers(0);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      /* pass */
    }    
  }
  
  public void testSoftClassCheckModify() {    
    c.generateBytecode();
    try {
      c.putSoftMethod(new AbstractSoftMethod(c) {
        @Override
        public void accept(ClassVisitor vis) {
        }
      });
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      /* pass */
    }
  }
}
