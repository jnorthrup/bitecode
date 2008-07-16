/* SoftClass.java - A mutable Java class
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
 * File version: $Revision: 1.12 $ $Date: 2005/11/09 21:05:04 $
 * Originated: 06-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import static java.util.Arrays.asList;

import static jen.SoftUtils.typeForClassList;
import static jen.SoftUtils.nullSafeArg;
import static jen.SoftUtils.javaToBinary;
import static jen.SoftUtils.binaryToJava;

/**
 * A high level, mutable representation of a Java(tm) class. <code>SoftClass</code> holds
 * information about a class, it's members and attributes internally as a mutable 
 * <a href='http://asm.objectweb.org/'>ASM</a>-compatible member node
 * tree, from which it can generate bytecode compatible with a given version of the
 * Java Virtual Machine. This class supports three broad styles of usage: as a 
 * Java-based API for the manipulation of ASM trees, as an easy-to-use, auto generating 
 * ASM event source around an existing reader or tree, and as a fully sealed-box 
 * {@link java.lang.Class} wrapper that manages the full process of initial   
 * population and generation, and allows methods, fields, and so on to be
 * manipulated via a high-level, collections-line API.
 * <p/>   
 * Methods are provided to support this latter pattern in a sensible way, working
 * around 'freezing' SoftClass instances once bytecode has been generated for them,
 * and requiring they be 'thawed' (and optionally provided with a custom visitor 
 * upon which to perform the next generation cycle) before they may be used again.
 * This is similar to the behaviour exhibited by the Javassist generation library.
 * although with SoftClass it only applies to direct generation - there is no limit
 * to the number of times one may generate a {@link ClassNode} or use a custom 
 * {@link ClassVisitor}.
 * <p/>
 * As well as generation to bytecode for serialization as <code>.class</code> files
 * or further manipulation, convenience wrappers also allow SoftClasses to be 
 * directly defined in any class-loader and used (as {@link java.lang.Class} 
 * instances) immediately.
 * <p/>   
 * <strong>This class is not thread-safe</strong>. It is very important that 
 * the user take steps to ensure that no concurrent access or modification to
 * any <code>SoftClass</code> instance take place. Failure to take such 
 * precautions <i>may</i> result in an early failure (indicated by an
 * {@link InconsistentSoftClassException} or {@link MalformedMemberException}), but 
 * even this cannot be guaranteed.
 * <p/>   
 * In all interactions with SoftClass' public API, Java(tm) language names are used to
 * refer to classes, types and members. These language-names (i.e. "java.lang.String") 
 * are converted internally to the 'binary name' format used by the JVM. In some 
 * cases, however, those extending the API (through inheritance) will be required to
 * provide binary names, and methods are provided to allow this.
 * <p/>
 * In all cases where order is not significant (such as order of methods, fields, 
 * and nested classes), order is not preserved by this class. This is a deliberate
 * limitation of the underlying member storage structure.
 * 
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.12 $ $Date: 2005/11/09 21:05:04 $
 */
public class SoftClass extends AbstractSoftMember
{
  // TODO Proper support for ANNOTATIONS
  
  // Perhaps these should be coalesced, but then it's providing the 'all method' 
  // and what not stuff. Lookups would be fine, since keys are different...
  private Map<String,NestedSoftClass> clazzMap;
  private Map<String,SoftField> fieldMap;
  private Map<String,SoftMethod> methodMap;
  
  // Allows us to replace refs - these are used internally to the package
  private final String originalName;
  private final String originalSuper;

  private int version;
  private String superName;
  private List<String> interfaces;
  private String outerClass;
  private String outerMethod;
  private String outerMethodDesc;
  //private String genericSignature;
  private String sourceFile;
  private String sourceDebug;

  /* directly copied through */
  private List<Attribute> attrs;
  private List<?> visibleAnnotations;
  private List<?> invisibleAnnotations;
  
  /* optional */
  private ClassLoader loader;

  /* **********************************
   * POPULATION FROM EXISTING INFO
   */
  /**
   * Initialize this SoftClass instance from the supplied ASM {@link ClassNode}. This method
   * is protected to allow subclasses to share initialization code with <code>SoftClass</code>.
   *  
   * @param node The {@link ClassNode} to initialize the instance from.
   */
  /* SUPPRESSWARNINGS BECAUSE WE TRUST ASM NODE WILL HAVE CORRECT LIST ELEMENTS ? */
  @SuppressWarnings("unchecked")  
  protected void initializeFromClassNode(ClassNode node) {
    // Modifiers and name are done by AbstractMember ctor    
    version = node.version;
    superName = node.superName;    
    outerClass = node.outerClass;
    outerMethod = node.outerMethod;
    outerMethodDesc = node.outerMethodDesc;
    sourceFile = node.sourceFile;
    sourceDebug = node.sourceDebug;    
    
    setSignature(node.signature);
    
    initNestedClassesHelper(node.innerClasses);
    initMethodsHelper(node.methods);
    initFieldsHelper(node.fields);

    interfaces = new ArrayList<String>();
    if (node.interfaces != null) interfaces.addAll(node.interfaces);
    
    attrs = new ArrayList();
    if (node.attrs != null) attrs.addAll(node.attrs);
    visibleAnnotations = new ArrayList();
    if (node.visibleAnnotations != null) visibleAnnotations.addAll(node.visibleAnnotations);
    invisibleAnnotations = new ArrayList();
    if (node.invisibleAnnotations != null) invisibleAnnotations.addAll(node.invisibleAnnotations);
  }
  
  private void initNestedClassesHelper(List<InnerClassNode> nestedClasses) {
    //clazzMap = Collections.synchronizedMap(new LinkedHashMap<String,NestedSoftClass>());    
    clazzMap = new LinkedHashMap<String,NestedSoftClass>();    

  /* nested classes */
    for (InnerClassNode nested : nestedClasses) {
      clazzMap.put(SoftClass.memberClassKey(nested), new ASMSoftNestedClass(
          this, nested));
    }
  }

  private void initMethodsHelper(List<MethodNode> methods) {
    // methodMap = Collections.synchronizedMap(new HashMap<String,SoftMethod>());
    methodMap = new HashMap<String,SoftMethod>();

    /* methods */
    for (MethodNode method : methods) {
      methodMap.put(SoftClass.memberMethodKey(method), new ASMSoftMethod(
          this, method));
    }
  }

  private void initFieldsHelper(List<FieldNode> fields) {
    //fieldMap = Collections.synchronizedMap(new HashMap<String,SoftField>());
    fieldMap = new HashMap<String,SoftField>();

    /* fields */
    for (FieldNode field : fields) {
      fieldMap.put(SoftClass.memberFieldKey(field), new ASMSoftField(this,
          field));
    }
  }

  /* **********************************
   * HELPERS
   */
  /**
   * Obtain a nested-class-map-compatible key for the given {@link InnerClassNode}.
   * The key returned will be in the correct format for use with the Map 
   * returned by the {@link #getNestedClassMap()} method, but will not necessarily
   * reflect any existing key in that map.
   * <p/>
   * The actual format of the key is implementation-specific and undefined by
   * this API. There is no requirement for cross compatibility of this (protected)
   * interface. It is provided as a helper for external access to internal <code>SoftClass</code> 
   * information only.
   * 
   * @param node The {@link InnerClassNode} to obtain a key for,
   * 
   * @see #getNestedClassMap()
   */
  /* classes are keyed on internal name */
  protected static String memberClassKey(InnerClassNode node) {
    return memberClassKey(node.name);
  }

  /**
   * Obtain a nested-class-map-compatible key for the given internal class name.
   * The key returned will be in the correct format for use with the Map 
   * returned by the {@link #getNestedClassMap()} method, but will not necessarily
   * reflect any existing key in that map.
   * 
   * @param name The binary class name to obtain a key for,
   * 
   * @see #getNestedClassMap()
   */
  /* classes are keyed on internal name */
  protected static String memberClassKey(String name) {
    return SoftUtils
        .nullSafeArg("SoftClass", "<internal>", "classNode", name);
  }

  /**
   * Obtain a method-map-compatible key for the given {@link MethodNode}.
   * The key returned will be in the correct format for use with the Map 
   * returned by the {@link #getMethodMap()} method, but will not necessarily
   * reflect any existing key in that map.
   * <p/>
   * The actual format of the key is implementation-specific and undefined by
   * this API. There is no requirement for cross compatibility of this (protected)
   * interface. It is provided as a helper for external access to internal <code>SoftClass</code> 
   * information only.
   * 
   * @param node The {@link MethodNode} to obtain a key for,
   * 
   * @see #getMethodMap()
   */
  /* methods on name+descriptor (e.g. someMethod(Ljava/lang/String;)V */
  protected static String memberMethodKey(MethodNode node) {
    return SoftUtils.nullSafeArg("SoftClass", "<internal>", "methodNode[.name]",
        node.name)
        + SoftUtils.nullSafeArg("SoftClass", "<internal>", "methodNode[.desc]",
            node.desc);
  }

  /**
   * Obtain a method-map-compatible key for the given {@link MethodNode}.
   * The key returned will be in the correct format for use with the Map 
   * returned by the {@link #getMethodMap()} method, but will not necessarily
   * reflect any existing key in that map.
   * <p/>
   * This overload allows methods to be specified by name and (internal) descriptor.
   * 
   * @param name The name of the method, e.g. <code>someMethod</code>
   * @param desc The method descriptor, e.g. <code>(Ljava/lang/String;)Ljava/lang/String</code>.
   *             You can easily obtain method descriptors with the ASM {@link org.objectweb.asm.commons.Method}
   *             class.
   *             
   * @see #getMethodMap()
   */
  protected static String memberMethodKey(String name, String desc) {
    return SoftUtils.nullSafeArg("SoftClass", "<internal>", "name", name)
        + SoftUtils.nullSafeArg("SoftClass", "<internal>", "desc", desc);
  }

  /**
   * Obtain a field-map-compatible key for the given {@link FieldNode}.
   * The key returned will be in the correct format for use with the Map 
   * returned by the {@link #getFieldMap()} method, but will not necessarily
   * reflect any existing key in that map.
   * <p/>
   * The actual format of the key is implementation-specific and undefined by
   * this API. There is no requirement for cross compatibility of this (protected)
   * interface. It is provided as a helper for external access to internal <code>SoftClass</code> 
   * information only.
   * 
   * @param node The {@link FieldNode} to obtain a key for,
   * 
   * @see #getFieldMap()
   */
  /* IMPL NOTE: fields are keyed on name only (as in Java) - desc is ignored */
  protected static String memberFieldKey(FieldNode node) {
    return SoftClass.memberFieldKey(SoftUtils.nullSafeArg("SoftClass",
        "<internal>", "fieldNode[.name]", node.name), node.desc); // allow this
                                                                  // to be null,
                                                                  // as it's
                                                                  // ignored
                                                                  // anyway
    // (it's unlikely to ever _be_ null with a real node)
  }

  /**
   * Obtain a field-map-compatible key for the given {@link FieldNode}.
   * The key returned will be in the correct format for use with the Map 
   * returned by the {@link #getFieldMap()} method, but will not necessarily
   * reflect any existing key in that map.
   * <p/>
   * This overload allows fields to be specified by name and (internal) descriptor.
   * 
   * @param name The name of the field, e.g. <code>someMethod</code>
   * @param desc The field descriptor, e.g. <code>Ljava/lang/String;</code>.
   * 
   * @see #getFieldMap()
   */
  protected static String memberFieldKey(String name, String desc) {
    return SoftUtils.nullSafeArg("SoftClass", "<internal>", "name", name);
  }
  
  /**
   * Obtain the {@link Map} holding {@link SoftMember} instances representing this
   * <code>SoftClass</code>' nested classes. The {@link #memberClassKey(InnerClassNode)} 
   * helper method can be used to obtain valid keys into this map.
   * <p/>
   * The nested class map is synchronized internally (by wrapping it 
   * with {@link Collections#synchronizedMap(java.util.Map)}).
   *  
   * @return The nested class {@link Map}. This includes all nested classes declared 
   *         by the class, including static, inner, and local classes.
   * 
   * @see #memberClassKey(InnerClassNode)
   */
  protected Map<String,NestedSoftClass> getNestedClassMap() {
    return clazzMap;
  }
  
  /**
   * Obtain the {@link Map} holding {@link SoftMethod} instances representing this
   * <code>SoftClass</code>' methods. The {@link #memberMethodKey(MethodNode)} 
   * helper method can be used to obtain valid keys into this map.
   * <p/>
   * The method map is synchronized internally (by wrapping it 
   * with {@link Collections#synchronizedMap(java.util.Map)}).
   *  
   * @return The method {@link Map}.
   * 
   * @see #memberMethodKey(MethodNode)
   * @see #memberMethodKey(String, String)
   */
  protected Map getMethodMap() {
    return methodMap;
  }
  
  /**
   * Obtain the {@link Map} holding {@link SoftField} instances representing this
   * <code>SoftClass</code>' fields. The {@link #memberFieldKey(FieldNode)} 
   * helper method can be used to obtain valid keys into this map.
   * <p/>
   * The field map is synchronized internally (by wrapping it 
   * with {@link Collections#synchronizedMap(java.util.Map)}).
   *  
   * @return The field {@link Map}.
   * 
   * @see #memberFieldKey(FieldNode)
   * @see #memberFieldKey(String, String)
   */
  protected Map<String,SoftField> getFieldMap() {
    return fieldMap;
  }
    
  /* ***************************************************************
   * INSTANCE INIT
   * 
   * Please keep call-thru ctors with their appropriate worker.
   * It gets confusing otherwise =-]
   */
  private static ClassNode classReaderCtorHelper(ClassReader reader,
      boolean skipDebug) {
    ClassNode node = new ClassNode();
    reader.accept(node, skipDebug);
    return node;
  }

  private static ClassNode javaClassCtorHelper(Class clazz, boolean skipDebug) {
    ClassNode node = new ClassNode();

    ClassReader r;
    try {
      r = new ClassReader(clazz.getName());
    } catch (IOException e) {
      throw (new BytecodeUnavailableException(clazz));
    }

    r.accept(node, skipDebug);
    return node;
  }

  /* *************************************************************** */  
  /* by class */
  
  /**
   * Create a new <code>SoftClass</code> from a Java {@link Class}, using a default
   * {@link ClassLoader} and retaining debug information.
   * 
   * @param clazz The {@link Class} from which to initialize the new <code>SoftClass</code>.
   *        
   * @throws BytecodeUnavailableException If the <code>.class</code> is not available
   *         as a resource.
   */
  public SoftClass(Class<?> clazz) {
    this(clazz, null);
  }

  /**
   * Create a new <code>SoftClass</code> from a Java {@link Class}, specifying the 
   * {@link ClassLoader} to use for resolution / definition, and retaining debug information.
   * <p/>
   * Passing a <code>null</code> <code>ClassLoader</code> results in a default 
   * loader being used.
   * 
   * @param clazz The {@link Class} from which to initialize the new <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   *        
   * @throws BytecodeUnavailableException If the <code>.class</code> is not available
   *         as a resource.
   */
  public SoftClass(Class<?> clazz, ClassLoader loader) {
    this(clazz, loader, false);
  }

  /**
   * Create a new <code>SoftClass</code> from a Java {@link Class}, supplying
   * the {@link ClassLoader} used for resolution and definition (if used)
   * and optionally ignoring debug information from the original class.
   * <p/>
   * Passing a <code>null</code> <code>ClassLoader</code> results in a default 
   * loader being used. This will be the first available of the current thread's
   * context loader, the same loader as this class, the loader from {@link Class},
   * or the system classloader.
   * <p/>
   * This constructor requires access to the original <code>.class</code> representing
   * the supplied <code>Class</code> (via <code>getResourceAsStream</code>).
   * If the bytes are not available, a {@link BytecodeUnavailableException} is thrown.
   * 
   * @param clazz The {@link Class} from which to initialize the new <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   * @param skipDebug If <code>true</code>, all debugging information in the original class
   *        will be ignored during initialization.
   *        
   * @throws BytecodeUnavailableException If the <code>.class</code> is not available
   *         as a resource.
   */
  /* WORKER 1 */
  public SoftClass(Class<?> clazz, ClassLoader loader, boolean skipDebug) {
    this(javaClassCtorHelper(
        SoftUtils.nullSafeArg(SoftClass.class,"clazz", clazz), skipDebug));
    this.loader = (loader != null) ? loader : SoftUtils.defaultLoaderHelper();
  }

  /* *************************************************************** */  
  /* by reader */
  
  /**
   * Create a new <code>SoftClass</code> from an ASM {@link ClassReader} using a default
   * {@link ClassLoader} and retaining debug information.
   * 
   * @param reader The {@link ClassReader} from which to initialize the new <code>SoftClass</code>.
   */
  public SoftClass(ClassReader reader) {
    this(reader, null);
  }

  /**
   * Create a new <code>SoftClass</code> from an ASM {@link ClassReader} specifying the
   * {@link ClassLoader} to use for resolution / definition and retaining debug information.
   * <p/>
   * Passing a <code>null</code> <code>ClassLoader</code> results in a default 
   * loader being used.
   * 
   * @param reader The {@link ClassReader} from which to initialize the new <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   */
  public SoftClass(ClassReader reader, ClassLoader loader) {
    this(reader, loader, false);
  }

  /* WORKER 2 */
  /**
   * Create a new <code>SoftClass</code> from an ASM {@link ClassReader} specifying the
   * {@link ClassLoader} to use for resolution / definition and optionally ignoring 
   * debug information.
   * <p/>
   * Passing a <code>null</code> <code>ClassLoader</code> results in a default 
   * loader being used.
   * 
   * @param reader The {@link ClassReader} from which to initialize the new <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   * @param skipDebug If <code>true</code>, all debugging information in the original class
   *        will be ignored during initialization.
   */
  public SoftClass(ClassReader reader, ClassLoader loader, boolean skipDebug) {
    this(classReaderCtorHelper(
        SoftUtils.nullSafeArg(SoftClass.class, "reader", reader), skipDebug));
    this.loader = (loader != null) ? loader : SoftUtils.defaultLoaderHelper();
  }

  /* *************************************************************** */  
  /* direct from node */
  
  /**
   * Create a new <code>SoftClass</code> from an ASM {@link ClassNode} using a default
   * {@link ClassLoader}. This constructor expects the node to be fully initialized 
   * and linked to member nodes as appropriate.
   * 
   * @param node The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   */
  public SoftClass(ClassNode node) {
    this(node, (ClassLoader)null);
  }

  /* WORKER 3 */
  /**
   * Create a new <code>SoftClass</code> from an ASM {@link ClassNode} using a default
   * {@link ClassLoader}. This constructor expects the node to be fully initialized 
   * and linked to member nodes as appropriate, and allows the {@link ClassLoader}
   * to be used for resolution and definition to be specified. 
   * 
   * @param node The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   */
  public SoftClass(ClassNode node, ClassLoader loader) {
    super((SoftUtils.nullSafeArg(SoftClass.class, "node", node)).access,node.name,true);
    initializeFromClassNode(SoftUtils.nullSafeArg(SoftClass.class, "node", node));
    this.originalName = node.name;
    this.originalSuper = node.superName;
    this.loader = (loader != null) ? loader : SoftUtils.defaultLoaderHelper();
  }
  
  /* *************************************************************** */  
  /* direct for brand new classes */
  
  /**
   * Create a new empty <code>SoftClass</code> with the specified access modifiers and
   * name. The new top-level class will directly subclass {@link Object}, implement no interfaces,
   * and have no generic signature.
   * <p/>
   * Access modifiers may be obtained by adding together the appropriate 
   * <code>ACC_XXXX</code> constants (defined on the ASM {@link org.objectweb.asm.Opcodes} 
   * interface, which <code>SoftClass</code> implements).  
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   */
  /* extends java.lang.Object */
  public SoftClass(int modifiers, String name) {
    this(modifiers,name,(String)null);
  }
  
  /**
   * Create a new empty <code>SoftClass</code> with the specified access modifiers, name and
   * superclass. The new top-level class will implement no interfaces, and have no generic 
   * signature.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   */
  public SoftClass(int modifiers, String name, String superName) {
    this(modifiers,name,superName,(String)null);
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified access modifiers, name and
   * superclass. The new top-level class will implement the specified interfaces, and have no generic 
   * signature.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param interfaces An array of interface classes this SoftClass will implement.
   */
  public SoftClass(int modifiers, String name, String superName, Class<?>[] interfaces) {
    this(modifiers,name,superName,(String)null,interfaces);
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified access modifiers, name and
   * superclass. The new top-level class will implement the specified interfaces, and have no generic 
   * signature.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param interfaces An array of interface classes this SoftClass will implement.
   */
  public SoftClass(int modifiers, String name, String superName, Type[] interfaces) {
    this(modifiers,name,superName,(String)null,interfaces);
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor
   * defaults to <code>V1_5</code> (JSE 5.0), applies no non-standard attributes, and generates top-level
   * classes that implement no interfaces.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   */
  public SoftClass(int modifiers, String name, String superName, String signature) {
    this(modifiers,name,superName,signature,(List<Class<?>>)null);
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor
   * defaults to <code>V1_5</code> (JSE 5.0), applies no non-standard attributes, and generates top-level
   * classes.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   * @param interfaces A {@link List} of {@link Class} instances representing the interfaces this class
   *        will implement, or <code>null</code> to implement no interfaces. You must of course ensure 
   *        all required methods are generated. The array is copied to an internal collection.
   */
  public SoftClass(int modifiers, String name, String superName, String signature, List<Class<?>> interfaces) {
    this(modifiers,name,superName,signature,typeForClassList(interfaces),(String)null,(String)null,(String)null);
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor
   * defaults to <code>V1_5</code> (JSE 5.0), applies no non-standard attributes, and generates top-level
   * classes.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   * @param interfaces An array of {@link Class} instances representing the interfaces this class
   *        will implement, or <code>null</code> to implement no interfaces. You must of course ensure 
   *        all required methods are generated. The array is copied to an internal collection.
   */
  public SoftClass(int modifiers, String name, String superName, String signature, Class<?>[] interfaces) {
    this(modifiers,name,superName,signature,asList(interfaces));
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified access modifiers, name and
   * superclass. The new top-level class will implement the specified interfaces, and have no generic 
   * signature.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param interfaces An array of interface classes this SoftClass will implement.
   */
  public SoftClass(int modifiers, String name, String superName, String signature, Type[] interfaces) {
    this(modifiers,name,superName,signature,asList(interfaces),(String)null,(String)null,(String)null);
  }
  
  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor
   * defaults to <code>V1_5</code> (JSE 5.0), applies no non-standard attributes, and generates top-level
   * classes.
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   * @param interfaces An array of {@link Class} instances representing the interfaces this class
   *        will implement, or <code>null</code> to implement no interfaces. You must of course ensure 
   *        all required methods are generated. The array is copied to an internal collection.
   */
  public SoftClass(int version, int modifiers, String name, String superName, String signature, Class<?>[] interfaces) {
    this(version,(Collection<Attribute>)null,modifiers,name,superName,signature,
        typeForClassList(asList(interfaces)),(String)null,(String)null,(String)null);
  }

  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor
   * defaults to <code>V1_5</code> (JSE 5.0), applies no non-standard attributes, and accepts an
   * array of {@link Class} for implemented interfaces (as opposed to an array of {@link Type}). 
   * 
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   * @param interfaces An array of {@link Class} instances representing the interfaces this class
   *        will implement, or <code>null</code> to implement no interfaces. You must of course ensure 
   *        all required methods are generated. The array is copied to an internal collection.
   * @param outerClass The outer class in which this (nested) class is declared, or <code>null</code>
   *        if this is not a nested class of any kind.
   * @param outerMethod The method name on the <code>outerClass</code> in which this (local or anonymous)
   *        inner class is declared, or <code>null</code> if this is not a local or anonymous inner class.
   * @param outerMethodDesc The method descriptor on the <code>outerClass</code> in which this (local or 
   *        anonymous) inner class is declared, or <code>null</code> if this is not a local or anonymous
   *        inner class.
   */
  public SoftClass(int modifiers, 
                   String name, 
                   String superName,
                   String signature, 
                   List<Type> interfaces,
                   String outerClass, 
                   String outerMethod,
                   String outerMethodDesc) {
    this(V1_5,(Collection<Attribute>)null,modifiers,name,superName,signature,interfaces,outerClass,outerMethod,outerMethodDesc);
  }  
  
  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor 
   * allows total flexibility with respect to top-level class information, including allowing
   * the <code>.class</code> format version and non-standard attributes to be specified. 
   * <p/>
   * Access modifiers may be obtained by adding together the appropriate 
   * <code>ACC_XXXX</code> constants.
   * 
   * @param version The <code>.class</code> format version specifier to generate.
   * @param attrs A {@link Collection} with the attributes to apply to the class declaration,
   *        or <code>null</code> to apply no attributes. The elements are copied into a new
   *        internal collection.
   * @param modifiers The {@link ClassNode} from which to initialize the new <code>SoftClass</code>.
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   * @param interfaces An array of ASM {@link Type} instances representing the interfaces this class
   *        will implement, or <code>null</code> to implement no interfaces. You must of course ensure 
   *        all required methods are generated. The array is copied to an internal collection.
   * @param outerClass The outer class in which this (nested) class is declared, or <code>null</code>
   *        if this is not a nested class of any kind.
   * @param outerMethod The method name on the <code>outerClass</code> in which this (local or anonymous)
   *        inner class is declared, or <code>null</code> if this is not a local or anonymous inner class.
   * @param outerMethodDesc The method descriptor on the <code>outerClass</code> in which this (local or 
   *        anonymous) inner class is declared, or <code>null</code> if this is not a local or anonymous
   *        inner class.
   */
  public SoftClass(int version,
      Collection<Attribute> attrs,                   
      int modifiers, 
      String name, 
      String superName,
      String signature, 
      List<Type> interfaces,
      String outerClass, 
      String outerMethod,
      String outerMethodDesc) {
    this(version,attrs,modifiers,name,superName,signature,interfaces,outerClass,outerMethod,outerMethodDesc,SoftUtils.defaultLoaderHelper());    
  }
  
  /* at 0.40 - Helper to implicitly add ACC_SUPER to class modifiers
   * for non-interface classes with a version above , and ACC_ABSTRACT to interface classes.
   * 
   * If you *need* to not have this (e.g. classes for Jdk 1.0), you'll need to call setModifiers
   * after constructing the class.
   */
  private static int modifiersHelper(int modifiers) {
    return (modifiers & ACC_INTERFACE) == 0 ? modifiers | ACC_SUPER : modifiers | ACC_ABSTRACT; 
  }
  
  /**
   * Create a new empty <code>SoftClass</code> with the specified metrics. This constructor 
   * allows total flexibility with respect to top-level class information, including allowing
   * the <code>.class</code> format version and non-standard attributes to be specified. 
   * It additionally allows a {@link ClassLoader} to be supplied, for use during resolution and
   * definition.
   * <p/>
   * Access modifiers may be obtained by adding together the appropriate 
   * <code>ACC_XXXX</code> constants.
   * 
   * @param version The <code>.class</code> format version specifier to generate.
   * @param attrs A {@link Collection} with the attributes to apply to the class declaration,
   *        or <code>null</code> to apply no attributes. The elements are copied into a new
   *        internal collection.
   * @param modifiers The access modifiers for the new class. This is a bitmap constructed from
   *        the various <code>ACC_XXXX</code> constants. If <code>ACC_INTERFACE</code> is not specified, 
   *        <code>ACC_SUPER</code> will be added implicitly. Otherwise, <code>ACC_ABSTRACT</code> 
   *        will be added. No further checking or modification is performed. 
   * @param name The fully-qualified language name for the new class 
   *        (e.g. <code>com.mycompany.MyClass</code>).
   * @param superName The fully-qualified language name for the new class' superclass, or 
   *        <code>null</code> to use {@link Object}.
   * @param signature The generic signature for this class, or <code>null</code> for no signature.
   *        This applies only when <code>version</code> is <code>&gt;= 49</code>,         
   * @param interfaces An array of ASM {@link Type} instances representing the interfaces this class
   *        will implement, or <code>null</code> to implement no interfaces. You must of course ensure 
   *        all required methods are generated. The array is copied to an internal collection.
   * @param outerClass The outer class in which this (nested) class is declared, or <code>null</code>
   *        if this is not a nested class of any kind.
   * @param outerMethod The method name on the <code>outerClass</code> in which this (local or anonymous)
   *        inner class is declared, or <code>null</code> if this is not a local or anonymous inner class.
   * @param outerMethodDesc The method descriptor on the <code>outerClass</code> in which this (local or 
   *        anonymous) inner class is declared, or <code>null</code> if this is not a local or anonymous
   *        inner class.
   * @param loader The {@link ClassLoader} to use for resolution and definition.
   */
  /* WORKER 4 */
  public SoftClass(int version,
                   Collection<Attribute> attrs,                   
                   int modifiers, 
                   String name, 
                   String superName,
                   String signature, 
                   List<Type> interfaces,
                   String outerClass, 
                   String outerMethod,
                   String outerMethodDesc,
                   ClassLoader loader) {
    super(modifiersHelper(modifiers),SoftUtils.javaToBinary(name),true); // the true is 'dummy' param
    
    this.version = version;
    this.attrs = new ArrayList<Attribute>();    
    if (attrs != null) attrs.addAll(attrs);
    this.superName = (modifiers & ACC_INTERFACE) == 0 ? 
        SoftUtils.javaToBinary(superName != null ? superName : "java.lang.Object") : 
        null;
        
    this.outerClass = outerClass;
    this.outerMethod = outerMethod;
    this.outerMethodDesc = outerMethodDesc;

    setSignature(signature);
    
    this.interfaces = Collections.synchronizedList(new ArrayList<String>());    
    if (interfaces != null) {
      for (Type t : interfaces) {
        this.interfaces.add(t.getInternalName());
      }
    }
    
    this.clazzMap = Collections.synchronizedMap(new HashMap<String,NestedSoftClass>());
    this.methodMap = Collections.synchronizedMap(new HashMap<String,SoftMethod>());
    this.fieldMap = Collections.synchronizedMap(new HashMap<String,SoftField>());
    
    this.invisibleAnnotations = new ArrayList();
    this.attrs = new ArrayList<Attribute>();
    this.visibleAnnotations = new ArrayList();
    this.invisibleAnnotations = new ArrayList();
    
    this.loader = loader;

    originalName = SoftUtils.javaToBinary(name);        // these two won't be used    
    originalSuper = this.superName;  // but they'll still be checked
  }
  
  /* *************************************************************** */  
  /* by SoftNestedClass */
  
  /**
   * Create a new <code>SoftClass</code> from a {@link NestedSoftClass} reference obtained
   * from another <code>SoftClass</code>, specifying that a default classloader be used when
   * required, and that debug information should be retained.. This is a convenience 
   * constructor that simply creates a new {@link ClassReader} for the specified nested 
   * class and calls the compatible constructor.
   * 
   * @param nestedClazz The {@link NestedSoftClass} from which to initialize the new 
   *        <code>SoftClass</code>.
   *        
   * @throws BytecodeUnavailableException If the <code>.class</code> is not available
   *         as a resource.
   */
  public SoftClass(NestedSoftClass nestedClazz) {
    this(nestedClazz, null);
  }

  /**
   * Create a new <code>SoftClass</code> from a {@link NestedSoftClass} reference obtained
   * from another <code>SoftClass</code>, specifying the {@link ClassLoader} to use for 
   * resolution / definition, and retaining debug information. This is a convenience 
   * constructor that simply creates a new {@link ClassReader} for the specified nested 
   * class and calls the compatible constructor.
   * <p/>
   * Passing a <code>null</code> <code>ClassLoader</code> results in a default 
   * loader being used.
   * 
   * @param nestedClazz The {@link NestedSoftClass} from which to initialize the new 
   *        <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   *        
   * @throws BytecodeUnavailableException If the <code>.class</code> is not available
   *         as a resource.
   */
  public SoftClass(NestedSoftClass nestedClazz, ClassLoader loader) {
    this(nestedClazz, loader, false);
  }
  
  private static ClassReader nestedCtorHelper(String name, ClassLoader loader, boolean skipDebug) {
    try {
      return new ClassReader(name);
    } catch (IOException e) {
      InputStream is = loader.getResourceAsStream("/"+javaToBinary(name));
      
      if (is == null) {
        throw(new BytecodeUnavailableException(name));
      }
      
      try {
        return new ClassReader(is);
      } catch (IOException e2) {
        throw(new BytecodeUnavailableException(name));
      }
    }
  }

  /**
   * Create a new <code>SoftClass</code> from a {@link NestedSoftClass} reference
   * obtained from another <code>SoftClass</code>, supplying the {@link ClassLoader} 
   * used for resolution and definition (if used) and optionally ignoring debug 
   * information from the original class. This is a convenience constructor that 
   * simply creates a new {@link ClassReader} for the specified nested class and 
   * calls the compatible constructor. Create a new <code>SoftClass</code> from 
   * a Java {@link Class}, 
   * <p/>
   * Passing a <code>null</code> <code>ClassLoader</code> results in a default 
   * loader being used. This will be the first available of the current thread's
   * context loader, the same loader as this class, the loader from {@link Class},
   * or the system classloader.
   * <p/>
   * This constructor requires access to the original <code>.class</code> representing
   * the supplied <code>Class</code> (via <code>getResourceAsStream</code>) from one
   * of the current Thread context classloader, or the supplied loader.
   * If the bytes are not available, a {@link BytecodeUnavailableException} is thrown.
   * 
   * @param nestedClazz The {@link NestedSoftClass} from which to initialize the new 
   *        <code>SoftClass</code>.
   * @param loader The {@link ClassLoader} to use for resolution, and definition if this
   *        <code>SoftClass</code> is to be defined.
   * @param skipDebug If <code>true</code>, all debugging information in the original class
   *        will be ignored during initialization.
   *        
   * @throws BytecodeUnavailableException If the <code>.class</code> is not available
   *         as a resource.
   */
  public SoftClass(NestedSoftClass nestedClazz, ClassLoader loader, boolean skipDebug) {
    this(nestedCtorHelper(SoftUtils.nullSafeArg(SoftClass.class,"clazz", nestedClazz).getName(), loader, skipDebug), loader);
  }
    
  /* ***************************************************************
   * PUBLIC INTERFACE - ACCESS
   */
  /**
   * Obtain the {@link ClassLoader} instance this {@link SoftClass} uses for
   * resolution and (optionally) definition.
   * 
   * @return The {@link ClassLoader} associated with this <code>SoftClass</code>,
   *         or <code>null</code> if none is associated.
   */
  public ClassLoader getClassLoader() {
    return loader;
  }

  /**
   * Set the {@link ClassLoader} instance this {@link SoftClass} uses for
   * resolution and (optionally) definition.
   * 
   * @param loader The {@link ClassLoader} to associate with this <code>SoftClass</code>,
   *        or <code>null</code> to revert to a default loader.
   */
  public void setClassLoader(ClassLoader loader) {
    this.loader = loader;
  }

  /**
   * Obtain the <code>.class</code> format version this <code>SoftClass</code> generates.
   * 
   * @return The <code>.class</code> format version.
   */
  public int getVersion() {
    return version;
  }

  /**
   * Set the <code>.class</code> format version this <code>SoftClass</code> generates.
   * 
   * @param version The new <code>.class</code> format version.
   */
  public void setVersion(int version) {
    checkModify();
    this.version = version;
  }

  /**
   * Obtain the access modifiers that apply to the generated class. The value
   * is a bit-map comprised of <code>ACC_XXXX</code> constants.
   * 
   * @return The access modifier bitmap.
   */
  public int getModifiers() {
    return super.getModifiers();
  }

  /**
   * Set the access modifiers that apply to the generated class.
   * 
   * @param modifiers The access modifier bitmap, obtained by combining the 
   *        <code>ACC_XXXX</code> constants.
   */
  public void setModifiers(int modifiers) {
    checkModify();
    super.setModifiers(modifiers);
  }

  /**
   * Obtain the fully-qualified language name of the generated class. 
   * 
   * @return The language name of the class.
   */
  public String getName() {
    return SoftUtils.binaryToJava(getInternalName());
  }

  /**
   * Obtain the fully-qualified binary name of the generated class. 
   * 
   * @return The binary name of the class.
   */
  public String getInternalName() {
    return super.getName();
  }

  /**
   * Set the generated class name from the specified fully-qualified language
   * name. 
   * 
   * @param name The new language name for the class.
   */
  public void setName(String name) {
    checkModify();
    setInternalName(SoftUtils.javaToBinary(name));
  }

  /**
   * Set the generated class name from the specified fully-qualified binary
   * name. 
   * 
   * @param internalName The new binary name for the class.
   */
  public void setInternalName(String internalName) {
    checkModify();
    super.setName(internalName);
  }

  /**
   * Obtain the fully-qualified language name of the generated class' superclass,
   * or {@code null} for an interface class.
   * 
   * @return The language name of the class.
   */
  public String getSuperClassName() {
    String bn = getSuperClassInternalName();
    return bn != null ? binaryToJava(bn) : null;
  }
  
  /**
   * Set the super class name for the generated class from the specified 
   * fully-qualified language name. 
   * 
   * @param superName The new language name for the class.
   */
  public void setSuperClassName(String superName) {
    checkModify();
    setSuperClassInternalName(SoftUtils.javaToBinary(superName));
  }
    
  /**
   * Obtain the {@link Class} representing the generated class' superclass, if
   * available. If the class is not available, this method throws an
   * {@link UnresolvableTypeHierarchyException}.
   * 
   * @return The superclass' {@link Class}, if available, or {@code null} if this
   *         is an interface class.
   * 
   * @throws UnresolvableTypeHierarchyException to indicate the superclass is not 
   *         available as a defined class.
   */
  public Class<?> getSuperClass() throws UnresolvableTypeHierarchyException {
    String sn = getSuperClassName();
    if (sn == null) return null;
    
    try {
      return SoftUtils.findClassHelper(loader,sn);
    } catch (NoClassDefFoundError e) {
      throw(new UnresolvableTypeHierarchyException(superName));
    }
  }

  /**
   * Set the {@link Class} representing the generated class' superclass.
   * 
   * @param clazz The superclass' {@link Class}.
   */
  public void setSuperClass(Class<?> clazz) {
    checkModify();
    
    if (clazz == null) {
      clazz = Object.class;
    }
    
    setSuperClassInternalName(Type.getType(clazz).getInternalName());
  }

  /**
   * Obtain the fully-qualified binary name of the generated class' superclass. 
   * 
   * @return The binary name of the class.
   */
  /* SUPER WORKER */
  public String getSuperClassInternalName() {
    return (getModifiers() & ACC_INTERFACE) == 0 ? superName : null;
  }
  
  /**
   * Set the super class name for the generated class from the specified 
   * fully-qualified binary name. 
   * 
   * @param superInternalName The new language name for the class.
   */
  /* SUPER WORKER */
  public void setSuperClassInternalName(String superInternalName) {
    checkModify();
    this.superName = superInternalName;
  }
  
  /**
   * Obtain the {@link List} of language names of interfaces implemented by
   * the generated class.
   * 
   * @return A new list with the language names of implemented interfaces.
   */
  public List<String> getInterfaces() {
    if (interfaces == null) return Collections.emptyList();
    List<String> ifaces = new ArrayList<String>(interfaces.size());
    for (String iface : interfaces) {
      ifaces.add(binaryToJava(iface));
    }
    return ifaces;
  }
  
  /**
   * Obtain the {@link List} of interfaces {link Class}es implemented by
   * the generated class. If the any referenced interface class is not available, 
   * this method throws an {@link UnresolvableTypeHierarchyException}.
   * 
   * @return A new list with the implemented interface {@code Class}es. 
   *         If this class implements no interfaces
   * 
   * @throws UnresolvableTypeHierarchyException to indicate one or more interfaces 
   *         are not available as a defined class.
   */
  public List<Class<?>> getInterfaceClasses() throws UnresolvableTypeHierarchyException {
    List<Class<?>> ifaces = new ArrayList<Class<?>>(getInterfaces().size());
    ClassLoader ldr = getClassLoader();
    
    for (String iface : getInterfaces()) {
      try {
        ifaces.add(ldr.loadClass(iface));
      } catch (ClassNotFoundException e) {
        throw(new UnresolvableTypeHierarchyException(binaryToJava(iface)));
      }
    }
    return ifaces;
  }
  
  /**
   * Adds the named interface to this SoftClass' <code>implements</code>
   * list. The specified name must be a valid Java identifier referring to an
   * <code>interface</code> class (though it does not necessarily have to refer 
   * to a loaded and/or available class at generation time).
   * <p/>
   * <strong>Note</strong> that merely adding an interface to a class does 
   * <i>not</i> actually implement it - you must ensure that all the methods
   * mandated by the interface are defined on the SoftClass, or you will 
   * receive either {@link AbstractMethodError} or 
   * {@link IncompatibleClassChangeError} errors at runtime.
   * 
   * @param iface The fully-qualified language name of the interface.
   */
  public void addInterface(String iface) {
    if (interfaces == null) {
      interfaces = new ArrayList<String>(3);    // the majority of cases?
    }
    
    if (!interfaces.contains(iface)) interfaces.add(SoftUtils.javaToBinary(iface)); 
  }
  
  /**
   * Adds the specified interface to this SoftClass' <code>implements</code>
   * list. This version should be preferred over {@link #addInterface(String)} 
   * when the class is available at generation time, as it helps ensure 
   * integrity in the generated class.
   * 
   * @param iface The interface {@link Class}. If this 
   */
  public void addInterface(Class<?> iface) {
    if (!nullSafeArg(getClass(),"addInterface","iface",iface).isInterface()) {
      throw(new IllegalArgumentException("Cannot implement '"+iface.getName()+"': it is not an interface"));      
    } 
    addInterface(iface.getName());
  }
  
  /**
   * Removes the named interface from this SoftClass' <code>implements</code>
   * list. The specified name must be a valid Java identifier. If it is not,
   * or if the specified interface is not implemented by this class, this
   * method does nothing.
   * <p/>
   * Removing an interface removes only the declaration - the actual methods
   * that implement the interface remain untouched. If the specified interface
   * is not implemented by this class, this method does nothing.
   * 
   * @param iface The fully-qualified language name of the interface.
   */
  public void removeInterface(String iface) {
    if (interfaces != null) {
      if (interfaces.contains(iface)) interfaces.remove(iface);
    }
  }
  
  /**
   * Removes the specified interface from this SoftClass' <code>implements</code>
   * list. If the specified {@link Class} is not an interface,
   * or if it is not implemented by this class, this method does nothing.
   * 
   * @param iface The interface {@link Class}.
   */
  public void removeInterface(Class<?> iface) {
    removeInterface(iface.getName());
  }
  
  /* **********************************
   * DEPRECATE 0.22 - KILL 0.30
   */
  /**
   * Obtain the Java 5 generic signature that applies to the generated class,
   * or <code>null</code> if the class has no generic signature, or is not
   * a version 49 or greater class.
   * 
   * @return The generic signature, or <code>null</code> if no signature applies.
   * 
   * @deprecated This method duplicates the inherited {@link AbstractSoftMember#getSignature()}
   *             method, and will be removed prior to a 1.0 release. 
   */
  public String getGenericSignature() {
    return getSignature();
  }

  /**
   * Set the Java 5 generic signature that applies to the generated class. You
   * may pass <code>null</code> to this method to remove any existing signature.
   * 
   * @param signature The new generic signature, or <code>null</code> to apply
   *        no generic signature.
   * 
   * @deprecated This method duplicates the inherited {@link AbstractSoftMember#setSignature(String)}
   *             method, and will be removed prior to a 1.0 release. 
   */
  public void setGenericSignature(String signature) {
    setSignature(signature);
  }
  /* ********************************** */
  
  /* 01/Oct/2005 */
  /* REMOVED DEPRECATED GET/SET FOR SOURCE AND SOURCEDEBUG INFO, SINCE THEY'RE IGNORED */
    
  /**
   * Obtain an ASM {@link Type} representing the generated class.
   * 
   * @return A new ASM {@link Type} instance representing the generated class.
   */
  public Type getType() {
    return Type.getType("L"+getInternalName()+";");    
  }  

  /* **********************************
   * PUBLIC INTERFACE - MEMBERS
   * 
   * Implementation note: The List-returning methods in this section all 
   * return an immutable view of an underlying collection. Because 
   * we have LinkedHashMaps we should maintain order through this.
   * 
   * It could do with cleaning up though...
   */
  /**
   * Obtain an immutable {@link List} view of the {@link NestedSoftClass}es declared
   * on the generated class.
   * <p/>
   * <strong>Please Note</strong> that nested classes are not fully supported by
   * this API at present. In particular, there are no associated <code>get</code>,
   * <code>put</code> or <code>remove</code> methods for manipulating nested 
   * classes. This support will be improved in future versions.
   * 
   * @return An immutable {@link List} with the {@link NestedSoftClass}es.
   */
  @SuppressWarnings("unchecked")  
  public List<NestedSoftClass> getNestedSoftClasses() {
    List<NestedSoftClass> l = new ArrayList<NestedSoftClass>(clazzMap.size());
    l.addAll(clazzMap.values());
    return Collections.unmodifiableList(l);
  }

  /**
   * Obtain a specific {@link NestedSoftClass} from this <code>SoftClass</code>' 
   * nested class list, based on the specified language name.
   * 
   * @param name The nested class name, e.g. <code>MyNestedClass</code> or 
   *        <code>$0$MyInnerClass</code>.
   * 
   * @return The {@link SoftField} with that name and descriptor, or 
   *         <code>null</code> if no matching method is declared.
   */
  public NestedSoftClass getNestedSoftClass(String name) {
    if (name == null)
      throw (new NullArgumentException(getClass(), "getNestedSoftClass", "name"));
    try {
      return clazzMap.get(memberClassKey(SoftUtils.javaToBinary(name)));
    } catch (NullPointerException e) {
      return null;
    } catch (ClassCastException e) {
      throw (new MalformedMemberException(name, NestedSoftClass.class, clazzMap
          .get(memberClassKey(name))));
    }
  }

  /**
   * Adds the specified {@link NestedSoftClass} to those to be declared on the 
   * generated class. This will replace any existing nested softclass with the 
   * exact same binary name.
   * 
   * @param nestedClass The {@link NestedSoftClass} to add to this <code>SoftClass</code>.
   */
  public void putNestedSoftClass(NestedSoftClass nestedClass) {
    checkModify();
    if (nestedClass == null)
      throw (new NullArgumentException(getClass(), "putNestedSoftClass", "nestedClass"));
    clazzMap.put(memberClassKey(nestedClass.getName()), nestedClass);
  }

  /**
   * Removes the specified {@link NestedSoftClass} from those to be declared on the 
   * generated class. If the supplied instance is not amongst those held by
   * this <code>SoftClass</code> then this method does nothing.
   * 
   * @param nestedClass The {@link NestedSoftClass} to remove from this <code>SoftClass</code>.
   */
  public void removeNestedSoftClass(NestedSoftClass nestedClass) {
    checkModify();
    if (nestedClass == null)
      throw (new NullArgumentException(getClass(), "removeNestedSoftClass", "nestedClass"));
    clazzMap.remove(memberClassKey(nestedClass.getName()));
  }

  /**
   * Obtain an immutable {@link List} view of the {@link SoftMethod} instances
   * representing methods declared on the generated class.
   * 
   * @return An immutable {@link List} with the {@link SoftMethod}s.
   */
  // Again, trust ASM
  @SuppressWarnings("unchecked")  
  public List<SoftMethod> getSoftMethods() {    
    List<SoftMethod> l = new ArrayList<SoftMethod>(methodMap.size());
    l.addAll(methodMap.values());
    return Collections.unmodifiableList(l);
  }

  /**
   * Obtain a specific {@link SoftMethod} from this <code>SoftClass</code>' 
   * method list, based on the specified name and method descriptor.
   * 
   * @param name The method name, e.g. <code>myMethod</code>.
   * @param descriptor The method descriptor, e.g. <code>(Ljava/lang/String;)V</code>.
   * 
   * @return The {@link SoftMethod} with that name and descriptor, or 
   *         <code>null</code> if no matching method is declared.
   */
  public SoftMethod getSoftMethod(String name, String descriptor) {
    if ((name == null) || (descriptor == null))
      throw (new NullArgumentException(getClass(), "getSoftMethod",
          "method, descriptor"));
    try {
      return methodMap.get(memberMethodKey(name, descriptor));
    } catch (NullPointerException e) {
      return null;
    } catch (ClassCastException e) {
      throw (new MalformedMemberException(descriptor, SoftMethod.class,
          methodMap.get(memberMethodKey(name, descriptor))));
    }
  }
  
  /**
   * Obtain a specific {@link SoftMethod} from this <code>SoftClass</code>' 
   * method list, based on the specified named method descriptor. The argument
   * to this method must be the method's language signature (not the generic
   * signature) without access modifiers. The language signature is effectively
   * the method declaration without access modifiers or generics information,
   * e.g. <code>String someMethod(com.mycompany.SomeClass, int)</code>
   * and <code>void <init>()</code>.
   * 
   * @param method The method descriptor signature.
   * 
   * @return The {@link SoftMethod} with that descriptor, or <code>null</code>
   *         if no matching method is declared.
   */
  public SoftMethod getSoftMethod(String method) {
    Method m = Method.getMethod(method);
    return (m != null) ? getSoftMethod(m.getName(),m.getDescriptor()) : null;    
  }

  /**
   * Adds the specified {@link SoftMethod} to those to be declared on the 
   * generated class. This will replace any existing method with the exact
   * same signature.
   * 
   * @param method The {@link SoftMethod} to add to this <code>SoftClass</code>.
   */
  public void putSoftMethod(SoftMethod method) {
    checkModify();
    if (method == null)
      throw (new NullArgumentException(getClass(), "putSoftMethod", "method"));
    methodMap.put(memberMethodKey(method.getName(), method.getDescriptor()),
        method);
  }

  /**
   * Removes the specified {@link SoftMethod} from those to be declared on the 
   * generated class. If the supplied instance is not amongst those held by
   * this <code>SoftClass</code> then this method does nothing.
   * 
   * @param method The {@link SoftMethod} to remove from this <code>SoftClass</code>.
   */
  public void removeSoftMethod(SoftMethod method) {
    checkModify();
    if (method == null)
      throw (new NullArgumentException(getClass(), "removeSoftMethod", "method"));
    methodMap.remove(memberMethodKey(method.getName(), method.getDescriptor()));
  }

  /**
   * Obtain an immutable {@link List} view of the {@link SoftField} instances
   * representing fields declared on the generated class.
   * 
   * @return An immutable {@link List} with the {@link SoftMethod}s.
   */
  public List<SoftField> getSoftFields() {
    List<SoftField> l = new ArrayList<SoftField>(fieldMap.size());
    l.addAll(fieldMap.values());
    return Collections.unmodifiableList(l);
  }
  
  /**
   * Obtain a specific {@link SoftField} from this <code>SoftClass</code>' 
   * field list, based on the specified name only (as in the Java language).
   * 
   * @param name The field name, e.g. <code>myField</code>.
   * 
   * @return The {@link SoftField} with that name and descriptor, or 
   *         <code>null</code> if no matching method is declared.
   */
  public SoftField getSoftField(String name) {
    if (name == null)
      throw (new NullArgumentException(getClass(), "getSoftField", "name"));
    try {
      return fieldMap.get(memberFieldKey(name, null));
    } catch (NullPointerException e) {
      return null;
    } catch (ClassCastException e) {
      throw (new MalformedMemberException(name, SoftField.class, fieldMap
          .get(memberFieldKey(name, null))));
    }
  }

  /**
   * Adds the specified {@link SoftField} to those to be declared on the 
   * generated class. This will replace any existing field with the exact
   * same name.
   * 
   * @param field The {@link SoftField} to add to this <code>SoftClass</code>.
   */
  public void putSoftField(SoftField field) {
    checkModify();
    if (field == null)
      throw (new NullArgumentException(getClass(), "putSoftField", "field"));
    fieldMap.put(memberFieldKey(field.getName(), field.getDescriptor()), field);
  }

  /**
   * Removes the specified {@link SoftField} from those to be declared on the 
   * generated class. If the supplied instance is not amongst those held by
   * this <code>SoftClass</code> then this method does nothing.
   * 
   * @param field The {@link SoftField} to remove from this <code>SoftClass</code>.
   */
  public void removeSoftField(SoftField field) {
    checkModify();
    if (field == null)
      throw (new NullArgumentException(getClass(), "removeSoftField", "field"));
    fieldMap.remove(memberFieldKey(field.getName(), field.getDescriptor()));
  }

  /* **********************************
   * PUBLIC INTERFACE - ENCLOSING CLASS
   */
  /**
   * Obtain the language name of the class that encloses the generated (nested) 
   * class. If this <code>SoftClass</code> does not generate a nested class
   * of any kind, this method returns <code>null</code>.
   * 
   * @return The language name of the enclosing class.
   */
  public String getEnclosingClassName() {
    return SoftUtils.binaryToJava(outerClass);
  }

  /**
   * Set the language name of the class that encloses the generated (nested) 
   * class.
   * 
   * @param outerClass The language name of the new enclosing class.
   */
  public void getEnclosingClassName(String outerClass) {
    this.outerClass = SoftUtils.binaryToJava(outerClass);
  }

  /**
   * Obtain the language name of the method that encloses the generated (local 
   * or anonymous inner) class. If this <code>SoftClass</code> does not generate
   * an appropriate inner class, this method returns <code>null</code>.
   * 
   * @return The language name of the enclosing method.
   */
  public String getEnclosingMethod() {
    return outerMethod;
  }

  /**
   * Obtain the binary descriptor of the method that encloses the generated (local 
   * or anonymous inner) class. If this <code>SoftClass</code> does not generate
   * an appropriate inner class, this method returns <code>null</code>.
   * 
   * @return The binary descriptor of the enclosing method.
   */
  public String getEnclosingDescriptor() {
    return outerMethodDesc;
  }
  
  /**
   * Set the language name of the method that encloses the generated (local 
   * or anonymous inner) class. 
   * 
   * @param method The language name of the new enclosing method.
   */
  public void setEnclosingMethod(String method) {
    checkModify();
    outerMethod = method;
  }

  /**
   * Set the binary descriptor of the method that encloses the generated (local 
   * or anonymous inner) class. 
   * 
   * @param desc The binary descriptor of the new enclosing method.
   */
  public void setEnclosingDescriptor(String desc) {
    checkModify();
    outerMethodDesc = desc;
  }

  /* **********************************
   * GENERATION METHODS
   */
  private boolean phrozen = false;
  private byte[] generatedBytecode;
  private ClassVisitor generationDelegate;

  // THESE CONTROL THE FROZEN FLAG BASED ON WHETHER CODE IS CACHED
  /**
   * Causes any cached bytecode to be dropped, and the state of the <em>frozen</em> flag
   * to be reset to <code>false</code>. These methods, along with {@link #cacheBytecode(byte[])},
   * exclusively control the state of the <em>frozen</em> flag.
   */
  protected final synchronized void dropCache() {
    dropCache(false);
  }

  /**
   * Causes any cached bytecode to be dropped, the state of the <em>frozen</em> flag
   * to be reset to <code>false</code>, and calls {@link System#gc()} to suggest that
   * garbage collection be run as soon as possible.
   * 
   * @param gc Determines whether or not {@link System#gc()} is called after dropping the
   *        cached bytecode and resetting the frozen flag.
   */
  @Deprecated
  protected final synchronized void dropCache(boolean gc) {
    generatedBytecode = null;
    phrozen = false;
    if (gc) System.gc();
  }
  
  /**
   * Causes the specified byte array to be stored in the bytecode cache, and sets the
   * state of the <em>frozen</em> flag to <code>true</code>.
   * 
   * @param byteCode The bytes representing the <code>.class</code> structure.
   * 
   * @return The cached bytes for convenient call-chaining.
   */
  protected final synchronized byte[] cacheBytecode(byte[] byteCode) {
    checkModify();
    phrozen = true;
    generatedBytecode = SoftUtils.nullSafeArg(getClass(),"<internal>","byteCode",byteCode);
    return generatedBytecode;
  }
  
  /**
   * Returns the contents of the generated <code>.class</code> cache, assuming the 
   * class has been generated. If no bytes are cached, and {@link IllegalStateException}
   * is thrown.
   * <p/>
   * <strong>Note</strong> that consistency between the state of a given SoftClass and the 
   * contents of it's cache can only be guaranteed where the state of the frozen flag
   * has remained consistent (where a subclass is in use, for example), and where all
   * members are 'well behaved' with regard to calling (usually indirectly) 
   * {@link #canModify()} before allowing their parameters to be changed. 
   * 
   * @return The cached bytes with the generated <code>.class</code> structure. 
   */
  protected final synchronized byte[] cachedBytecode() {
    if (!isFrozen()) throw(new IllegalStateException("No bytecode is cached"));
    return generatedBytecode;
  }

  protected final void checkModify() {
    if (!canModify())
      throw (new IllegalStateException("Cannot modify frozen SoftClass '"+getName() + "'"));
  }
  
  /** 
   * Returns <code>true</code> if this <code>SoftClass</code> is not frozen
   * and should allow modification to take place.
   */
  /* !! don't break the call-through */
  protected final boolean canModify() {
    return !phrozen;
  }
  
  /**
   * Determines whether this <code>SoftClass</code> is <em>frozen</em> -
   * that is, whether it has been used to generate bytecode.
   * 
   * @return <code>true</code> if this SoftClass is frozen.
   */
  public final boolean isFrozen() {
    return phrozen;
  }

  /**
   * Retrieve the {@link ClassVisitor} to which this softclass will delegate
   * generation. If no delegate has been supplied a default {@link ClassWriter}
   * will be assigned internally, and returned. 
   * <p/> 
   * <i>Implementation note</i>: If the delegate is found to be <code>null</code>
   * but this SoftClass is frozen, then a new delegate will <strong>not</strong> 
   * be created. Such a condition is explicitly outside normal operating parameters
   * for this class, and is taken to indicate an unexpected failure. In the 
   * interests of predictable behaviour, an {@link InconsistentSoftClassException} is
   * immediately thrown.
   * 
   * @return The delegate visitor.
   */
  protected ClassVisitor getDelegate() {
    if (generationDelegate == null) {
      if (!isFrozen()) {
        generationDelegate = new ClassWriter(true);
      } else {
        throw (new InconsistentSoftClassException(this));
      }
    }
    return generationDelegate;
  }

  /**
   * Set the {@link ClassVisitor} to which this softclass will delegate
   * generation. Passing <code>null</code> results in a default
   * {@link ClassWriter} being used.
   * 
   * @param visitor
   *          The delegate visitor.
   */
  protected void setDelegate(ClassVisitor visitor) {
    checkModify();
    generationDelegate = visitor;
  }
 
  /**
   * 'Thaws' this SoftClass if frozen. This dumps the cached bytecode and sets
   * the supplied visitor as the generation delegate. If the SoftClass is not
   * frozen this operates identically to {@link #setDelegate(ClassVisitor)}
   * 
   * @param v
   * 
   * @see #generateBytecode()
   */
  public void thaw(ClassVisitor v) {
    dropCache();
    setDelegate(v);
  }
  
  /**
   * 'Thaws' this SoftClass if frozen. This dumps the cached bytecode and creates
   * a new {@link ClassWriter} (with auto-calculated maxs). If the 
   * SoftClass is not frozen this operates identically to 
   * {@link #setDelegate(ClassVisitor)} when passed a 
   * <code>new ClassWriter(true)</code>.
   * 
   * @see #generateBytecode()
   */
  public void thaw() {
    thaw(new ClassWriter(true));
  }

  /**
   * Generate a complete <code>.class</code> structure from this <code>SoftClass</code>, 
   * and return it. The first time this is called the generated bytecode will be cached,
   * and the softclass <em>frozen</em>, preventing further modification. Subsequent
   * calls to this method will return the cached bytes, rather than generating anew.
   * A frozen <code>SoftClass</code> can be <em>thawed</em>, allowing modification and
   * regeneration. This is facilitated by the {@link #thaw(ClassVisitor)} methods. 
   * <p/>
   * The byte array returned from this method is suitable for storage to 
   * <code>.class</code> files, or direct definition with a JVM.
   * 
   * @return Bytes representing a complete <code>.class</code> file structure.
   * 
   * @see #thaw(ClassVisitor)
   * @see #defineClass(ClassLoader)
   */
  public byte[] generateBytecode() {
    if (isFrozen()) {
      return cachedBytecode();
    } else {
      if (!ClassWriter.class.isAssignableFrom(getDelegate().getClass()))
        throw (new IllegalStateException(
            "auto-generation is only supported with ClassWriter delegate"));
      generateNode().accept(generationDelegate);
      return cacheBytecode(((ClassWriter)generationDelegate).toByteArray());
    }
  }

  /**
   * Convenience {@link #defineClass(ClassLoader)} overload that uses the 
   * {@link ClassLoader} associated with this <code>SoftClass</code>, or a
   * default loader (first available of the current
   * thread's context loader, <code>SoftClass.class.getClassLoader()</code>, 
   * or the system classloader) if none is associated.
   * 
   * @return A new {@link Class} instance representing the defined class.
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
  // N.B. Deliberately NOT parameterised with <?> to allow useful assignment
  public Class defineClass() {
    ClassLoader loader = this.loader;
    if (loader == null) loader = SoftUtils.defaultLoaderHelper(getName());
    return defineClass(loader);
  }

  /**
   * Convenience method that generates the bytes for this class (causing it to be
   * frozen), and attempts to define the {@link Class} instance in the specified
   * {@link ClassLoader} by reflectively accessing the {@link ClassLoader#defineClass}
   * method.
   * 
   * @param loader The {@link ClassLoader} in which to define the class.
   * 
   * @return A new {@link Class} instance representing the defined class.
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
  // N.B. Deliberately NOT parameterised with <?> to allow useful assignment
  public Class defineClass(ClassLoader loader) {
    try {
      return SoftUtils.defineClassHelper(loader, generateBytecode());
    } catch (InvocationTargetException e) {
      throw(new RuntimeException(e.getCause()));
    }
  }
  
  private void nestedGenerationHelper(ClassVisitor vis) {
    for (SoftMember member : clazzMap.values()) {
      member.accept(vis);
    }    
  }
  
  private void methodGenerationHelper(ClassVisitor vis) {
    for (SoftMethod c : methodMap.values()) {
      c.accept(vis);
    }    
  }
  
  private void fieldGenerationHelper(ClassVisitor vis) {
    for (SoftField c : fieldMap.values()) {
      c.accept(vis);
    }    
  }
  
  /**
   * Generate an ASM {@link ClassNode} from this <code>SoftClass</code>, for
   * use in a larger generation run for example. This does <strong>not</strong>
   * freeze the <code>SoftClass</code> - there is no guarantee that a node
   * generated from a given instance will accurately reflect that instance at
   * any point in the future. 
   * 
   * @return A new {@link ClassNode} that will generate the same class as this
   *         <code>SoftClass</code> instance (at the time of the call).
   */
  // trust ASM
  @SuppressWarnings("unchecked")
  public ClassNode generateNode() {    
    ClassNode n = new ClassNode();
    n.version = getVersion();
    n.access = getModifiers();
    
    n.name = super.getName();
    n.superName = superName;    
    n.outerClass = outerClass;
    n.outerMethod = outerMethod;
    n.outerMethodDesc = outerMethodDesc;
    n.signature = getGenericSignature();
    n.sourceDebug = sourceDebug;
    n.sourceFile = "<Generated SoftClass>";
    
    n.interfaces.addAll(interfaces);
    
    // copy through for now
    n.attrs = attrs;
    n.visibleAnnotations = visibleAnnotations;
    n.invisibleAnnotations = invisibleAnnotations;
    
    // attributeGenerationHelper(n)    
    
    nestedGenerationHelper(n);
    methodGenerationHelper(n);
    fieldGenerationHelper(n);
    
    return n;    
  }  
  
  /** 
   * Allows <code>SoftClass</code> instances to be generated to a custom
   * ASM visitor.
   * 
   * @param v The visitor implementation which should be called back for
   *        each attribute, member and instruction.
   */
  // TODO This needn't be using the node at all. Removing that would completely
  //      remove the need for nodes in many cases.
  public void accept(ClassVisitor v) {
    generateNode().accept(v);
  }
  
  /* used by the copyref adapters. Package private and not guaranteed for anything else. */
  String originalName() {
    return originalName;
  }
  
  String originalSuper() {
    return originalSuper;
  }
  
  public String toString() {
    return "(softclass) "+getName(); 
  }
}
