/* WrappedSoftMethodTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:46 $
 * Originated: Oct 17, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.util.ArrayList;
import java.util.List;

import jen.NullArgumentException;
import jen.SoftClass;
import jen.SoftMethod;
import jen.fields.GeneratedSoftField;
import jen.methods.CopiedMethodTestCase.TestCompThree;
import jen.methods.CopiedMethodTestCase.TestCompFour;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import junit.framework.TestCase;

import static java.util.Collections.singletonList;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

@SuppressWarnings("unchecked")
public class WrappedSoftMethodTestCase extends TestCase
{
  public static interface TestCompFive {
    public int getIntToo();
    public void thrower() throws Exception;
  }
  
  /* RECURSIVE, ALLOWS TESTING INVOCATION SWITCHING */
  public String getStringRenamed(String s) {
    if (s == null) {
      return getStringRenamed(getClass().getName()+" : ");
    } else {
      return s + getClass().getName();
    }
  }
  
  public int getIntToo() {
    return 2;
  }
  
  public int getIntEight() {
    return 8;
  }
  
  public void thrower() throws Exception {
    throw(new Exception("Peekaboo!"));
  }
  
  public void testCanGenerateAWrappedMethodWithNoWrapping() throws Exception {
    SoftClass demo = new SoftClass(DemoClass.class);    
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.WrapperTestClass1000");
    
    newSc.addInterface(TestCompThree.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftField(new GeneratedSoftField(newSc,"str",String.class));
    newSc.putSoftMethod(new BeanPropertySetter(newSc,"str",String.class));

    SoftMethod run = demo.getSoftMethod("String getStringRegular()");
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,run, (List)null, (List)null));

    Class<? extends TestCompThree> clazz = newSc.defineClass();
    
    TestCompThree r = clazz.newInstance();
    r.setStr("TEST STRING");
    assertThat(r.getStringRegular(),isEqual("TEST STRING"));    
  }  
  
  public void testCanGenerateAWrappedMethodWithBeforeWrapping() throws Exception {
    SoftClass demo = new SoftClass(getClass());    
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.WrapperTestClass2000");
    
    newSc.addInterface(TestCompFour.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftField(new GeneratedSoftField(newSc,"str",String.class));
    newSc.putSoftMethod(new BeanPropertySetter(newSc,"str",String.class));

    SoftMethod run = demo.getSoftMethod("String getStringRenamed(String)");
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,run, 
        singletonList((MethodFragment)new MethodFragment() {
          public void accept(GeneratorAdapter gen) {
            gen.pop();
            gen.push("sneaky string : ");        
          }
        }), null));

    Class<? extends TestCompFour> clazz = newSc.defineClass();
    
    TestCompFour r = clazz.newInstance();
    assertThat(r.getStringRenamed(null),isEqual("sneaky string : jen.tests.WrapperTestClass2000"));    
  }  
  
  public void testCanGenerateAWrappedMethodWithBothWrapping() throws Exception {
    SoftClass demo = new SoftClass(getClass());    
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.WrapperTestClass3000");
    
    newSc.addInterface(TestCompFour.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftField(new GeneratedSoftField(newSc,"str",String.class));
    newSc.putSoftMethod(new BeanPropertySetter(newSc,"str",String.class));

    SoftMethod run = demo.getSoftMethod("String getStringRenamed(String)");
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,run, 
        singletonList((MethodFragment)new MethodFragment() {
          public void accept(GeneratorAdapter gen) {
            gen.pop();
            gen.push("sneaky string : ");        
          }
        }),        
        singletonList((MethodFragment)new MethodFragment() {
          public void accept(GeneratorAdapter gen) {
            gen.push('e');
            gen.push('a');
            gen.invokeVirtual(Type.getType(String.class),Method.getMethod("String replace(char, char)"));            
          }
        })));

    Class<? extends TestCompFour> clazz = newSc.defineClass();
    
    TestCompFour r = clazz.newInstance();
    assertThat(r.getStringRenamed(null),isEqual("snaaky string : jan.tasts.WrapparTastClass3000"));    
  }  
  
  public void testCanGeneratePrimitiveAndThrowsWrappedMethods() throws Exception {
    SoftClass demo = new SoftClass(getClass());    
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.WrapperTestClass4000");
    
    newSc.addInterface(TestCompFive.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    

    SoftMethod run = demo.getSoftMethod("int getIntToo()");
    SoftMethod thrower = demo.getSoftMethod("void thrower()");
    
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,run,null,        
        singletonList((MethodFragment)new MethodFragment() {
          public void accept(GeneratorAdapter gen) {
            gen.push(10);
            gen.math(GeneratorAdapter.ADD,Type.INT_TYPE);
          }
        })));    
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,thrower, (List)null, (List)null));

    Class<? extends TestCompFive> clazz = newSc.defineClass();
    
    TestCompFive r = clazz.newInstance();
    
    // returns 2, we sneak ten on the end
    assertThat(r.getIntToo(),isEqual(12));
    
    try {
      r.thrower();
    } catch (Exception e) {
      assertThat(e.getMessage(),isEqual("Peekaboo!"));
    }    
  }
  
  public void testCanGenerateWrapperWithDifferentName() throws Exception {
    SoftClass demo = new SoftClass(getClass());    
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.WrapperTestClass5000");
    
    newSc.addInterface(TestCompFive.class);
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    

    SoftMethod run = demo.getSoftMethod("int getIntEight()");
    SoftMethod thrower = demo.getSoftMethod("void thrower()");
    
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,run,"getIntToo",null,        
        new MethodFragment[] { new MethodFragment() {
          public void accept(GeneratorAdapter gen) {
            gen.push(10);
            gen.math(GeneratorAdapter.ADD,Type.INT_TYPE);
          }
        }}));    
    newSc.putSoftMethod(new WrappedSoftMethod(newSc,thrower, new MethodFragment[0], new MethodFragment[0]));

    Class<? extends TestCompFive> clazz = newSc.defineClass();
    
    TestCompFive r = clazz.newInstance();
    
    // returns 2, we sneak ten on the end
    assertThat(r.getIntToo(),isEqual(18));    
  }
  
  // TODO test with different modifiers
  
  public static abstract class AbsClazz {
    public abstract String anAbstract();
  }
  
  public void testCantGenerateAWrappedAbstractMethod() throws Exception {
    SoftClass abs = new SoftClass(AbsClazz.class);    
    SoftMethod absMethod = abs.getSoftMethod("String anAbstract()");
    try {
      new WrappedSoftMethod(abs, absMethod, (List)null, (List)null);
    } catch (IllegalArgumentException e) {
      /* pass */
    }
  }  
  
  public void testNullsThrowNAEs() throws Exception {
    SoftClass abs = new SoftClass(AbsClazz.class);    
    SoftMethod absMethod = abs.getSoftMethod("String abAbstract()");
    
    try {
      new WrappedSoftMethod(null, absMethod, (List)null, (List)null);
    } catch (NullArgumentException e) {
      /* pass */
    }
    
    try {
      new WrappedSoftMethod(abs, null, (List)null, (List)null);
    } catch (NullArgumentException e) {
      /* pass */
    }
    
    try {
      new WrappedSoftMethod(null, absMethod, new ArrayList(), new ArrayList());
    } catch (NullArgumentException e) {
      /* pass */
    }
    
    try {
      new WrappedSoftMethod(null, null, (List)null, (List)null);
    } catch (NullArgumentException e) {
      /* pass */
    }    
  }  
  
}
