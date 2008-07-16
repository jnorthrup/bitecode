/* CopiedJavaMethod.java - Copied compiled method
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
 * File version: $Revision: 1.5 $ $Date: 2005/10/23 02:15:20 $
 * Originated: Sep 25, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

import jen.AbstractSoftMethod;
import jen.SoftClass;
import jen.SoftMethod;

/** 
 * Convenience {@link CopiedSoftMethod} wrapper implementation that allows 
 * {@link Method}s to be copied, either from a specified method or from a 
 * compiled Java method declared on an (anonymous) subclass of this class. 
 * In other words, a copying <code>SoftMethod</code> that generates a 
 * specified (or selected - see below) {@link Method} on the generated 
 * {@link SoftClass}.
 * <p/>
 * As well as the ability to copy specified methods, this class allows the
 * method to be copied from it's own (virtual) class, a subclass of this one.
 * It also provides a convenience constructor that will select a method with 
 * a given {@link jen.tools.Toolbox#methodSignature informal signature} declared
 * on that subclass. The primary intended purpose of this style of usage is to 
 * allow anonymous subclasses to provide compiled Java language methods directly
 * for a {@code SoftClass}.   
 * <p/>
 * For example, you might copy an existing Method like so:
 * <br/>
 * <pre>  Method theMethod = clazz.getDeclaredMethod(...);
 *   SoftClass sc = new SoftClass(...);
 *   
 *   sc.putSoftMethod(new CopiedJavaMethod(sc,theMethod);</pre>
 * <br/>
 * Alternatively, you could supply an anonymous subclass upon which the method
 * is declared:
 * <br/>
 * <pre>  SoftClass sc = new SoftClass(...);
 *   
 *   sc.putSoftMethod(new CopiedJavaMethod(sc,"String toGenerate(String)") {
 *     public String toGenerate(String arg) {
 *       return arg + " - MyAPI";
 *     }
 *   });</pre>
 * <br/>
 * In many cases you will need to call existing members declared on the target
 * class. This can be achieved by declaring empty 'placeholder' members on the
 * source class:
 * <br/>
 * <pre>  SoftClass sc = new SoftClass(...);
 *   
 *   sc.putSoftMethod(new CopiedJavaMethod(sc,"String toGenerate(String)") {
 *     int anExistingField = 0;
 *     String anExistingMethod(String arg) { return null; }
 *     
 *     public String toGenerate(String arg) {
 *       return anExistingMethod(arg) + " - MyAPI";
 *     }
 *   });</pre>
 * <br/>
 * Only the method referenced by the CopiedJavaMethod constructor will be copied, 
 * and references to these 'placeholders' switched to reference the generated 
 * class. Their modifiers, (field) value and (method) behaviour is not important - 
 * only the name and type(s) are relevant.
 * <p/>
 * Generating methods with the second form is often convenient, but not 
 * particularly efficient. It should be avoided if you have large number of 
 * methods to generate, or if those methods require backing fields and 
 * methods on their definition classes, since memory requirements could easily
 * become excessive.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.5 $ $Date: 2005/10/23 02:15:20 $ 
 */
/* IMPL NOTE: This is implemented somewhat differently to the others. */
public class CopiedJavaMethod extends AbstractSoftMethod
{
  private final SoftMethod method;
  
  /**
   * Create a new {@code CopiedJavaMethod} that will copy the specified
   * {@link Method} to the generated class.
   * 
   * @param sc The {@link SoftClass} upon which this method will 
   *        be generated.
   *        
   * @param m The {@link Method} that is to be copied. This will be copied
   *        with the same name, modifiers, generic signature, and so on. 
   *        Relative references are switched as appropriate using a 
   *        {@link jen.AbstractReferenceSwitchingSoftMethod reference switching adapter}.
   *        
   * @throws IncompatibleClassChangeError if the specified method does not exist on this class,
   *         indicating that the class' internal representation has incompatibly changed.      
   */
  /* WORKER 1 */
  public CopiedJavaMethod(SoftClass sc, Method m) {
    super(sc);
    
    this.method = new SoftClass(m.getDeclaringClass()).
        getSoftMethod(m.getName(),Type.getMethodDescriptor(m));
    
    // unlikely right now but wait 'till retransform classes becomes common...
    if (method == null) throw(new IncompatibleClassChangeError(
        "The actual and apparent states of the declaring class are inconsistent"));
  }
  
  /**
   * Create a new {@code CopiedJavaMethod} that will copy the method
   * with the specified <em>method signature</em> from an (anonymous)
   * subclass. See the {@link CopiedJavaMethod class description} for
   * more information.
   *   
   * @param sc The {@link SoftClass} upon which this method will 
   *        be generated.
   *        
   * @param methodSignature The <em>method signature</em> of the method,
   *        declared on this anonymous subclass
   */
  /* WORKER 2 */
  protected CopiedJavaMethod(SoftClass sc, String methodSignature) {
    super(sc);
    
    method = new SoftClass(getClass()).getSoftMethod(methodSignature);
    if (method == null) throw(new IllegalArgumentException("No method '" + methodSignature + "' declared on anonymous subclass"));
  }
  
  /**
   * Create a new {@code CopiedJavaMethod} that will copy the first
   * declared method (in source order) from an (anonymous)
   * subclass. See the {@link CopiedJavaMethod class description} for
   * more information.
   *   
   * @param sc The {@link SoftClass} upon which this method will 
   *        be generated.
   *        
   * @param methodSignature The <em>method signature</em> of the method,
   *        declared on this anonymous subclass
   *        
   * @deprecated This constructor is fundementally broken and will be 
   *             removed at 0.50. It does not function as expected and
   *             should not be used. 
   */
  // FIXME: Remove at 0.50
  //
  //            ... getDeclaredMethods isn't ordered, and nor is SoftClass method list.
  //
  /* WORKER 3 */
  protected CopiedJavaMethod(SoftClass sc) {
    super(sc);

    java.lang.reflect.Method[] ms = getClass().getDeclaredMethods();
    if (ms.length > 0) {
      method = new SoftClass(getClass()).getSoftMethod(ms[0].getName(),Type.getMethodDescriptor(ms[0]));
    } else {
      throw new IllegalArgumentException("Class declares no methods");
    }
  }
    
  @Override
  public int getModifiers() {
    return method.getModifiers();
  }
  
  @Override
  public String getName() {
    return method.getName();
  }
  
  @Override
  public String getSignature() {
    return method.getSignature();
  }
  
  @Override
  public List<Type> getArgumentTypes() {
    return method.getArgumentTypes();
  }

  @Override
  public String getDescriptor() {
    return method.getDescriptor();
  }

  @Override
  public Type getReturnType() {
    return method.getReturnType();
  }

  @Override
  public List<Type> getThrowsTypes() {
    return method.getThrowsTypes();
  }
  
  /**
   * Returns the {@link SoftMethod} that internally represents the Java
   * method that is to be copied. 
   * 
   * @return The {@code SoftMethod} to be copied.
   */
  public SoftMethod getOriginalSoftMethod() {
    return method;
  }

  /* ************ */
  public void accept(ClassVisitor v) {
    new CopiedSoftMethod(getSoftClass(),method).accept(v);
  }
}
