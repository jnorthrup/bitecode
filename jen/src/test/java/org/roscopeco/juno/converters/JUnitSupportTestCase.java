/* JUnitSupportTestCase.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:34 $
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno.converters;

import org.roscopeco.juno.pre50.JunoTestCase;

/**  
 * <strong>This test-case also has a simple main() method that demos
 * the description features of constraints.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:34 $
 */
public class JUnitSupportTestCase extends JunoTestCase
{
  /* Demonstrates usage of the 'assertThat' method (static import from 
   * JUnitSupport) along with the syntax sugar provided by Constraints.
   */
  public void testSimpleAssertThat() {
    String s = "Hello";
    Integer i = new Integer(15);
    
    assertThat(s,not(isNull()));            
    assertThat(s,instOf(String.class));            
    assertThat(s,startsWith("H"));
    assertThat(s,and(startsWith("H"),endsWith("o")));
    assertThat(i,instOf(Integer.class));
    assertThat(i,between(new Integer(10),new Integer(20)));
  }
  
  public static void main(String args[]) {
    // Demonstrate string representations
    System.out.println("This just demonstrates the display usage of constraints. See the\n" +
                       "unit tests for usage details. See also FailingConstraintTest\n");
    System.out.println("Expecting: "+isNull());
    System.out.println("Expecting: "+not(isNull()));
    System.out.println("Expecting: "+or(startsWith("h"),endsWith("g")));
    System.out.println("Expecting: "+xor(and(not(isNull()),exactSize(10)),endsWith("g")));
    System.out.println("Expecting: "+larger(25));
    System.out.println("Expecting: "+and(smaller(100),contains("user")));
    System.out.println("Expecting: "+and(gt(new Integer(100)),eq(new Integer(100))));
    System.out.println("Expecting: "+not(and(gt(new Integer(100)),eq(new Integer(500)))));
    System.out.println("Expecting: "+or(gt(new Integer(100)),eq(new Integer(150))));
    System.out.println("Expecting: "+xor(gt(new Integer(100)),eq(new Integer(85))));
    System.out.println("Expecting: "+and(not(empty()),not(larger(500))));
    System.out.println("Expecting: "+instOf(java.util.Map.class));    
    System.out.println("Expecting: "+isEqual(java.util.Map.class));    
    System.out.println("Expecting: "+isEqual(new String("Hello there")));    
    System.out.println("Expecting: "+isSame(new String("Hello there")));    
  }

}
