/* SwitchSuperTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/02 02:15:28 $
 * Originated: 13-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import junit.framework.TestCase;

public class SwitchSuperTestCase extends TestCase
{
  // Deprecated to save warnings (DemoClass is annotated with Deprecated) 
  @Deprecated  
  public void testCanSwitchSuperClass() throws Exception {
    SoftClass sc = new SoftClass(DemoClass.class);
    sc.setName("jen.tests.NewClass02");
    
    sc.setSuperClass(Thread.class);
      
    Class<?> newClazz = sc.defineClass();    
    Object o = newClazz.getConstructor(new Class[] { String.class }).newInstance("Ctor Arg");
    
    // don't really multithread...
    Thread thread = (Thread)o;
    thread.run();    
  }
}
