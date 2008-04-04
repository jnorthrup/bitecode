/* UnresolvableTypeHierarchyException.java - Missing super/interface class
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/18 01:28:45 $
 * Originated: Oct 17, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * Thrown to indicate that one or more classes referenced from the inheritance
 * hierarchy of a {@code SoftClass} could not be resolved. Specifically, if this 
 * exception is thrown any of the {@link Class}-returning methods, then either 
 * the SoftClass' superclass, or an implemented interface, could not be resolved to 
 * a currently defined {@code Class}. This is not necessarily a fatal error - the 
 * superclass or interface(s) may be part of the same generation run, or this class
 * may not be intended for use in the current classloading environment.  
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/18 01:28:45 $ 
 */
public class UnresolvableTypeHierarchyException extends Exception
{
  /**
   * Create a new {@code UnresolvableTypeHierarchyException}, with the specified name
   * for the missing class.
   *  
   * @param clazz The missing {@code Class}.
   */
  public UnresolvableTypeHierarchyException(String className) {
    super("Missing class: "+className);
  }
}
