/* NestedSoftClass.java - A nested class on a SoftClass
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/02 02:15:29 $
 * Originated: Oct 1, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/**
 * Represents a nested (possibly of the local or anonymous inner variety) class 
 * declaration on a specific {@link SoftClass}. This 
 * can be seen as a reference to the actual class defininiton, rather
 * than the definition itself.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/02 02:15:29 $ 
 */
public interface NestedSoftClass extends SoftMember
{
  /**
   * Obtain the fully-qualified language name of the inner class.
   * 
   * @return The language name of the inner class.
   */
  public String getInnerName();

  /**
   * Obtain the fully-qualified language name of the outer (or <em>enclosing</em>)
   * class.
   * 
   * @return The language name of the outer class.
   */
  public String getOuterName();
}