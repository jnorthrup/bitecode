/* BeanPropertySetter.java - Bean property setter SoftMember
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:34 $
 * Originated: 09-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import jen.NullArgumentException;
import jen.SoftClass;

/** 
 * Represents a JavaBean(tm)-compatible <em>property setter</em> method on
 * a generated {@link SoftClass}. For a <code>propName</code> 
 * <em>someProp</em>, <code>fieldName</code> <em>someField</em>, and 
 * <code>type</code> <i>java.lang.String</i>, this class would generate on
 * <code>clazz</code> <em>SomeClass</em> bytecode equivalent to that compiled 
 * from the following Java code:
 * <p/>
 * <pre>    public void setSomeProp(java.lang.String someField) {
 *       this.someField = someField;
 *     }</pre>
 * <p/>
 * The backing field must exist <em>on the generated class</em> - this class
 * performs no validation of this fact, and it does not necessarily have to 
 * be present on the SoftClass when this member is added. It must be of the 
 * same type as the <code>type</code> specified here, but may have any 
 * access modifier(s). Both reference and primitive fields are supported.
 * <p/>
 * Once a new method is created it can be added to the softclass using the
 * {@link SoftClass#putSoftMethod(SoftMethod)} method.
 * <p/>
 * <strong>Note</strong>: As with the {@link BeanPropertySetter}, this member
 * does not support field inheritance, regardless of access modifier.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:34 $ 
 */
public class BeanPropertySetter extends GeneratedSoftMethod
{
  private String fieldName;
  
  private static final String methodNameHelper(String propName, Type type) {
    if ((propName == null) || (propName == null)) {
      throw(new NullArgumentException(BeanPropertySetter.class,"propName and type"));
    }
    
    return new StringBuilder().append("set").
                               append(Character.toUpperCase(propName.charAt(0))).
                               append(propName.substring(1)).
                               toString();
  }
  
  /**
   * Create a new bean property getter method for the specified JavaBean(tm)
   * compatible property on the specified SoftClass. This constructor assumes
   * the backing field shares it's name with the bean property.
   * <strong>This member defaults to <code>ACC_PUBLIC</code>, rather than
   * <code>package private</code> access.</strong>. This decision was taken
   * because bean accessors are more often public than not.
   * 
   * @param clazz The {@link SoftClass} upon which this getter will be generated.
   * @param propName The name of the bean-property.
   * @param type The {@link Class} representing the type of this property.
   */
  public BeanPropertySetter(SoftClass clazz, String propName, Class type) {
    this(clazz,propName,propName,type);
  }
  
  /**
   * Create a new bean property getter method for the specified JavaBean(tm)
   * compatible property on the specified SoftClass. This constructor allows
   * the name of the backing field to be specified independently.
   * <strong>This member defaults to <code>ACC_PUBLIC</code>, rather than
   * <code>package private</code> access.</strong>. This decision was taken
   * because bean accessors are more often public than not.
   * 
   * @param clazz The {@link SoftClass} upon which this getter will be generated.
   * @param propName The name of the bean-property.
   * @param fieldName The name backing field.
   * @param type The {@link Class} representing the type of this property.
   */
  public BeanPropertySetter(SoftClass clazz, String propName, String fieldName, Class type) {
    this(clazz,ACC_PUBLIC,propName,fieldName,type);
  }
    
  /**
   * Create a new bean property getter method for the specified JavaBean(tm)
   * compatible property on the specified SoftClass. This constructor allows
   * the access modifiers (for the method) and name of the backing field to be 
   * specified independently.
   * 
   * @param clazz The {@link SoftClass} upon which this getter will be generated.
   * @param modifiers The access modifiers for the getter method.
   *                  (See {@link org.objectweb.asm.Opcodes#ACC_PUBLIC} etc). 
   * @param propName The name of the bean-property.
   * @param fieldName The name backing field.
   * @param type The {@link Class} representing the type of this property.
   */
  public BeanPropertySetter(SoftClass clazz, int modifiers, String propName, String fieldName, Class type) {
    this(clazz,modifiers,propName,fieldName,Type.getType(type));
  }
  
  /**
   * Create a new bean property getter method for the specified JavaBean(tm)
   * compatible property on the specified SoftClass. This constructor allows
   * the access modifiers (for the method) and name of the backing field to be 
   * specified independently, and accepts an ASM {@link org.objectweb.asm.Type}
   * rather than {@link Class} for the property type. This allows properties 
   * with an unavailable (e.g. not-yet-generated) class as their type to be 
   * manipulated.
   * 
   * @param clazz The {@link SoftClass} upon which this getter will be generated.
   * @param modifiers The access modifiers for the getter method.
   *                  (See {@link org.objectweb.asm.Opcodes#ACC_PUBLIC} etc). 
   * @param propName The name of the bean-property.
   * @param fieldName The name backing field.
   * @param type The {@link org.objectweb.asm.Type} representing the type of this 
   *             property.
   */
  public BeanPropertySetter(SoftClass clazz, int modifiers, String propName, String fieldName, Type type) {
    super(clazz,modifiers,methodNameHelper(propName,type),null,new Type[] { type },Type.VOID_TYPE,null);
    this.fieldName = fieldName;
  }
  
  /**
   * Obtains the name of the backing field that this getter returns.
   * 
   * @return The field name.
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Sets the name of the backing field that this getter returns.
   * 
   * @param fieldName The new backing-field name.
   */
  public void setFieldName(String fieldName) {
    checkModify();
    this.fieldName = fieldName;
  }
  
  /**
   * Generates this method code.
   */
  protected void generateCode(GeneratorAdapter code) {
    code.loadThis();
    code.loadArg(0);
    code.putField(getSoftClass().getType(),getFieldName(),getArgumentTypes().get(0));
    code.returnValue();
    code.endMethod();
  }
}
