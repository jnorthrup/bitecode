/* MalformedMemberException.java - Who put that there?
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
 * File version: $Revision: 1.1 $ $Date: 2005/09/14 10:35:38 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * Thrown to indicate that a {@link SoftClass}'s internal member map has 
 * become corrupted, usually as a result of directly registering a SoftMethod
 * with a field key, for example.
 * <p/>
 * This exception exists to provide an escape for use at any point during 
 * the life of the instance, in order to provide the cleanest failure 
 * semantics when such a problem is first discovered. Since any generated
 * class would most likely be corrupt with such a member map, it is 
 * desirable to prevent generation from proceeding in these circumstances.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/09/14 10:35:38 $ 
 */
public class MalformedMemberException extends RuntimeException
{
  private final Object key;
  private final Class expectedType;
  private final Object actual;

  /**
   * Create a new <code>MalformedMemberException</code> with the specified arguments.
   * 
   * @param key The internal member key that was searched.
   * @param expectedType The {@link SoftMember} subclass that was expected.
   * @param actual The actual {@link SoftMember} that was found.
   */
  public MalformedMemberException(Object key, Class expectedType, Object actual) {
    this.key = key;
    this.expectedType = expectedType;
    this.actual = actual;
  }
  
  public Object getActual() {
    return actual;
  }

  public Class getExpectedType() {
    return expectedType;
  }

  public Object getKey() {
    return key;
  }

  public String getMessage() {
    return "key "+key+": expected instance of "+expectedType.getName()+", got "+actual;
  }
}
