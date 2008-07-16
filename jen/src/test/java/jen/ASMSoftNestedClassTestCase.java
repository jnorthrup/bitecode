/* ASMSoftNestedClassTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/23 02:15:17 $
 * Originated: Oct 2, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import org.roscopeco.juno.pre50.JunoTestCase;

public class ASMSoftNestedClassTestCase extends JunoTestCase
{
  static final String SSNC_NAME = "jen.ASMSoftNestedClassTestCase$NestedClass";
  static final String ISNC_NAME = "jen.ASMSoftNestedClassTestCase$1LocalClass";
  static final String ASNC_NAME = "jen.ASMSoftNestedClassTestCase$1";
  
  // NESTED classes have outer, INNER classes don't
  static final String STATIC_OUTER = "jen.ASMSoftNestedClassTestCase";
  static final String INNER_OUTER = null;

  // NESTED and LOCAL INNER classes have inner, ANONYMOUS INNER don't
  static final String SSNC_INNER = "NestedClass";
  static final String ISNC_INNER = "LocalClass";
  static final String ASNC_INNER = null;
  
  static final SoftClass sc = new SoftClass(ASMSoftNestedClassTestCase.class);
  
  static final NestedSoftClass ssnc = sc.getNestedSoftClass(SSNC_NAME);
  static final NestedSoftClass isnc = sc.getNestedSoftClass(ISNC_NAME);
  static final NestedSoftClass asnc = sc.getNestedSoftClass(ASNC_NAME);
  
  public static final class NestedClass {
    public void aMethod() { }
  }
  
  public void justATestMethod() {
    class LocalClass {
      void anotherMethod() { }
    }
    
    LocalClass o = new LocalClass() {
      public void anotherMethod() { /* override! */ }
    };
    
    o.anotherMethod();
  }  

  public void testGetName() {
    assertThat(ssnc.getName(),isEqual(SSNC_NAME));
    assertThat(isnc.getName(),isEqual(ISNC_NAME));
    assertThat(asnc.getName(),isEqual(ASNC_NAME));
  }

  /* FUNCTIONAL AND INSTANTIATION TEST performed elsewhere 
  public void testAccept() {
  }

  public void testASMSoftNestedClass() {
  }
  */

  public void testGetInnerName() {
    assertThat(ssnc.getInnerName(),isEqual(SSNC_INNER));
    assertThat(isnc.getInnerName(),isEqual(ISNC_INNER));
    assertThat(asnc.getInnerName(),isEqual(ASNC_INNER));
  }

  public void testGetOuterName() {
    assertThat(ssnc.getOuterName(),isEqual(STATIC_OUTER));
    assertThat(isnc.getOuterName(),isEqual(INNER_OUTER));
    assertThat(asnc.getOuterName(),isEqual(INNER_OUTER));
  }
  
  public void testGetNode() {
    assertThat(((ASMSoftNestedClass)ssnc).getNode().innerName,isEqual(SSNC_INNER));
    assertThat(((ASMSoftNestedClass)isnc).getNode().innerName,isEqual(ISNC_INNER));
    assertThat(((ASMSoftNestedClass)asnc).getNode().innerName,isEqual(ASNC_INNER));
  }
  
  public void testAccept() {
    ClassNode n = new ClassNode();
    isnc.accept(n);
    
    assertThat(((InnerClassNode)((List<?>)n.innerClasses).get(0)).innerName,isEqual(ISNC_INNER));
  }
}
