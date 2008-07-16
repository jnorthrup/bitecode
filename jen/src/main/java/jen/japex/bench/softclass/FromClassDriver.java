/* FromClassDriver.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/21 23:11:30 $
 * Originated: Oct 21, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.japex.bench.softclass;

import jen.SoftClass;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;

/** 
 * Tests SoftClass performance instantiating from (i.e. finding, loading and parsing) 
 * a java.lang.Class.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/21 23:11:30 $ 
 */
public class FromClassDriver extends JapexDriverBase
{
  static final int iter = 10000;
  
  Class clazz;
  
  public void prepare(TestCase test) {
    String cn = test.getParam("fromClass");
    try {
      clazz = Class.forName(cn);      
    } catch (Exception e) {
      // maybe NPE as well as CNFE
      throw(new RuntimeException(cn == null ? "Unable to resolve "+cn+" for use in FromClassDriver" : 
                                              "The fromClass parameter MUST be specified in Japex config!"));      
    }
  }
  
  public void warmup(TestCase test) {
    SoftClass sc = new SoftClass(clazz);
    sc.generateBytecode();
  }

  @Override
  public void run(TestCase test) {
    SoftClass sc = new SoftClass(clazz);
    sc.generateBytecode();
  }
  
  // Do this, otherwise gc kicks in on tests 2 and 4 and badly skews results
  public void terminateDriver() {
    System.gc();
    try {      
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      /* ignore */
    }
  }
}
