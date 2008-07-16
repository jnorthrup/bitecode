/* InvokerGenerationStrategy.java - Abstract generation strategy for dispatchers
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
 * File version: $Revision: 1.5 $ $Date: 2005/10/30 23:47:46 $
 * Originated: Oct 5, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Method;

import jen.SoftClass;

/** 
 * Encapsulates the proxy invocation strategy for a {@link SoftProxy} generation.
 * Implementations of this interface are responsible for generating proxy invocation 
 * methods for supplied {@link Method}s, and generating any instance initialization
 * required by the invokers. A custom implementation can be supplied at generation
 * time to replace the {@link java.lang.reflect.InvocationHandler}-based strategy 
 * employed by {@link jen.tools.DefaultInvokerStrategy}.
 * <p/>
 * {@code SoftProxy} generates a set of static final fields on the proxy class,
 * holding the {@code Method} instances relating to each method. The field name
 * can be obtained for a given {@code Method} instance via the 
 * {@link SoftProxy#methodFieldName(String)} method. This can be used to 
 * generate a {@code GETSTATIC} instruction to place the appropriate {@code Method}
 * on to the stack, from where it can be used as appropriate. 
 * <p/>
 * These fields, and the associated {@code <clinit>} block, are generated implicitly,
 * and cannot be directly influenced. There is no guarantee that either will have
 * been generated on the {@link SoftClass} at the time the methods of this class 
 * are called, and furthermore there is no guarantee that, apart from the 
 * afterGeneration method (see below) these methods will be called in any particular
 * order.
 * <p/>
 * The {@link #afterGeneration(SoftClass)} method is a special case, that is called
 * exactly once per proxy, and always after all other generation has taken place.
 * This can be used to handle any final generation required by the strategy.
 * <p/>
 * Implementations of this class are not required to be thread-safe, or even provide
 * meaningful operation across multiple proxy generations. Although the 
 * {@code DefaultInvokerStrategy}  
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.5 $ $Date: 2005/10/30 23:47:46 $ 
 */
/*
 * N.B. 0.30 breaks binary compatibility on this interface. Sorry about that :(
 * 
 * The impact is not especially great - it's mainly that the previous generateInstanceInit
 * method has been renamed to 'afterGeneration', and is now guaranteed to be called after
 * the invocation methods have been generated.
 * 
 * Well-behaved old implementations can easily be migrated up by either refactoring 
 * the method, or adding a new afterGeneration method and calling the generateInstanceInit
 * method from it. This should be fine since previously the method wasn't guaranteed to
 * be called before the invokers anyway.
 */
public interface InvokerGenerationStrategy
{
  /**
   * Called prior to generation to determine whether a strategy implementation is
   * able to proxy the supplied classes. This allows proxies to limit the types
   * and combination of classes they can proxy, beyond the basic limitations imposed
   * by SoftProxy (and the JVM itself) - at most one non-interface, at most one
   * non-public class, any number of interfaces.
   * 
   * @param classes The array of classes for which a proxy will be generated.
   * 
   * @return {@code true} if the strategy agrees to generate a proxy, or 
   *         {@code false} to disallow the generation.
   */
  public boolean canProxyClasses(Class<?>[] classes);
  
  /**
   * Called during {@link SoftClass} generation to add an invocation dispatcher
   * for method {@link Method m}. This method is called exactly once for each 
   * method for which a proxy is required. These calls may occur at any time, and 
   * in any order with respect to one-another and generation of other members.
   * 
   * @param sc The proxy {@code SoftClass} being generated. 
   * @param m The proxied {@code Method}. 
   */
  public void generateInvoker(SoftClass sc, Method m);
  
  /**
   * Called just after {@link SoftClass} generation to perform any final generation 
   * required by the strategy. This method is guaranteed to be called
   * <strong>once per proxy</strong>, and <strong>after</strong> both instance
   * initialization and all invokers have been generated. It will usually be
   * called just before the {@code SoftClass} is used to generate bytecode.
   * 
   * @param sc The proxy {@code SoftClass} that has been generated. 
   */
  public void afterGeneration(SoftClass sc);
}
