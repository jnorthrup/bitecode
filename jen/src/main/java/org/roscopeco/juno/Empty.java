/* Smaller.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:27 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * Special-case exact-size constraint that requires zero elements.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:27 $ 
 */
public class Empty extends ExactSize
{
  /** 
   * Create a new constraint that requires the compared sized object be
   * empty (i.e. have zero elements). See {@link ExactSize} for more
   * information on how 'size' is determined.
   * 
   * @param target The target size to match.
   */
  public Empty() {
    super(0);
  }

  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("empty");    
  }      
}
