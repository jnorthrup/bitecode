/* CompLess.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:29 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * Constraint that requires a comparable be greater than another
 * comparable.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:29 $ 
 */
public class CompGreater extends Constraint
{
  final Comparable predicate;
  
  /** 
   * Create a new constraint that requires the compared object le
   * logically 'greater than' the predicate. This constructor throws 
   * NPE if passed <code>null</code>.
   * 
   * @param predicate The predicate for this constraint.
   */
  public CompGreater(Comparable predicate) {
    if (predicate == null) throw new NullPointerException();
    this.predicate = predicate;
  }

  /**
   * {@inheritDoc}
   * 
   * Obviously the object tested must be a <code>Comparable</code>. 
   * <code>false</code> is returned otherwise. 
   */
  public boolean eval(Object o) {
    if (!(o instanceof Comparable)) return false;
    try {
      return (((Comparable)o).compareTo(predicate) > 0);
    } catch (ClassCastException e) {
      return false;
    }
  }

  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("greater than ").append(predicate);    
  }      
}
