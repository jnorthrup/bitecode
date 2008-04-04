/* JunoTestCase.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:39 $
 * Originated: 09-Aug-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno.pre50;


import org.roscopeco.juno.Constraint;
import org.roscopeco.juno.Constraints;
import org.roscopeco.juno.converters.JUnitSupport;

import junit.framework.TestCase;

/** 
 * Abstract JUnit test-case providing Juno support for unit-testing
 * in pre-5.0 JREs.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:39 $ 
 */
public abstract class JunoTestCase extends TestCase
{
  /* assertions */
  public static void assertThat(Object o, Constraint c) {
    JUnitSupport.assertThat(null,o,c);
  }
  
  public static void assertThat(String message, Object o, Constraint c) {
    JUnitSupport.assertThat(message,o,c);
  }
  
  /* basic constraints */
  public static Constraint isTrue() {
    return Constraints.isTrue();
  }
  
  public static Constraint isFalse() {
    return Constraints.isFalse();
  }
  
  public static Constraint isNull() {
    return Constraints.isNull();
  }
  
  public static Constraint notNull() {
    return Constraints.notNull();
  }
  
  public static Constraint isSame(Object predicate) {
    return Constraints.isSame(predicate);    
  }
  
  public static Constraint notSame(Object predicate) {
    return Constraints.notSame(predicate);    
  }
  
  public static Constraint instOf(Class predicate) {
    return Constraints.instOf(predicate);
  }
  
  /** .equals(Object) test */
  public static Constraint isEqual(Object predicate) {
    return Constraints.isEqual(predicate);
  }  
  
  public static Constraint notEqual(Object predicate) {
    return Constraints.notEqual(predicate);
  }  
  
  /* logic */
  public static Constraint not(Constraint operand) {
    return Constraints.not(operand);
  }
  
  public static Constraint and(Constraint one, Constraint two) {
    return Constraints.and(one,two);
  }
  
  public static Constraint or(Constraint one, Constraint two) {
    return Constraints.or(one,two);
  }

  public static Constraint xor(Constraint one, Constraint two) {
    return xor(one,two);
  }
    
  /* Comparables */
  /** Comparable equality */
  public static Constraint eq(Comparable predicate) {
    return Constraints.eq(predicate);
  }
  
  public static Constraint neq(Comparable predicate) {
    return Constraints.neq(predicate);
  }
  
  public static Constraint gt(Comparable predicate) {
    return Constraints.gt(predicate);
  }
  
  public static Constraint lt(Comparable predicate) {
    return Constraints.lt(predicate);
  }
  
  public static Constraint between(Comparable lower, Comparable upper) {
    return Constraints.between(lower,upper);
  }
    
  /* Strings */
  public static Constraint contains(CharSequence predicate) {
    return Constraints.contains(predicate);
  }
  
  public static Constraint endsWith(CharSequence predicate) {
    return Constraints.endsWith(predicate);
  }
  
  public static Constraint startsWith(CharSequence predicate) {
    return Constraints.startsWith(predicate);
  }
  
  /* Size */
  public static Constraint empty() {
    return Constraints.empty();
  }
  
  public static Constraint exactSize(long size) {
    return Constraints.exactSize(size);
  }
  
  public static Constraint larger(long than) {
    return Constraints.larger(than);
  }
      
  public static Constraint smaller(long than) {
    return Constraints.smaller(than);
  }
}
