/* ClassloaderUnavailableException.java - Loader not found
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:41 $
 * Originated: 03-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * Thrown to indicate that neither a system or thread context classloader 
 * reference could be obtained for class generation. This doesn't affect
 * the state of the generation, and the bytecode for the class should 
 * still be available (via the {@link SoftClass#generateBytecode()} method).
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:41 $ 
 */
public class ClassloaderUnavailableException extends RuntimeException
{
  private final String name;
  
  /**
   * Creates an instance of ClassloaderUnavailableException to indicate that
   * a loader could not be found in which to define the specified 
   * {@link SoftClass}. 
   * 
   * @param softClass The SoftClass that was to be defined.
   */
  public ClassloaderUnavailableException(SoftClass softClass) {
    if (softClass == null) throw(new NullArgumentException(getClass(),"softClass"));
    this.name = softClass.getName();    
  }
  
  /**
   * Creates an instance of ClassloaderUnavailableException to indicate that
   * a loader could not be found in which to load the specified class.
   * 
   * @param javaClass The name of the class that was to be loaded.
   */
  public ClassloaderUnavailableException(String javaClass) {
    if (javaClass == null) throw(new NullArgumentException(getClass(),"javaClass"));
    this.name = javaClass;    
  }
  
  /**
   * Retrieves the name of the class that was being loaded or defined.
   * 
   * @return The problematic class name.
   */
  public String getName() {
    return name;
  }
  
  public String toString() {
    return "No classloader was available: "+getName();
  }
}
