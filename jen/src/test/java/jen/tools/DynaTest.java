/* DynaTest.java - Awaiting description
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/30 23:47:46 $
 * Originated: Oct 27, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

/** 
 * Not yet documented.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/30 23:47:46 $ 
 */
public class DynaTest
{  
  @NoDispatch
  private String allArgs(Object... args) {
    StringBuilder b = null;    
    for (Object o : args) {
      Class<?> c = null;
      c = o == null ? Object.class : o.getClass();      
      b = b == null ? new StringBuilder().append(c.getSimpleName()) : b.append(", ").append(c.getSimpleName());
    }    
    return b.toString();    
  }
  
  public String overLoadOne(Integer i, String s) {
    return "(Integer, String) : " + allArgs(i,s);
  }
  
  public String overLoadOne(Long i, String s) {
    return "(Long, String) : " + allArgs(i,s);
  }
  
  public String overLoadOne(Number n, CharSequence c) {
    return "(Number, CharSequence) : " + allArgs(n,c);
  }
  
  public String overLoadOne(Integer i, Object o) {
    return "(Integer, Object) : " + allArgs(i,o);
  }
    
  public String overLoadOne(Object s, CharSequence o) {
    return "(Object, CharSequence) : " + allArgs(s,o);
  }
  
  public String overLoadOne(Object o, String s) {
    return "(Object, String) : " + allArgs(o,s);
  }
  
  public String overLoadOne(String s, String o) {
    return "(String, String) : " + allArgs(s,o);
  }
  
  public String overLoadOne(String s, Object o, Number i) {
    return "(String, Object, Number) : " + allArgs(s,o,i);
  }  
  
  public String overLoadOne(Object s, Object o, Integer i) {
    return "(Object, Object, Integer) : " + allArgs(s,o,i);
  }  
  
  public String overLoadOne(Thread s, Object o, Number i) {
    return "(Thread, Object, Number) : " + allArgs(s,o,i);
  }  
  
  public String overLoadOne(Runnable r, Runnable o, Object i) {
    return "(Runnable, Runnable, Object) : " +  allArgs(r,o,i);
  }
  
  
  public String overLoadOne(boolean b, Runnable r, Object o, int i) {
    return "(boolean, Runnable, Object, int) : boolean, " +  allArgs(r,o) + ", int";    
  }
  
  
  /* **********************************
   * NEITHER OF THESE USE MULTI DISPATCH
   * 
   * These are convenience methods that allow us to simply
   * pull Objects from a collection (e.g.) and pass them
   * at compile time, while getting multi dispatch at 
   * runtime.
   */
  @NoDispatch
  public String overLoadOne(Object o, Object o2) {
    return "(Object, Object) : " + allArgs(o,o2);
  }
  
  @NoDispatch
  public String overLoadOne(Object o, Object o2, Object o3) {
    return "(Object, Object, Object) : " + allArgs(o,o2,o3);        
  }  
}
