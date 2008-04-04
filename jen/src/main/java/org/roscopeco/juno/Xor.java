/* Xor.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:31 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

/** 
 * Not yet documented.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:31 $ 
 */
public class Xor extends Constraint
{
  final Constraint one;
  final Constraint two;
  
  /** 
   * Create a new constraint that requires either constraint <code>one</code>
   * or constraint <code>two</code> to be met, but not both.
   * 
   * @param one
   * @param two
   */
  public Xor(Constraint one, Constraint two) {
    this.one = one;
    this.two = two;    
  }

  /* (non-Javadoc)
   * @see org.roscopeco.juno.Constraint#eval(java.lang.Object)
   */
  public boolean eval(Object o) {
    return (one.eval(o) ^ two.eval(o));
  }
  public StringBuilder describe(StringBuilder buffer) {
    return two.describe(one.describe(buffer).append(" or "));
  }    
}
