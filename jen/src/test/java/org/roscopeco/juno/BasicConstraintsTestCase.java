/* BasicConstraintsTestCase.java - Awaiting description
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
 * Originated: 29-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno;

import junit.framework.TestCase;

public class BasicConstraintsTestCase extends TestCase
{
  private static final Object same = new Object();       // string pooling is a nightmare
  private static final String eqs = "EQUAL";
  private static final Integer fiftyfive = new Integer(55);
  private static final String fourString = "four";
  private static final String twelveString = "longerstring";
  private static final String emptyString = "";  
  private static final String[] emptyArray = new String[0];
  private static final String[] fourArray = new String[] {"one","two","three","four"};
  private static final java.util.List fourList = java.util.Arrays.asList(fourArray);

  /* ******************************
   * BASIC
   ***************************** */
  public void testBasicNull() {
    Constraint nullc = new Null();
    assertTrue("Broken null 1",nullc.eval(null));
    assertFalse("Broken null 2",nullc.eval(new Object()));
  }
  
  public void testBasicSame() {
    Constraint samec = new Same(same);
    assertTrue("Broken same 1",samec.eval(same));
    assertFalse("Broken equals 2",samec.eval(new Object())); // pooling
  }
  
  public void testBasicEquals() {
    Constraint eqc = new Equals(eqs);
    assertTrue("Broken equals 1",eqc.eval(eqs));
    assertTrue("Broken equals 2",eqc.eval("EQUAL"));
    assertFalse("Broken equals 3",eqc.eval("EUQAL"));
    assertFalse("Broken equals 4",eqc.eval(null));
  }
  
  public void testBasicInstOf() {
    Constraint instc = new InstanceOf(String.class);
    assertTrue("Broken instanceof 1",instc.eval(eqs));
    assertFalse("Broken instanceof 2",instc.eval(same));
    assertFalse("Broken instanceof 3",instc.eval(null));    
  }
  
  /* ******************************
   * LOGIC
   ***************************** */
  public void testLogicNot() {
    Constraint notc = new Not(new Null()); /* not null */    
    assertTrue("Broken not 1",notc.eval(new Object()));
    assertFalse("Broken not 2",notc.eval(null));
  }
  
  public void testLogicAnd() {
    Constraint andc = new And(new StartsWith("te"),new EndsWith("st")); /* 'test' */
    assertTrue("Broken and 1",andc.eval("test"));  
    assertTrue("Broken and 2",andc.eval("terrorist"));
    assertFalse("Broken and 3",andc.eval("tenor"));    
    assertFalse("Broken and 4",andc.eval("fast"));    
    assertFalse("Broken and 5",andc.eval("cask"));    
  }
  
  public void testLogicOr() {
    Constraint orc = new Or(new StartsWith("te"),new EndsWith("st")); /* 'terrorist', 'tenor', 'mist' */ 
    assertTrue("Broken or 1",orc.eval("test"));  
    assertTrue("Broken or 2",orc.eval("terrorist"));
    assertTrue("Broken or 3",orc.eval("tenor"));    
    assertTrue("Broken or 4",orc.eval("fast"));    
    assertFalse("Broken and 5",orc.eval("cask"));
  }
  
  public void testLogicXor() {
    Constraint xorc = new Xor(new StartsWith("te"),new EndsWith("st")); /* 'tell', 'fast' */
    assertFalse("Broken xor 1",xorc.eval("test"));  
    assertFalse("Broken xor 2",xorc.eval("terrorist"));
    assertTrue("Broken xor 3",xorc.eval("tenor"));    
    assertTrue("Broken xor 4",xorc.eval("fast"));    
    assertFalse("Broken xand 5",xorc.eval("cask"));    
  }
  
  /* ******************************
   * COMPARABLE
   ***************************** */
  public void testComparableEquals() {
    Constraint ceqc = new CompEqual(fiftyfive);
    assertTrue("Broken compeq 1",ceqc.eval(new Integer(55)));
    assertFalse("Broken compeq 2",ceqc.eval(new Integer(86)));
    assertFalse("Broken compeq 3",ceqc.eval("abc"));
  }
  
  public void testComparableGreater() {
    Constraint ceqc = new CompGreater(fiftyfive);
    assertTrue("Broken compgt 1",ceqc.eval(new Integer(86)));
    assertFalse("Broken compgt 2",ceqc.eval(new Integer(55)));
    assertFalse("Broken compgt 3",ceqc.eval(new Integer(31)));
    assertFalse("Broken compgt 4",ceqc.eval("abc"));
  }
  
  public void testComparableBetween() {
    Constraint cbtw = new Between(new Integer(80),new Integer(100));
    assertTrue("Broken between 1",cbtw.eval(new Integer(81)));
    assertTrue("Broken between 2",cbtw.eval(new Integer(90)));
    assertTrue("Broken between 3",cbtw.eval(new Integer(100)));
    assertFalse("Broken between 4",cbtw.eval(new Integer(79)));
    assertFalse("Broken between 5",cbtw.eval(new Integer(101)));
    assertFalse("Broken between 6",cbtw.eval("abc"));
  }
  
  public void testComparableBetweenAgain() {
    Constraint cbtw = new Between(new Character('c'),new Character('w'));
    assertTrue("Broken between 1",cbtw.eval(new Character('d')));
    assertTrue("Broken between 2",cbtw.eval(new Character('s')));
    assertTrue("Broken between 3",cbtw.eval(new Character('p')));
    assertFalse("Broken between 4",cbtw.eval(new Character('a')));
    assertFalse("Broken between 5",cbtw.eval(new Character('z')));
    assertFalse("Broken between 6",cbtw.eval(new Integer(48)));
    assertFalse("Broken between 6",cbtw.eval("abc"));
  }
  
  public void testComparableLess() {
    Constraint ceqc = new CompLess(fiftyfive);
    assertTrue("Broken complt 1",ceqc.eval(new Integer(31)));
    assertFalse("Broken complt 2",ceqc.eval(new Integer(55)));
    assertFalse("Broken complt 3",ceqc.eval(new Integer(86)));
    assertFalse("Broken complt 4",ceqc.eval("abc"));
  }
  
  /* ******************************
   * SIZE
   ***************************** */
  public void testSizeEmpty() {
    Constraint emptyc = new Empty();
    assertTrue("Broken empty 1",emptyc.eval(emptyArray));
    assertTrue("Broken empty 2",emptyc.eval(emptyString));
    assertFalse("Broken empty 3",emptyc.eval(fourString));
    assertFalse("Broken empty 4",emptyc.eval(new Object()));
  }
  
  public void testSizeExact() {    
    Constraint fourc = new ExactSize(4);
    assertTrue("Broken exact 1",fourc.eval(fourString));
    assertTrue("Broken exact 2",fourc.eval(fourArray));
    assertTrue("Broken exact 3",fourc.eval(fourList));
    assertFalse("Broken exact 4",fourc.eval(twelveString));
  }
  
  public void testSizeLarger() {
    Constraint abovetenc = new Larger(10);
    assertTrue("Broken larger 1",abovetenc.eval(twelveString));
    assertFalse("Broken larger 2",abovetenc.eval(fourList));
    assertFalse("Broken larger 3",abovetenc.eval(fourArray));
  }
  
  public void testSizeSmaller() {
    Constraint belowfivec = new Smaller(5);
    assertTrue("Broken smaller 1",belowfivec.eval(fourArray));
    assertTrue("Broken smaller 1",belowfivec.eval(fourString));
    assertFalse("Broken smaller 1",belowfivec.eval(twelveString));      
  }
}
