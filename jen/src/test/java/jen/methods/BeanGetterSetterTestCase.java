/* BeanGetterSetterTestCase.java - Awaiting description
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
 * File version: $Revision: 1.4 $ $Date: 2005/10/20 22:34:45 $
 * Originated: 09-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import org.objectweb.asm.ClassWriter;

import org.roscopeco.juno.pre50.JunoTestCase;

import jen.SoftClass;
import jen.SoftUtils;
import jen.fields.GeneratedSoftField;

/* TESTS ALL THREE OF NULL CTOR, GETTER AND SETTER */
public class BeanGetterSetterTestCase extends JunoTestCase
{
  /* NotQuiteABean needs a few things to be a bean:
   * 
   *    + Null ctor
   *    + non-final backing field (be careful!)
   *    + getter / setter for it
   *    
   * To be true to the beanspec it also needs to implement serializable, but
   * you must be careful with generated serializables (will the class be 
   * available for deserialization. If you're generating, you need to ensure
   * you keep binary compatibility and the same serialVersionUID between
   * the two...) so we'll ignore it here and just take care of the above.
   */
  public void testCanDeriveABean() throws Exception {
    SoftClass sc = new SoftClass(NotQuiteABean.class);
    sc.setName("jen.methods.ProperBean");
    
    sc.putSoftMethod(new SimpleNullConstructor(sc,"Initial value"));
    
    // this will replace the existing final field
    sc.putSoftField(new GeneratedSoftField(sc,"beanProp",String.class));
    
    sc.putSoftMethod(new BeanPropertyGetter(sc,"beanProp",String.class));
    sc.putSoftMethod(new BeanPropertySetter(sc,"beanProp",String.class));
    
    // Visit it manually for a change (no need for this really...)
    ClassWriter writer = new ClassWriter(-1);
    sc.accept(writer);
    
    Class<?> newBeanClass = SoftUtils.defineClass(Thread.currentThread().getContextClassLoader(),writer.toByteArray());
   
    Object newBean = newBeanClass.newInstance();
    
    Object o = SoftUtils.reflectInvoke(newBean,"getBeanProp");
    assertThat(o,isEqual("Initial value"));
    
    SoftUtils.reflectInvoke(newBean,"setBeanProp","New value");

    o = SoftUtils.reflectInvoke(newBean,"getBeanProp");
    assertThat(o,isEqual("New value"));
  }

}
