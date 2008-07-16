/* NewClassTestCase.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:27 $
 * Originated: 13-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.Opcodes;

import junit.framework.TestCase;

public class NewClassTestCase extends TestCase
{
  public void testCanCreateNewClass() throws Exception {
    SoftClass sc = new SoftClass(Opcodes.ACC_PUBLIC,"jen.tests.BrandSpankingNewClass","java.lang.Object");
    
    // We need a constructor. It'll just call through to <init>() on the super
    SoftMethod m = new ExampleNullConstructor(sc,Opcodes.ACC_PUBLIC,null,true);
    sc.putSoftMethod(m);
    
    Class<?> newClazz = sc.defineClass();
    newClazz.newInstance();   
  }
}
