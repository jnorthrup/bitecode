/* Constraints.java - Awaiting description
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
 * Static utility class that provides nicer syntax for building constraints.
 * This class provides static methods for each of the main constraint types,
 * that simply construct the appropriate constraint from the given value(s)
 * and return it. The purpose of this is to allow constraints to be built
 * from chained method calls:
 * <p/><ul>
 * <code>assertThat(something, eq(5));</code><br/>
 * <code>assertThat(something, and(not(eq(5)),lt(10));</code><br/>
 * <code>assertThat(aString, and(not(null()),or(startsWith("me"),endsWith("you")));</code><br/>
 * </ul><p/>
 * When developing under Java 5, <code>import static</code> can be used to make
 * these methods available in the unit scope. There are no instances of this class.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:27 $ 
 */
public final class Constraints
{
  /* basic constraints */
  public static Constraint isTrue() {
    return new True();
  }
  
  public static Constraint isFalse() {
    return not(isTrue());
  }
  
  public static Constraint isNull() {
    return new Null();
  }
  
  public static Constraint notNull() {
    return not(isNull());
  }
  
  public static Constraint isSame(Object predicate) {
    return new Same(predicate);    
  }
  
  public static Constraint notSame(Object predicate) {
    return not(isSame(predicate));    
  }
  
  public static Constraint instOf(Class predicate) {
    return new InstanceOf(predicate);
  }
  
  /** .equals(Object) test */
  public static Constraint isEqual(Object predicate) {
    return new Equals(predicate);
  }  
  
  public static Constraint notEqual(Object predicate) {
    return not(isEqual(predicate));
  }  
  
  /* logic */
  public static Constraint not(Constraint operand) {
    return new Not(operand);
  }
  
  public static Constraint and(Constraint one, Constraint two) {
    return new And(one,two);
  }
  
  public static Constraint or(Constraint one, Constraint two) {
    return new Or(one,two);
  }

  public static Constraint xor(Constraint one, Constraint two) {
    return new Xor(one,two);
  }
    
  /* Comparables */
  /** Comparable equality */
  public static Constraint eq(Comparable predicate) {
    return new CompEqual(predicate);
  }
  
  public static Constraint neq(Comparable predicate) {
    return not(eq(predicate));
  }
  
  public static Constraint gt(Comparable predicate) {
    return new CompGreater(predicate);
  }
  
  public static Constraint lt(Comparable predicate) {
    return new CompLess(predicate);
  }
  
  public static Constraint between(Comparable lower, Comparable upper) {
    return new Between(lower,upper);
  }
    
  /* Strings */
  public static Constraint contains(CharSequence predicate) {
    return new Contains(predicate);
  }
  
  public static Constraint endsWith(CharSequence predicate) {
    return new EndsWith(predicate);
  }
  
  public static Constraint startsWith(CharSequence predicate) {
    return new StartsWith(predicate);
  }
  
  /* Size */
  public static Constraint empty() {
    return new Empty();
  }
  
  public static Constraint exactSize(long size) {
    return new ExactSize(size);
  }
  
  public static Constraint larger(long than) {
    return new Larger(than);
  }
      
  public static Constraint smaller(long than) {
    return new Smaller(than);
  }
      
  private Constraints() {}
}
