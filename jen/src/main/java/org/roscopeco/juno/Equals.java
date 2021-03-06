/* Equals.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:30 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * A simple Equality constraint.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:30 $ 
 */
public class Equals extends Constraint
{
  final Object predicate; 
  
  /**
   * Create a new constraint that requires equal objects.
   * 
   * @param predicate The object with which equality will be compared.
   */
  public Equals(Object predicate) {
    this.predicate = predicate; 
  }

  /**
   * {@inheritDoc}
   * 
   * This constraint considers <code>null</code> (only) equal to 
   * <code>null</code>.
   */
  public boolean eval(Object o) {
    if (predicate == null) return o == null;
    return predicate.equals(o);
  }
  
  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("equal to \"").append(predicate).append("\"");    
  }      
}
