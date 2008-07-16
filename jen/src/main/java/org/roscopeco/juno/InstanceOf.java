/* InstanceOf.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:28 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * Constraint that requires an instance of the predicate Class.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:28 $ 
 */
public class InstanceOf extends Constraint
{
  final Class predicate;
  
  /** 
   * Create a new constraint that the object be an instance of the
   * predicate class.
   * 
   * @param one
   * @param two
   */
  public InstanceOf(Class predicate) {
    this.predicate = predicate;
  }

  /**
   * {@inheritDoc}
   * 
   * This constraint matches <code>null</code> against the class of <code>Void</code>.
   */
  public boolean eval(Object o) {
    if (o == null) return (predicate == Void.class);
    return predicate.isInstance(o);
  }
  
  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("an instance of ").append(predicate.getName());    
  }
}
