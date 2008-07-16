/* StaticMixinMethod.java - Supports static 'mixins'
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/17 23:34:53 $
 * Originated: Oct 17, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import jen.SoftClass;

import static jen.SoftUtils.typeForClassArray;
import static jen.SoftUtils.nullSafeArg;

/** 
 * Allows static 'mixin' methods to be applied to a generated {@code SoftClass}.
 * This is a simple {@link SoftMethod} implementation that simply generates
 * an appropriate instance method for the specified (static) mixin method.
 * <p/>
 * The {@link Method} supplied to this class at instantiation must be  
 * {@code static}, with at least one parameter (which must be of reference type).
 * The generated mixin will copy all parameters except this first one, which 
 * will be used to pass the calling instance to the static method.
 * At present only loose checking on the argument and return types - it is
 * up to you to ensure that mixins are compatible with the class to which
 * they are applied.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/17 23:34:53 $ 
 */
public class StaticMixinMethod extends GeneratedSoftMethod
{
  final String mixinName;
  final Type mixinClass;
  final org.objectweb.asm.commons.Method mixinMethod;
  
  private static Class[] argArrayHelper(Class[] args) {
    if (args == null || args.length < 1 || args[0].getClass().isPrimitive()) {
      throw (new IllegalArgumentException("Mixin methods must have a reference argument at position 0"));      
    }
    
    Class[] newArgs = new Class[args.length - 1];
    System.arraycopy(args,1,newArgs,0,args.length-1);
    return newArgs;
  }
  
  /**
   * Create a new {@code StaticMixinMethod} that will treat the specified 
   * {@link Method} as a static 'mixin' method.
   * The name and access modifiers will be the same as those for the 
   * supplied static method. 
   * 
   * @param sc The {@link SoftClass} upon which this mixin method will be 
   *        generated.
   * @param method The {@code java.lang.reflect.Method} representing the static
   *               mixin method to generate.
   *               
   * @throws IllegalArgumentException if the specified {@code Method} is not
   *         suitable for use as a mixin method (see the 
   *         {@link StaticMixinMethod class description}).               
   */
  public StaticMixinMethod(SoftClass sc, Method method) {
    this(sc,method,method.getName());
  }
  
  /**
   * Create a new {@code StaticMixinMethod} that will treat the specified 
   * {@link Method} as a static 'mixin' method. 
   * When generating the dynamic method, the specified name will be used,
   * and access modifiers copied from the mixin method (minus the 
   * {@code ACC_STATIC} flag).
   * 
   * @param sc The {@link SoftClass} upon which this mixin method will be 
   *        generated.
   * @param method The {@code java.lang.reflect.Method} representing the static
   *               mixin method to generate.
   * @param mixinName The name for the new mixin method.
   *               
   * @throws IllegalArgumentException if the specified {@code Method} is not
   *         suitable for use as a mixin method (see the 
   *         {@link StaticMixinMethod class description}).               
   */
  public StaticMixinMethod(SoftClass sc, Method method, String mixinName) {
    this(sc,method,method.getModifiers(),mixinName);
  }
  
  /**
   * Create a new {@code StaticMixinMethod} that will treat the specified 
   * {@link Method} as a static 'mixin' method. 
   * When generating the dynamic method, the specified access modifiers will
   * be used, and the method name copied from the mixin method (minus the 
   * {@code ACC_STATIC} flag).
   * 
   * @param sc The {@link SoftClass} upon which this mixin method will be 
   *        generated.
   * @param method The {@code java.lang.reflect.Method} representing the static
   *               mixin method to generate.
   * @param modifiers The JVM access nodifiers to apply to the new mixin method.
   *               
   * @throws IllegalArgumentException if the specified {@code Method} is not
   *         suitable for use as a mixin method (see the 
   *         {@link StaticMixinMethod class description}).               
   */
  public StaticMixinMethod(SoftClass sc, Method method, int modifiers) {
    this(sc,method,modifiers,method.getName());
  }
  
  /**
   * Create a new {@code StaticMixinMethod} that will treat the specified 
   * {@link Method} as a static 'mixin' method. 
   * When generating the dynamic method, the specified name and modifiers will be
   * used.
   * <p/>
   * <strong>Note</strong> that the static bit will be unset if set.
   * 
   * @param sc The {@link SoftClass} upon which this mixin method will be 
   *        generated.
   * @param method The {@code java.lang.reflect.Method} representing the static
   *               mixin method to generate.
   * @param modifiers The JVM access nodifiers to apply to the new mixin method.
   * @param mixinName The name for the new mixin method.
   *               
   * @throws IllegalArgumentException if the specified {@code Method} is not
   *         suitable for use as a mixin method (see the 
   *         {@link StaticMixinMethod class description}).               
   */
  public StaticMixinMethod(SoftClass sc, Method method, int modifiers, String mixinName) {
    super(sc,
          (modifiers & ACC_STATIC) != 0 ? modifiers ^ ACC_STATIC : modifiers,
          nullSafeArg(StaticMixinMethod.class,"mixinName",mixinName),
          // FIXME we should have a SoftUtils util method for the binary signature...
          new CopiedJavaMethod(sc,method).getSignature(),
          argArrayHelper(nullSafeArg(StaticMixinMethod.class,"mixinMethod",method).getParameterTypes()),
          method.getReturnType(),
          method.getExceptionTypes());
  
    if (((method.getModifiers() & ACC_STATIC) == 0) || ((method.getModifiers() & ACC_ABSTRACT) != 0)) 
      throw(new IllegalArgumentException("Mixin method '"+method+"' must be static, non abstract"));
    
    this.mixinName = mixinName;
    this.mixinClass = Type.getType(method.getDeclaringClass());
    this.mixinMethod = new org.objectweb.asm.commons.Method(method.getName(),
        Type.getType(method.getReturnType()), typeForClassArray(method.getParameterTypes()));
  }
  
  @Override
  protected void generateCode(GeneratorAdapter gen) {
    gen.loadThis();
    gen.loadArgs();
    gen.invokeStatic(mixinClass,mixinMethod);
    gen.returnValue();
  }
}
