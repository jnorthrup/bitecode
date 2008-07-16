/* JavaMethodCopierTestCase.java - Awaiting description
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
 * File version: $Revision: 1.5 $ $Date: 2005/10/23 02:15:29 $
 * Originated: Sep 25, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.io.IOException;
import java.util.List;

import jen.SoftClass;
import jen.SoftUtils;
import jen.fields.GeneratedSoftField;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.roscopeco.juno.pre50.JunoTestCase;

@SuppressWarnings("unchecked")
public class JavaMethodCopierTestCase extends JunoTestCase
{
  public static interface TCFour {
    public String getValue();    
  }
  
  public static interface TCSix {
    public String getValue() throws Exception;    
  }  
  
  public CopiedJavaMethod newCopiedMethod(SoftClass newSc) {
    return new CopiedJavaMethod(newSc,"java.lang.String getValue()") {
      public String getValue() {
        String str1 = "TEST";
        String str2 = " TOKEN";
        return str1+str2;
      }
    };
  }
  
  public void testItChokesOnNonExistedSubclassMethods() throws Exception {
    SoftClass sc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.NotAClassClassyClassClass");
    
    try {
      new CopiedJavaMethod(sc,"int wotNoMethod(java.util.List, long, jen.SoftClass)") {
        public <T extends List> int theTestMethod(T b, long l, SoftClass sc) throws IllegalArgumentException, IllegalStateException {
          return 42;
        }
      };
      fail("Expected IAE");
    } catch (IllegalArgumentException e) {
      // PASS
    }
  }
  
  public void testBasicMetricsWorkAsExpectedWithIt() throws Exception {
    SoftClass sc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.RealClassBaby");
    CopiedJavaMethod m = new CopiedJavaMethod(sc,"int theTestMethod(java.util.List, long, jen.SoftClass)") {
      public <T extends List> int theTestMethod(T b, long l, SoftClass sc) throws IllegalArgumentException, IllegalStateException {
        return 42;
      }
    };
    
    assertThat(m,notNull());
    assertThat(m.getModifiers(),isEqual(Opcodes.ACC_PUBLIC));
    assertThat(m.getName(),isEqual("theTestMethod"));
    assertThat(m.getReturnType(),isEqual(Type.INT_TYPE));
    assertThat(m.getArgumentTypes(),exactSize(3));
    assertThat(m.getArgumentTypes().get(0),isEqual(Type.getType(List.class)));
    assertThat(m.getArgumentTypes().get(1),isEqual(Type.LONG_TYPE));
    assertThat(m.getArgumentTypes().get(2),isEqual(Type.getType(SoftClass.class)));
    assertThat(m.getThrowsTypes(),exactSize(2));
    assertThat(m.getThrowsTypes().get(0),isEqual(Type.getType(IllegalArgumentException.class)));
    assertThat(m.getThrowsTypes().get(1),isEqual(Type.getType(IllegalStateException.class)));   
    assertThat(m.getSignature(),isEqual("<T::Ljava/util/List;>(TT;JLjen/SoftClass;)I"));
    assertThat(m.getDescriptor(),isEqual("(Ljava/util/List;JLjen/SoftClass;)I"));
    
    // goes without saying but there you go...
    assertThat(m.getOriginalSoftMethod(),notNull());
    assertThat(m.getOriginalSoftMethod().getName(),isEqual(m.getName()));    
  }
  
  public void testCanGenerateASpecifiedBasicMethodWithIt() throws Exception {
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass20");
    
    newSc.addInterface(TCFour.class.getName());
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftMethod(newCopiedMethod(newSc)); 
    
    Class<? extends TCFour> clazz = newSc.defineClass();
    
    TCFour r = clazz.newInstance();
    assertThat(r.getValue(),isEqual("TEST TOKEN"));    
  }  
  
  /* without explicitly specifying the method(s) */
  public void testCanGenerateAnImplicitBasicMethodWithIt() throws Exception {
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass30");
    
    newSc.addInterface(TCFour.class.getName());
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftMethod(newCopiedMethod(newSc)); 
    
    Class<? extends TCFour> clazz = newSc.defineClass();
    
    TCFour r = clazz.newInstance();
    assertThat(r.getValue(),isEqual("TEST TOKEN"));    
  }  
  
  public void testCanGenerateAMethodAccessingAFieldWithIt() throws Exception {
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass40");
    
    newSc.addInterface(TCFour.class.getName());
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftField(new GeneratedSoftField(newSc,"str",String.class));
    newSc.putSoftMethod(new BeanPropertySetter(newSc,"str",String.class));

    newSc.putSoftMethod(new CopiedJavaMethod(newSc,"java.lang.String getValue()") {
      // need to mimic the field
      private String str;
      
      public String getValue() {
        return str;
      }
    }); 
    
    Class<? extends TCFour> clazz = newSc.defineClass();
    
    TCFour r = clazz.newInstance();
    SoftUtils.reflectInvoke(r,"setStr",new Object[] { "TEST TOKEN" });
    assertThat(r.getValue(),isEqual("TEST TOKEN"));    
  }  
    
  public void testCanGenerateAThrowsMethodWithIt() throws Exception {
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass60");
    
    newSc.addInterface(TCSix.class.getName());
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftMethod(new CopiedJavaMethod(newSc, "String getValue()") {
      public String getValue() throws Exception {
        throw(new IOException("EXPECTED EXCEPTION"));
      }
    }); 
    
    Class<? extends TCSix> clazz = newSc.defineClass();
    
    TCSix r = clazz.newInstance();
    
    try {
      r.getValue();
    } catch (IOException e) {
      /* pass */
    }
  }    
  
  static class ArbitraryMethodSource {
    public String getValue() {
      String str1 = "TEST";
      String str2 = " TOKEN";
      return str1+str2;
    }    
  }
  
  public void testCanCopyArbitraryMethodsWithIt() throws Exception {
    SoftClass newSc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.SomeSoftClass70");
    
    newSc.addInterface(TCFour.class.getName());
    newSc.putSoftMethod(new SimpleNullConstructor(newSc));    
    newSc.putSoftMethod(new CopiedJavaMethod(newSc,ArbitraryMethodSource.class.getMethod("getValue")));
    
    Class<? extends TCFour> clazz = newSc.defineClass();
    
    TCFour r = clazz.newInstance();
    assertThat(r.getValue(),isEqual("TEST TOKEN"));    
  }    
}
