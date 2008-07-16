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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:26 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * An exact-size constraint.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:26 $ 
 */
public class ExactSize extends SizeConstraint
{
  final long than;
  
  /** 
   * Create a new constraint that requires the compared object be
   * exactly the size specified. 'Size' in this context is determined
   * by the appropriate method for the type.
   * 
   * @param target The target size to match.
   */
  public ExactSize(long target) {
    this.than = target;
  }

  /**
   * {@inheritDoc}
   * 
   * Attempts to determine the size of the object in the appropriate
   * manner for it's type, or by reflection.
   */
  public boolean eval(Object o) {
    long size = sizeOf(o);
    return (size != Long.MIN_VALUE) ? than == size : false;
  }

  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("with exactly ").append(than).append(" element(s)");    
  }      
}
