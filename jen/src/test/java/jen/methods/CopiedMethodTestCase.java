/* CopiedMethodTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:45 $
 * Originated: 13-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.lang.reflect.Method;

import jen.SoftClass;
import jen.SoftMethod;
import jen.fields.GeneratedSoftField;

import org.objectweb.asm.Opcodes;

import org.roscopeco.juno.pre50.JunoTestCase;

public class CopiedMethodTestCase extends JunoTestCase
{
  public static interface TestCompThree {
    public String getStringRegular();
    public void setStr(String str);
  }
  
  public static interface TestCompFour {
    public String getStringRenamed(String s);
  }
  
  /* RECURSIVE, ALLOWS TESTING INVOCATION SWITCHING */
  public String getStringRegular(String s) {
    if (s == null) {
      return getStringRegular(getClass().getName()+" : ");
    } else {
      return s + getClass().getName();
    }
  }
  
  private String getStringToo(String s) {
    return "Was private";
  }
  
  // This test gens a class that 'borrows' the 'getStringRegular()' method 
  // from DemoClass. for this to work it also defines the backing field, and a setter 
  // to inject a test value
  
  @SuppressWarnings("unchecked")
  public void testCanCopyAMethod() throws Exception {
    SoftClass demo = new SoftClass(DemoClass.class);    
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass");
    
    newSc.addInterface(TestCompThree.class.getName());
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftField(new GeneratedSoftField(newSc,"str",String.class));
    newSc.putSoftMethod(new BeanPropertySetter(newSc,"str",String.class));

    SoftMethod run = demo.getSoftMethod("java.lang.String getStringRegular()");
    newSc.putSoftMethod(new CopiedSoftMethod(newSc,run));

    Class<? extends TestCompThree> clazz = newSc.defineClass();
    
    TestCompThree r = clazz.newInstance();
    r.setStr("TEST STRING");
    assertThat(r.getStringRegular(),isEqual("TEST STRING"));    
  }  
  
  // This test ensures that the ref switching adapter properly handles method renaming,
  // by renaming a method to suit an interface. Furthermore, the method is recursive,
  // and outputs it's declaring class name, which is tested with an assert. The point
  // of that is to make sure that not only did the method get renamed, but also that
  // any self-invocations were also switched.
  
  @SuppressWarnings("unchecked")
  public void testCanCopyAndRenameARecursiveMethod() throws Exception {    
    SoftClass test = new SoftClass(getClass());           
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass1000");
    
    newSc.addInterface(TestCompFour.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    

    SoftMethod getStrMethod = test.getSoftMethod("String getStringRegular(String)");
    newSc.putSoftMethod(new CopiedSoftMethod(newSc,getStrMethod,"getStringRenamed"));

    Class<? extends TestCompFour> clazz = newSc.defineClass();
    
    TestCompFour r = clazz.newInstance();
    assertThat(r.getStringRenamed(null),isEqual("jen.tests.SomeSoftClass1000 : jen.tests.SomeSoftClass1000"));    
  }

  
  // Test that modifiers can be changed during copy
  
  @SuppressWarnings("unchecked")
  public void testCanCopyRenameAndChangeAccess() throws Exception {
    SoftClass test = new SoftClass(getClass());           
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass2000");
    
    newSc.addInterface(TestCompFour.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    

    SoftMethod getStrMethod = test.getSoftMethod("String getStringToo(String)");
    newSc.putSoftMethod(new CopiedSoftMethod(newSc,getStrMethod,SoftClass.ACC_PUBLIC,"getStringRenamed"));

    Class<? extends TestCompFour> clazz = newSc.defineClass();
    
    for (Method m : clazz.getDeclaredMethods()) {
      System.out.println(m);
    }
    
    TestCompFour r = clazz.newInstance();
    assertThat(r.getStringRenamed(null),isEqual("Was private"));    
  }

}
