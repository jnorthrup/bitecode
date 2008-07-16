/* NullArgumentException.java - Incorrect null arg
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/03 18:36:21 $
 * Originated: 04-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

/** 
 * Thrown to indicate that a method argument was illegally null.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/03 18:36:21 $ 
 */
public class NullArgumentException extends RuntimeException
{
  private final String arguments;
  private final String method;
  private final String className;
  
  /** 
   * Indicate illegal null arguments to a constructor.
   * 
   * @param clazz The (optional) Class upon which the constructor is declared.
   * @param arguments The name of the arguments that was illegally null.
   * 
   * @throws NullArgumentException If the supplied <code>arguments</code> is 
   *         <code>null</code>. 
   */
  public NullArgumentException(Class clazz, String arguments) {
    this(clazz.getName(),"<init>",arguments);
  }
  
  /** 
   * Indicate illegal null arguments to Java method.
   * 
   * @param clazz The (optional) Class upon which the constructor is declared.
   * @param method The name or signature of the method              
   * @param arguments The name of the arguments that was illegally null.
   * 
   * @throws NullArgumentException If the supplied <code>arguments</code> is 
   *         <code>null</code>. 
   */
  public NullArgumentException(Class clazz, String method, String arguments) {
    this(clazz.getName(),method,arguments);
  }
  
  /** 
   * Indicate illegal null arguments to named method.
   * 
   * @param className The (optional) name of the class upon which the constructor
   *              is declared.
   * @param method The name or signature of the method              
   * @param arguments The (required) name of the arguments that was illegally null.
   * 
   * @throws NullArgumentException If the supplied <code>arguments</code> is 
   *         <code>null</code>. 
   */
  public NullArgumentException(String className, String method, String arguments) {
    if (arguments == null) throw(new NullArgumentException(getClass(),"<init>","arguments"));
    this.arguments = arguments;
    this.method = method;
    this.className = className;
  }
  
  /**
   * Obtain the name of the class upon which the method that received the
   * illegal null arguments, if specified.
   * 
   * @return The declaring class name, or <code>null</code> if no class
   *         was specified.
   */
  public String getClassName() {
    return className;
  }

  /**
   * Obtain the name of the the method that received the illegal null 
   * arguments, if specified.
   * 
   * @return The method name, or <code>null</code> if no method name
   *         was specified.
   */
  public String getMethod() {
    return method;
  }

  /**
   * Obtain the names of the illegal null arguments that caused this exception.
   * 
   * @return The argument name(s). This will never return <code>null</code>.
   */
  public String getArguments() {
    return arguments;
  }
  
  public String getMessage() {
    return withSuffixOrNowt(
        withSuffixOrNowt(getClassName(),".")+withSuffixOrNowt(getMethod(),"."),":")+
        ((getArguments().indexOf(" ") > -1) || (getArguments().indexOf(",") > -1) ? 
            "Arguments '" : 
            "Argument '")+
          arguments+"' must be non-null'";
  }

  /* **********************************
   * HELPERS
   */
  private static String withSuffixOrNowt(String s, String suffix) {
    return ((s != null) && (s.length() > 0)) ? s+suffix : "";    
  }  
}
