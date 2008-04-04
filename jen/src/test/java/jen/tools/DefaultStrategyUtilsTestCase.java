/* DefaultStrategyUtilsTestCase.java - Awaiting description
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/20 22:34:42 $
 * Originated: Oct 9, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import jen.tools.SoftProxyTestCase.ArgInvHandler;

import static org.roscopeco.juno.Constraints.isFalse;
import static org.roscopeco.juno.Constraints.isTrue;
import static org.roscopeco.juno.pre50.JunoTestCase.assertThat;

import junit.framework.TestCase;

public class DefaultStrategyUtilsTestCase extends TestCase
{
  public void testIsDefaultProxyClass() {
    Class<TestProxyInterface> tifc = SoftProxy.createProxyClass(TestProxyInterface.class);    
    assertThat(DefaultInvokerStrategy.isDefaultProxyClass(tifc),isTrue());
    assertThat(DefaultInvokerStrategy.isDefaultProxyClass(getClass()),isFalse());

    // TODO test with a proxy with another strategy!!!
  }
  
  public void testIsProxy() {
    TestProxyInterface tif = 
      SoftProxy.newProxyInstance(TestProxyInterface.class, new ArgInvHandler());
    
    assertThat(DefaultInvokerStrategy.isDefaultProxy(tif),isTrue());
    assertThat(DefaultInvokerStrategy.isDefaultProxy(this),isFalse());
    
    // TODO test with a proxy with another strategy!!!
  }
}
