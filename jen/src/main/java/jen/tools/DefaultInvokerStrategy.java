/* DefaultInvokerStrategy.java - Default InvokerGenerationStrategy
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
 * File version: $Revision: 1.11 $ $Date: 2005/10/31 14:12:28 $
 * Originated: Oct 6, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import jen.SoftClass;
import jen.UnresolvableTypeHierarchyException;
import jen.fields.GeneratedSoftField;
import jen.methods.GeneratedSoftMethod;

import static jen.SoftUtils.binaryToDescriptor;

import static jen.tools.Toolbox.OBJECT_TYPE;
import static jen.tools.Toolbox.METHOD_TYPE;
import static jen.tools.Toolbox.OBJECT_ARRAY_TYPE;

import static jen.tools.Toolbox.namedMethodDescriptor;
import static jen.tools.Toolbox.methodSignature;
import static jen.tools.Toolbox.primitiveTypeIdent;
import static jen.tools.Toolbox.defaultPrimitiveReturn;
import static jen.tools.Toolbox.knownMethod;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_TRANSIENT;

/**
 * Default {@link InvokerGenerationStrategy} implementation. This
 * implementation generates dynamic proxy classes that are externally compatible
 * with standard Java&trade; SE platform proxies (created by
 * {@link java.lang.reflect.Proxy java.lang.reflect.Proxy}), while allowing
 * advanced capabilities, such as concrete method proxies and forward invocation
 * (i.e. {@code super} calls) to be handled from any standard
 * {@link java.lang.reflect.InvocationHandler} implementations.
 * <p/>
 * For the most part the behaviour of proxy classes and instances generated with this
 * strategy mirrors that of those created with {@link java.lang.reflect.Proxy}.
 * Obviously there are some differences, since this class supports proxy generation
 * for both classes and interfaces, and the important ones to note are:
 * <p/>
 * <ul>
 * <li>Generated proxy classes do <strong>not</strong> extend
 * {@code java.lang.reflect.Proxy}. Proxies generated from interfaces alone
 * will extend {@code java.lang.Object}, while those generated with a non-interface
 * class (possibly amongst interfaces) will extend that non-interface class.</li>
 * <p/>
 * <li>Generated proxy classes, and the proxy methods generated upon them, are
 * declared {@code public}. They are <strong>not</strong> declared {@code final}.</li>
 * <p/>
 * <li>Generated proxy classes will declare one constructor for each public
 * constructor declared by the proxied non-interface class (if any). If no 
 * non-interface is proxied, or it declares no public constructors, then the 
 * null constructor is considered to be implicitly declared. Each generated 
 * constructor will have an {@code InvocationHandler} argument at position zero, 
 * followed by the proxied constructor arguments in order. Constructors will have
 * {@code public} access, and proxy classes themselves have no null constructor.</li>
 * <p/>
 * <li>You may supply one or more interface classes, up to one non-interface class,
 * or a combination of one class and one or more interfaces. As with {@code Proxy},
 * at most one of these may be non-public..</li>
 * <p/>
 * <li>The {@link SoftProxy#isProxyClass(Class)} and
 * {@link SoftProxy#isProxy(Object)}
 * methods should be used in place of {@link java.lang.reflect.Proxy#isProxyClass(Class)}
 * to determine whether a given class (or instance) is a Jen proxy. It is also possible
 * to determine whether a given class is a <em>default proxy</em> (i.e. generated with this
 * strategy) with the {@link #isDefaultProxyClass(Class)} and {@link #isDefaultProxy(Object)}
 * methods.</li>
 * <p/>
 * <li>{@code SoftProxy} performs no caching of generated proxy classes, nor retains any kind of
 * reference to them once returned to the caller. Successive calls to the generation methods
 * with the same parameters will result in multiple identical proxy classes being generated.
 * This allows, for example, multiple proxies to be generated with different invoker strategies,
 * but must be used with care, since certain expected behaviours (such as assignability) can
 * become quite arbitrary in such situations.</li>
 * <p/>
 * <li>Forward invocation of the {@code super} method in dynamic proxy methods <strong>is</strong>
 * supported in the case of non-abstract class methods, though not directly via the supplied
 * {@code Method} object's
 * {@link java.lang.reflect.Method#invoke(Object, Object[])}
 * method (which handles only virtual invocation). Instead, {@code InvocationHandler}
 * implementations that wish to dispatch invocations to the proxied method implementations should use
 * {@link #invokeSuper(Object, Method, Object[])} which invokes an additional proxy
 * method that executes the appropriate {@code INVOKESPECIAL} instruction. Of course, making use
 * of this feature requires the Jen Tools library be present at runtime, although it is not
 * difficult to manually call the super invocation method (with a prefix of {@code __super_}
 * by reflection if that is not the case.
 * </li>
 * <p/>
 * <li>Since default proxy classes have no non-platform dependencies, and are implemented as standard
 * classes, it is possible to serialize them (either as {@code .class} files or across the wire)
 * in order to reuse them, possibly in another JVM instance. This is of course subject to a few caveats,
 * in that compatible classes must exist when the class is resolved, and you will almost certainly
 * want to change the class name (with {@link SoftClass#setName(String)}) before generating the
 * class.</li>
 * </ul>
 * <p/>
 * Proxies generated with the this strategy utilise a dynamic invocation handler,
 * with an instance of {@link InvocationHandler} supplied via the (sole) constructor being called
 * for each method invocation on the proxy. From the point of view of the user of this API, this
 * arrangement is identical to that implemented in {@code Proxy}.
 * <p/>
 * In terms of performance, there is little to choose between any contemporary dynamic proxy
 * implementation, be it the so-called 'reflective proxies' provided by the platform, or compatible
 * implementations such as those generated by Cglib and of course Jen. Although it is very difficult
 * to accurately measure performance of low-level operations such as argument-marshalling and dispatch
 * on the JVM, informal comparison with the three proxy implementations mentioned above has shown a
 * variance of less than 15msec on average over 200,000 method invocations (without compile-time
 * inlining, using the Sun 1.5.0_05 client JVM in mixed mode).
 * Forward invocation to the proxied method does carry a performance penalty, especially at present
 * since reflection is involved (along with an technically unnecessary unmarshal / marshal cycle),
 * although this will be improved as development continues. In general this penalty may not prove
 * too much of an inconvenience, especially when it is considered that in the majority of cases
 * actual method invocation and return accounts for a (varying) fraction of the execution time
 * of the method.
 * <p/>
 * Of course, although the different proxy implementations are very close to one another in terms
 * of performance, this baseline invocation speed is far lower than that for the equivalent static
 * invocation.
 * <p/>
 * As mentioned above, proxy classes will declare a basic call-through public constructor for 
 * each public superclass constructor, with the {@code InvocationHandler} argument prepended to
 * the super constructor arguments. Non-public constructors, like non-public methods, 
 * are ignored. 
 * <p/>
 * This class is both multiple-generation and thread safe. A singleton instance (maintained by
 * {@code SoftProxy} handles all default-strategy generation.
 * <p/>
 * <strong>N.B.</strong> No support is currently provided for copying generic type signatures 
 * and annotations to proxy classes and members, and any such signature or annotation will be 
 * omitted from the generatied proxy classe. Since generics are currently (1.5.0) implemented
 * via type erasure (with appropriate casts generated at compile time), this should have little 
 * impact on the runtime operation of the proxies', and in any event it is important to return
 * the expected type for the proxied method from invocation handler methods. It will, however, 
 * affect the operation of the various generics-enabled {@code Class} methods when used with 
 * Proxy classes - for any kind of reflective operation requiring access to type signatures or 
 * annotations, the superclass or interface (i.e. the appropriate proxied class) should be used. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @author William Lee (william&lt;at&gt;imageunion.com)
 * 
 * @version $Revision: 1.11 $ $Date: 2005/10/31 14:12:28 $
 */
public class DefaultInvokerStrategy implements InvokerGenerationStrategy {
    /**
     * The name of the field on the generated proxy class that holds the {@link InvocationHandler}
     * supplied at instantiation (value: {@value}).
     */
    protected static final String INV_HANDLER_FIELD = "__inv";

    /**
     * The prefix used for super invocation methods (value: {@value}).
     */
    protected static final String SUPERINVOKER_PREFIX = "__super_";

    protected static final Type INV_HANDLER_TYPE = Type.getType(InvocationHandler.class);

    private static final org.objectweb.asm.commons.Method INV_HANDLER_INVOKE =
            new org.objectweb.asm.commons.Method("invoke", OBJECT_TYPE,
                    new Type[]{OBJECT_TYPE, METHOD_TYPE, OBJECT_ARRAY_TYPE});

    private static final org.objectweb.asm.commons.Method NULL_CTOR =
            org.objectweb.asm.commons.Method.getMethod("void <init>()");

    /* **********************************
    * MEMBER IMPLEMENTATIONS
    */

    /*
     * The {@link jen.SoftMethod} that provides constructors for proxy classes
     * generated with the default strategy. For each public constructor declared on
     * the superclass, an instance of this class will generate a public constructor
     * with the following signature:
     * <p/>
     * {@code public SomeClass$Proxy0(java.lang.reflect.InvocationHandler [,argType0 ... ,argTypeN ]}}
     */
    static final class DefaultProxyConstructor extends GeneratedSoftMethod {

        /**
         * create a NonNullProxyConstructor
         *
         * @param sc        the soft class this method is added to
         * @param pArgTypes the argument types of this constructor
         */
        DefaultProxyConstructor(SoftClass sc, Class[] pArgTypes) {
          this(sc, ACC_PUBLIC, pArgTypes);
        }

        DefaultProxyConstructor(SoftClass sc, int modifiers, Class[] pArgTypes) {
          super(sc, modifiers, "<init>", pArgTypes, void.class);
        }

        /**
         * generate the constructor code
         *
         * @param gen the adapter
         */
        protected void generateCode(GeneratorAdapter gen) {
            gen.loadThis();
            gen.dup();
            // binary -> descriptor is line of least resistance...
            StringBuffer xSignatureType = new StringBuffer("void <init> (");
            for (int i = 1; i < getArgumentTypes().size(); i++) {
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
            gen.loadArg(0);
            gen.putField(getSoftClass().getType(), INV_HANDLER_FIELD, Type.getType(InvocationHandler.class));
            gen.returnValue();
            gen.endMethod();
        }
    }

    /*
     * The {@link jen.SoftMethod} that provides the {@code super} invocation method
     * for each non-abstract proxied method. This method takes it's name from
     * the {@link #superInvokerName} method, and has the same descriptor as the
     * proxied method itself.
     */
    static final class DefaultSuperInvoker extends GeneratedSoftMethod {
        private final Type superType;
        private final Type retType;
        private final boolean withParams;
        private final org.objectweb.asm.commons.Method superMethod;

        DefaultSuperInvoker(SoftClass softClass, Method m) {
            super(softClass, ACC_PUBLIC | ACC_SYNTHETIC,
                    superInvokerName(m), null, m.getParameterTypes(), m.getReturnType(), m.getExceptionTypes());

            this.retType = Type.getType(m.getReturnType());
            this.superType = Type.getType(binaryToDescriptor(getSoftClass().getSuperClassInternalName()));
            this.superMethod = org.objectweb.asm.commons.Method.getMethod(methodSignature(m));
            this.withParams = m.getParameterTypes().length > 0;
        }

        protected void generateCode(GeneratorAdapter gen) {
            // It's probably _not_ a constructor, but never mind eh... INVOKESPECIAL does super calls too.
            gen.loadThis();
            if (withParams) gen.loadArgs();
            gen.invokeConstructor(superType, superMethod);
            gen.returnValue();
            gen.endMethod();
        }
    }

    /*
     * The {@link jen.SoftMethod} that provides invocation dispatch method
     * for each proxied method (abstract or concrete). This is the actual
     * proxy method implementation.
     */
    static final class DefaultInvokerDispatcher extends GeneratedSoftMethod {
        private final Type retType;
        private final String methodDesc;

        DefaultInvokerDispatcher(SoftClass softClass, Method m) {
            super(softClass, SoftClass.ACC_PUBLIC,
                    m.getName(), null, m.getParameterTypes(), m.getReturnType(), m.getExceptionTypes());

            this.retType = Type.getType(m.getReturnType());
            this.methodDesc = namedMethodDescriptor(m);
        }

        protected void generateCode(GeneratorAdapter gen) {
            // this runs a tiny bit quicker than a double-dup / swap
            gen.loadThis();
            gen.dup();                                                                                               // this .. this
            gen.getField(getSoftClass().getType(), "__inv", INV_HANDLER_TYPE);                                         // this .. InvHandler
            gen.loadThis();                                                                                          // this .. InvHandler .. this
            gen.getStatic(getSoftClass().getType(), SoftProxy.methodFieldName(methodDesc), METHOD_TYPE);               // this .. InvHandler .. this .. Method
            gen.loadArgArray();                                                                                      // this .. InvHandler .. this .. Method .. args[]
            gen.invokeInterface(INV_HANDLER_TYPE, INV_HANDLER_INVOKE);                                                // [(retval)]

            Label retInsn = gen.newLabel();

            if (retType != Type.VOID_TYPE) {
                String retDesc = retType.getDescriptor();
                if (retDesc.startsWith("L") || retDesc.startsWith("[")) {
                    // need to checkCast reference
                    gen.checkCast(retType);
                } else {
                    // We need to skip unboxing if invoker returned null (it gives NPE)
                    Label nullPushSkip = gen.newLabel();
                    gen.dup();                                                                                           // retval .. retval
                    gen.ifNonNull(nullPushSkip);                                                                         // retval (is null)

                    defaultPrimitiveReturn(primitiveTypeIdent(retType), gen);                                             // xCONST_0
                    gen.goTo(retInsn);

                    gen.mark(nullPushSkip);
                    gen.unbox(retType);                                                                                  // retval (or xCONST_0)
                }
            }
            // Now this there must be an appropriately typed (unboxed or const) value on top.
            gen.mark(retInsn);
            gen.returnValue();
            gen.endMethod();
        }
    }

    /* **********************************
    * HELPERS AND UTIL
    */

    /**
     * Obtains a valid name method name for the specified method's super invocation
     * method. The supplied {@link Method} should represent the <em>proxied</em>
     * method, rather than the <em>proxy</em> method itself, and the return value
     * will be the name of the method (on the proxy class) that performs static
     * dispatch to that superclass method.
     * <p/>
     * This will always return a valid name, regardless of whether the method will
     * actually exist (abstract methods, for example, do not have a super invoker),
     * and no checking is performed that the supplied method belongs to a proxied
     * class.
     */
    static String superInvokerName(Method m) {
        return new StringBuilder().append(SUPERINVOKER_PREFIX).
                append(m.getName()).
                toString();
    }

    /**
     * Returns {@code true} if the specified {@link Class} appears to be a
     * generated proxy class created with the {@code DefaultInvokerStrategy}. This is done by
     * checking for the presence of a serial number field generated by {@code SoftProxy}
     * and the invocation handler field generated by this strategy.
     * <p/>
     * Note that no verification of the fields is performed.
     *
     * @param c The {@code Class} to be tested.
     * @return {@code true} if the class {@code c} appears to be a dynamic proxy class,
     *         or {@code false} otherwise.
     */
    public static boolean isDefaultProxyClass(Class<?> c) {
        try {
            return (c.getField(SoftProxy.SERIAL_FIELD) != null) && (c.getDeclaredField(INV_HANDLER_FIELD) != null);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the supplied {@link Object} appears to be a dynamic
     * proxy instance created with {@code DefaultInvokerStrategy}. See
     * {@link #isDefaultProxyClass(Class)} for details on how this check is performed.
     *
     * @param o The {@code Object} to be tested.
     * @return {@code true} if the class {@code c} appears to be a default-strategy
     *         dynamic proxy, or {@code false} otherwise.
     */
    public static boolean isDefaultProxy(Object o) {
        return isDefaultProxyClass(o.getClass());
    }

    /**
     * Supports forward invocation of the superclass method on non-abstract class method
     * proxies generated with the {@link DefaultInvokerStrategy}. At present this involves
     * reflection. This method can safely be called on any object, since it simply returns
     * null with non-default proxies - this is intentional, to allow InvocationHandlers to
     * be interchanged between proxy implementations.
     * 
     * @param proxy The proxy instance.
     * @param m The method for which the superclass method is to be invoked.
     * @param args The method arguments.
     * 
     * @return The return value from the super method, or {@code null} if the method returns 
     *         nothing (i.e. is declared {@code void}), is abstract (i.e. is an interface
     *         or abstract class method), or if the supplied {@code Object} is not a valid
     *         default-strategy proxy instance.
     */
    public static final Object invokeSuper(Object proxy, Method m, Object[] args) throws Throwable {
      // short circuit abstract or interface
      if ((m.getModifiers() & ACC_ABSTRACT) != 0) {
        return null;
      }
      
      Class c = proxy.getClass();
      if (isDefaultProxyClass(c)) {
        try {
          return knownMethod(c,superInvokerName(m), (Class[])m.getParameterTypes()).invoke(proxy,args);
        } catch (InvocationTargetException e) {
          throw (e.getCause() != null) ?
              e.getCause() : 
              new RuntimeException(
                  "Proxy: Unchained InvocationTargetException exception on super."+
                    m.getName()+" invocation",
                  e);
        }
      }
      
      // We want to support interchangeable InvocationHandlers, so if it's a non-concrete or non-default proxy
      // we *don't* throw, but just return null (as the defaults do if you try to invoke the supplied method).
      //throw(new IllegalArgumentException("Proxy: invokeSuper requires a default-strategy proxy instance"));
      
      return null;
    }
    
    /* **********************************
    * PUBLIC INTERFACE
    */
    /**
     * {@inheritDoc}
     * This implementation returns {@code true} for any combination
     * of classes.
     */
    public boolean canProxyClasses(Class<?>[] classes) {
      return true;
    }

    /**
     * {@inheritDoc}
     * This implementation generates a single {@link InvocationHandler} instance field
     * named {@value #INV_HANDLER_FIELD} and a {@link SuperConstructor}.
     * <p/>
     * The generated field is generated {@code private transient final}. It is not
     * possible to modify the invocation handler on a given proxy instance after
     * instantiation, as this would make all proxies inherently unsafe for use in
     * multithreaded environments.
     */
    /* 0.30 - switched from old generateInstanceInit */
    public void afterGeneration(SoftClass sc) {
      // invocation handler field, and ctor
      sc.putSoftField(new GeneratedSoftField(sc,
              ACC_PRIVATE | ACC_FINAL | ACC_TRANSIENT,
              "__inv",
              InvocationHandler.class));

      
      try {
        Constructor<?>[] superCtors = sc.getSuperClass().getConstructors();
        
        if (superCtors.length == 0) {
          // No public constructors on super, so see if we can do a default null ctor
          try {
            Constructor<?> xConstructor = sc.getSuperClass().getDeclaredConstructor(new Class[0]);
            if ((xConstructor.getModifiers() & ACC_PRIVATE) == 0 && 
                !(xConstructor.getModifiers() == 0 && !sc.getName().startsWith(xConstructor.getDeclaringClass().getPackage().getName()))) {
              sc.putSoftMethod(new DefaultProxyConstructor(sc, new Class[] { InvocationHandler.class }));
              return;
            }            
          } catch (NoSuchMethodException e) { /* throw below */  }
          
          // no visible super null ctor
          throw(new IllegalArgumentException("No superclass constructor is visible"));
          
        } else {
          for (Constructor<?> xConstructor : superCtors) {
            Class[] xArgTypes = new Class[xConstructor.getParameterTypes().length + 1];
            System.arraycopy(xConstructor.getParameterTypes(), 0, xArgTypes, 1, xArgTypes.length - 1);
            xArgTypes[0] = InvocationHandler.class;
            sc.putSoftMethod(new DefaultProxyConstructor(sc, xArgTypes));
          }
        }          
      } catch (UnresolvableTypeHierarchyException xEx) {
          throw new IllegalStateException("type hierarchy cannot be resolved - " + xEx.getMessage());
      }
    }

    /**
     * {@inheritDoc}
     * This implementation generates a {@link DefaultInvokerDispatcher} for the
     * specified method, along with a {@link DefaultSuperInvoker} if the method
     * is not abstract.
     */
    public void generateInvoker(SoftClass sc, Method m) {
        sc.putSoftMethod(new DefaultInvokerDispatcher(sc, m));

        // super invoker if not abstract
        if ((m.getModifiers() & ACC_ABSTRACT) == 0) {
            sc.putSoftMethod(new DefaultSuperInvoker(sc, m));
        }
    }

    /* **********************************
    * INSTANCE INIT
    */

    /**
     * Protected default constructor, for subclass invocation (typically implicit).
     * A single instance of {@link DefaultInvokerStrategy} itself is created
     * internally by {@link SoftProxy}, and is available via the static final
     * {@link SoftProxy#DEFAULT_INVOKER_STRATEGY} field.
     */
    protected DefaultInvokerStrategy() { }
}