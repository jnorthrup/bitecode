/* SoftUtils.java - General utils
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
 * File version: $Revision: 1.10 $ $Date: 2005/10/23 02:15:22 $
 * Originated: 13-Mar-2004
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import static java.util.Collections.emptyList;

/** 
 * Provides general utility methods for working with SoftClass and generated 
 * classes in general. This is a completely static class with no instances. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.10 $ $Date: 2005/10/23 02:15:22 $ 
 */
public final class SoftUtils
{
  /* **********************************
   * PUBLIC API
   */
  /**
   * Reflectively invoke a (possibly inherited virtual) method (or constructor) on
   * the specified object instance, with the supplied arguments. When a constructor 
   * invocation (denoted by the special name {@code <init>}) is made, if the first
   * argument is a {@link Class} object, then that class will supply the constructor.
   * Otherwise, the object's class will be searched, and a constructor invoked if
   * possible. The return value of this method is the return value of the invoked
   * method, the new instance created by the constructor, or {@code null} if the
   * method returned null or did not declare a return type. 
   * <p/>
   * The <code>setAccess</code> argument determines whether or not this method should
   * attempt to force access if it receives an {@link IllegalAccessException} when
   * invoking the method. 
   *  
   * @param o The object upon which the method is to be invoked.
   * @param methodName The name of the method.
   * @param setAccess if <code>true</code> {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
   *        will be called on the method or constructor object if it is not normally accessible.
   * @param argTypes An array of classes representing the argument types for the method.
   *        Primitive types may be referenced by their appropriate class (e.g. 
   *        <code>int.class</code>, <code>byte[].class</code>, etc) and passed in the
   *        <code>args</code> array in their standard wrapper types.
   * @param args An array of arguments to pass to the method. May be <code>null</code>
   *        for a no-arg method.
   *        
   * @return The return value from the method (wrapped if primitive), or new instance
   *         if a constructor was invoked.
   * 
   * @throws NoSuchMethodException if the named method is not declared or inherited.
   * @throws IllegalAccessException if the named method is not accessible.
   * @throws SecurityException If <code>setAccess</code> is true but the named method is still
   *         not accessible (e.g. private methods).
   * @throws InvocationTargetException if an invoked method throws an exception. 
   * @throws InstantiationException if an invoked constructor throws an exception.
   */
  /* WORKER */
  public static Object reflectInvoke(Object o, String methodName, boolean setAccess, Class[] argTypes, Object[] args) 
  throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    if ("<init>".equals(methodName)) {
      // If 'o' is itself a Class, use it. Otherwise, use it's class. This is
      // a convenience.
      Class<?> clazz = Class.class.equals(nullSafeArg("SoftUtils","reflectInvoke","o (Class)",o).getClass()) ? (Class)o : o.getClass();
      Constructor<?> ctor = clazz.getDeclaredConstructor(argTypes);
      
      try {
        return ctor.newInstance(args);
      } catch (IllegalAccessException e) {
        if (setAccess) ctor.setAccessible(true);            
        return ctor.newInstance(args);        
      }
    } else {
      Method method = o.getClass().getDeclaredMethod(methodName,argTypes);
      if (method == null) {
        method = o.getClass().getMethod(methodName,argTypes);
      }
      
      try {
        return method.invoke(o,args);
      } catch (IllegalAccessException e) {
        if (setAccess) method.setAccessible(true);            
        return method.invoke(o,args);        
      }
    }
  }
  
  /**
   * Reflectively invoke a (possibly inherited virtual) method (or constructor) on
   * the specified object instance, with the supplied arguments. This version uses
   * the exact classes of the <code>args</code> elements to determine the method
   * to call (and thus it cannot, for example, support primitives).
   * <p/>
   * See {@link #reflectInvoke(Object, String, boolean, Class[], Object[])} for
   * the semantic details of this method with respect to the <code>setAccess</code>
   * flag.
   * 
   * @param o The object upon which the method is to be invoked.
   * @param methodName The name of the method.
   * @param setAccess if <code>true</code> {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
   *        will be called on the method or constructor object if it is not normally accessible.
   * @param args Argument(s) to pass to the method (varargs)
   *        
   * @return The return value from the method (wrapped if primitive), or new instance
   *         if a constructor was invoked.
   * 
   * @throws NoSuchMethodException if the named method is not declared or inherited.
   * @throws IllegalAccessException if the named method is not accessible.
   * @throws SecurityException If <code>setAccess</code> is true but the named method is still
   *         not accessible (e.g. private methods).
   * @throws InvocationTargetException if an invoked method throws an exception. 
   * @throws InstantiationException if an invoked constructor throws an exception.
   */
  public static Object reflectInvoke(Object o, String methodName, boolean setAccess, Object... args) 
  throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    return reflectInvoke(o,methodName,setAccess,classForObjectArray(args),args);
  }
  
  /**
   * Reflectively invoke a (possibly inherited virtual) method (or constructor) on
   * the specified object instance, with the no arguments. This version allows the
   * <code>setAccess</code> flag to be specified.
   * <p/>
   * See {@link #reflectInvoke(Object, String, boolean, Class[], Object[])} for
   * the semantic details of this method with respect to the <code>setAccess</code>
   * flag.
   * 
   * @param o The object upon which the method is to be invoked.
   * @param methodName The name of the method.
   * @param setAccess if <code>true</code> {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
   *        will be called on the method or constructor object if it is not normally accessible.
   *        
   * @return The return value from the method (wrapped if primitive), or new instance
   *         if a constructor was invoked.
   * 
   * @throws NoSuchMethodException if the named method is not declared or inherited.
   * @throws IllegalAccessException if the named method is not accessible.
   * @throws SecurityException If <code>setAccess</code> is true but the named method is still
   *         not accessible (e.g. private methods).
   * @throws InvocationTargetException if an invoked method throws an exception. 
   * @throws InstantiationException if an invoked constructor throws an exception.
   */
  /* IMPL NOTE: Provided because varargs makes it ambiguous calling void args with setAccess otherwise */
  public static Object reflectInvoke(Object o, String methodName, boolean setAccess) 
  throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    return reflectInvoke(o,methodName,setAccess,new Class[0],new Object[0]);
  }
  
  
  /**
   * Reflectively invoke a (possibly inherited virtual) method (or constructor) on
   * the specified object instance, with the supplied arguments. This version will
   * <strong>not</strong> call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
   * on the method or constructor.
   *  
   * @param o The object upon which the method is to be invoked.
   * @param methodName The name of the method.
   * @param args Argument(s) to pass to the method (varargs)
   *        
   * @return The return value from the method (wrapped if primitive), or new instance
   *         if a constructor was invoked.
   * 
   * @throws NoSuchMethodException if the named method is not declared or inherited.
   * @throws IllegalAccessException if the named method is not accessible.
   * @throws InvocationTargetException if an invoked method throws an exception. 
   * @throws InstantiationException if an invoked constructor throws an exception.
   */
  public static Object reflectInvoke(Object o, String methodName, Object... args) 
  throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    return reflectInvoke(o,methodName,false,args);
  }
  
  /**
   * Reflectively invoke a (possibly inherited virtual) method (or constructor) on
   * the specified object instance, with the supplied arguments, wrapping any exceptions 
   * thrown in a {@link java.lang.RuntimeException}.
   * <p/>
   * If an {@link java.lang.reflect.InvocationTargetException} is thrown this method
   * will try to unwrap the causal exception, and wrap it in a RuntimeException. All
   * other exceptions are simply wrapped.
   * 
   * @param o The object upon which the method is to be invoked.
   * @param methodName The name of the method.
   * @param args Argument(s) to pass to the method (varargs)
   *        
   * @return The return value from the method (wrapped if primitive), or new instance
   *         if a constructor was invoked.
   * 
   * @throws RuntimeException if an exception is thrown by the method. 
   * 
   * @deprecated For obvious reasons.
   */
  public static Object reflectInvokeRTE(Object o, String methodName, Object... args) {
    try {
      return reflectInvoke(o,methodName,args);
    } catch (InvocationTargetException e) {
      throw(e.getCause() != null ? new RuntimeException(e.getCause()) : new RuntimeException(e));
    } catch (Exception e) {
      throw(new RuntimeException(e));
    }
  }  

  /* **********************************
   * LIST METHODS FOR WORKING WITH JEN
   */
  /**
   * Obtain a {@link List} of ASM {@link Type} instances representing the
   * supplied {@link Class} {@code List}.
   * 
   * @param classes The {@code List} of {@link java.lang.Class}es to obtain a type list for.
   * 
   * @return A new {@code List} of {@code Type} instances.
   */
  public static List<Type> typeForClassList(List<Class<?>> classes) {
    if (classes == null) return emptyList();
    ArrayList<Type> ret = new ArrayList<Type>(classes.size());

    for (Class c : classes) {
      ret.add(Type.getType(c));     
    }
    
    return ret;
  }

  /**
   * Obtain a {@link List} of ASM {@link Class} instances representing the
   * supplied {@link Type} {@code List}. The specified {@link ClassLoader}
   * is used to load classes. Bear in mind that {@code Class} objects do not
   * necessarily exist for all {@code Type} instances.
   * 
   * @param types The {@code List} of {@code Class}es to obtain a type list for.
   * @param loader The {code ClassLoader} to use for {@code Class} resolution.
   * 
   * @return A new {@code List} of {@code Class} instances.
   * 
   * @throws ClassNotFoundException if any of the classes represented in the specified
   *         {@code Type} list cannot be found.
   */
  public static List<Class<?>> classForTypeList(List<Type> types, ClassLoader loader) 
  throws ClassNotFoundException {
    if (types == null) return emptyList();
    ArrayList<Class<?>> ret = new ArrayList<Class<?>>(types.size());

    for (Type t : types) {
      ret.add(loader.loadClass(t.getClassName()));     
    }
    
    return ret;
  }

  /**
   * Obtain a {@link List} of <strong>binary</strong> class-names representing the
   * supplied {@code List} of {@link Type}s.
   * 
   * @param types The {@code List} of {@code Type}s to obtain
   *        a class-name list for.
   * 
   * @return A new {@code List} of class-names instances.
   */
  public static List<String> stringForTypeList(List<Type> types) {
    if (types == null) return emptyList();
    ArrayList<String> ret = new ArrayList<String>(types.size());

    for (Type t : types) {
      ret.add(t.getInternalName());     
    }
    
    return ret;
  }
  
  /**
   * Obtain a {@link List} of ASM {@link Type} instances representing the
   * supplied {@code List} of class-names.
   * 
   * @param classNames The {@code List} of {@code String} classnames to obtain
   *        a type list for.
   * 
   * @return A new {@code List} of {@code Type} instances.
   */
  public static List<Type> typeForStringList(List<String> classNames) {
    if (classNames == null) return emptyList();
    ArrayList<Type> ret = new ArrayList<Type>(classNames.size());

    for (String c : classNames) {
      ret.add(Type.getType(SoftUtils.binaryToDescriptor(c)));     
    }
    
    return ret;
  }
  
  /**
   * Obtain a {@link List} of {@link Class} instances representing the classes of
   * the supplied {@link Object} {@code List}.
   * 
   * @param objects The {@code List} of {@code Object}s to obtain a class list for.
   * 
   * @return A new {@code List} of {@code Class} instances.
   */
  public static List<Class<?>> classForObjectList(List<Object> objects) {
    if (objects == null) return emptyList();
    ArrayList<Class<?>> ret = new ArrayList<Class<?>>(objects.size());
    
    for (Object o : objects) {
      ret.add(o.getClass());     
    }
    
    return ret;
  }

  /* **********************************
   * ARRAY METHODS FOR WORKING WITH ASM
   */
  /**
   * Obtain an array of ASM {@link org.objectweb.asm.Type} instances representing the
   * supplied class array.
   * 
   * @param classes The array of {@link java.lang.Class}es to obtain a type array for.
   * 
   * @return A new array of {@link org.objectweb.asm.Type} instances.
   */
  public static Type[] typeForClassArray(Class<?>[] classes) {
    if (classes == null) return new Type[0];
    Type[] ret = new Type[classes.length];
    
    for (int i = 0; i < classes.length; i++) { 
      ret[i] = Type.getType(classes[i]);     
    }
    
    return ret;
  }
  
  /**
   * Obtain an array of Java {@link java.lang.Class}es representing the types found in the
   * supplied ASM {@link org.objectweb.asm.Type} array.
   * 
   * @param objects The array of {@link java.lang.Object}es to obtain a class-name array for.
   * 
   * @return A new array of {@link java.lang.Class}es.
   */
  public static Class<?>[] classForObjectArray(Object[] objects) {
    if (objects == null || objects.length == 0) return new Class[0];
    Class[] ret = new Class[objects.length];
    
    for (int i = 0; i < objects.length; i++) { 
      ret[i] = objects[i].getClass();     
    }
    
    return ret;
  }
    
  /**
   * Obtain an ASM method descriptor representing the supplied Java constructor.
   * 
   * @param ctor The {@link java.lang.reflect.Constructor} for which a 
   *        descriptor is required.
   * 
   * @return A new void method descriptor for the constructor.
   */
  public static String getConstructorDescriptor(Constructor<?> ctor) {
    String r = Type.getMethodDescriptor(Type.VOID_TYPE,
        SoftUtils.typeForClassArray(ctor.getParameterTypes()));
    return r;
  }

  /* **********************************
   * GENERAL HELPERS
   */
  /**
   * Generate bytecode from the given SoftClass to an 
   * {@link java.io.OutputStream}.
   * 
   * @param sc The {@link SoftClass} that is to be generated.
   * @param stream The {@link java.io.OutputStream} to which bytecode should
   *        be written.
   *        
   * @throws IOException to indicate an error while writing to the stream.
   */
  public static void generateToStream(SoftClass sc, OutputStream stream)
  throws IOException {
    ClassWriter writer = new ClassWriter(true);
    sc.accept(writer);
    stream.write(writer.toByteArray());
  }

  /* Null-safe arg helpers
   */ 
  /**
   * Helper for null-safe argument usage. This method checks the supplied 
   * argument against {@code null}, and returns it if not. If the argument
   * is {@code null}, a {@link NullArgumentException} is thrown with the
   * specified caller and argument name. The intention is to provide more
   * meaningful error messages than straight {@link NullPointerException}.
   * <p/>
   * This version is intended for use from constructors, and will automatically
   * append the name {@code <init>}.
   * 
   * @param caller The class that declares the calling method.
   * @param argName The name of the argument.
   * @param arg The argument to check against {@code null}.
   * 
   * @return {@code arg} if non-{@code null}.
   * 
   * @throws NullArgumentException if {@code arg} is found to be {@code null}.
   */
  public static final <T extends Object> T nullSafeArg(Class caller, String argName, T arg) {
    return nullSafeArg(caller,"<init>",argName,arg);
  }
  
 /**
  * Helper for null-safe argument usage. This method checks the supplied 
  * argument against {@code null}, and returns it if not. If the argument
  * is {@code null}, a {@link NullArgumentException} is thrown with the
  * specified caller and argument name. The intention is to provide more
  * meaningful error messages than straight {@link NullPointerException}.
  * <p/>
  * This version takes method information from a supplied {@link Method}.
  * 
  * @param caller The method that is calling the null-check.
  * @param argName The name of the argument.
  * @param arg The argument to check against {@code null}.
  * 
  * @return {@code arg} if non-{@code null}.
  * 
  * @throws NullArgumentException if {@code arg} is found to be {@code null}.
  */
  public static final <T extends Object> T nullSafeArg(Method caller, String argName, T arg) {
    return nullSafeArg(caller.getDeclaringClass(),caller.getName(),argName,arg);
  }
  
  /**
   * Helper for null-safe argument usage. This method checks the supplied 
   * argument against {@code null}, and returns it if not. If the argument
   * is {@code null}, a {@link NullArgumentException} is thrown with the
   * specified caller and argument name. The intention is to provide more
   * meaningful error messages than straight {@link NullPointerException}.
   * <p/>
   * This version allows the calling class and method to be specified separately.
   * 
   * @param callerClass The class that declares the calling method.
   * @param caller The calling method.
   * @param argName The name of the argument.
   * @param arg The argument to check against {@code null}.
   * 
   * @return {@code arg} if non-{@code null}.
   * 
   * @throws NullArgumentException if {@code arg} is found to be {@code null}.
   */
  public static final <T extends Object> T nullSafeArg(Class callerClass, String caller, String argName, T arg) {
    return nullSafeArg(callerClass.getName(),caller,argName,arg);  
  }
  
  /**
   * Helper for null-safe argument usage. This method checks the supplied 
   * argument against {@code null}, and returns it if not. If the argument
   * is {@code null}, a {@link NullArgumentException} is thrown with the
   * specified caller and argument name. The intention is to provide more
   * meaningful error messages than straight {@link NullPointerException}.
   * <p/>
   * This version allows the calling class and method to be specified separately.
   * 
   * @param callerClass The class that declares the calling method.
   * @param caller The calling method.
   * @param argName The name of the argument.
   * @param arg The argument to check against {@code null}.
   * 
   * @return {@code arg} if non-{@code null}.
   * 
   * @throws NullArgumentException if {@code arg} is found to be {@code null}.
   */
  public static final <T extends Object> T nullSafeArg(String callerClass, String caller, String argName, T arg) {
    if (arg == null) throw(new NullArgumentException(callerClass,caller,argName));
    return arg;    
  }
  
  /**
   * Attempts to define a class in the specified classloader by reflectively invoking
   * the {@link ClassLoader#defineClass(String, byte[], int, int)} method.
   * 
   * @param loader The <code>ClassLoader</code> in which the class should be defined.
   * @param bytes The bytecode representing this class.
   * 
   * @return A new {@link Class} instance representing the class.
   * 
   * @throws InvocationTargetException to indicate that the classloader threw an 
   *         exception (available via the {@link Throwable#getCause()} method).
   *         
   * @throws RuntimeException To wrap any <code>NoSuchMethod</code> or 
   *         <code>IllegalAccess</code> thrown by the classloader. A well formed 
   *         ClassLoader should never cause these to be thrown, hence the unchecked
   *         nature.
   *         
   * @throws SecurityException if the caller does not have the required JVM permissions
   *         for reflective invocation and/or class definition.                  
   */
  public static Class defineClass(ClassLoader loader, byte[] bytes) 
  throws InvocationTargetException {
    return defineClassHelper(loader,bytes);
  }
  
  /**
   * Converts a language class-name to a to an internal <em>type descriptor</em>.
   * 
   * @param javaName The language name to convert.
   * 
   * @return The given language name as a type descriptor. 
   */
  public static String javaToDescriptor(String javaName) {
    return binaryToDescriptor(javaToBinary(javaName));
  }
  
  /**
   * Converts a binary class-name to an internal <em>type descriptor</em>.
   * 
   * @param internalName The binary name to convert.
   * 
   * @return The given binary name as a type descriptor. 
   */
  public static String binaryToDescriptor(String internalName) {
    if (internalName == null) return null;
    return "L"+internalName+";";
  }
  
  /**
   * Converts a language class-name to a binary class-name.
   * 
   * @param javaName The language name to convert.
   * 
   * @return The binary version of the given language name.
   */
  public static String javaToBinary(String javaName) {
    if (javaName == null) return null;
    return javaName.replace(".","/");
  }
    
  /**
   * Converts a binary class-name to a language class-name.
   * 
   * @param internalName The binary name to convert.
   * 
   * @return The language version of the given binary name.
   */
  public static String binaryToJava(String internalName) {
    if (internalName == null) return null;
    return internalName.replace("/",".");
  }
    
  /**
   * Returns a default {@link ClassLoader}. This is the first available of:
   * <p/>
   * <ul>
   *    <li>The current thread context loader</li>
   *    <li>SoftClass.class.getClassLoader()</li>
   *    <li>Class.class.getClassLoader()</li>
   *    <li>ClassLoader.getSystemClassLoader()</li>
   * </ul>
   * If none of these return a valid loader, then a {@link ClassloaderUnavailableException}
   * is thrown, and you should perhaps check your configuration.
   * 
   * @return A default class loader.
   */
  public static final ClassLoader defaultClassLoader() {
    return defaultLoaderHelper("<runtime>");    
  }
  
  /**
   * Returns a new ASM {@link org.objectweb.asm.commons.Method} representing the
   * specified {@link SoftMethod}.
   * 
   * @param sm The {@code SoftMethod} for which a {@code Method} is required.
   * 
   * @return A new {@code Method}.
   */
  public static org.objectweb.asm.commons.Method asmForSoftMethod(SoftMethod sm) {
    return new org.objectweb.asm.commons.Method(
        sm.getName(),
        sm.getReturnType(),
        sm.getArgumentTypes().toArray(new Type[sm.getArgumentTypes().size()]));   
  }

  
  /* **********************************
   * PROTECTED API
   */
  static Class defineClassHelper(ClassLoader loader, byte[] bytes) 
  throws InvocationTargetException {
    
    Class c = null;
    
    try {
      Method defineClass = ClassLoader.class.getDeclaredMethod(
          "defineClass", new Class[] { String.class, byte[].class, int.class, int.class });

      defineClass.setAccessible(true);      
      Object o = defineClass.invoke(loader,new Object[] { null, bytes, new Integer(0), new Integer(bytes.length)});
      if (o != null) c = (Class)o;
    } catch (NoSuchMethodException e) {      
      throw(new RuntimeException(e));
    } catch (IllegalAccessException e) {      
      throw(new RuntimeException(e));
    }
    
    if (c != null) {
      return c;
    } 

    throw(new RuntimeException("The supplied classloader is broken"));          
  }
  
  static final ClassLoader defaultLoaderHelper(String name) { 
    // Order of preference, different JVMs treat loaders differently...
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    if (loader == null) loader = SoftClass.class.getClassLoader();
    if (loader == null) loader = Class.class.getClassLoader();
    if (loader == null) loader = ClassLoader.getSystemClassLoader();
    if (loader == null) throw(new ClassloaderUnavailableException(name));
    return loader;
  }
  
  static final ClassLoader defaultLoaderHelper() {
    return defaultLoaderHelper("<initialization>");    
  }
  
  /* helper that looks for a class we're expecting to be here (i.e. a superclass or similar)
   * it throws NoClassDefFoundError instead of CNFE because it's used in places where we
   * should _already_ have checked the class is available (or the jvm should if it hasn't
   * been modified yet). 
   */
  static final Class findClassHelper(String name) {
    return findClassHelper(defaultLoaderHelper(),name);
  }
  
  static final Class findClassHelper(ClassLoader loader, String name) {
    try {
      return nullSafeArg(SoftUtils.class,"findClassHelper","loader",loader).loadClass(
          ""+nullSafeArg(SoftUtils.class,"findClassHelper","name",name));
    } catch (ClassNotFoundException e) {
      throw(new NoClassDefFoundError(name));
    }    
  }
   
  private SoftUtils() { }
}
