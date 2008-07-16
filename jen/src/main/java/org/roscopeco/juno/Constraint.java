/* Constraint.java - Base constraint class.
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
 * Represents an individual constraint (like 'not equals' and 'or').
 * This is the base class for constraints, and is based on 
 * the million and one similar ideas out there. I resent bringing in
 * an entire mock object (etc) library to the runtime path just for 
 * this, so we have this as a very small feature library.
 * <p/>
 * As well as the (abstract) evaluate method, this class overrides 
 * {@link #toString} and provides the {@link #describe} method, in
 * order that constraints return a meaningful description of their
 * requirements when coerced to a string. 
 * <p/>
 * The concrete foundation for this class (and indeed, the library)
 * was an entry on <a href='http://joe.truemesh.com/blog/'>Joe Walnes'
 * Blog</a>.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:27 $ 
 */
public abstract class Constraint
{
  /** 
   * For subclass instantiation (typically implicit).
   */
  protected Constraint() {}
  
  /**
   * Determine whether the given object meets this constraint. 
   * 
   * @param o The object to test
   * @return <code>true</code> if the object met the constraint.
   */
  public abstract boolean eval(Object o);

  /**
   * Determines the 'value of' the specified object as a 
   * comparison operand to this constraint. The object returned should
   * be a printable (via {@code toString()}) value object representing
   * the supplied object's value. In practice this will usually be the
   * object itself (as returned by this implementation). This method
   * is provided for information purposes only.
   * 
   * @param o The object to obtain the 'value of'.
   * 
   * @return The value of the specified object.
   */
  public Object valueOf(Object o) {
    return o;
  }
  
  /**
   * Constraint implementations should override this method to provide a
   * description of the constraint, in the form of the expectation. 
   * This may be chained and nested, so care must be taken to ensure that
   * you provide <i>only the expected value(s)</i>.
   * <p/>
   * For example, a simple constraint that simply checked for equality might
   * return <code>"equal to "+predicate.toString()</code>. Specifically, avoid
   * capitals and punctuation, and do not pad with whitespace.
   * 
   * @param buffer The {@link java.lang.StringBuilder} the constraint should 
   *        append it's description to.
   *        
   * @return The modified (or replaced) <code>StringBuffer</code>.
   */
  public StringBuilder describe(StringBuilder buffer) {
    return buffer;
  }
  
  /**
   * Returns a description of this constraint.
   */
  public String toString() {
    return describe(new StringBuilder()).toString();
  }
}
