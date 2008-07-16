/* SuperConstructor.java - Passthru-to-super constructor
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/29 19:29:50 $
 * Originated: Oct 28, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import jen.SoftClass;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import static jen.SoftUtils.binaryToDescriptor;

/**
 * Simple pass-through-to-{@code super} constructor {@link SoftMethod} 
 * implementation. This class will generate a constructor with a 
 * specified signature that simply delegates the call to an inherited
 * constructor with that name. For example, an instance of this
 * class:
 * <p/>
 * <pre>    new SuperConstructor(mySoftClass,SoftClass.ACC_PUBLIC,Object.class,String.class,int.class);</pre>
 * <p/>
 * Would cause the (equivalent of) the following to be generated:
 * <p/>
 * <pre>    public MyGeneratedClass(Object o, String s, int i) {
 *       super(o,s,i);
 *     }</pre>
 * <p/>
 * As with all {@code SoftMember} implementations, this does not (and
 * cannot reasonably) check that the super constructor will exist when the
 * class is generated - if it does not the class will fail verification.
 * 
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/29 19:29:50 $ 
 */
public final class SuperConstructor extends GeneratedSoftMethod 
{
  /**
   * Create a new {@code SuperConstructor} that will generate a constructor
   * with (and delegating to) a constructor with the specified declared
   * argument types. The constructor will be declared {@code public}. 
   *
   * @param sc        the soft class this method is added to
   * @param pArgTypes the argument types of this constructor
   */
  public SuperConstructor(SoftClass sc, Class... pArgTypes) {
    this(sc, ACC_PUBLIC, pArgTypes);
  }

  /**
   * Create a new {@code SuperConstructor} that will generate a constructor
   * with (and delegating to) a constructor with the specified declared
   * argument types. The generated constructor will have the specified
   * modifiers (a bitmap of the {@code SoftClass.ACC_XXXX} constants).
   *
   * @param sc        the soft class this method is added to.
   * @param modifiers the modifiers for this constructor.
   * @param pArgTypes the argument types of this constructor.
   */
  /* TYPE WORKER */
  public SuperConstructor(SoftClass sc, int modifiers, Type... pArgTypes) {
    super(sc, modifiers, "<init>", (String)null, pArgTypes, Type.VOID_TYPE, (Type[])null);
  }
  
  /**
   * Create a new {@code SuperConstructor} that will generate a constructor
   * with (and delegating to) a constructor with the specified declared
   * argument types. The generated constructor will have the specified
   * modifiers (a bitmap of the {@code SoftClass.ACC_XXXX} constants).
   *
   * @param sc        the soft class this method is added to.
   * @param modifiers the modifiers for this constructor.
   * @param pArgTypes the argument types of this constructor.
   */
  /* CLASS WORKER */
  public SuperConstructor(SoftClass sc, int modifiers, Class... pArgTypes) {
    super(sc, modifiers, "<init>", pArgTypes, void.class);
  }

  /**
   * Called during {@link SoftClass} generation to generate this constructor.
   *
   * @param gen the {@link GeneratorAdapter} to which generation is taking 
   *            place.
   */
  protected void generateCode(GeneratorAdapter gen) {
    gen.loadThis();
    gen.dup();
    StringBuffer xSignatureType = new StringBuffer("void <init> (");
    for (int i = 0; i < getArgumentTypes().size(); i++) {
      gen.loadArg(i);
      Type xType = getArgumentTypes().get(i);
      xSignatureType.append(xType.getClassName());
      if (i < getArgumentTypes().size() - 1) {
        xSignatureType.append(",");
      }
    }
    xSignatureType.append(")");
    gen.invokeConstructor(Type.getType(binaryToDescriptor(getSoftClass().getSuperClassInternalName())),
        org.objectweb.asm.commons.Method.getMethod(xSignatureType.toString())); 
    
    gen.returnValue();
    gen.endMethod();
  }    
}