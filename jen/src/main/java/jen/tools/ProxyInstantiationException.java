/* ProxyInstantiationException.java - newProxyInstance prob
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/31 02:43:00 $
 * Originated: Oct 22, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

/** 
 * Thrown to indicate an exception during instantiation of a proxy class via
 * the {@link SoftProxy#newProxyInstance} method. This exception is an unchecked
 * (runtime) exception, since it applies specifically to the case where a proxy
 * has been directly generated, and it's throwing will often indicate an
 * erroneous set of constructor arguments or other exception from within the
 * constructor.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/31 02:43:00 $ 
 */
public class ProxyInstantiationException extends RuntimeException
{
  public ProxyInstantiationException() {
    super();
  }

  public ProxyInstantiationException(String message) {
    super(message);
  }

  public ProxyInstantiationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProxyInstantiationException(Throwable cause) {
    super(cause);
  }
}
