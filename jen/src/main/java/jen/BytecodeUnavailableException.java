/* BytecodeUnavailableException.java - Bytecode can't be found
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/13 16:18:11 $
 * Originated: 03-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import static jen.SoftUtils.nullSafeArg;

/** 
 * Thrown to indicate that the Java bytecode for a given class is not 
 * available. Typically this will be thrown by one of the {@link SoftClass}
 * constructors when instantiated with a {@link java.lang.Class} that is 
 * either generated, synthetic, or non-local.
 * 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/13 16:18:11 $ 
 */
public class BytecodeUnavailableException extends RuntimeException
{
  private final String javaClassName;
  private final String msg;
  
  /**
   * Create an instance of BytecodeUnavailableException for the specified Java
   * class.
   * 
   * @param javaClass The problematic {@link java.lang.Class}.
   */
  public BytecodeUnavailableException(Class javaClass) {
    this(javaClass.getName());
  }
  
  /**
   * Create an instance of BytecodeUnavailableException for the specified Java
   * class name.
   * 
   * @param javaClass The problematic {@link java.lang.Class}.
   */
  public BytecodeUnavailableException(String javaClassName) {
    this.javaClassName = nullSafeArg(BytecodeUnavailableException.class,"javaClassName",javaClassName); 
    this.msg = "Bytecode unavailable for SoftClass initialization: "+javaClassName;
  }
  
  /**
   * Retrieve the Java Class for which bytecode was unavailable.
   * 
   * @return The problematic {@link java.lang.Class}.
   * 
   * @deprecated The class is no longer held internally, so this method will attempt
   *             to find the class by name (using {@link Class#forName(java.lang.String) Class.forName}).
   *             If the class is not found, this method returns {@code null}.
   *             The {@link #getJavaClassName()} method should be used in preference to this one.
   */
  public Class getJavaClass() {
    try {
      return Class.forName(javaClassName);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
  
  /**
   * Retrieve the language name of the Java Class for which bytecode was unavailable.
   * 
   * @return The problematic class' fully qualified language name.
   */
  public String getJavaClassName() {
    return javaClassName;
  }
  
  /**
   * Obtain a descriptive message for this {@code BytecodeUnavailableException}, in 
   * the form <code>Bytecode unavailable for SoftClass initialization: <em>javaClassName</em></code>.
   * 
   * @return A descriptive message for this exception.
   */
  public String toString() {
    return msg;
  }
}
