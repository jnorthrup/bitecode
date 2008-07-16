/* SoftUtilsTestCase.java - Awaiting description
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
 * File version: $Revision: 1.5 $ $Date: 2005/10/23 02:15:18 $
 * Originated: 13-Mar-04
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.roscopeco.juno.pre50.JunoTestCase;

import static jen.SoftUtils.*;

public class SoftUtilsTestCase extends JunoTestCase
{
  
  public void testNullSafeArg() {
    assertThat(nullSafeArg(getClass(),"testMethod","test",new Object()),instOf(Object.class));
    assertThat(nullSafeArg(getClass(),"testMethod","test","Test Str"),instOf(String.class));
    
    try {
      nullSafeArg(getClass(),"testMethod","test",null);      
    } catch (NullArgumentException e) {
      assertThat(e.getMethod(),isEqual("testMethod"));
      assertThat(e.getClassName(),isEqual(getClass().getName()));
      assertThat(e.getArguments(),isEqual("test"));
    }
  }  
  
  // TODO Tests needed for:
  //
  //     + type/class/string list/array converters
  //     + method signature / descriptor
  //     + default classloaders
  //     + etc.
  //
  // currently a lot of this is brittle tested through other stuff.
  
}
