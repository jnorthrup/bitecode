/* Between.java - Awaiting description
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

import org.roscopeco.juno.Constraints;

/** 
 * Numerically between constraint.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:29 $ 
 */
public class Between extends Constraint
{
  final Comparable one;
  final Comparable two;
  final Constraint worker;
  
  /** 
   * Create a new constraint that requires the supplied Comparable to
   * be between the supplied comparables. This is an inclusive test.
   * 
   * @param lower The lower bound
   * @param upper The upper bound
   */
  public Between(Comparable lower, Comparable upper) {
    this.one = lower;
    this.two = upper;    
    this.worker = Constraints.and( Constraints.xor(Constraints.gt(one),Constraints.eq(one)) , Constraints.xor(Constraints.lt(two),Constraints.eq(two))); 
  }

  /* (non-Javadoc)
   * @see org.roscopeco.juno.Constraint#eval(java.lang.Object)
   */
  public boolean eval(Object o) {
    return worker.eval(o); 
  }
  
  public StringBuilder describe(StringBuilder buffer) {
    return buffer.append("between ").append(one).append(" and ").append(two);
  }    
}
