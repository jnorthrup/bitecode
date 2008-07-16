/* GeneratedSoftField.java - Basic generated field type.
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:42 $
 * Originated: 10-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.fields;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

import jen.AbstractSoftField;
import jen.SoftClass;

/** 
 * Basic generated Java field implementation. This allows any type of field
 * to be easily generated simply by constructing the object appropriately
 * and adding it to a {@link SoftClass} instance (via the 
 * {@link SoftClass#putSoftField(SoftField)}) method. Both static and instance
 * fields are supported, with special-case treatment of <code>static final</code>
 * fields with respect to support for their <code>initial-value</code> attribute
 * that allows certain value types (primitives, string / class literals) 
 * to be directly defined in the constant pool.
 * <p/>
 * <strong>Note</strong> that, as indicated, this support does not extend to
 * arbitrary <code>Object</code>s. Any fields that require non-simple initial
 * values must be handled manually (usually in either <code>&lt;clinit&gt;</code>
 * or <code>&lt;init&gt;</code>).
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:42 $ 
 */
public class GeneratedSoftField extends AbstractSoftField
{
  private Object initialValue;
  
  /**
   * Create a new <code>GeneratedSoftField</code> for the specified {@link SoftClass}
   * with the specified parameters. This version specifies only the name and
   * type. 
   * <p/>
   * <strong>Note</strong> that this class defaults to using
   * <i>package private</i> access when called with this constructor, unlike 
   * the null constructor and bean getter / setter members. This decision was 
   * taken to provide for the common case of a bean-property backing field.
   * 
   * @param softClass The {@link SoftClass} this field will be used with.
   * @param name The name for this field. This must follow the Java identifier
   *        naming rules.
   * @param type The {@link java.lang.Class} representing this field's
   *        type.
   */
  public GeneratedSoftField(SoftClass softClass, String name, Class type) {
    this(softClass, 0, name, type);
  }
  
  /**
   * Create a new <code>GeneratedSoftField</code> for the specified {@link SoftClass}
   * with the specified parameters. This version specifies just the field 
   * modifiers, name and type.
   * 
   * @param softClass The {@link SoftClass} this field will be used with.
   * @param modifiers The access modifiers for this field. 
   * @param name The name for this field. This must follow the Java identifier
   *        naming rules.
   * @param type The {@link java.lang.Class} representing this field's
   *        type.
   */
  public GeneratedSoftField(SoftClass softClass, int modifiers, String name, Class type) {
    this(softClass, modifiers, name, null, Type.getType(type));
  }
  
  /**
   * Create a new <code>GeneratedSoftField</code> for the specified {@link SoftClass}
   * with the specified parameters. This version allows the field to be
   * fully customized, and accepts a Java {@link java.lang.Class} for the type
   * parameter.
   * 
   * @param softClass The {@link SoftClass} this field will be used with.
   * @param modifiers The access modifiers for this field. 
   * @param name The name for this field. This must follow the Java identifier
   *        naming rules.
   * @param signature The generic signature for this field, if any.
   * @param type The {@link org.objectweb.asm.Type} representing this field's
   *        type.
   */
  public GeneratedSoftField(SoftClass softClass, int modifiers, String name,
      String signature, Class type) {
    this(softClass, modifiers, name, signature, Type.getType(type));
  }

  /**
   * Create a new <code>GeneratedSoftField</code> for the specified {@link SoftClass}
   * with the specified parameters. This version allows the field to be
   * fully customized, and accepts an ASM {@link org.objectweb.asm.Type} type
   * parameter.
   * 
   * @param softClass The {@link SoftClass} this field will be used with.
   * @param modifiers The access modifiers for this field. 
   * @param name The name for this field. This must follow the Java identifier
   *        naming rules.
   * @param signature The generic signature for this field, if any.
   * @param type The {@link org.objectweb.asm.Type} representing this field's
   *        type.
   */
  public GeneratedSoftField(SoftClass softClass, int modifiers, String name,
      String signature, Type type) {
    this(softClass, modifiers, name, signature, type, null);
  }
    
  /**
   * Create a new <code>GeneratedSoftField</code> for the specified {@link SoftClass}
   * with the specified parameters. This version generates a 
   * <code>static final</code> constant field (adding those modifiers if
   * necessary) that will hold the specified initial value.
   * <p/>
   * See {@link #GeneratedSoftField(SoftClass, int, String, String, Type, Object)} for
   * semantic details of this method.
   * 
   * @param softClass The {@link SoftClass} this field will be used with.
   * @param modifiers The access modifiers for this field. 
   * @param name The name for this field. This must follow the Java identifier
   *        naming rules.
   * @param signature The generic signature for this field, if any.
   * @param type The {@link org.objectweb.asm.Type} representing this field's
   *        type.
   * @param initialValue
   */
  public GeneratedSoftField(SoftClass softClass, int modifiers, String name,
      String signature, Class type, Object initialValue) {
    this(softClass, modifiers, name, signature, Type.getType(type),initialValue);
  }

  /**
   * Create a new <code>GeneratedSoftField</code> for the specified {@link SoftClass}
   * with the specified parameters. This version generates a 
   * <code>static final</code> constant field (adding those modifiers if
   * necessary) that will hold the specified initial value.
   * <p/>
   * Note that the actual types that can represent an initial value are limited
   * to primitives (via their wrappers), {@link java.lang.String}s, and
   * {@link java.lang.Class} where the class version is >= 49.0.
   * <p/>
   * Passing <code>null</code> for the initial value results in the field
   * being generated as any other field.
   * 
   * @param softClass The {@link SoftClass} this field will be used with.
   * @param modifiers The access modifiers for this field. 
   * @param name The name for this field. This must follow the Java identifier
   *        naming rules.
   * @param signature The generic signature for this field, if any.
   * @param type The {@link org.objectweb.asm.Type} representing this field's
   *        type.
   * @param initialValue
   */
  // MAIN WORKER - The null behaviour is important!
  public GeneratedSoftField(SoftClass softClass, int modifiers, String name,
      String signature, Type type, Object initialValue) {
    super(softClass, (initialValue != null) ? modifiers | ACC_STATIC | ACC_FINAL : modifiers, name, signature, type);
    this.initialValue = initialValue;
  }

  /** 
   * Obtain the <em>initial value</em> for this field. This is only used if the 
   * field is <code>static final</code> at the time of generation. 
   * 
   * @return The initial value for this <code>static final</code> field.
   */
  public Object getInitialValue() {
    return initialValue;
  }

  /**
   * Set the <em>initial value</em> for this field. Unlike the associated 
   * constructor, calling this method <strong>will not</strong> set the
   * <code>static final</code> access modifiers.
   * 
   * @param initialValue The new initial (wrapped) primitive or literal 
   *        value for this field. 
   */
  public void setInitialValue(Object initialValue) {
    checkModify();
    this.initialValue = initialValue;
  }
  
  /* (non-Javadoc)
   * @see jen.AbstractSoftMember#accept(org.objectweb.asm.ClassVisitor)
   */
  public void accept(ClassVisitor vis) {
    FieldVisitor fv = vis.visitField(getModifiers(),getName(),getDescriptor(),getSignature(),getInitialValue());
    generateField(fv);
    fv.visitEnd();    
  }
  
  /**
   * Allows subclasses to generate any attributes or annotations on the field
   * during generation. This implementation does nothing.
   * 
   * @param fv The {@link FieldVisitor} to which this fields attributes should
   *        be generated.
   */
  protected void generateField(FieldVisitor fv) { }
}
