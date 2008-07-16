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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:29 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * A 'larger than' constraint.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:29 $ 
 */
public class Larger extends SizeConstraint
{
  final long than;
  
  /** 
   * Create a new constraint that requires the compared object be a 'sized'
   * object (i.e. have one of the common methods <code>size()</code> or 
   * <code>length()</code>, or an integer field <code>length</code>), and
   * that that size be larger than the specified size. 
   * 
   * @param than The target size.
   */
  public Larger(long than) {
    if (than < 0) throw new IllegalArgumentException("You want 'CompGreater'");
    this.than = than;
  }

  /**
   * {@inheritDoc}
   * 
   * Attempts to determine the size of the object in the appropriate
   * manner for it's type, or by reflection. 
   */
  public boolean eval(Object o) {
    long size = sizeOf(o);
    if (size == -1) return false;
    return than < size;
  }

  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("larger than ").append(than).append(" element(s)");    
  }      
}
