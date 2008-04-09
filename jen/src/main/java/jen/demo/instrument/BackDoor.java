/* BackDoor.java - Secret backdoor interface
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/01 10:48:20 $
 * Originated: 23-Jun-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.demo.instrument;

/** 
 * Secret backdoor interface that gets applied to classes loaded.
 * This must be loaded in the Boot classloader.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/01 10:48:20 $ 
 */
public interface BackDoor
{
  public int getObjectId();  
  public void setObjectId(int newValue);
}
