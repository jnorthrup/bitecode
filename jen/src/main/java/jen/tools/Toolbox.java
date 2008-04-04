/* Toolbox.java - Jen Tools
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
 * File version: $Revision: 1.13 $ $Date: 2005/10/31 01:50:46 $
 * Originated: Oct 4, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import static jen.SoftUtils.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.*;

import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;

/** 
 * General utility methods for working with the Jen Tools library. This class
 * provides several methods that assist when working with the tools
 * provided by the library, such as conversions between methods and named
 * descriptors, primitive wrapper type conversions, and {@code super} invocation
 * on {@link DefaultInvokerStrategy default proxy} methods.
 * <p/>
 * This is a purely static class with no instances or shared state.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.13 $ $Date: 2005/10/31 01:50:46 $ 
 */
public final class Toolbox
{
  /* **********************************
   * MISC CONSTS
   */
  static final Type CLASS_TYPE = Type.getType(Class.class);
  static final Type CLASS_ARRAY_TYPE = Type.getType(Class[].class);
  static final Type OBJECT_TYPE = Type.getType(Object.class);
  static final Type OBJECT_ARRAY_TYPE = Type.getType(Object[].class);
  static final Type STRING_TYPE = Type.getType(String.class);
  static final Type METHOD_TYPE = Type.getType(Method.class);
  static final Type MAP_TYPE = Type.getType(Map.class);
  static final Type LIST_TYPE = Type.getType(List.class);
  
  static final org.objectweb.asm.commons.Method CLASS_GETMETHOD = 
    new org.objectweb.asm.commons.Method("getMethod",METHOD_TYPE, 
        new Type[] { STRING_TYPE, CLASS_ARRAY_TYPE });              
  
  static final org.objectweb.asm.commons.Method CLASS_GETPARAMS = 
    new org.objectweb.asm.commons.Method("getParameterTypes",CLASS_ARRAY_TYPE,new Type[0]);
  
  /* **********************************
   * PUBLIC API
   */
  /** 
   * Returns an internal type descriptor for the specified Class.
   * e.g. {@code int.class} becomes {@code I} , {@code String.class} becomes
   * {@code Ljava/lang/String;}, and {@code long[][]} becomes {@code [[J}.
   * 
   * @param type The {@link Class} for which a type descriptor is required.
   */ 
  public static final String typeDescriptor(Class type) {
    return Type.getType(type).getDescriptor();    
  }
  
  /** 
   * Returns a single character primitive type identifier. If the supplied {@link Class}
   * does not represent a primitive type, an {@link IllegalArgumentException} is thrown.
   * 
   * @param primType A {@link Class} representing a primitive type (e.g. 
   *        {@code int.class} or {@code void.class}.
   */ 
  public static final char primitiveClassIdent(Class primType) {
    if (!nullSafeArg(Toolbox.class,"primitiveTypeIdent",primType).isPrimitive()) 
      throw(new IllegalArgumentException("'"+primType+"' is not a primitive type"));
    return typeDescriptor(primType).charAt(0);
  }
  
  /** 
   * Returns a single character primitive type identifier. If the supplied {@link Type}
   * does not represent a primitive type, an {@link IllegalArgumentException} is thrown.
   * 
   * @param primType An ASM {@link Type} representing a primitive type (e.g. 
   *        {@code Type.getType(int.class)} or {@code Type.getType(void.class)}.
   */ 
  public static final char primitiveTypeIdent(Type primType) {
    if (nullSafeArg(Toolbox.class,"primitiveTypeIdent",primType).getDescriptor().length() != 1) 
      throw(new IllegalArgumentException("'"+primType+"' is not a primitive type"));
    return primType.getDescriptor().charAt(0);
  }
  
  /** 
   * Returns the appropriate java.lang.Wrapper class for the given type identifier
   * 
   * @param type An internal type identifier, e.g. {@code I} for {@code int} and 
   *        {@code Z} for {@code boolean}.
   */
  public static final Class primitiveWrapperClass(char type) {
    switch (type) {
      case 'B' : 
        return Byte.class;
      case 'C' : 
        return Character.class;
      case 'D' : 
        return Double.class;
      case 'F' : 
        return Float.class;
      case 'I' : 
        return Integer.class;
      case 'J' : 
        return Long.class;
      case 'S' : 
        return Short.class;
      case 'V' :
        return Void.class;
      case 'Z' : 
        return Boolean.class;
      default  : throw(new IllegalArgumentException("'"+type+"' is not a valid type identifier"));
    }    
  }
  
  /** Returns the appropriate java.lang.Wrapper Type (ASM) for the given type identifier */
  public static final Type primitiveWrapperType(char type) {
    return Type.getType(primitiveWrapperClass(type));
  }
  
  /**
   * Creates a general internal method descriptor that uniquely identifies a method
   * on a given class. This is an informal format that can be used for output, 
   * map keying, and so forth.
   * </p> 
   * The output of this method, for this method, is
   * {@code methodDescriptor(Ljava/lang/Method;)Ljava/lang/String;}.
   */
  public static final String namedMethodDescriptor(Method m) {
    return m.getName()+Type.getMethodDescriptor(m);    
  }
  
  static final String shortNameIfPoss(Class pt) {
    if ((pt.getPackage() != null) && 
        ("java.lang".equals(pt.getPackage().getName()))) { 
      return pt.getSimpleName();
    } else {
      return pt.getName();
    }    
  }
  
  /**
   * Creates a general internal method descriptor that uniquely identifies a method
   * on a given class. This is an informal format that is compatible with the 
   * {@link org.objectweb.asm.commons.Method#getMethod(java.lang.String)} method.
   * </p> 
   * The output of this method, for this method, is
   * {@code java.lang.String methodSignature(java.lang.Method)}. 
   */
  public static final String methodSignature(Method m) {
    StringBuilder b = new StringBuilder().
      append(m.getReturnType().isArray() ?
              shortNameIfPoss(m.getReturnType().getComponentType()) + "[]" : shortNameIfPoss(m.getReturnType())
      ).append(" ").append(m.getName()).append("(");

    boolean f = false;

    for (Class<?> pt : m.getParameterTypes()) {
      if (f) {
        b.append(", ");
      } else {
        f = true;
      }
      if(pt.isArray()){
          b.append(shortNameIfPoss(pt.getComponentType()));
          b.append("[]");
      } else {        
        b.append(shortNameIfPoss(pt));
      }
    }

    return b.append(")").toString();
  }
  
  /**
   * Selects an appropriate method overload based on the runtime types of the
   * specified arguments. This version will consider only public methods,
   * and generally prefers concrete classes over interfaces when matching
   * the argument types.
   * <p/>
   * See {@link #findMatchingMethod(Method[], boolean, String, Object[])} for details
   * of the search algorithm used.
   * 
   * @param clazz The {@link Class} from which a method is required.
   * @param onlyPublic If {@code true}, only public methods will be considered.
   * @param preferConcrete If {@code true}, the search algorithm will be slightly
   *        modified to prefer concrete classes over interfaces 
   *        (<strong>recommended</strong>).
   * @param name The method name.
   * @param args The actual arguments for which a method is required.
   * 
   * @return The {@link Method} whose declared argument types most closely match
   *         the runtime types of the supplied arguments.
   *         
   * @throws NoSuchMethodException If no method is declared with the specified name
   *         and compatible argument types.         
   */ 
  public static final Method findMatchingMethod(Class clazz, String name, Object... args) 
  throws NoSuchMethodException {
    return findMatchingMethod(clazz,true,true,name,args);    
  }
  
  /**
   * Selects an appropriate method overload based on the runtime types of the
   * specified arguments. This version will optionally search all declared
   * methods, and allows the concrete-preference to be specified.
   * <p/>
   * See {@link #findMatchingMethod(Method[], boolean, String, Object[])} for details
   * of the search algorithm used.
   * 
   * @param clazz The {@link Class} from which a method is required.
   * @param onlyPublic If {@code true}, only public methods will be considered.
   * @param preferConcrete If {@code true}, the search algorithm will be slightly
   *        modified to prefer concrete classes over interfaces for arguments
   *        (<strong>recommended</strong>).
   * @param name The method name.
   * @param args The actual arguments for which a method is required.
   * 
   * @return The {@link Method} whose declared argument types most closely match
   *         the runtime types of the supplied arguments.
   *         
   * @throws NoSuchMethodException If no method is declared with the specified name
   *         and compatible argument types.         
   */ 
  public static final Method findMatchingMethod(Class clazz, boolean onlyPublic, boolean preferConcrete, String name, Object... args) 
  throws NoSuchMethodException {
    return findMatchingMethod(onlyPublic ? clazz.getMethods() : clazz.getDeclaredMethods(), preferConcrete, name, args);    
  }
  
  /**
   * Selects an appropriate method overload based on the runtime types of the
   * specified arguments. This version will examines methods from the supplied
   * array. Methods that do not have the specified name are ignored - the 
   * primary use for this method is to select from an existing array, usually
   * from {@link Class#getMethods()} or 
   * {@link Class#getDeclaredMethods() getDeclaredMethods()}.
   * 
   * <h4>Search algorithm</h4>
   * 
   * The method search is carefully designed to give reliable, predictable 
   * operation while retaining the high throughput required by any dynamic 
   * invocation strategy. While this implementation leaves little room for 
   * flexibility with respect to dispatch in situations where the choice of
   * method is not entirely clear, a measure of flexibility is provided in
   * the form of the {@code preferConcrete} option, which allows optional 
   * weighting to be applied against interfaces 
   * ({@code preferConcrete} == {@code true}) during selection, preferring
   * matches to concrete types over matches to interfaces. 
   * <p/>
   * This is the default configuration, and is recommended. It means that, for example,
   * a method with a {@code String} argument will be chosen over a method with a
   * {@code CharSequence} argument at the same position for invocation with a String 
   * argument. This weighting is deliberately kept slight, however, so that the scoring 
   * also shows a general preference for finding matches for more arguments rather than 
   * close matches for a few. For example, a {@code Runnable, CharSequence} will still 
   * be chosen over {@code Object, String} or {@code Thread, Object} for actual
   * arguments of classes {@code Thread, String}.
   * 
   * @param methods The array of {@link Method}s from which to select a method.
   * @param preferConcrete If {@code true}, the search algorithm will be slightly
   *        modified to prefer concrete classes over interfaces for arguments
   *        (<strong>recommended</strong>).
   * @param name The method name.
   * @param args The actual arguments for which a method is required.
   * 
   * @return The {@link Method} whose declared argument types most closely match
   *         the runtime types of the supplied arguments.
   *         
   * @throws NoSuchMethodException If no method is declared with the specified name
   *         and compatible argument types.         
   */ 
  /* WORKER */
  public static final Method findMatchingMethod(Method[] methods, boolean preferConcrete, String name, Object... args) 
  throws NoSuchMethodException {
    Method best = null;
    int bestScore = Integer.MAX_VALUE;    
    int ifaceWeight = preferConcrete ? 1 : 0;
    
    for (Method m : methods) {
      Class<?>[] paramTypes = m.getParameterTypes();
      if (m.getName().equals(name) && fastCompat(paramTypes,args)) {
        
        int i = scoreMethod(paramTypes,args, ifaceWeight);
        
        // special case: 0 is exact match
        if (i == 0) return m;
        
        if (i < bestScore) {
          best = m;
          bestScore = i;
        }
      }      
    }
    
    if (best == null) {     
      throw(new NoSuchMethodException(noSuchMethodHelper(name,args)));
    }
    
    return best;
  }
  
  /**
   * Selects an appropriate constructor overload from the specified array.
   * This version will consider only public constructors, and generally prefers 
   * concrete classes over interfaces when matching the argument types.
   * <p/>
   * See {@link #findMatchingMethod(Method[], boolean, String, Object[])} for details
   * of the search algorithm used.
   * 
   * @param ctors The array of {@link Constructor}s from which to select a method.
   * @param args The actual arguments for which a constructor is required.
   * 
   * @return The {@link Constructor} whose declared argument types most closely match
   *         the runtime types of the supplied arguments.
   *         
   * @throws NoSuchMethodException If no constructor is found with compatible 
   *         argument types.         
   */ 
  public static final <T> Constructor<T> findMatchingConstructor(Class<T> clazz, Object... args) 
  throws NoSuchMethodException {
    return findMatchingConstructor(clazz,true,true,args);    
  }
  
  /**
   * Selects an appropriate constructor overload from the specified array.
   * This version will optionally search all declared constructors, and allows
   * the concrete-preference to be specified.
   * <p/>
   * See {@link #findMatchingMethod(Method[], boolean, String, Object[])} for details
   * of the search algorithm used.
   * 
   * @param ctors The array of {@link Constructor}s from which to select a method.
   * @param onlyPublic If {@code true}, the search algorithm will consider only
   *        {@code public} constructors.
   * @param preferConcrete If {@code true}, the search algorithm will be slightly
   *        modified to prefer concrete classes over interfaces for arguments
   *        (<strong>recommended</strong>).
   * @param args The actual arguments for which a constructor is required.
   * 
   * @return The {@link Constructor} whose declared argument types most closely match
   *         the runtime types of the supplied arguments.
   *         
   * @throws NoSuchMethodException If no constructor is found with compatible 
   *         argument types.         
   */ 
  @SuppressWarnings("unchecked")    // Class will only have it's own constructor types
  // FIXME maybe should be Class<? super T>
  public static final <T> Constructor<T> findMatchingConstructor(Class<T> clazz, boolean onlyPublic, boolean preferConcrete, Object... args) 
  throws NoSuchMethodException {    
    return (Constructor<T>)findMatchingConstructor(onlyPublic ? clazz.getConstructors() : clazz.getDeclaredConstructors(), preferConcrete, args);    
  }
  
  /**
   * Selects an appropriate constructor overload from the specified array.
   * The primary use for this method is to select from an existing array, usually
   * from {@link Class#getConstructors()} or 
   * {@link Class#getDeclaredConstructors() getDeclaredConstructors()}.
   * <p/>
   * See {@link #findMatchingMethod(Method[], boolean, String, Object[])} for details
   * of the search algorithm used.
   * 
   * @param ctors The array of {@link Constructor}s from which to select a method.
   * @param preferConcrete If {@code true}, the search algorithm will be slightly
   *        modified to prefer concrete classes over interfaces for arguments
   *        (<strong>recommended</strong>).
   * @param args The actual arguments for which a constructor is required.
   * 
   * @return The {@link Constructor} whose declared argument types most closely match
   *         the runtime types of the supplied arguments.
   *         
   * @throws NoSuchMethodException If no constructor is found with compatible 
   *         argument types.         
   */
  /* WORKER */
  public static final Constructor<?> findMatchingConstructor(Constructor<?>[] ctors, boolean preferConcrete, Object... args) 
  throws NoSuchMethodException {
    Constructor<?> best = null;
    int bestScore = Integer.MAX_VALUE;    
    int ifaceWeight = preferConcrete ? 1 : 0;
    
    for (Constructor<?> c : ctors) {
      Class<?>[] paramTypes = c.getParameterTypes();
      if (fastCompat(paramTypes,args)) {
        
        int i = scoreMethod(paramTypes, args, ifaceWeight);
        
        // special case: 0 is exact match
        if (i == 0) return c;
                
        if (i < bestScore) {
          best = c;
          bestScore = i;
        }
      }      
    }
    
    if (best == null) {     
      throw(new NoSuchMethodException(noSuchMethodHelper("<init>",args)));
    }
    
    return best;
  }
  
  /* **********************************
   * NON-PUBLIC API
   * 
   * Method scoring for findMatchingMethod
   */
  /* This method calculates 'how assignable' the actual argument is to the declared
   * argument, and provides a 'score' for it. This is used to find the 'closest 
   * match' for method args during dynamic invocation.
   * 
   * It is assumed on entry that declared.isAssignableFrom(actual) is true.
   * 
   * Basically, if declared == actual, this method returns 0. If declared is directly
   * assignable from any interface implemented by actual, it returns zero. 
   * If neither is true, then these checks are repeated on the superclass, with the
   * return being 1 if either pass. If not, on to the next superclass, score 2, and
   * so on (each interface pass adds the interface weight to the score also). 
   * 
   * Thus the return value from this method will be a general indication of how
   * 'different' the two assignable classes are, that can be used to compare and
   * choose between several compatible methods at runtime. The higher the number, the
   * more 'different' the classes. This is weighted according to the ifaceWeight
   * as described in the {@link #findMatchingMethod(Method[], ... )} javadoc.
   * THE RECOMMENDED VALUES FOR WEIGHT ARE ZERO (NONE) OR ONE (FULL). HIGHER
   * VALUES CAN CAUSE UNEXPECTED RESULTS (e.g. all-Object overloads being chosen for
   * invocations even when an exact match is available).
   * 
   * If this method gets right back to Object and hasn't found a link, it throws
   * an IllegalArgumentException.
   * 
   * This is an expanded version of the Multi dispatch proxy methods.
   */
  static final int scoreClass(Class<?> declared, Class<?> actual, int ifaceWeight) {
    int score = 0;
    
    // precalc to save time in loop. Can't just return now as we don't know how
    // far up from object we are...
    boolean declIsObject = Object.class == declared;
    
    while (actual != null) {
      // Is this our declared class?
      if (declared == actual) {
        return score;         
      }
      
      // check interfaces then. If any of the the declared type can be assigned to any of actual's 
      // interfaces, this is our score.
      //
      // If the declared type is Object we don't do this, since it is always assignable from every
      // interface, so throws out the results.
      if (!declIsObject) {
        for (Class<?> iface : actual.getInterfaces()) {
          
          // == won't catch all, but will catch the vast majority (it would only miss
          // if the actual arg implements an interface of which declared arg type is 
          // a superinterface). Since the majority will hit on an identity check, 
          // and it's cheap (while assignableFrom is expensive) lets try to shortcut
          // on that first...
          if (declared == iface || declared.isAssignableFrom(iface)) {
            // Weight against interfaces, but not too much
            return score + ifaceWeight;        
          }
          
        }
      }
      
      // No match, so up to super (or throw if this is Object)
      if (actual == Object.class) {      
        // The two aren't compatible at all!
        throw(new IllegalArgumentException("Incompatible classes!"));      
      } else {
        score++;
        actual = actual.getSuperclass();             
      }
    }    
    
    return score;
  }
  
  /* Calculate the score for a given method invocation with the specified
   * args. The lower the method's score, the better the match.
   */
  static final int scoreMethod(Class<?>[] dec, Object[] args, int ifaceWeight) {    
    if (args == null || args.length != dec.length) {
      throw(new IllegalArgumentException("Incompatible classes!"));      
    }
    
    int score = 0;
    
    for (int i = 0; i < dec.length; i++) {
      Object o = args[i];
      if (o == null) continue;    // null is assignable to any
      
      score += scoreClass(dec[i],o.getClass(),ifaceWeight);      
    }
    
    return score;    
  }
  
  /* Helper that 'prechecks' a method is call-compatible, before scoring is done */
  static final boolean fastCompat(Class<?>[] clazz, Object[] args) {
    
    if (clazz == null || args == null || clazz.length != args.length) {
      return false;
    }
    
    for (int i = 0; i < args.length; i++) {
      
      // Optimize: Object is assignable from everything
      Object o = args[i];
      if (clazz[i] == Object.class || o == null) {
        continue;
      }
      
      // Any incompatible arg means the whole method is no good
      if (!clazz[i].isAssignableFrom(o.getClass())) {
        return false;
      }      
    }
    
    // If we're here it's good
    return true;
  }  
  
  /* Helper to create exception string in findMatchingMethod */
  static final String noSuchMethodHelper(String methodName, Object[] args) {
    StringBuffer msg = new StringBuffer().
                          append("Unable match invocation of ").
                          append(methodName);

    if (args != null && args.length > 0) {
      msg.append(" with arguments [");
      
      boolean multi = false;
      
      for (Object a : args) {
        if (multi) {
          msg.append(", ");
        }
        multi = true;
        
        msg.append("'").append(a.toString()).append("'");        
      }
            
      msg.append("]");
    }
    
    return msg.toString();    
  }  
  
  /* Just a wrapper to let us get a method that really _is_ there, or die trying */
  static Method knownMethod(Class clazz, String name, Class... params) {
    try {
      return clazz.getMethod(name,params);
    } catch (NoSuchMethodException e) {
      throw(new RuntimeException("Proxy: One or more expected Methods are missing.",e));      
    }
  }
  
  /* Swaps the top stack reference for a default (zero) const of the specified primitive type.
   * Usually the popped ref will be null. Used by the invoker strategies.
   */
  static void defaultPrimitiveReturn(char type, GeneratorAdapter gen) {
    gen.pop();
    switch (type) {
         case 'B' :
         case 'C' :
         case 'I' :
         case 'S' :
         case 'Z' :
             gen.push(0);
             break;
         case 'D' :
             gen.push((double) 0);
             break;
         case 'F' :
             gen.push((float) 0);
             break;
         case 'J' :
             gen.push((long) 0);
             break;
         case 'V' :
             throw(new IllegalArgumentException("Cannot push a void reference"));
    }
  }

  /* TODO remove at 0.50 */
  /**
   * @deprecated Use {@link DefaultInvokerStrategy#invokeSuper(Object, Method, Object[])}
   *             instead.
   */
  public static Object invokeSuper(Object proxy, Method m, Object[] args) throws Throwable {
    return DefaultInvokerStrategy.invokeSuper(proxy,m,args);
  }
  
  private Toolbox() { }  
}
