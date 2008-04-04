/* NewSoftClassTestCase.java - Awaiting description
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
 * File version: $Revision: 1.8 $ $Date: 2005/10/30 23:44:49 $
 * Originated: 06-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.roscopeco.juno.pre50.*;

import java.net.*;

public class SoftClassTestCase extends JunoTestCase
{
  ClassReader rdr;
  
  protected void setUp() throws Exception {
    rdr = new ClassReader(SimpleDummy.class.getName());    
  }
  
  public void testNewSoftClassWithNulls() {
    try {
      // NULL CLASS
      try {
        new SoftClass((Class)null);
        fail("Expected NPE");
      } catch (NullArgumentException e) {
        /* pass */
      }
      
      // NULL READER
      try {
        new SoftClass((ClassReader)null);
        fail("Expected NPE");
      } catch (NullArgumentException e) {
        /* pass */
      }

      // NULL NODE
      try {
        new SoftClass((ClassNode)null);
        fail("Expected NPE");
      } catch (NullArgumentException e) {
        /* pass */
      }
    } catch (Exception e) {
      fail("Unexpected exception "+e);
    }
  }
  
  /*
   * Class under test for void NewSoftClass(ClassNode)
   */
  public void testNewSoftClassClassNode() throws Exception {
    ClassNode node = new ClassNode();
    rdr.accept(node,0);
    SoftClass sc = new SoftClass(rdr);
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
  }

  /*
   * Class under test for void NewSoftClass(ClassReader)
   */
  public void testNewSoftClassClassReader() {
    SoftClass sc = new SoftClass(rdr);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
  }

  /*
   * Class under test for void NewSoftClass(ClassReader, ClassLoader, boolean (true))
   */
  public void testNewSoftClassClassReaderLoaderNullbooleantrue() {
    SoftClass sc = new SoftClass(rdr,null,true);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
    
    // not really much use, this...
    
    assertThat(sc.getClassLoader(),notNull());
  }

  /*
   * Class under test for void NewSoftClass(ClassReader, boolean (false))
   */
  public void testNewSoftClassClassReaderLoaderCustombooleanfalse() {
    ClassLoader loader = new URLClassLoader(new URL[0]);
    SoftClass sc = new SoftClass(rdr,loader,false);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));

    assertThat(sc.getClassLoader(),isSame(loader));
  }

  /*
   * Class under test for void NewSoftClass(Class)
   */
  public void testNewSoftClassClass() {
    SoftClass sc = new SoftClass(SimpleDummy.class);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
  }

  /*
   * Class under test for void NewSoftClass(Class, Loader (null), boolean (false))
   */
  public void testNewSoftClassClassbooleantrue() {
    SoftClass sc = new SoftClass(rdr,null,false);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
    
    assertThat(sc.getClassLoader(),notNull());
  }

  public void testGetModifiers() {
    SoftClass sc = new SoftClass(SimpleDummy.class);    
    assertThat(sc.getModifiers() & Opcodes.ACC_PUBLIC,gt(0));
  }

  public void testGetName() {
    SoftClass sc = new SoftClass(SimpleDummy.class);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
  }

  public void testSetName() {
    SoftClass sc = new SoftClass(SimpleDummy.class); 
    sc.setName("jen.NewSimpleDummy");
    assertThat(sc.getName(),isEqual("jen.NewSimpleDummy"));
  }

  public void testGetSuperClassWhenThereIsntOne() throws Exception {
    SoftClass sc = new SoftClass(rdr);    
    assertThat(sc.getName(),isEqual("jen.SimpleDummy"));
    assertThat(sc.getSuperClass().getName(),isEqual("java.lang.Object"));
  }
  
  public void testGetSuperClassWhenThereIsOne() throws Exception {     
    SoftClass sc = new SoftClass(RuntimeException.class);    
    assertThat(sc.getName(),isEqual("java.lang.RuntimeException"));
    assertThat(sc.getSuperClass().getName(),isEqual("java.lang.Exception"));
  }

  /*
   * Class under test for void NewSoftClass.setSuperClass(Class)
   */
  public void testSetSuperClassClassWithoutPrior() throws Exception {
    SoftClass sc = new SoftClass(SimpleDummy.class); 
    assertThat(sc.getSuperClass().getName(),isEqual("java.lang.Object"));
    sc.setSuperClass(java.util.ArrayList.class);
    assertThat(sc.getSuperClass().getName(),isEqual("java.util.ArrayList"));
  }
  
  public void testSetSuperClassClassWithPrior() throws Exception {
    SoftClass sc = new SoftClass(RuntimeException.class); 
    assertThat(sc.getSuperClass().getName(),isEqual("java.lang.Exception"));
    sc.setSuperClass(java.util.ArrayList.class);
    assertThat(sc.getSuperClass().getName(),isEqual("java.util.ArrayList"));
  }

  public void testGetInterfaces() {
    SoftClass sc = new SoftClass(AbstractSoftMember.class); 
    assertThat(sc.getInterfaces(),exactSize(2));
  }

  public void testGetGenericSignature() { /* external test (we don't build for 5.0) */ }

  public void testGetNestedClasses() {
    SoftClass sc = new SoftClass(WithNested.class); 
    assertThat(sc.getNestedSoftClasses(),exactSize(3));    
  }

  /* MODIFIED 13/10/05 - ORDERING NO LONGER SUPPORTED */
  public void testGetFields() {
    SoftClass sc = new SoftClass(SimpleDummy.class); 
    assertThat(sc.getSoftFields(),exactSize(2));
    assertThat(sc.getSoftFields().get(0).getName(),or(isEqual("refField"),isEqual("primField")));
    assertThat(sc.getSoftFields().get(1).getName(),or(isEqual("refField"),isEqual("primField")));
  }

  public void testGetMethods() {
    SoftClass sc = new SoftClass(SimpleDummy.class); 
    assertThat(sc.getSoftMethods(),exactSize(9));
    
    /* Cannot test this any more, ordering not supported
     * 
     *    R.B. 13/Oct/05
        
    assertThat(sc.getSoftMethods().get(0).getName(),isEqual("<init>"));
    assertThat(sc.getSoftMethods().get(4).getName(),isEqual("voidMethod"));
    */    
  }

  public void testGetEnclosingClassWhenThereIsntOne() throws ClassNotFoundException {
    SoftClass sc = new SoftClass(SimpleDummy.class); 
    assertThat(sc.getEnclosingClassName(),isNull());
  }

  public void testGetEnclosingMethodWhenThereIsntOne() throws ClassNotFoundException {
    SoftClass sc = new SoftClass(SimpleDummy.class); 
    assertThat(sc.getEnclosingMethod(),isNull());

    sc = new SoftClass(WithNested.NestedClass.class); 
    assertThat(sc.getEnclosingMethod(),isNull());
  }
  
  /* NOT YET SUPPORTED - TEST WHEN _IS_ ONE */
  
  public void testGenerateInLoader() throws Exception {
    ClassLoader loader = new URLClassLoader(new URL[0]);
    SoftClass sc = new SoftClass(rdr,loader,false);  
    sc.setName("jen.SimpleDummyTwo");

    sc.defineClass();
    
    assertThat(loader.loadClass("jen.SimpleDummyTwo"),notNull());
    
    // different context loaders handle this differently, but we want either
    // a null return or CNFE to indicate it _didn't_ end up in that loader.
    try {
      assertThat(Thread.currentThread().getContextClassLoader().loadClass("jen.SimpleDummyTwo"),isNull());
    } catch (ClassNotFoundException e) {
      /* PASS ALSO */
    }    
  }  
}
