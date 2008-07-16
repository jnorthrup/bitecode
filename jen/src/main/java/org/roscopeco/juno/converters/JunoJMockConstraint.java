/* JunoJMockConstraint.java - Juno - JMock bridge
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/19 18:27:35 $
 * Originated: 13-Jul-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package org.roscopeco.juno.converters;

/** 
 * Allows Juno constraints to be used as <a href='http://jmock.codehaus.org'>
 * JMock</a>-compatible constraints, for use within the mock generation and
 * expectation framework.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/19 18:27:35 $
 */
public class JunoJMockConstraint implements org.jmock.core.Constraint {
  final org.roscopeco.juno.Constraint delegate;
  
  JunoJMockConstraint(org.roscopeco.juno.Constraint delegate) {
    this.delegate = delegate;
  }
  
  public boolean eval(Object arg) {
    return delegate.eval(arg);
  }
  
  public StringBuffer describeTo(StringBuffer arg) {
    StringBuilder sb = new StringBuilder();
    delegate.describe(sb);
    return arg.append(sb);
  }
  
  public String toString() {
    return describeTo(new StringBuffer()).toString();
  }  
}