/* SoftField.java - An individual field on a SoftClass
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
 * An individual field on a {@link SoftClass}. During generation, only those
 * fields from the underlying class (if any) that have a corresponding 
 * <code>SoftField</code> implementation will be generated. Any additional
 * <code>SoftMethod</code>s will be generated at the end (before additional
 * {@link SoftMethod}s).
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/02 02:15:29 $ 
 */
public interface SoftField extends SoftMember
{
  /** 
   * @return The method's descriptor (bytecode signature).
   */
  public String getDescriptor();
  
  /**
   * @return This field's {@link org.objectweb.asm.Type}.
   */
  public Type getType();  
}
