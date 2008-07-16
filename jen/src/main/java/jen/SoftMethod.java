/* SoftMethod.java - An individual method on a SoftClass
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/03 17:59:41 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.util.List;

import org.objectweb.asm.Type;

/**
 * An individual method on a {@link SoftClass}. During generation, only those
 * methods from the underlying class (if any) that have a corresponding 
 * <code>SoftMethod</code> implementation will be generated. Any additional
 * <code>SoftMethod</code>s will be generated at the end (after additional
 * {@link SoftField}s).
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/03 17:59:41 $
 */
public interface SoftMethod extends SoftMember
{
  
  /** 
   * @return A {@link java.util.List} of {@link Type}s representing the
   *         method's argument types.
   */
  public List<Type> getArgumentTypes();

  /** 
   * @return A {@link Type} representing the method's return type.
   */
  public Type getReturnType();

  /** 
   * @return A {@link java.util.List} of {@link Type}s representing the
   *         method's exception types.
   */
  public List<Type> getThrowsTypes();
  
  /** 
   * @return The method's discriptor (bytecode signature).
   */
  public String getDescriptor();
}