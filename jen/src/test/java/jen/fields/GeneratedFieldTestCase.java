/* GeneratedFieldTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:44 $
 * Originated: 10-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.fields;

import org.objectweb.asm.Opcodes;

import org.roscopeco.juno.pre50.JunoTestCase;

import jen.SoftClass;
import jen.methods.SimpleNullConstructor;

public class GeneratedFieldTestCase extends JunoTestCase
{
  public void testGenerateAFieldOnANewClass() throws Exception {
    SoftClass sc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.fields.NewPropClass",null);
    sc.putSoftMethod(new SimpleNullConstructor(sc,Opcodes.ACC_PUBLIC));
    sc.putSoftField(new GeneratedSoftField(sc,Opcodes.ACC_PUBLIC,"newField",String.class));
    Class<?> newClazz = sc.defineClass();
    
    assertThat(newClazz.getFields(),exactSize(1));
    assertThat(newClazz.getFields()[0].getName(),isEqual("newField"));
    assertThat(newClazz.getFields()[0].getType(),isEqual(String.class));
    
    Object o = newClazz.newInstance(); 
    
    assertThat(newClazz.getField("newField").get(o),isNull());
    newClazz.getField("newField").set(o,"newValue");
    assertThat(newClazz.getField("newField").get(o),isEqual("newValue"));
  }

  public void testGenerateAStaticFieldOnANewClass() throws Exception {
    SoftClass sc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.fields.NewPropClass01",null);
    sc.putSoftMethod(new SimpleNullConstructor(sc,Opcodes.ACC_PUBLIC));
    sc.putSoftField(new GeneratedSoftField(sc,Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,"newField",String.class));
    Class newClazz = sc.defineClass();
    
    assertThat(newClazz.getFields(),exactSize(1));
    assertThat(newClazz.getFields()[0].getName(),isEqual("newField"));
    assertThat(newClazz.getFields()[0].getType(),isEqual(String.class));
    
    assertThat(newClazz.getField("newField").get(null),isNull());
    newClazz.getField("newField").set(null,"newValue");
    assertThat(newClazz.getField("newField").get(null),isEqual("newValue"));
  }

  public void testGenerateAStaticFinalWithInitialValueOnANewClass() throws Exception {
    SoftClass sc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.fields.NewPropClass02",null);
    sc.putSoftMethod(new SimpleNullConstructor(sc,Opcodes.ACC_PUBLIC));
    sc.putSoftField(new GeneratedSoftField(sc,Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,"newField",null,String.class,"initialValue"));    
    Class newClazz = sc.defineClass();
    
    assertThat(newClazz.getFields(),exactSize(1));
    assertThat(newClazz.getFields()[0].getName(),isEqual("newField"));
    assertThat(newClazz.getFields()[0].getType(),isEqual(String.class));
    
    assertThat(newClazz.getField("newField").get(null),isEqual("initialValue"));
    
    try {
      newClazz.getField("newField").set(null,"newValue");
      fail("Should have gotten IllegalAccessException");
    } catch (IllegalAccessException e) {
      /* PASS */
    }
    
    assertThat(newClazz.getField("newField").get(null),isEqual("initialValue"));
  }

}
