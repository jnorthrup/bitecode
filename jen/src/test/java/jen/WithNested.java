/* WithNested.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:28 $
 * Originated: 06-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * Not yet documented.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:28 $ 
 */
public class WithNested
{
  public static final class NestedClass {
    public String toString() { return "NestedClass"; }
  }

  public class InnerClass {
    public String toString() { return "InnerClass"; }
  }

  public WithNested() {
    super();
  }
  
  public Object withLocal() {
    class LocalClass {
      public String toString() { return "LocalClass"; }
    }
    return new LocalClass();
  }
}
