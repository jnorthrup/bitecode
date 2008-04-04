/* True.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:26 $
 * Originated: 13-Jul-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * A constraint that requires the argument be either a <code>Boolean</code>
 * with the value true. This constraint also supports C-style numerical 
 * booleans (when passed an implementation of <code>Number</code>) - in this
 * case, true is returned for any value <code>&gt;0</code>.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:26 $ 
 */
public class True extends Constraint
{
  public boolean eval(Object o) {
    // Is a Boolean true ?
    try {
      return ((Boolean)o).booleanValue();
    } catch (Exception e) {
      // Is a number > 0 ?
      try {
        return ((Number)o).intValue() > 0;
      } catch (Exception e2) {
        return false;
      }
    }
  }

  /* (non-Javadoc)
   * @see org.roscopeco.juno.Constraint#describe(java.lang.StringBuilder)
   */
  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("true");
  }

}
