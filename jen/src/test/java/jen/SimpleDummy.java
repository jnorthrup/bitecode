/* SimpleDummy.java - Awaiting description
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:29 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * <strong>DO NOT MODIFY THIS CLASS FOR FURTHER TESTS</strong> - It's purpose
 * is specifically to present known metrics for validation of SoftClass methods.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:29 $ 
 */
public class SimpleDummy
{
  private static Object refField;
  public int primField;
  
  public SimpleDummy() { }
  public SimpleDummy(boolean dummy) throws ClassNotFoundException, InstantiationException { }
  public SimpleDummy(String dummy, String arg2) { }
  private SimpleDummy(String dummy) { }
  
  public static void voidMethod() { }  
  public void throwsMethod() throws ClassNotFoundException, InstantiationException { 
    throw new ClassNotFoundException(); 
  }  
  public String argsMethod(String arg1, String arg2) { return ""+arg1+arg2; }
  private String privateMethod() { return null; }
  
  // for reflectInvoke
  protected String protectedMethod() {
    return "TEST";
  }
}
