/* CopiedMethodTestCase.java - Awaiting description
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
* File version: $Revision: 1.3 $ $Date: 2005/10/18 01:28:45 $
* Originated: 13-Sep-2005
* Author: Ross Bamford (rosco<at>roscopeco.co.uk)
*/

package jen.methods;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import jen.AbstractSoftMethod;
import jen.SoftClass;
import jen.SoftMethod;

import static java.util.Collections.unmodifiableList;
import static java.util.Arrays.asList;

import static jen.SoftUtils.nullSafeArg;
import static jen.SoftUtils.asmForSoftMethod;

/**
 * {@link CopiedSoftMethod}-based invocation-wrapping {@link SoftMethod}.
 * This class copies the specified source {@code SoftMethod} to the 
 * generated class with {@code private} access and an internal name,
 * and generates an <em>invocation wrapper</em> method, inserting the
 * specified {@link MethodFragment}s before and after the invocation as
 * appropriate. The effect is similar to (though not the same as) that of
 * the so-called <em>around advice</em> common in Aspect Oriented 
 * Programming. 
 * <p/>
 * This class supports insertion of custom code both <em>before</em> and 
 * <em>after</em> the source method invocation using separate 
 * {@code MethodFragment} lists. The following applies to the usage of
 * fragments in these lists:
 * <p/>
 * <ul>
 *    <li><strong>Before invocation:</strong>
 *    <ul>
 *      <li>Before generating the wrapped invocation, each fragment in the
 *      supplied {@code before} list will be called exactly once, passing
 *      in the {@link GeneratorAdapter} being used to generate the method.
 *      These calls will happen in the exact order of the items in the list.</li>
 *      <p/>
 *      <li>Prior to the first instruction of the first {@code before} fragment,
 *      the stack will hold <strong>exactly</strong> a single copy of the
 *      {@code this} reference (<strong>only</strong> in case of a non-static wrapped 
 *      method), and exactly one copy to each of the method arguments.</li>
 *      <p/>
 *      <li>After the last instruction of the final {@code before} fragment, the
 *      stack <strong>must</strong> be in a consistent state for the appropriate
 *      {@code INVOKExxxx} instruction (either {@code STATIC} or {@code SPECIAL}, 
 *      depending upon the method modifiers). This does <strong>not</strong> require
 *      the <em>same</em> elements - merely <em>compatible</em> ones.</li>
 *    </ul>
 *    </li>
 *    <p/>
 *    <li><strong>After invocation:</strong>
 *    <ul>
 *      <li>After generating the wrapped invocation, each fragment in the
 *      supplied {@code after} list will be called exactly once, passing
 *      in the {@link GeneratorAdapter} being used to generate the method.
 *      These calls will happen in the exact order of the items in the list.</li>
 *      <p/>
 *      <li>Prior to the first instruction of the first {@code after} fragment,
 *      the stack will hold the same elements as before the wrapped invocation,
 *      upto the element prior to the {@code this} reference and method arguments 
 *      (if appropriate), followed by the return value (either primitive
 *      or reference) from the method <strong>only</strong> if one is declared.
 *      </li>
 *      <p/>
 *      <li>After to the last instruction of the final {@code after} fragment,
 *      the top stack element will be returned to the caller. This 
 *      <strong>must</strong> be compatible with the return type of the method
 *      </li>
 *    </ul>
 *    </li>
 *    <p/>
 *    <li><strong>Also note</strong> that any exception thrown by any fragment
 *    will cause the entire SoftClass generation to fail.</li>
 * </ul>
 * <p/>
 * There are additional guidelines for the creation of well-behaved method fragments,
 * as described in it's {@link MethodFragment documentation}. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.3 $ $Date: 2005/10/18 01:28:45 $
 */
public class WrappedSoftMethod extends AbstractSoftMethod
{
  static final List<MethodFragment> NO_FRAGMENTS = unmodifiableList(new ArrayList<MethodFragment>());
  
  final List<MethodFragment> before;
  final List<MethodFragment> after;
  
  final SoftMethod source;
  
  private static int modifiersHelper(int orig) {
    if ((orig & SoftClass.ACC_ABSTRACT) != 0) 
        throw(new IllegalArgumentException("WrappedSoftMethod: Cannot wrap an abstract method"));
    
    int mod = SoftClass.ACC_PRIVATE;    
    if ((orig & SoftClass.ACC_STATIC) != 0) mod &= SoftClass.ACC_STATIC;
    
    return mod & SoftClass.ACC_SYNTHETIC;
  }
  
  private static String nameHelper(String orig) {
    StringBuilder s = new StringBuilder();
    return s.append("__").append(orig).append("_").
             append(s.hashCode()).toString();
  }
  
  /**
   * Convenience constructor to support anonymous fragment arrays. See
   * {@link #WrappedSoftMethod(SoftClass, SoftMethod, List, List)} for
   * operational detail.
   * 
   * @param sc The {@link SoftClass} upon which this method will be generated.
   * @param source The {@link SoftMethod} that is to be wrapped.
   * @param before An array of {@code MethodFragment}s to insert before the invocation.
   * @param after An array of {@code MethodFragment}s to insert after the invocation.
   */
  public WrappedSoftMethod(SoftClass sc, SoftMethod source, MethodFragment[] before, MethodFragment[] after) {
    this(sc,source,
        before != null ? asList(before) : NO_FRAGMENTS,
        after != null ? asList(after) : NO_FRAGMENTS);
  }
  
  /**
   * Create a new {@code WrappedSoftMethod} that will copy the specified {@code source}
   * method (which may be from any class) to the generated class, wrapping invocations
   * with the specified {@code before} and {@code after} {@link MethodFragment}s.
   * Either (or both) fragment lists may be {@code null} if no fragments are 
   * required at that point. 
   * 
   * @param sc The {@link SoftClass} upon which this method will be generated.
   * @param source The {@link SoftMethod} that is to be wrapped.
   * @param before A {@link List} of {@code MethodFragment}s to insert before the invocation.
   * @param after A {@link List} of {@code MethodFragment}s to insert after the invocation.
   */
  public WrappedSoftMethod(SoftClass sc, SoftMethod source, List<MethodFragment> before, List<MethodFragment> after) {
    this(sc,nullSafeArg(WrappedSoftMethod.class,"source",source),source.getModifiers(),source.getName(),before,after);
  }
    
  /**
   * Convenience constructor to support anonymous fragment arrays. See
   * {@link #WrappedSoftMethod(SoftClass, SoftMethod, int, String, List, List)} for
   * operational detail. This version allows a new name to be specified, but
   * retains the access modifiers from the original method.
   * 
   * @param sc The {@link SoftClass} upon which this method will be generated.
   * @param source The {@link SoftMethod} that is to be wrapped.
   * @param newName The name for the new method.
   * @param before An array of {@code MethodFragment}s to insert before the invocation.
   * @param after An array of {@code MethodFragment}s to insert after the invocation.
   */
  public WrappedSoftMethod(SoftClass sc, SoftMethod source, String newName, MethodFragment[] before, MethodFragment[] after) {
    this(sc,nullSafeArg(WrappedSoftMethod.class,"source",source),source.getModifiers(),newName,
        before != null ? asList(before) : NO_FRAGMENTS,
        after != null ? asList(after) : NO_FRAGMENTS);
  }
  
  /**
   * Convenience constructor to support anonymous fragment arrays. See
   * {@link #WrappedSoftMethod(SoftClass, SoftMethod, int, String, List, List)} for
   * operational detail.
   * 
   * @param sc The {@link SoftClass} upon which this method will be generated.
   * @param source The {@link SoftMethod} that is to be wrapped.
   * @param newModifiers The access modifiers for the new method.
   * @param newName The name for the new method.
   * @param before An array of {@code MethodFragment}s to insert before the invocation.
   * @param after An array of {@code MethodFragment}s to insert after the invocation.
   */
  public WrappedSoftMethod(SoftClass sc, SoftMethod source, int newModifiers, String newName, MethodFragment[] before, MethodFragment[] after) {
    this(sc,nullSafeArg(WrappedSoftMethod.class,"source",source),newModifiers,newName,
        before != null ? asList(before) : NO_FRAGMENTS,
        after != null ? asList(after) : NO_FRAGMENTS);
  }
  
  /**
   * Create a new {@code WrappedSoftMethod} that will copy the specified {@code source}
   * method (which may be from any class) to the generated class, wrapping invocations
   * with the specified {@code before} and {@code after} {@link MethodFragment}s.
   * This version allows the wrapper method to be given a new name and access modifiers.
   * 
   * @param sc The {@link SoftClass} upon which this method will be generated.
   * @param source The {@link SoftMethod} that is to be wrapped.
   * @param newModifiers The access modifiers for the new method.
   * @param newName The name for the new method.
   * @param before A {@link List} of {@code MethodFragment}s to insert before the invocation.
   * @param after A {@link List} of {@code MethodFragment}s to insert after the invocation.
   */
  /* WORKER */
  public WrappedSoftMethod(SoftClass sc, SoftMethod source, int newModifiers, String newName, List<MethodFragment> before, List<MethodFragment> after) {
    super(sc,newModifiers,nullSafeArg(WrappedSoftMethod.class,"newName",newName),
          nullSafeArg(WrappedSoftMethod.class,"source",source).getSignature(),
          source.getArgumentTypes(),source.getReturnType(),source.getThrowsTypes());

    // source is a copied method we rename and make private
    this.source = new CopiedSoftMethod(sc,source,modifiersHelper(source.getModifiers()),nameHelper(source.getName()));
    
    this.before = new ArrayList<MethodFragment>(before != null ? before : NO_FRAGMENTS);
    this.after = new ArrayList<MethodFragment>(after != null ? after : NO_FRAGMENTS);    
  }
  
  /**
   * Generates the wrapper method, and copies the source method to a private
   * method with an internal name.
   */
  @Override
  public void accept(ClassVisitor vis) {
    // firstly generate the wrapper
    GeneratorAdapter gen = new GeneratorAdapter(getModifiers(),
        asmForSoftMethod(this),            
        getSignature(),
        getThrowsTypes().toArray(new Type[getThrowsTypes().size()]),
        vis);

    // load this and args
    if ((getModifiers() & ACC_STATIC) == 0) {
      gen.loadThis();
    }    
    gen.loadArgs();
    
    // before fragments
    for (MethodFragment frag : before) {
      frag.accept(gen);
    }
    
    // invoke wrapped
    if ((getModifiers() & ACC_STATIC) != 0) {
      gen.invokeStatic(getSoftClass().getType(),asmForSoftMethod(source));
    } else if ((getModifiers() & ACC_PRIVATE) != 0) {
      // IS ACTUALLY INVOKESPECIAL
      gen.invokeConstructor(getSoftClass().getType(),asmForSoftMethod(source));      
    } else {
      gen.invokeVirtual(getSoftClass().getType(),asmForSoftMethod(source));      
    }

    // after fragments
    for (MethodFragment frag : after) {
      frag.accept(gen);
    }
    
    // return and done
    gen.returnValue();
    gen.endMethod();
    
    // Now generate the copied wrapped method
    source.accept(vis);
  }
}
