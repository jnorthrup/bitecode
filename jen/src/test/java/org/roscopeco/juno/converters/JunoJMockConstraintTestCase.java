/* JunoJMockConstraintTestCase.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:33 $
 * Originated: 13-Jul-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno.converters;

import org.roscopeco.juno.converters.JunoJMockConstraint;
import org.roscopeco.juno.pre50.JunoTestCase;

public class JunoJMockConstraintTestCase extends JunoTestCase
{
  final org.roscopeco.juno.Constraint testCons = isFalse();
  
  public void testJunoJMockConstraint() {
    JunoJMockConstraint t = new JunoJMockConstraint(testCons);
    assertThat(t.delegate,isSame(testCons));
  }

  public void testEval() {
    JunoJMockConstraint t = new JunoJMockConstraint(testCons);
    assertThat(new Boolean(t.eval(new Boolean(false))),isTrue());
    assertThat(new Boolean(t.eval(new Boolean(true))),not(isTrue()));
  }

  public void testDescribeTo() {
    StringBuffer sb = new StringBuffer();
    JunoJMockConstraint t = new JunoJMockConstraint(testCons);
    t.describeTo(sb);
    assertThat(sb.toString(),not(isNull()));
    assertThat(sb.toString(),isEqual("not true"));
    assertThat(sb.toString(),isEqual(t.toString()));
  }

}
