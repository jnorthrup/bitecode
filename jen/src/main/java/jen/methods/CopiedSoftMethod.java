/* CopiedSoftMethod.java - 'Copied' SoftMethod
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
 * File version: $Revision: 1.3 $ $Date: 2005/10/18 01:28:44 $
 * Originated: 13-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import jen.AbstractReferenceSwitchingSoftMethod;
import jen.SoftClass;
import jen.SoftMethod;

import org.objectweb.asm.ClassVisitor;

/** 
 * Allows a {@link SoftMethod} to be 'copied' to another class. This is an
 * {@link AbstractReferenceSwitchingSoftMethod} implementation that delegates
 * generation to the specified <code>SoftMethod</code> <em>source</em>,
 * wrapping the {@link ClassVisitor} in a reference switching adapter.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/18 01:28:44 $ 
 */
public class CopiedSoftMethod extends AbstractReferenceSwitchingSoftMethod
{
  private final SoftMethod source;
  
  /**
   * Create a new CopiedSoftMethod that will generate the specified <code>source</code>
   * method on the specified {@link SoftClass}. When generated, all logical references
   * to <code>this</code> and <code>super</code> will be replaced with references 
   * appropriate for this {@link SoftClass}.
   * <p/>
   * <strong>Note</strong> that, beyond reference fixing, it is your responsibility to
   * ensure that the method <em>fits</em> into this <code>SoftClass</code>. If, for
   * example, the method refers to an instance field, <code>myField</code> of type
   * <code>java.lang.String</code>, then you must ensure a compatible field exists
   * on the generated class or it will fail verification. 
   * 
   * @param softClass The {@link SoftClass} this method will be generated on.
   * @param source The {@link SoftMethod} this method will be generated from.
   */
  public CopiedSoftMethod(SoftClass softClass, SoftMethod source) {
    this(softClass, source, source.getName());
  }

  /**
   * Create a new CopiedSoftMethod that will generate the specified <code>source</code>
   * method on the specified {@link SoftClass}. When generated, the method will be given
   * the specified name, and all logical references to <code>this</code> and 
   * <code>super</code> will be replaced with references appropriate for this {@link SoftClass}.
   * 
   * @param softClass The {@link SoftClass} this method will be generated on.
   * @param source The {@link SoftMethod} this method will be generated from.
   * @param newName The new name for the copied method.
   * 
   * @see #CopiedSoftMethod(SoftClass, SoftMethod)
   */
  public CopiedSoftMethod(SoftClass softClass, SoftMethod source, String newName) {
    this(softClass, source, source.getModifiers(), newName);    
  }

  /**
   * Create a new CopiedSoftMethod that will generate the specified <code>source</code>
   * method on the specified {@link SoftClass}. When generated, the method will be given
   * the specified name and access modifiers, and all logical references to <code>this</code> and 
   * <code>super</code> will be replaced with references appropriate for this {@link SoftClass}.
   * 
   * @param softClass The {@link SoftClass} this method will be generated on.
   * @param source The {@link SoftMethod} this method will be generated from.
   * @param newName The new name for the copied method.
   * @param modifiers The new access modifiers for the copied method.
   * 
   * @see #CopiedSoftMethod(SoftClass, SoftMethod)
   */
  public CopiedSoftMethod(SoftClass softClass, SoftMethod source, int modifiers, String newName) {
    super(softClass, modifiers, newName, source.getSignature(), 
        source.getArgumentTypes(), source.getReturnType(), source.getThrowsTypes());
    this.source = source;
    
    /* that's not the end of the matter as far as naming goes - the refswitchadapter
     * is also involved (to swap recursive INVOKExxx instructions) 
     */
  }
  
  public void accept(ClassVisitor v) {
    source.accept(switchingAdapter(v,
       source.getSoftClass().getName(),           getSoftClass().getName(),
       source.getSoftClass().getSuperClassName(), getSoftClass().getSuperClassName(),
       source.getName(),                          getName(),
       getModifiers()));    
  }
}
