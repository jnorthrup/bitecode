/* SoftProxy.java - Supports proxy generation with Jen
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
 * File version: $Revision: 1.12 $ $Date: 2005/10/31 01:50:45 $
 * Originated: Oct 3, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import jen.NullArgumentException;
import jen.SoftClass;
import jen.SoftUtils;
import jen.fields.GeneratedSoftField;
import jen.methods.GeneratedSoftMethod;

import static jen.SoftClass.ACC_PUBLIC;
import static jen.SoftClass.ACC_STATIC;
import static jen.SoftClass.ACC_FINAL;

import static jen.SoftUtils.nullSafeArg;
import static jen.SoftUtils.defaultClassLoader;
import static jen.tools.Toolbox.*;

import static org.objectweb.asm.Opcodes.ACC_SUPER;

/** 
 * Provides static methods for creating <em>dynamic proxy</em> 
 * {@link SoftClass}es, {@link Class}es, and instances. {@code SoftProxy}
 * provides a mostly-compatible, slightly more capable replacement for
 * the platform proxy support in JDK 1.3+. For example:
 * <p/>
 * <pre>      InvocationHandler handler = new MyInvocationHandler(...);
 *      Class&lt;Foo&gt; proxyClass = SoftProxy.createClass(Foo.class)
 *      Foo f = proxyClass.getConstructor(InvocationHandler.class).newInstance(handler);</pre>
 * <p/>
 * Or more simply:
 * <pre>      Foo f = SoftProxy.newInstance(Foo.class,handler);</pre>
 * <p/>
 * Where {@code Foo} can be any class or interface, abstract or concrete. 
 * <p/>
 * The external behaviour of a proxy at runtime is entirely defined by the 
 * {@link InvokerGenerationStrategy} that was used used to generate it, making
 * it impossible to provide any general documentation on the runtime operation of proxies.
 * This documentation will instead focus on the specifics of {@code SoftProxy}
 * in terms of API usage and operation, while leaving the documentation specific to
 * proxy operational behaviour of proxy instances for the strategy implementation
 * references themselves.<br/>
 * The default strategy generation methods (i.e. those that do not accept an instance
 * of {@code InvokerGenerationStrategy}) provided by this class all generate proxy
 * classes using the {@link DefaultInvokerStrategy} implementation. See that class'
 * documentation for details of default proxy behaviour.<br/> 
 * <p/>
 * Generated proxy {@code Class} objects should not be considered as full classes, and should 
 * instead be viewed as a temporary dynamic reference to the proxied class object, valid only as 
 * long as at least one reference is retained in application code. Package selection 
 * functions as for {@code Proxy}, and the generated class name is undefined. The convention
 * suggested in {@code Proxy}'s API documentation (that of using the {@code $Proxy}
 * namespace for proxies) <strong>is</strong> observed by {@code SoftProxy}, with
 * an additional suffix carrying the proxy serial number (since it is possible to proxy the
 * same class multiple times, possibly with different strategies). Internally, proxy classes 
 * are defined directly within the classloader supplied (or a default choice if none is 
 * specified), and so the actual lifecyle with respect to garbage collection is dependent 
 * upon that classloader. If you need to ensure proxy classes can be garbage collected on 
 * within their own lifecycle you will need to generate to a SoftClass, and define the class
 * manually in a temporary classloader. Do note, however, that this places additional 
 * restrictions on the proxy generation (specifically with respect to proxying non-public 
 * classes or interfaces).
 * <p/>
 * {@code SoftProxy} is a purely static class with no instances or volatile shared state, and should 
 * be safe for use from multiple concurrent threads. Note, however, that {@code SoftClass} 
 * itself <strong>is not</strong> thread-safe, and care must be taken that no single instance 
 * is shared between threads, especially where it is being mutated before generation.
 * It is recommended that the {@link #createProxyClass(Class)} and {@link #newProxyInstance(Class, InvocationHandler)}
 * methods be preferred over manual generation to a {@code SoftClass} unless you have a specific
 * requirement, for example, to serialize the proxy class rather than define it immediately. 
 *  
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.12 $ $Date: 2005/10/31 01:50:45 $ 
 */
// IMPL NOTE: Yes, I damn well do think all-static is right for this. 'kay?
public class SoftProxy
{
  static final int PROXY_MODIFIERS = ACC_PUBLIC | ACC_SUPER;
  
  /* ********************************** */ 
  /**
   * Holds the sole instance of {@link DefaultInvokerStrategy}, used to generate
   * proxies when no custom implementation is supplied.
   */
  public static final InvokerGenerationStrategy DEFAULT_INVOKER_STRATEGY = 
    new DefaultInvokerStrategy();
  
  static final String SERIAL_FIELD = "_SCP_GEN";
  
  /* ********************************** */ 
  private static final Object serialLock = new Object(); 
  private static int serial = 0;
  
  /* ********************************** 
   * The <clinit> implementation
   */
  private static final class ProxyClinit extends GeneratedSoftMethod
  {
    private final Map<String, Method> methodMap;

    private ProxyClinit(SoftClass softClass, int access, String name, Map<String, Method> methodMap) {
      super(softClass, access, name);
      this.methodMap = methodMap;
    }

    protected void generateCode(GeneratorAdapter gen) {        
      Label ltry = gen.mark();        
      
      for (String key : methodMap.keySet()) {
        Method m = methodMap.get(key);
        Class[] paramTypes = m.getParameterTypes();
        
        gen.push(Type.getType(m.getDeclaringClass()));                                       // MethodClass         
        
        // Class.getMethod call                                                               
        gen.push(m.getName());                                                               // MethodClass .. methodName 
        gen.push(paramTypes.length);                                                         // MethodClass .. methodName .. argc
        gen.newArray(Type.getType(Class.class));                                             // MethodClass .. methodName .. paramTypes[]
        
        // BUILD ARRAY OF METHOD PARAM TYPES - need the index
        for (int i = 0; i < paramTypes.length; i++) {
          gen.dup();                                                                         // MethodClass .. methodName .. pT[] .. pT[]
          gen.push(i);                                                                       // MethodClass .. methodName .. pT[] .. pT[] .. idx
          
          // Primitive types have to be handled with care, or ASM barfs on 'em.
          // It appears it's using the type name to lookup a class - NoClassDefFound 'I'.
          // In any event the accepted way to do this is like GETSTATIC on the Wrapper.TYPE
          Type paramType = Type.getType(paramTypes[i]);          
          if (paramTypes[i].isPrimitive()) {
            char primType = paramType.getDescriptor().charAt(0);              
            gen.getStatic((primitiveWrapperType(primType)) ,"TYPE",CLASS_TYPE);              // MethodClass .. methodName .. pT[] .. pT[] .. idx .. paramType[idx]              
          } else {
            gen.push(Type.getType(paramTypes[i]));                                           // MethodClass .. methodName .. pT[] .. pT[] .. idx .. paramType[idx]
          }
          
          gen.arrayStore(CLASS_TYPE);                                                        // MethodClass .. methodName .. pT[]                                 
        }          
        
        gen.invokeVirtual(CLASS_TYPE,CLASS_GETMETHOD);                                       // Method
        gen.putStatic(getSoftClass().getType(),methodFieldName(key),METHOD_TYPE);            // ...  
      }
      
      gen.returnValue();
      
      // Exception handler
      Label lcatch = gen.mark();
      gen.catchException(ltry,lcatch,Type.getType(NoSuchMethodException.class));        
      gen.throwException(Type.getType(RuntimeException.class),"Proxy: One or more expected Methods are missing.");
      
      gen.endMethod();
    }
  }
  
  static int nextSerial() {
    synchronized (serialLock) {
      return serial++;
    }
  }
  
  /* ********************************** 
   * IMPLEMENTATION HELPERS
   */
  /* Should this method be proxied? */
  private static boolean acceptProxyMethod(Method m) {
    return ((m.getModifiers() & ACC_FINAL) == 0) &&
           ((m.getModifiers() & ACC_STATIC) == 0);
  }
  
  private static HashMap<String, Method> initialMethodMap() {
    HashMap<String,Method> methodMap = new HashMap<String,Method>();
    
    // public non-final Object methods    
    methodMap.put("equals(Ljava/lang/Object;)Z",knownMethod(Object.class,"equals",Object.class));
    methodMap.put("hashCode()I",knownMethod(Object.class,"hashCode"));
    methodMap.put("toString()Ljava/lang/String;",knownMethod(Object.class,"toString"));
    
    return methodMap;
  }
  
  private static String proxyName(String pkg, String name, int serial) {
    if ((pkg != null) && (!"".equals(pkg)) && (!pkg.endsWith("."))) pkg += "."; 
    return pkg+name+"$Proxy"+serial;    
  }
  
  /* Helper to create a proxy's base-name, by traversing any nested classes */
  private static String baseName(Class<?> c) {
    Class<?> t = c;
    StringBuilder b = new StringBuilder();
    
    while (t.getEnclosingClass() != null) {
      t = t.getEnclosingClass();
      b.insert(0,t.getSimpleName() + "$");      
    }
    
    return b.append(c.getSimpleName()).toString();
  }
    
  /* nobody's perfect */
  private static InvokerGenerationStrategy nullSafeStrategy(InvokerGenerationStrategy in) {
    return (in != null ? in : DEFAULT_INVOKER_STRATEGY);
  }
  
  /* ********************************** 
   * GENERATION LINE
   */
  /* Prepares the given SoftClass to be a proxy for the given classes. This has the following
   * responsibilities:
   * 
   *  1) Ensure that the class array contains at most one non-interface class,
   *     and at most one non-public class or interface.
   *     
   *  2) Set the SoftClass' package and name appropriately using the next serial.
   *     If a non-interface class is amongst those proxied, it's name is used as
   *     the base name. Otherwise, the first class-name is used.
   *  
   *  3) Ensure that the SoftClass' extends and implements clauses are setup.
   *  
   *  4) Ensure the SoftClass' modifiers are set properly.
   *  
   *  6) Add an appropriate constructor and invocation handler field.
   *  
   *  5) Return a Map of all methods that are to be generated on this 
   *     SoftClass. This should have all the public methods from the classes.
   *     In case of duplicates, the Method will be from the first class encountered.
   *     
   * It doesn't do anything with setting up the Method fields, or generating the actual
   * invocation methods.
   */
  /* 06/Oct/05 - Added checking of Classes in ClassLoader. This is kind of strange given that
   *             proxies can be serialized, but it's safer. If we don't do this we could have
   *             wierd exceptions in the clinit. */
  /* 23/Oct/05 - Added null element protection in class array, fixed to proxy from local Class
   *             (from supplied loader) _just in case_. More consistent behaviour in face of
   *             null classes, empty classes.
   * 28/Oct/05 - Classloader at this point is now parent - proxy loader is instantiated
   *             only if using the direct class or instance methods.             
   */
  private static Map<String,Method> prepareProxySoftClass(ClassLoader loader, SoftClass sc, Class<?>[] classes, int serial) {
    HashMap<String,Method> methodMap = initialMethodMap();
    String pkgName = null;
    String baseName = null;
     
    Class<?>[] nsClasses = nullSafeArg(SoftProxy.class,"create","clazz",classes);
    if (nsClasses.length == 0) throw(new IllegalArgumentException("SoftProxy: No classes in supplied array"));    
    
    for (Class c : nsClasses) {
      
      if (c == null) throw(new IllegalArgumentException("SoftProxy: Inconsistent class array supplied"));    
      
      // Ensure this class is visible from our loader. 
      // Not all classloaders throw from loadClass, so be defensive about that.
      // We'll use the local one to actually proxy (it will almost certainly be the
      // same instance anyway).
      Class rc = null;
      try {
        rc = loader.loadClass(c.getName());
      } catch (ClassNotFoundException e) { /* nothing */ }
      
      if (rc == null) {
        throw(new IllegalArgumentException("SoftProxy: "+rc+" is not visible from the supplied ClassLoader"));        
      }
      
      // If we've got a final, we may as well bomb out now...
      if ((rc.getModifiers() & ACC_FINAL) != 0) {
        throw(new IllegalArgumentException("SoftProxy: Cannot proxy final class "+rc));
      }
      
      // If we've got any non-public classes, we'll need to use their package
      if ((rc.getModifiers() & ACC_PUBLIC) == 0) {
        if (pkgName != null) {
          throw(new IllegalArgumentException("SoftProxy: Only a single non-public class or interface allowed per proxy class."));
        } else {
          pkgName = rc.getPackage().getName();
        }
      }
            
      // Base name should be either first class, or the one non-interface if there is one
      if ((baseName == null) || (!c.isInterface())) {
        baseName = baseName(rc);
      }
      
      // impl / extends
      if (rc.isInterface()) {
        sc.addInterface(rc.getName());
      } else {
        if (!"java.lang.Object".equals(sc.getSuperClassName())) {
          throw(new IllegalArgumentException("SoftProxy: Only a single non-interface class allowed per proxy class."));          
        } else {
          sc.setSuperClass(rc);
        }
      }
      
      for (Method m : rc.getMethods()) {
        String methodDesc = namedMethodDescriptor(m);
        if (acceptProxyMethod(m)) {
          if (!methodMap.containsKey(methodDesc) && (!java.lang.Object.class.equals(m.getDeclaringClass()))) {
            methodMap.put(methodDesc,m);          
          }
        }
      }        
    }    
    
    // SETUP THE SOFTCLASS
    sc.setModifiers(PROXY_MODIFIERS);
    sc.setName(proxyName(pkgName != null ? pkgName : "",baseName,serial));
    
    return methodMap;
  }
  
  /* generates a public static final long field with the current time as init value */
  private static void generateProxySerial(SoftClass sc, int serial) {
    sc.putSoftField(new GeneratedSoftField(sc,
        ACC_STATIC | ACC_FINAL | ACC_PUBLIC,
        SERIAL_FIELD,
        null,
        int.class,
        serial));
  }
  
  /* Generates the class init (<clinit>) method for a Proxy class. This is responsible for
   * populating the static Method fields. 
   */
  private static void generateProxyClassInit(SoftClass sc, Map<String, Method> methodMap) {
    sc.putSoftMethod(new ProxyClinit(sc, ACC_STATIC, "<clinit>", methodMap));
  }
  
  /* Generates proxy invokers for all methods in the supplied Map, and creates the static
   * final fields that will hold the Method object for that method. This is the Method that
   * is passed by the invoker to the InvocationHandler 'invoke' method. The field is named
   * _Mnnnnnnnn , where 'nnnn' is the absolute value of the String key from the map.
   * 
   * The static final fields obviously don't have initial values - that's handled by the
   * <clinit> method (generated separately). 
   * 
   * The invoker methods themselves are generated by the strategy in use.
   */
  private static void generateProxyInvokers(SoftClass sc, Map<String,Method> methodMap, InvokerGenerationStrategy strategy) {
    for (Method m : methodMap.values()) {
      sc.putSoftField(new GeneratedSoftField(sc,
          ACC_STATIC | ACC_FINAL,
          methodFieldName(namedMethodDescriptor(m)),
          Method.class));        
      
      strategy.generateInvoker(sc,m);
    }
  }
    
  /* ********************************** 
   * MAIN GENERATION METHOD
   */
  static SoftClass generateSoftClassWorker(ClassLoader loader, Class<?>[] clazz, InvokerGenerationStrategy strategy) {
    if (!strategy.canProxyClasses(clazz)) {
      throw(new IllegalArgumentException("Proxy strategy '"+strategy.getClass().getName()+"' cannot proxy the specified class(es)"));      
    }
    
    int serial = nextSerial();
    
    SoftClass sc = new SoftClass(0,"TEMPORARY NAME");    
    Map<String,Method> methodMap = prepareProxySoftClass(loader, sc,clazz,serial);
    sc.setClassLoader(loader);
    
    // Generate the gubbins
    generateProxySerial(sc,serial);
    generateProxyClassInit(sc,methodMap);
    generateProxyInvokers(sc,methodMap, strategy);
    
    strategy.afterGeneration(sc);    
    return sc;
  }
  
  /* ********************************** 
   * PUBLIC API
   */
  /**
   * Create a new {@link SoftClass} that will generate a dynamic proxy for the specified
   * classes. This method allows the {@link InvokerGenerationStrategy} used to generate
   * the proxy invocation methods and instance initialization to be supplied. 
   * <p/>
   * The supplied class array must conform to the same requirements as for 
   * {@link #createProxyClass(ClassLoader, Class[])}.
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   *        
   * @param clazz The {@link Class}es for which a proxy is to be generated.
   * 
   * @param strategy An {@code InvokerGenerationStrategy} that will be called back to
   *        generate the proxy class' instance members.
   * 
   * @return A new {@link SoftClass} configured to generate a proxy for the specified
   *         {@code Class}.
   */
  /* WORKER */
  public static SoftClass createProxy(ClassLoader loader, Class<?>[] clazz, InvokerGenerationStrategy strategy) {
    return generateSoftClassWorker(nullSafeArg(SoftProxy.class,"create","loader",loader),
                                   nullSafeArg(SoftProxy.class,"create","clazz",clazz),
                                   nullSafeStrategy(strategy));  // silently replaces null with DEFAULT_INV_STRAT
  }
  
  /**
   * Create a new {@link SoftClass} that will generate a dynamic proxy for the specified
   * classes. This method allows the {@link InvokerGenerationStrategy} used to generate
   * the proxy invocation methods and instance initialization to be supplied. 
   * <p/>
   * The supplied class array must conform to the same requirements as for 
   * {@link #createProxyClass(ClassLoader, Class[])}.
   * 
   * @param clazz The {@link Class}es for which a proxy is to be generated.
   * 
   * @param strategy An {@code InvokerGenerationStrategy} that will be called back to
   *        generate the proxy class' instance members.
   * 
   * @return A new {@link SoftClass} configured to generate a proxy for the specified
   *         {@code Class}.
   */
  public static SoftClass createProxy(Class[] clazz, InvokerGenerationStrategy strategy) {
    return createProxy(SoftUtils.defaultClassLoader(),clazz,strategy);
  }
  
  /**
   * Create a new {@link SoftClass} that will generate a dynamic proxy for the specified
   * classes.
   * <p/>
   * The supplied class array must conform to the same requirements as for 
   * {@link #createProxyClass(ClassLoader, Class[])}.
   * 
   * @param clazz The {@link Class}es for which a proxy is to be generated.
   * 
   * @return A new {@link SoftClass} configured to generate a proxy for the specified
   *         {@code Class}.
   */
  public static SoftClass createProxy(Class[] clazz) {
    return createProxy(SoftUtils.defaultClassLoader(),clazz,DEFAULT_INVOKER_STRATEGY);
  }
  
  /**
   * Create a new {@link SoftClass} that will generate a dynamic proxy for the specified
   * single class. This method can be used in preference to the direct class or instance
   * generation methods if you have special requirements that cannot be handled by a
   * custom invoker strategy.
   * 
   * @param clazz The {@link Class} for which a proxy is to be generated.
   * 
   * @return A new {@link SoftClass} configured to generate a proxy for the specified
   *         {@code Class}.
   */
  public static SoftClass createProxy(Class<?> clazz) {    
    return createProxy(new Class<?>[] { nullSafeArg(SoftProxy.class,"newProxyInstance","clazz",clazz) });
  }
  
  /**
   * Create a new dynamic proxy class for the specified {@link Class}es, defined
   * in the specified {@link ClassLoader}. The supplied class
   * array may contain any number of interface classes, no more than one non-interface 
   * (abstract or concrete) class, and no more than one non-public class or 
   * interface. See the {@link SoftProxy class description} for details on the
   * limitations of this proxy implementation.
   * <p/>
   * This method allows the {@link InvokerGenerationStrategy} used to generate
   * the proxy invocation methods and instance initialization to be supplied. As a result,
   * this method can make no guarantees about the public layout of the proxy class.
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   * 
   * @param classes The array of {@link Class}es for which a proxy is to be obtained.
   * 
   * @param strategy An {@code InvokerGenerationStrategy} that will be called back to
   *        generate the proxy class' instance members.
   * 
   * @return The new proxy {@code Class}.
   */
  /* CLASS WORKER */
  public static Class<?> createProxyClass(ClassLoader loader, Class<?>[] classes, InvokerGenerationStrategy strategy) {
    if (loader == null) loader = defaultClassLoader();
    return createProxy(loader, classes,strategy).defineClass(loader);
  }
  
  /**
   * Create a new dynamic proxy class for the specified {@link Class}es, defined
   * in a default classloader. The supplied class array may contain any number of
   * interface classes, no more than one non-interface (abstract or concrete) class, 
   * and no more than one non-public class or interface. See the 
   * {@link SoftProxy class description} for details on the limitations of this 
   * proxy implementation.
   * <p/>
   * This method allows the {@link InvokerGenerationStrategy} used to generate
   * the proxy invocation methods and instance initialization to be supplied. As a result,
   * this method can make no guarantees about the public layout of the proxy class.
   * 
   * @param classes The array of {@link Class}es for which a proxy is to be obtained.
   * 
   * @param strategy An {@code InvokerGenerationStrategy} that will be called back to
   *        generate the proxy class' instance members.
   * 
   * @return The new proxy {@code Class}.
   */
  public static Class<?> createProxyClass(Class<?>[] classes, InvokerGenerationStrategy strategy) {
    return createProxyClass(defaultClassLoader(), classes,strategy);
  }
  
  /**
   * Create a new dynamic proxy class for the specified {@link Class}es, defined
   * in the specified {@link ClassLoader}. The supplied class
   * array may contain any number of interface classes, no more than one non-interface 
   * (abstract or concrete) class, and no more than one non-public class or 
   * interface. See the {@link SoftProxy class description} for details on the
   * limitations of this proxy implementation.  
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   * @param classes The array of {@link Class}es for which a proxy is to be obtained.
   * 
   * @return The new proxy {@code Class}.
   */
  public static Class<?> createProxyClass(ClassLoader loader, Class<?>[] classes) {
    return createProxyClass(loader, classes,DEFAULT_INVOKER_STRATEGY);
  }
  
  /**
   * Create a new dynamic proxy class for the specified {@link Class}es, defined
   * in a default classloader. The order of preference for the class-loader 
   * is:
   * <p/>
   * <ul>
   *    <li>The current {@link Thread#currentThread()}{@link Thread#getContextClassLoader() getContextClassLoader()}</li>
   *    <li>{@code SoftClass.class.getClassLoader()}</li>  
   *    <li>{@code Class.class.getClassLoader()}</li>  
   *    <li>{@code ClassLoader.getSystemClassLoader()}</li>  
   * </ul>
   * <p>
   * The supplied class array must conform to the same requirements as for 
   * {@link #createProxyClass(ClassLoader, Class[])}.
   * 
   * @param classes The array of {@link Class} for which a proxy is to be obtained.
   * 
   * @return The new proxy {@code Class}.
   */
  public static Class<?> createProxyClass(Class<?>[] classes) {
    return createProxyClass(defaultClassLoader(),classes);
  }
    
  /**
   * Create a new dynamic proxy class for the specified (single) {@link Class}, defined
   * in the specified {@link ClassLoader}. This method allows the 
   * {@link InvokerGenerationStrategy} used to generatethe proxy invocation methods 
   * and instance initialization to be supplied. As a result, this method can make 
   * no guarantees about the public layout of the proxy class.
   * <p/>
   * This method uses the generic type of the supplied class to return a typed 
   * {@code Class} object to allow the resulting {@code Class} to be used without
   * casting. See the {@link SoftProxy class description} for more information
   * on proxy typing. 
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   *        
   * @param clazz The {@link Class} for which a proxy is to be obtained.
   * 
   * @param strategy An {@code InvokerGenerationStrategy} that will be called back to
   *        generate the proxy class' instance members.
   * 
   * @return The proxy {@code Class}.
   */
  @SuppressWarnings("unchecked")  // if not safe, something is v. wrong, and we want CCE anyway
  public static <T> Class<T> createProxyClass(ClassLoader loader, Class<T> clazz, InvokerGenerationStrategy strategy) {
    return (Class<T>)createProxyClass(loader,
        new Class<?>[] { nullSafeArg(SoftProxy.class,"newProxyInstance","clazz",clazz) },
        strategy);    
  }  
  
  /**
   * Create a new dynamic proxy class for the specified (single) {@link Class}, defined
   * in a default {@link ClassLoader}. See {@link #createProxyClass(Class[])}
   * for details of order of preference for the class-loader. This is a convenience
   * method that will generate the proxy with the {@link DefaultInvokerStrategy}.
   * <p/>
   * This method uses the generic type of the supplied class to return a typed 
   * {@code Class} object to allow the resulting {@code Class} to be used without
   * casting. See the {@link SoftProxy class description} for more information
   * on proxy typing. 
   * 
   * @param clazz The {@link Class} for which a proxy is to be obtained.
   * 
   * @return The new proxy {@code Class}.
   */
  public static <T> Class<T> createProxyClass(Class<T> clazz) {
    return createProxyClass(defaultClassLoader(), clazz, DEFAULT_INVOKER_STRATEGY);
  }
  
  /**
   * Create a new dynamic proxy instance for the specified {@link Class}es, defined
   * in the specified {@link ClassLoader}. The proxy will be 
   * generated with the specified custom {@link InvokerGenerationStrategy} instance,
   * and so it's exact layout (including the constructor(s) that are available)
   * is dependent upon the implementation supplied - this method will generate the
   * proxy and then attempt to find an appropriate constructor for the supplied
   * arguments using {@link Toolbox#findMatchingConstructor(Class, Object[])}
   * method.
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   * @param strategy The custom {@code InvokerGenerationStrategy} to use for this proxy.
   * @param classes The {@code Class} for which a proxy is to be obtained.
   * @param args The arguments to pass to the constructor. These will be passed to
   *        the closest-matching compatible constructor (if one is available).
   *        See {@link Toolbox#findMatchingConstructor(Class, Object[])} for details.
   * 
   * @return The new proxy instance.
   * 
   * @throws ProxyInstantiationException If an error occurs during proxy instantiation.
   *         The exact cause of the error may be available via the {@code getCause()} 
   *         method.
   */
  /* WORKER */
  public static Object newProxyInstance(ClassLoader loader, 
                                        Class<?>[] classes, 
                                        InvokerGenerationStrategy strategy, 
                                        Object... args) {
    try {
      return Toolbox.findMatchingConstructor(createProxyClass(loader,classes,strategy),args).
          newInstance(args);
      
    } catch (InvocationTargetException e) {
      throw(new ProxyInstantiationException("Proxy: Instantiation failed", e.getCause() != null ? e.getCause() : e));
    } catch (NoSuchMethodException e) {
      throw(new ProxyInstantiationException("Proxy: Unmatched constructor call", e));
    } catch (IllegalAccessException e) { 
      throw(new ProxyInstantiationException("Proxy: Unmatched constructor call", e));
    } catch (InstantiationException e) {
      throw(new ProxyInstantiationException("Proxy: Instantiation failed", e));      
    }
  }
  
  /**
   * Create a new dynamic proxy instance for the specified {@link Class}es, defined
   * in the specified {@link ClassLoader}. This is a convenience
   * method that will generate the proxy with the {@link DefaultInvokerStrategy}, 
   * and will require an {@link InvocationHandler} instance as the first constructor
   * argument.
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   * @param classes The {@code Class} for which a proxy is to be obtained.
   * @param args The arguments to pass to the constructor. These will be passed to
   *        the closest-matching compatible constructor (if one is available).
   *        See {@link Toolbox#findMatchingConstructor(Class, Object[])} for details.
   * 
   * @return The new proxy instance.
   * 
   * @throws ProxyInstantiationException If an error occurs during proxy instantiation.
   *         The exact cause of the error may be available via the {@code getCause()} 
   *         method.
   *         
   * @throws IllegalArgumentException If the first constructor argument is not an
   *         instance of {@code InvocationHandler}.
   */
  public static Object newProxyInstance(ClassLoader loader, Class<?>[] classes, Object... args) {
    if (args == null || args.length < 1 || args[0] == null) {
      throw(new NullArgumentException("SoftProxy","newProxyInstance","args[0]"));
    }
    
    if (!(args[0] instanceof InvocationHandler)) {
      throw(new IllegalArgumentException(
          "Default strategy proxies constructors require an instance of java.lang.reflect.InvocationHandler"));
    }
    
    return newProxyInstance(loader,classes,DEFAULT_INVOKER_STRATEGY,args);
  }
  
  /**
   * Create a new dynamic proxy instance for the specified {@link Class}es, defined
   * in a default {@link ClassLoader}. This is a convenience
   * method that will generate the proxy with the {@link DefaultInvokerStrategy}, 
   * and will require an {@link InvocationHandler} instance as the first constructor
   * argument. 
   * <p/>
   * See {@link #createProxyClass(Class[])} for details of order of preference for 
   * the class-loader.
   * 
   * @param classes The {@link Class} for which a proxy is to be obtained.
   * @param args The arguments to pass to the constructor. These will be passed to
   *        the closest-matching compatible constructor (if one is available).
   *        See {@link Toolbox#findMatchingConstructor(Class, Object[])} for details.
   * 
   * @return The new proxy instance.
   * 
   * @throws ProxyInstantiationException If an error occurs during proxy instantiation.
   *         The exact cause of the error may be available via the {@code getCause()} 
   *         method.
   *         
   * @throws IllegalArgumentException If the first constructor argument is not an
   *         instance of {@code InvocationHandler}.
   */
  public static Object newProxyInstance(Class<?>[] classes, Object... args) {
    return newProxyInstance(defaultClassLoader(), classes, args);
  }
  
  /**
   * Create a new dynamic proxy instance for the specified (single) {@link Class}, defined
   * in the specified {@link ClassLoader}. This is a convenience
   * method that will generate the proxy with the specified {@link InvokerGenerationStrategy}.
   * <p/>
   * This method uses the generic type of the supplied class to return a 
   * reference of that type. See the {@link SoftProxy class description} 
   * for more information on proxy typing. 
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   * @param clazz The {@link Class} for which a proxy is to be obtained.
   * @param strategy The custom {@code InvokerGenerationStrategy} to use for this proxy.
   * @param args The arguments to pass to the constructor. These will be passed to
   *        the closest-matching compatible constructor (if one is available).
   *        See {@link Toolbox#findMatchingConstructor(Class, Object[])} for details.
   * 
   * @return The new proxy instance.
   * 
   * @throws ProxyInstantiationException If an error occurs during proxy instantiation.
   *         The exact cause of the error may be available via the {@code getCause()} 
   *         method.
   *         
   * @throws IllegalArgumentException If the first constructor argument is not an
   *         instance of {@code InvocationHandler}.
   */
  @SuppressWarnings("unchecked")    // Proxy will always return compatible to itself 
  public static <T> T newProxyInstance(ClassLoader loader, Class<T> clazz, InvokerGenerationStrategy strategy, Object... args) { 
    return (T)newProxyInstance(loader, new Class[] { nullSafeArg(SoftProxy.class,"newProxyInstance","clazz",clazz), }, strategy, args);
  }
  
  /**
   * Create a new dynamic proxy instance for the specified (single) {@link Class}, defined
   * in the specified {@link ClassLoader}. This is a convenience
   * method that will generate the proxy with the {@link DefaultInvokerStrategy}, 
   * and will require an {@link InvocationHandler} instance as the first constructor
   * argument. 
   * <p/>
   * This method uses the generic type of the supplied class to return a 
   * reference of that type. See the {@link SoftProxy class description} 
   * for more information on proxy typing. 
   * 
   * @param loader The {@link ClassLoader} from which to load classes, and in which the
   *               proxy class should be defined.
   * @param clazz The {@link Class} for which a proxy is to be obtained.
   * @param args The arguments to pass to the constructor. These will be passed to
   *        the closest-matching compatible constructor (if one is available).
   *        See {@link Toolbox#findMatchingConstructor(Class, Object[])} for details.
   * 
   * @return The new proxy instance.
   * 
   * @throws ProxyInstantiationException If an error occurs during proxy instantiation.
   *         The exact cause of the error may be available via the {@code getCause()} 
   *         method.
   *         
   * @throws IllegalArgumentException If the first constructor argument is not an
   *         instance of {@code InvocationHandler}.
   */
  public static <T> T newProxyInstance(ClassLoader loader, Class<T> clazz, Object... args) { 
    return newProxyInstance(loader, clazz, DEFAULT_INVOKER_STRATEGY, args);
  }
  
  /**
   * Create a new dynamic proxy instance for the specified (single) {@link Class}, defined
   * in a default {@link ClassLoader}. This is a convenience
   * method that will generate the proxy with the {@link DefaultInvokerStrategy}, 
   * and will require an {@link InvocationHandler} instance as the first constructor
   * argument. 
   * <p/>
   * This method infers the reference type to return from the generic type of the 
   * supplied class. See the {@link SoftProxy class description} 
   * for more information on proxy typing. 
   * 
   * @param clazz The {@link Class} for which a proxy is to be obtained.
   * @param args The arguments to pass to the constructor. These will be passed to
   *        the closest-matching compatible constructor (if one is available).
   *        See {@link Toolbox#findMatchingConstructor(Class, Object[])} for details.
   * 
   * @return The new proxy instance.
   * 
   * @throws ProxyInstantiationException If an error occurs during proxy instantiation.
   *         The exact cause of the error may be available via the {@code getCause()} 
   *         method.
   *         
   * @throws IllegalArgumentException If the first constructor argument is not an
   *         instance of {@code InvocationHandler}.
   */
  public static <T> T newProxyInstance(Class<T> clazz, Object... args) {
    return newProxyInstance(defaultClassLoader(), clazz, args); 
  }
  
  /* ********************************** 
   * INFO AND UTILITY METHODS
   */
  /**
   * Returns {@code true} if the specified {@link Class} appears to be a 
   * {@code SoftProxy}-generated proxy class. This is done by checking for the presence
   * of a serial number field generated by {@code SoftProxy}.
   * <p/>
   * Note that no verification of the serial is performed, since it is perfectly legal
   * for two proxies to have the same serial number. This could be the case, for example,
   * where a proxy class has been serialized as a {@code .class} file for use in future
   * instrumentation, for example.
   * 
   * @param c The {@code Class} to be tested.
   * 
   * @return {@code true} if the class {@code c} appears to be a dynamic proxy class,
   *         or {@code false} otherwise.
   */
  public static boolean isProxyClass(Class<?> c) {
    try {
      return (c.getField(SERIAL_FIELD) != null);
    } catch (NoSuchFieldException e) {
      return false;
    }
  }
  
  /**
   * Returns {@code true} if the supplied {@link Object} appears to be a dynamic
   * proxy instance. See {@link #isProxyClass(Class)} for details on how this
   * check is performed.
   * 
   * @param o The {@code Object} to be tested.
   * 
   * @return {@code true} if the class {@code c} appears to be a dynamic proxy,
   *         or {@code false} otherwise.
   */
  public static boolean isProxy(Object o) {
    return isProxyClass(o.getClass());
  }
  
  /**
   * Returns the name of the internal {@code static final java.lang.reflect.Method}
   * field generated on Proxy objects. This method is provided to allow custom 
   * invoker generation strategies to easily obtain the appropriate field name
   * from which to obtain their {@link java.lang.reflect.Method} instance.
   *  
   * @param desc The method descriptor (see {@link Toolbox#methodDescriptor(Method)}.
   * 
   * @return The field name, in the format {@code _Mnnnnnnnnnn}, where {@code nnnnnnnnnn}
   *         is the absolute value of the descriptor {@link String} object's 
   *         {@link Object#hashCode() hashcode}.
   */
  public static String methodFieldName(String desc) {
    return "_M"+Math.abs(desc.hashCode());    
  }
  
  /* static only */
  private SoftProxy() { }
}
