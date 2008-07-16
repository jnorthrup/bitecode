/* MockMethod.java - Individual mock method.
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/08 01:23:25 $
 * Originated: Oct 6, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/** 
 * Represents an individual method on a mock invocation handler. You must
 * supply an implementation of this interface for each mock method you wish
 * to customise. When searching for an appropriate method, each {@code MockMethod}
 * is queried in turn, and the first that indicates it can handle the invocation
 * is called upon to do so.
 * <p/>
 * Any methods on the proxy that do not have a matching MockMethod will be handled
 * by the <em>default MockMethod</em>, which simply returns <code>null</code>.
 * <strong>Note that this includes both equals and hashCode at present</strong>, so
 * you <strong>must</strong> provide stubs for them if you plan to use your mocks
 * as collection keys, for example.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/08 01:23:25 $ 
 */
public interface MockMethod extends InvocationHandler
{  
  /**
   * Called prior to {@link #invoke(Object, Method, Object[])} to determine whether
   * this {@code MockMethod} can handle the specified invocation.
   * 
   * @param proxy The proxy instance upon which the method was invoked.
   * @param m The real (proxied) method for which the invoked proxy method was
   *        created.
   * @param args The actual arguments passed to the method, with primitives
   *        wrapped by the appropriate wrapper type.
   */
  public boolean invocationMatches(Object proxy, Method m, Object[] args);
  
  /**
   * Handle an invocation of this mock method. This method is specified by 
   * {@link InvocationHandler} and behaves as defined there.
   * 
   * @param proxy The proxy instance upon which the method was invoked.
   * @param m The real (proxied) method for information or forward invocation.
   * @param args The actual arguments passed to the method, with primitives
   *        wrapped by the appropriate wrapper type.
   */
  public Object invoke(Object proxy, Method m, Object[] args) throws Throwable;
}
