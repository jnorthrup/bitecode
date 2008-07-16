/* AddConstructorTestCase.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/20 22:34:40 $
 * Originated: 05-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;


import org.roscopeco.juno.pre50.JunoTestCase;

public class AddConstructorTestCase extends JunoTestCase
{
  public void testAddConstructor() {
    SoftClass c = new SoftClass(NeedsANullCtor.class);
    c.setName("jen.GotANullCtor");    
    c.putSoftMethod(new ExampleNullConstructor(c,"Dummy Null Argument"));
    Class<?> dc2 = c.defineClass();
    
    assertThat("Defined class",dc2,not(isNull()));
    assertThat("Defined class name",dc2.getName(),isEqual("jen.GotANullCtor"));
    
    Object o = null;
    try {
      o = dc2.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Instantiation failed with "+e);
    }
    
    // Same number of methods and fields
    assertThat(o.getClass().getDeclaredFields(),exactSize(NeedsANullCtor.class.getDeclaredFields().length));    
    assertThat(o.getClass().getDeclaredMethods(),exactSize(NeedsANullCtor.class.getDeclaredMethods().length));    
    assertThat(o.getClass().getDeclaredConstructors(),exactSize(NeedsANullCtor.class.getDeclaredConstructors().length+1));    
    assertThat(o.getClass().getDeclaredClasses(),exactSize(NeedsANullCtor.class.getDeclaredClasses().length));    
  }

}
