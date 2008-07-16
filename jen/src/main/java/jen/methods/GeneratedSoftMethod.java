/* GeneratedSoftMethod.java - Useful base-class for generated methods
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
 * File version: $Revision: 1.6 $ $Date: 2005/10/19 14:43:35 $
 * Originated: 08-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import jen.AbstractSoftMethod;
import jen.SoftClass;

import static java.util.Arrays.asList;

/** 
 * Base-class for generated {@link jen.SoftMethod} implementations. This
 * class provides a convenient wrapper around {@link AbstractSoftMethod}
 * that will automatically create an ASM {@link GeneratorAdapter} based
 * on the method parameters and pass it to the 
 * {@link #generateCode(GeneratorAdapter)} method.
 * <p/>
 * <strong>Note</strong> that both <code>visitCode</code> and 
 * <code>visitEnd</code> <strong>are</strong> called by this class at
 * the appropriate time. You must still call <code>visitMaxs</code> 
 * with appropriate arguments (see below). Failure to do so will 
 * result in verification failures.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.6 $ $Date: 2005/10/19 14:43:35 $ 
 */
public class GeneratedSoftMethod extends AbstractSoftMethod
{
  private static <T extends Object> List<T> nullSafeAsList(T[] t) {
    if (t == null) return null;
    return asList(t);    
  }
  
  /* **********************************
   * CONVENIENCE CTORS
   */
  /**
   * Create a new void no-arg <code>GeneratedSoftMethod</code> with the 
   * specified name and access modifiers.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers for the method.
   * @param name The name for the method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name) {
    this(softClass, access, name, void.class);
  }
    
  /**
   * Create a new no-arg <code>GeneratedSoftMethod</code> with the specified 
   * parameters.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers for this method this class.
   * @param name The name for the method.
   * @param returnType Return type of this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name, Class returnType) {
    this(softClass, access, name, (Class)null, returnType);
  }
  
  /**
   * Create a new single-argument <code>GeneratedSoftMethod</code> with 
   * the specified parameters. No generic signature may be specified.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers for the method.
   * @param name The name for the method.
   * @param singleArgType Class of the single argument to this method.
   * @param returnType Return type of this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      Class singleArgType, Class returnType) {
    this(softClass, access, name, null, singleArgType, returnType);
  }
  
  /**
   * Create a new single-argument <code>GeneratedSoftMethod</code> with 
   * the specified parameters. This is a convenience constructor.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers for the method.
   * @param name The name for the method.
   * @param signature The generic signature for this method, if any.
   * @param singleArgType Class of the single argument to this method.
   * @param returnType Return type of this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      String signature, Class singleArgType, Class returnType) {
    this(softClass, access, name, signature, (singleArgType != null) ? new Class[] { singleArgType } : null, 
        returnType);
  }
  
  /**
   * Create a new <code>GeneratedSoftMethod</code> with the specified 
   * parameters. This version accepts {@link java.lang.Class} types,
   * does not generate a <code>throws</code> clause, and accepts no
   * generics information.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers from this class.
   * @param name The name for the method.
   * @param argumentTypes Argument-type array for this method.
   * @param returnType Return type of this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      Class[] argumentTypes, Class returnType) {
    this(softClass, access, name, null, argumentTypes, returnType);
  }
  
  /**
   * Create a new <code>GeneratedSoftMethod</code> with the specified 
   * parameters. This version accepts {@link java.lang.Class} types
   * and does not generate a <code>throws</code> clause.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers from this class.
   * @param name The name for the method.
   * @param signature The generic signature for this method, if any.
   * @param argumentTypes Argument-type array for this method.
   * @param returnType Return type of this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      String signature, Class[] argumentTypes, Class returnType) {
    this(softClass, access, name, signature, argumentTypes, returnType, null);
  }
  
  /**
   * Create a new <code>GeneratedSoftMethod</code> with the specified 
   * parameters. This version accepts {@link org.objectweb.asm.Type} types.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers from this class.
   * @param name The name for the method.
   * @param signature The generic signature for this method, if any.
   * @param argumentTypes Argument-type array for this method.
   * @param returnType Return type of this method.
   * @param throwsTypes Exception types thrown by this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      String signature, Type[] argumentTypes, Type returnType,
      Type[] throwsTypes) {
    this(softClass,access,name,signature,nullSafeAsList(argumentTypes),returnType,nullSafeAsList(throwsTypes));    
  }
      
  /**
   * Create a new <code>GeneratedSoftMethod</code> with the specified 
   * parameters. This version accepts {@link java.lang.Class} types.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers from this class.
   * @param name The name for the method.
   * @param signature The generic signature for this method, if any.
   * @param argumentTypes Argument-type array for this method.
   * @param returnType Return type of this method.
   * @param throwsTypes Exception types thrown by this method.
   */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      String signature, Class<?>[] argumentTypes, Class returnType,
      Class<?>[] throwsTypes) {
    this(softClass,access,name,signature,nullSafeAsList(argumentTypes),returnType,nullSafeAsList(throwsTypes));    
  }
  
  /* **********************************
   * WORKER CTORS
   */
  /**
   * Create a new <code>GeneratedSoftMethod</code> that will belong to the 
   * specified subclass. Further information <strong>must</strong> be 
   * supplied before this method can be generated.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   */
  protected GeneratedSoftMethod(SoftClass softClass) {
    super(softClass);
  }

  /**
   * Create a new <code>GeneratedSoftMethod</code> with the specified 
   * parameters. This version accepts {@link org.objectweb.asm.Type} types.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers from this class.
   * @param name The name for the method.
   * @param signature The generic signature for this method, if any.
   * @param argumentTypes Argument-type list for this method.
   * @param returnType Return type of this method.
   * @param throwsTypes Exception types thrown by this method.
   */
  /* WORKER 1 */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      String signature, List<Type> argumentTypes, Type returnType,
      List<Type> throwsTypes) {
    super(softClass, access, name, signature, argumentTypes, returnType,
        throwsTypes);
  }

  /**
   * Create a new <code>GeneratedSoftMethod</code> with the specified 
   * parameters. This version accepts {@link java.lang.Class} types.
   * 
   * @param softClass The {@link SoftClass} this method is declared upon.
   * @param access The access modifiers from this class.
   * @param name The name for the method.
   * @param signature The generic signature for this method, if any.
   * @param argumentTypes Argument-type array for this method.
   * @param returnType Return type of this method.
   * @param throwsTypes Exception types thrown by this method.
   */
  /* WORKER 2 */
  public GeneratedSoftMethod(SoftClass softClass, int access, String name,
      String signature, List<Class<?>> argumentTypes, Class returnType,
      List<Class<?>> throwsTypes) {
    super(softClass, access, name, signature, argumentTypes, returnType,
        throwsTypes);
  }
  
  
  /* **********************************
   * INSTANCE METHODS
   */
  /**
   * {@inheritDoc}
   * <p/>
   * This implementation simply creates a new {@link GeneratorAdapter} with the
   * appropriate name and signature, before passing it into the 
   * {@link #generateCode(GeneratorAdapter)} method.
   * <p/>
   * <strong>The implementation is responsible for ending the visit, either by
   * calling {@link GeneratorAdapter#endMethod()}, or handling the maxs and
   * calling {@link ClassVisitor#visitEnd()} on the adapter (if using it as a
   * standard method visitor.
   */
  public void accept(ClassVisitor vis) {
    List<Type> tl = getThrowsTypes();    
    GeneratorAdapter a = new GeneratorAdapter(getModifiers(),new Method(getName(),getDescriptor()),getSignature(),
        (tl != null) ? tl.toArray(new Type[tl.size()]) : null, vis);
    generateCode(a);
  }
  
  /**
   * Called to generate the code after a method node has been created with this 
   * SoftMember's information. This default implementation generates a method
   * that simply returns (if <code>void</code>) or return <code>null</code>,
   * and calls {@link ClassVisitor#visitEnd()}.
   * <p/>
   * The {@link GeneratorAdapter} used by this method is a specialised 
   * {@link ClassVisitor} implementation that, as well as supporting generation
   * via the standard <code>visitXXXX</code> method, provides a higher-level
   * API for bytecode generation.
   * <p/>
   * Subclasses <strong>must</strong> ensure they terminate the visit 
   * appropriately when overriding this method, either by visiting maxs and
   * calling {@link ClassVisitor#visitEnd} if doing 'traditional' generation,
   * or using {@link GeneratorAdapter#endMethod()} otherwise.
   * 
   * @param gen An ASM {@link GeneratorAdapter} to which the method should be 
   *        generated.
   */
  protected void generateCode(GeneratorAdapter gen) {
    gen.visitCode();
    if (getReturnType() == Type.VOID_TYPE) {
      gen.visitInsn(RETURN);      
    } else {
      gen.visitInsn(ACONST_NULL);
      gen.visitInsn(ARETURN);
    }
    gen.visitMaxs(0,0);   
    gen.visitEnd();
  }
  
}
