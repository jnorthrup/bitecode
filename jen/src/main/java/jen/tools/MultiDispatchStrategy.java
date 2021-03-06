/* MultiDispatchStrategy.java - Proxy strategy that does multi dispatch
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
 * File version: $Revision: 1.5 $ $Date: 2005/11/05 15:44:11 $
 * Originated: Oct 28, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jen.SoftClass;
import jen.SoftField;
import jen.UnresolvableTypeHierarchyException;
import jen.fields.GeneratedSoftField;
import jen.methods.GeneratedSoftMethod;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import static jen.SoftClass.ACC_ABSTRACT;
import static jen.SoftClass.ACC_TRANSIENT;
import static jen.SoftClass.ACC_PRIVATE;
import static jen.SoftClass.ACC_FINAL;

import static jen.SoftUtils.binaryToDescriptor;
import static jen.SoftUtils.typeForClassArray;

import static jen.tools.Toolbox.STRING_TYPE;
import static jen.tools.Toolbox.CLASS_TYPE;
import static jen.tools.Toolbox.OBJECT_ARRAY_TYPE;
import static jen.tools.Toolbox.OBJECT_TYPE;
import static jen.tools.Toolbox.methodSignature;
import static jen.tools.Toolbox.primitiveTypeIdent;
import static jen.tools.Toolbox.defaultPrimitiveReturn;

import static jen.tools.DefaultInvokerStrategy.superInvokerName;

/**
 * Adds support for <em>multiple dynamic dispatch</em> to a Java(tm)
 * class. This alternative {@link InvokerGenerationStrategy} implementation 
 * allows {@link SoftProxy} to generate special 'multiple dispatch proxies' for
 * overloaded public methods in any concrete class. This requires no special
 * implementation or convention on the proxied class, and can be applied 
 * to any existing class (including platform classes). These proxies retain full 
 * compatibility with the Java language, and can be used interchangeably with
 * the standard implementation of the proxied class with no changes to the
 * calling code. It is not possible to generate interface or abstract-class
 * proxies with this strategy - the default strategy provides for that.
 * <p/>
 * The dispatch implemented by this strategy's proxies is true multiple dynamic
 * dispatch, in that the actual method to invoke is selected at runtime based
 * upon the actual types of all method arguments. As standard, Java implements
 * dynamic dispatch on only the first argument (the logical {@code this} argument),
 * while static dispatch is used to select a method overload (the decision is
 * made at compile time, based on the apparent compile-type type of the arguments).
 * This strategy 'emulates' multiple dispatch on top of the standard method
 * invocation by generating appropriate conditional logic for a given class, 
 * and providing a layer of synthetic methods through which an invocation is passed
 * in order to dispatch to the correct method overload for a given set of arguments.
 * A major benefit of this implementation is that it is entirely transparent to 
 * the caller, and can be used without following any special pattern or 
 * performing additional compilation steps. The {@link NoDispatch} annotation
 * assists this pattern by allowing all-{@link Object} overloads to be written for
 * compile time convenience, and ignored by the multiple dispatcher in the
 * generated class.  
 * <p/>
 * Multiple dispatch proxies are implemented statically, at runtime. In other
 * words, they are statically-bound classes based on standard method invocation
 * as opposed to Reflection, generated specifically for the proxied class. 
 * As with all {@code SoftProxy} proxies, they can be serialized as well as
 * used immediately for reuse across multiple application runs or JVMs.
 * While invocation performance on a given proxy is obviously poorer than 
 * that for the equivalent single dispatch, the impact is not as great as you might
 * perhaps imagine - the word 'Proxy' seems irrevocably linked with the 
 * Reflection-based proxies implemented by the Platform (and Jen), but in the 
 * case of multiple dispatch proxies generated by this strategy, no reflective
 * invocation is involved. Although no detailed performance analysis has yet been
 * performed with these proxies, informal testing on a sample class (multiple overloads
 * of a single method, together with a number of 'regular' methods and two {@link NoDispatch}
 * accessors) has shown round-trip invocation of multi-dispatch methods to take roughly two to 
 * three times as long as equivalent static invocation, with an observed difference of 
 * about 0.15ms per iteration across 10,000 post-warmup runs on a P4 1.7 GHz, 
 * Sun client JVM 1.5.0_05). It is thought that the majority of this performance hit 
 * arises through the fact that the Hotspot compiler does not inline the (more complex) 
 * multiple dispatch method call. Performance is favourable compared to reflective invocation, 
 * with round-trip invocation and return typically completing in half the time (the difference
 * in this test between static and reflective invocation was generally in the region of
 * 0.35 - 0.45ms)
 * <p>
 * Proxies generated by this strategy have no non-platform dependencies, and thus
 * do not require the Jen classes be present at runtime.
 * 
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.5 $ $Date: 2005/11/05 15:44:11 $
 */
/*
 * FIXME Method scoring doesnt work with single arg, close complex interface hierarchy (collections),
 *       unless the 'catch-all' overload is marked with @NoDispatch. This _might_ be desirable, as
 *       it makes the whole thing more controlled, *but* it's a limitation, esp when working with
 *       platform classes (is that going to be a requirement?).
 *       
 *       To fix, we'd need to score higher for each superclass traversal, and higher still for moving
 *       to the interfaces. Even so, interfaces might be a problem since I'm not sure whether the
 *       getInterfaces gives all or declared. This is still a problem with complex inheritance
 *       hierarchies such as Collections.
 */
public class MultiDispatchStrategy implements InvokerGenerationStrategy
{
  private static final String MULTI_DISPATCHER_NAME = "__multi_dispatch";
  
  private static final org.objectweb.asm.commons.Method MULTI_DISPATCHER =
    new org.objectweb.asm.commons.Method(MULTI_DISPATCHER_NAME, OBJECT_TYPE,
          new Type[]{STRING_TYPE, OBJECT_ARRAY_TYPE});

  private static final org.objectweb.asm.commons.Method METHOD_MAP_HELPER =
    new org.objectweb.asm.commons.Method("__method_map_helper", Type.getType(HashMap.class),
          new Type[0]);

  private static final org.objectweb.asm.commons.Method FIND_MATCHING_METHOD =
    new org.objectweb.asm.commons.Method("__find_matching_method", STRING_TYPE,
          new Type[]{STRING_TYPE, OBJECT_ARRAY_TYPE});

  private static final org.objectweb.asm.commons.Method GET_CLASS =
    new org.objectweb.asm.commons.Method("getClass", CLASS_TYPE, new Type[0]);

  private static final org.objectweb.asm.commons.Method EQUALS =
    new org.objectweb.asm.commons.Method("equals", Type.BOOLEAN_TYPE, new Type[] { OBJECT_TYPE });
  
  private static final Type NO_SUCH_METHOD = Type.getType(NoSuchMethodError.class);

  static final Type HASHMAP_TYPE = Type.getType(HashMap.class);  
  static final String METHOD_MAP_FIELD = "__method_map";
  
  // this is flipped in afterGeneration - MultiDispatchStrategy is only good
  // for one generation.
  private boolean locked = false;
  
  private final Map<String,List<Method>> methods = new HashMap<String, List<Method>>();
  
  private final void checkLock() {
    if (locked) {
      throw(new IllegalStateException("Each MultiDispatchStrategy may be used for at most one generation"));      
    }
  }
  
  /* This is a variation on SuperConstructor that calls the method map helper at the end. */
  static final class MultiDispatchConstructor extends GeneratedSoftMethod {
    MultiDispatchConstructor(SoftClass sc, Class... pArgTypes) {
      this(sc, ACC_PUBLIC, pArgTypes);
    }
    
    MultiDispatchConstructor(SoftClass sc, int modifiers, Class... pArgTypes) {
      super(sc, modifiers, "<init>", pArgTypes, void.class);
    }
    
    protected void generateCode(GeneratorAdapter gen) {
      // to super ctor
      gen.loadThis();
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
      
      // init method map
      gen.loadThis();
      gen.loadThis();
      gen.invokeVirtual(getSoftClass().getType(),METHOD_MAP_HELPER);
      gen.putField(getSoftClass().getType(),METHOD_MAP_FIELD,HASHMAP_TYPE);
      
      gen.returnValue();
      gen.endMethod();
    }        
  }
  
  static final class MultiDispatchInvoker extends GeneratedSoftMethod
  {
    private final Type retType;
    private final String methodName;

    MultiDispatchInvoker(SoftClass softClass, Method m) {
      super(softClass, ACC_PUBLIC | ACC_FINAL, m.getName(), null, 
          m.getParameterTypes(), m.getReturnType(), m.getExceptionTypes());

      this.retType = Type.getType(m.getReturnType());
      this.methodName = m.getName();
    }

    protected void generateCode(GeneratorAdapter gen) {
      gen.loadThis();
      gen.push(methodName);
      gen.loadArgArray();
      gen.invokeConstructor(getSoftClass().getType(),MULTI_DISPATCHER);

      Label retInsn = gen.newLabel();

      // Need to fix a bit for primitives and voids
      if (retType != Type.VOID_TYPE) {
          String retDesc = retType.getDescriptor();
          if (retDesc.startsWith("L") || retDesc.startsWith("[")) {
              // need to checkCast reference
              gen.checkCast(retType);
          } else {
              // We need to skip unboxing if invoker returned null (it gives
              // NPE)
              Label nullPushSkip = gen.newLabel();
              gen.dup();        
              gen.ifNonNull(nullPushSkip);
              
              defaultPrimitiveReturn(primitiveTypeIdent(retType), gen);
              gen.goTo(retInsn);

              gen.mark(nullPushSkip);
              gen.unbox(retType);    
          }
      }
      
      // Now there must be an appropriately typed (unboxed or const)
      // value on top.
      gen.mark(retInsn);
      gen.returnValue();
      gen.endMethod();
    }
  }
  
  // FIXME This is exactly the same as the default super invoker, but with different modifiers.
  //       Refactor that, then ...   
  static final class MultiSuperInvoker extends GeneratedSoftMethod {
    private final Type superType;
    private final Type retType;
    private final boolean withParams;
    private final org.objectweb.asm.commons.Method superMethod;

    MultiSuperInvoker(SoftClass softClass, Method m) {
        super(softClass, ACC_PRIVATE | ACC_SYNTHETIC | ACC_FINAL,
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
  
  static final class MultiDispatcher extends GeneratedSoftMethod
  {
    private final Map<String,List<Method>> methods;

    MultiDispatcher(SoftClass softClass, Map<String,List<Method>> methods) {
      super(softClass, ACC_PRIVATE | ACC_FINAL | ACC_SYNTHETIC , MULTI_DISPATCHER_NAME, 
          new Class[] { String.class, Object[].class }, Object.class);

      // this is internal only, and final in outer, so should be okay
      this.methods = methods;
    }

    protected void generateCode(GeneratorAdapter gen) {
      gen.loadThis();
      gen.loadArgs();
      gen.invokeVirtual(getSoftClass().getType(),FIND_MATCHING_METHOD);
      
      gen.storeLocal(3,STRING_TYPE);
      
      for (String name : methods.keySet()) {
        for (Method m : methods.get(name)) {
          StringBuilder b = null;
          
          Class<?>[] pt = m.getParameterTypes();    
          for (Class<?> c : pt) {
            if (b == null) {
              b = new StringBuilder().append(c.getName());
            } else {
              b = b.append(c.getName());
            }     
          }
          
          String mstr = m.getName() + (b != null ? b.toString() : "");
          gen.push(mstr);
          gen.loadLocal(3);
          gen.invokeVirtual(STRING_TYPE,EQUALS);
          
          // if false, goto next
          Label nextLabel = new Label();
          gen.ifZCmp(GeneratorAdapter.EQ,nextLabel);
          
          // otherwise, super invoker
          gen.loadThis();
          
          if (pt.length > 0) {
            for (int i = 0; i < pt.length; i++) {
              gen.loadArg(1);
              gen.push(i);
              gen.arrayLoad(OBJECT_TYPE);
              
              // special primitive handling
              if (pt[i] != Object.class) {
                if (pt[i].isPrimitive()) {
                  // unbox prim
                  gen.unbox(Type.getType(pt[i]));
                  
                } else {
                  // checkcast ref
                  gen.checkCast(Type.getType(pt[i]));
                }
              }            
            }          
          }
  
          // Super invoker is private, so we need an invoke special
          Type retType = Type.getType(m.getReturnType());
          gen.invokeConstructor(getSoftClass().getType(),
              new org.objectweb.asm.commons.Method(superInvokerName(m),
                  retType, typeForClassArray(m.getParameterTypes())));               
          
          // if it's a void method we'll need a null
          if (m.getReturnType() == void.class) {
            gen.visitInsn(ACONST_NULL);
          } else if (m.getReturnType().isPrimitive()) {
            gen.box(retType);
          }
          
          // gen.push((String)null);
          gen.returnValue();
          
          // was false, so mark start of next (or end)
          gen.mark(nextLabel);        
        }
      }
      
      Type sbType = Type.getType(StringBuilder.class);
      gen.newInstance(sbType);
      gen.dup();
      gen.invokeConstructor(sbType,org.objectweb.asm.commons.Method.getMethod("void <init>()"));
      gen.push("Unable to find suitable overload for ");      
      gen.invokeVirtual(sbType,org.objectweb.asm.commons.Method.getMethod("StringBuilder append(String)"));
      gen.loadArg(0);
      gen.invokeVirtual(sbType,org.objectweb.asm.commons.Method.getMethod("StringBuilder append(String)"));
      gen.invokeVirtual(OBJECT_TYPE,org.objectweb.asm.commons.Method.getMethod("String toString()"));
      gen.storeLocal(4,STRING_TYPE);

      // new exception
      gen.newInstance(NO_SUCH_METHOD);
      gen.dup();
      gen.loadLocal(4,STRING_TYPE);
      gen.invokeConstructor(NO_SUCH_METHOD,org.objectweb.asm.commons.Method.getMethod("void <init>(String)"));      
      gen.throwException();
      gen.endMethod();
    }
  }

  /**
   * {@inheritDoc}
   * This implementation returns true only if the specified array
   * contains a single concrete class.  
   */
  public boolean canProxyClasses(Class<?>[] classes) {
    return (classes != null && classes.length > 0 && classes[0] != null && (classes[0].getModifiers() & ACC_ABSTRACT) == 0);
  }
  
  public void afterGeneration(SoftClass sc) {
    checkLock();
    locked = true;
    
    // Generate constructors    
    try {
      Constructor<?>[] superCtors = sc.getSuperClass().getConstructors();
      if (superCtors.length == 0) {
        // No public constructors on super, so see if we can do a default null ctor
        try {
          Constructor<?> xConstructor = sc.getSuperClass().getDeclaredConstructor(new Class[0]);
          if ((xConstructor.getModifiers() & ACC_PRIVATE) == 0 && 
              !(xConstructor.getModifiers() == 0 && !sc.getName().startsWith(xConstructor.getDeclaringClass().getPackage().getName()))) {
            sc.putSoftMethod(new MultiDispatchConstructor(sc));
            return;
          }            
        } catch (NoSuchMethodException e) { /* throw below */  }
        
        // no visible super null ctor
        throw(new IllegalArgumentException("No superclass constructor is visible"));
        
      } else {
        for (Constructor<?> xConstructor : superCtors) {
          sc.putSoftMethod(new MultiDispatchConstructor(sc, (Class[])xConstructor.getParameterTypes()));
        }
      }          
    } catch (UnresolvableTypeHierarchyException xEx) {
        throw new IllegalStateException("type hierarchy cannot be resolved - " + xEx.getMessage());
    }
    
    // proxies are final with this strategy
    sc.setModifiers(sc.getModifiers() | ACC_FINAL);
    
    copyMultiHelpers(sc);
    sc.putSoftMethod(new MultiDispatcher(sc,methods));        
  }

  public void generateInvoker(SoftClass sc, Method m) {
    checkLock();
    
    // generate invoker, because we _do_ want incoming invocation from this method
    // passed to the dispatcher - just not passed _back_ to the proxied version.
    sc.putSoftMethod(new MultiDispatchInvoker(sc, m));

    // Only generate a super invoker and dispatch condition (in __multi_dispatch)
    // if this isn't a static only method.
    
    if (!m.isAnnotationPresent(NoDispatch.class) && (m.getModifiers() & ACC_ABSTRACT) == 0) {
    
      // Get the list for methods with this name, or create anew if
      // non-existent.
      List<Method> l = methods.get(m.getName());
      if (l == null) {
        l = new ArrayList<Method>();
        methods.put(m.getName(), l);
      }
      
      // Add to method map (used to generate the init and dispatch on the class)
      l.add(m);
      
      // super invoker
      sc.putSoftMethod(new MultiSuperInvoker(sc, m));
    }
  }

  /* Copies the helper methods to the class */
  private void copyMultiHelpers(SoftClass sc) {
    SoftClass source = MultiDispatchMethods.getSoftClass();    
    sc.putSoftMethod(MultiDispatchMethods.generateScoreClass(sc));
    sc.putSoftMethod(MultiDispatchMethods.generateScoreMethod(sc));
    sc.putSoftMethod(MultiDispatchMethods.generateMethodString(sc));
    sc.putSoftMethod(MultiDispatchMethods.generateFastCompat(sc));
    sc.putSoftMethod(MultiDispatchMethods.generateMethodMapHelper(sc,methods));
    sc.putSoftMethod(MultiDispatchMethods.generateFindMatchingMethod(sc));
    sc.putSoftMethod(MultiDispatchMethods.generatePrimitiveWrapper(sc));
    
    SoftField sourceF = source.getSoftField(METHOD_MAP_FIELD);
    sc.putSoftField(new GeneratedSoftField(sc,ACC_PRIVATE + ACC_FINAL + ACC_TRANSIENT,METHOD_MAP_FIELD,sourceF.getSignature(),sourceF.getType()));
  }
}
