/* JUnitSupport.java - Static import support for JUnit.
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:36 $
 * Originated: Oct 19, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno.converters;

import junit.framework.AssertionFailedError;

import org.roscopeco.juno.Constraint;

/** 
 * JUnit-compatible <code>assertThat</code> methods for use with
 * {@code import static} in unit tests. If you are using writing for a
 * pre-1.5 target, use {@link org.roscopeco.juno.pre50.JunoTestCase}
 * instead.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:36 $ 
 */
public class JUnitSupport
{
  /* assertions */
  public static void assertThat(Object o, Constraint c) {
    assertThat(null,o,c);
  }
  
  public static void assertThat(String message, Object o, Constraint c) {
    if (!c.eval(o)) {
      String prefix = message != null ? message+" - " : "";      
      throw(new AssertionFailedError(prefix+"Expected: "+c.toString()+", Actual: "+(o == null ? "null" : c.valueOf(o).toString())));
    }
  }
}
