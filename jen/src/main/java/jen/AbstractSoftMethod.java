/* AbstractSoftMethod.java - SoftMethod base class
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
 * File version: $Revision: 1.4 $ $Date: 2005/10/19 14:43:37 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

/** 
 * Base-class for {@link SoftMethod} implementations.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.4 $ $Date: 2005/10/19 14:43:37 $ 
 */
public abstract class AbstractSoftMethod extends AbstractSoftMember implements SoftMethod
{
  private final List<Type> argumentTypes = new ArrayList<Type>();
  private final List<Type> throwsTypes = new ArrayList<Type>();
  private Type returnType = Type.VOID_TYPE;
  
  /**
   * Create a new method on the specified {@link SoftClass}.
   * 
   * @param softClass The SoftClass to which this method will belong.
   */
  protected AbstractSoftMethod(SoftClass softClass) {
    super(softClass);
  }
  
  /**
   * Create a new field on the specified {@link SoftClass} with the specified
   * parameters.
   * 
   * @param softClass The SoftClass to which this method will belong.
   * @param modifiers The access modifiers for this method.
   * @param name The name of the method.
   * @param signature The J5 generic signature for this method.
   * @param argumentTypes Argument types (ASM Type).
   * @param returnType Return type.
   * @param throwsTypes Exception types.
   */
  protected AbstractSoftMethod(SoftClass softClass, 
                               int modifiers, 
                               String name, 
                               String signature,
                               List<Type> argumentTypes, 
                               Type returnType, 
                               List<Type> throwsTypes) {
    super(softClass, modifiers, name, signature);
    if (argumentTypes != null) this.argumentTypes.addAll(argumentTypes);
    if (throwsTypes != null) this.throwsTypes.addAll(throwsTypes);
    this.returnType = returnType;
  }
  
  /**
   * Create a new field on the specified {@link SoftClass} with the specified
   * parameters. This version implicitly converts the supplied classes to
   * ASM {@link org.objectweb.asm.Type}s.
   * 
   * @param softClass The SoftClass to which this method will belong.
   * @param modifiers The access modifiers for this method.
   * @param name The name of the method.
   * @param signature The J5 generic signature for this method.
   * @param argumentTypes Argument types (ASM Type).
   * @param returnType Return type.
   * @param throwsTypes Exception types.
   */
  protected AbstractSoftMethod(SoftClass softClass, 
                               int modifiers, 
                               String name,
                               String signature,
                               List<Class<?>> argumentTypes,
                               Class returnType,
                               List<Class<?>> throwsTypes) {
    
    this(softClass, modifiers, name, signature,
         SoftUtils.typeForClassList(argumentTypes),
         Type.getType(returnType),
         SoftUtils.typeForClassList(throwsTypes));
  }
  
  
  
  /* (non-Javadoc)
   * @see jen.SoftMethod#getArgumentTypes()
   */
  public List<Type> getArgumentTypes() {
    if (!canModify()) {
      return Collections.unmodifiableList(argumentTypes);
    } else {
      return argumentTypes;
    }
  }

  /* (non-Javadoc)
   * @see jen.SoftMethod#getReturnType()
   */
  public Type getReturnType() {
    return returnType;
  }
  
  protected void setReturnType(Type returnType) {
    checkModify();
    this.returnType = returnType; 
  }

  /* (non-Javadoc)
   * @see jen.SoftMethod#getThrowsTypes()
   */
  public List<Type> getThrowsTypes() {
    if (!canModify()) {
      return Collections.unmodifiableList(throwsTypes);
    } else {
      return throwsTypes;
    }
  }

  /* (non-Javadoc)
   * @see jen.SoftMethod#getDescriptor()
   */
  public String getDescriptor() {
    return Type.getMethodDescriptor(
        getReturnType(),
        getArgumentTypes().toArray(new Type[getArgumentTypes().size()]));
  }
}
