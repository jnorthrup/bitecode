/* TestProxyClass.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/05 10:56:33 $
 * Originated: Oct 3, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

/** 
 * Not yet documented.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/05 10:56:33 $ 
 */
public class TestProxyClass
{
  public String basicMethod() {
    return "TEST METHOD";
  }
  
  public String argsMethod(int arg1, Class arg2) {
    return ""+arg1+" "+arg2;
  }
  
  //public void throwsMethod() throws IOException {
  //  throw(new IOException());
  //}  
}
