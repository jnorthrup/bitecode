/* InconsistentSoftClassException.java - concurrent modification occured
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/02 02:15:30 $
 * Originated: 07-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * Indicates that a {@link SoftClass} instance upon which some operation was
 * to be performed has become internally corrupted and may not be used for
 * direct generation.
 * <p/>
 * Although you may find that inconsistent instances can still be used to 
 * supply (or visit) ASM nodes, this is not recommended. Any instance of 
 * <code>SoftClass</code> which raises this exception should be discarded.  
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/02 02:15:30 $ 
 */
public class InconsistentSoftClassException extends RuntimeException
{
  private static String messageHelper(SoftClass clazz, String message, Throwable cause) {
    StringBuilder b = new StringBuilder();
    b.append("SoftClass ").append(clazz != null ? clazz.getName() : "<unknown>").
      append(" is inconsistent");
    
    if (message != null) {
      b.append(": ").append(message);
    }
    
    if (cause != null) {
      b.append(" (").append(cause).append(")");
    }
    
    return b.toString();
  }
  
  /**
   * 
   * @param clazz
   */
  public InconsistentSoftClassException(SoftClass clazz) {
    this(clazz,null,null);
  }

  /**
   * 
   * @param clazz
   * @param message
   */
  public InconsistentSoftClassException(SoftClass clazz, String message) {
    this(clazz,message,null);
  }

  /**
   * @param message
   * @param cause
   */
  public InconsistentSoftClassException(SoftClass clazz, String message, Throwable cause) {
    super(messageHelper(clazz,message,cause));    
  }

  /**
   * @param cause
   */
  public InconsistentSoftClassException(SoftClass clazz, Throwable cause) {
    this(clazz,null,cause);
  }

}
