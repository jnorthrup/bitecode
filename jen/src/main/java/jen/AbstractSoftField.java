/* AbstractSoftField.java - SoftField base class
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/02 02:15:29 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.Type;

/** 
 * Base-class for {@link SoftField} implementations.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/02 02:15:29 $ 
 */
public abstract class AbstractSoftField extends AbstractSoftMember implements SoftField
{
  private Type type;
  
  /**
   * Create a new field on the specified {@link SoftClass}.
   * 
   * @param softClass The {@link SoftClass} upon which this field will be 
   *        generated.
   */
  protected AbstractSoftField(SoftClass softClass) {
    super(softClass);
  }

  /**
   * Create a new field on the specified {@link SoftClass}, with the specified
   * modifiers, name and type..
   * 
   * @param softClass
   * @param access
   * @param name
   * @param signature
   * @param type
   */
  protected AbstractSoftField(SoftClass softClass, int access, String name, String signature, Type type) {    
    super(softClass, access, name, signature);
    this.type = type;
  }
  
  /* (non-Javadoc)
   * @see jen.SoftField#getType()
   */
  public String getDescriptor() {
    return type.getDescriptor();
  }
  
  /* (non-Javadoc)
   * @see jen.SoftField#getType()
   */
  public Type getType() {
    return type;
  }
  
  protected void setType(Type type) {
    checkModify();
    this.type = type;
  }  
}
