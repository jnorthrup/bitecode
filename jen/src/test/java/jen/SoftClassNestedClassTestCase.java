/* SoftClassNestedClassTestCase.java - Awaiting description
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
 * Originated: Oct 13, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import junit.framework.TestCase;

import static org.roscopeco.juno.Constraints.*;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

public class SoftClassNestedClassTestCase extends TestCase
{
  static class NestedClass {
    public void aMethod() { }
  }
  
  static final Class local;
  
  static final SoftClass mySc = new SoftClass(SoftClassNestedClassTestCase.class);
  
  static {
    class LocalClass {
      public void anotherMethod() { }
    }
    
    local = LocalClass.class;
  }
    
  public void testCanCreateSoftClassFromSoftNestedClassForNested() {
    NestedSoftClass snc = mySc.getNestedSoftClass(NestedClass.class.getName());
    
    assertThat(snc,notNull());
    assertThat(snc.getName(),isEqual(NestedClass.class.getName()));
    
    SoftClass nested = new SoftClass(snc);
    
    assertThat(nested.getName(),isEqual(NestedClass.class.getName()));    
    assertThat(nested.getSoftMethod("void aMethod()"),notNull());    
  }
  
  public void testCanCreateSoftClassFromSoftNestedClassForLocal() {
    NestedSoftClass snc = mySc.getNestedSoftClass(local.getName());
    
    assertThat(snc,notNull());
    assertThat(snc.getName(),isEqual(local.getName()));
    
    SoftClass nested = new SoftClass(snc);
    
    assertThat(nested.getName(),isEqual(local.getName()));    
    assertThat(nested.getSoftMethod("void anotherMethod()"),notNull());    
  }

}
