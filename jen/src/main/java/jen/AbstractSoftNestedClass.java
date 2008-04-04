/* AbstractSoftNestedClass.java - Base NestedSoftClass implementation
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/17 23:34:51 $
 * Originated: 07-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import org.objectweb.asm.ClassVisitor;

import static jen.SoftUtils.binaryToJava;
import static jen.SoftUtils.javaToBinary;

/** 
 * Base-class for {@link NestedSoftClass} implementations.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/17 23:34:51 $ 
 */
public abstract class AbstractSoftNestedClass extends AbstractSoftMember implements NestedSoftClass
{
  private final String inner;
  private final String outer;
  
  /**
   * Create a new static nested class on the specified outer {@link SoftClass}.
   * 
   * @param outer The {@link SoftClass} upon which this nested class reference is generated.
   * @param modifiers The access modifiers (<code>ACC_XXXX</code> constants) for this nested
   *        class.
   * @param name The full language name of this nested class, in the format 
   *        <code>package.OuterName$InnerName</code>. 
   * @param outerName The outer part of the class name, in the format 
   *        <code>package.OuterName</code>. 
   * @param innerName The inner part of the class name, in the format 
   *        <code>InnerName</code>. 
   */
  protected AbstractSoftNestedClass(SoftClass outer, int modifiers, String name, String outerName, String innerName) {
    super(SoftUtils.nullSafeArg(AbstractSoftNestedClass.class,"outer",outer),modifiers,javaToBinary(name),null);
    this.inner = javaToBinary(innerName);
    this.outer = javaToBinary(outerName);
  }
  
  /**
   * Create a new local inner class on the specified outer {@link SoftClass}.
   * 
   * @param outer The {@link SoftClass} upon which this nested class reference is generated.
   * @param modifiers The access modifiers (<code>ACC_XXXX</code> constants) for this nested
   *        class.
   * @param name The full language name of this local class, in the format 
   *        <code>package.OuterName$1InnerName</code>. 
   * @param innerName The inner part of the class name, in the format 
   *        <code>InnerName</code>. 
   */
  protected AbstractSoftNestedClass(SoftClass outer, int modifiers, String name, String innerName) {
    this(outer,modifiers,name,null,innerName);
  }
  
  /**
   * Create a new anonymous inner class on the specified outer {@link SoftClass}.
   * 
   * @param outer The {@link SoftClass} upon which this nested class reference is generated.
   * @param modifiers The access modifiers (<code>ACC_XXXX</code> constants) for this nested
   *        class.
   * @param name The full language name of this nested class, in the format 
   *        <code>package.OuterName$1</code>. 
   */
  protected AbstractSoftNestedClass(SoftClass outer, int modifiers, String name) {
    this(outer,modifiers,name,null);
  }
  
  public String getName() {
    return binaryToJava(getInternalName());
  }

  public String getInternalName() {
    return super.getName();
  }
  
  public String getInnerName() {
    return binaryToJava(inner);    
  }
  
  public String getOuterName() {
    return binaryToJava(outer);    
  }
  
  public abstract void accept(ClassVisitor vis);
}
