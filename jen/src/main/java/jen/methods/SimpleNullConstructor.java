/* SimpleNullConstructor.java - A very simple null constructor
 *
 * This file is part of an example, and is donated to the public domain.
 * You may freely do with this code as you wish, ENTIRELY AT YOUR OWN RISK.
 *
 * File version: $Revision: 1.4 $ $Date: 2005/10/19 14:43:35 $
 * Originated: 02-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.methods;

import java.util.ArrayList;
import java.util.List;

import jen.AbstractSoftMethod;
import jen.SoftClass;
import jen.SoftUtils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static java.util.Arrays.asList;

import static java.util.Collections.emptyList;

/**
 * Simple null-argument constructor implementation. This will generate a
 * no-argument constructor that passes a predefined (array of) literal
 * constant (i.e. primitives, Strings, and Class literals) to another
 * constructor on either <code>this</code> or <code>super</code>, chosen
 * based on supplied argument types.
 * <p/>
 * <strong>Implementation note</strong>: Where constructors do not accept 
 * the <code>modifiers</code>, this class defaults to <code>public</code>
 * access rather than package-private to better suit common requirements.
 * Package-private members may be generated by passing <code>0</code> 
 * (zero) to the appropriate modifier-enabled constructor.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.4 $ $Date: 2005/10/19 14:43:35 $
 */
public final class SimpleNullConstructor extends AbstractSoftMethod
{
  /* Object[] for dealing with varargs */
  private static Class<?>[] argTypesHelper(Object[] args) {
    if (args == null) return new Class[0];
    ArrayList<Class> c = new ArrayList<Class>(args.length);
    for (Object o : args) {
      c.add(o.getClass());
    }
    return c.toArray(new Class[args.length]);
  }
  
  private static List<Class<?>> emptyClassList = emptyList();
  
  private final List<Class<?>> argTypes;
  private final List<?> args;  
  private final boolean toSuper;
  
  /**
   * Generate a public null-argument constructor that simply calls a
   * null constructor on the <code>super</code> class. 
   *    
   * @param softClass The {@link SoftClass} this constructor is to be used
   *        with.
   */
  public SimpleNullConstructor(SoftClass softClass) {
    this(softClass,ACC_PUBLIC);    
  }
  
  /**
   * Generate a null-argument constructor that calls a
   * null constructor on the <code>super</code> class. The generated
   * constructor will have the specified access modifiers.
   *    
   * @param softClass The {@link SoftClass} this constructor is to be used
   *        with.
   *        
   * @param modifiers The access modifiers for this constructor.        
   */
  public SimpleNullConstructor(SoftClass softClass, int modifiers) {
    this(softClass,modifiers,true,(Object[])null);    
  }
  
  /**
   * Generate a public null-argument constructor that passes the specified 
   * literal constant argument(s) to the appropriate constructor on 
   * <code>this</code>. This class defaults to public access rather than
   * package-private to better suit common requirements.
   * <p/> 
   * For example, given {@link SoftClass} <code>sc</code> with the name
   * <code>MySoftClass</code>, the following code:
   * <p/>
   * <pre>   new SimpleNullConstructor(sc,"My arg");</pre>
   * <p/>
   * would result in generation of the equivalent to the following code:
   * <p/>
   * <pre>   public MySoftClass() {
   *      this("My arg");
   *    }</pre>
   *    
   * @param softClass The {@link SoftClass} this constructor is to be used
   *        with.
   *        
   * @param args The literal constant argument(s) that should be passed
   *        to the backing constructor. <strong>See the 
   *        {@link SimpleNullConstructor class description} for details on
   *        the rules pertaining to this parameter.</strong> 
   */
  public SimpleNullConstructor(SoftClass softClass, Object... args) {
    this(softClass, (args == null), args);      
  }
  
  /**
   * Generate a public null-argument constructor that passes the specified 
   * literal constant argument(s) to the appropriate constructor on 
   * <code>this</code> or <code>super</code>. This class defaults to public 
   * access rather than package-private to better suit common requirements. 
   *    
   * @param softClass The {@link SoftClass} this constructor is to be used
   *        with.
   *        
   * @param toSuper If <code>true</code>, the backing constructor will be 
   *        invoked on <code>super</code> rather than <code>this</code>.
   *                
   * @param args The literal constant argument(s) that should be passed
   *        to the backing constructor. <strong>See the 
   *        {@link SimpleNullConstructor class description} for details on
   *        the rules pertaining to this parameter.</strong> 
   */
  public SimpleNullConstructor(SoftClass softClass, boolean toSuper, Object... args) {
    this(softClass,ACC_PUBLIC,toSuper,args);    
  }
  
  /**
   * Generate a public null-argument constructor that passes the specified 
   * literal constant arguments to the appropriate constructor on 
   * <code>this</code> or <code>super</code>. The constructor is selected
   * based on the actual class(es) of the supplied array elements.
   *    
   * @param softClass The {@link SoftClass} this constructor is to be used
   *        with.
   *        
   * @param modifiers The access modifiers for this constructor.
   * 
   * @param toSuper If <code>true</code>, the backing constructor will be 
   *        invoked on <code>super</code> rather than <code>this</code>.        
   *        
   * @param args Arguments to pass to the constructor (varargs). 
   */
  public SimpleNullConstructor(SoftClass softClass, int modifiers, boolean toSuper, Object... args) {
    this(softClass,modifiers,toSuper,argTypesHelper(args),args);        
  }
  
  /**
   * Generate a public null-argument constructor that passes the specified 
   * literal constant arguments to the appropriate constructor on 
   * <code>this</code> or <code>super</code>. The constructor is selected
   * based on the supplied argument-type array, each element of which must
   * specify a class that is assignable from the corresponding argument
   * element.
   *    
   * @param softClass The {@link SoftClass} this constructor is to be used
   *        with.
   *        
   * @param modifiers The access modifiers for this constructor.        
   *        
   * @param argTypes Array of argument types for this method. 
   *
   * @param args Array of arguments for this method. 
   *        
   * @param toSuper If <code>true</code>, the backing constructor will be 
   *        invoked on <code>super</code> rather than <code>this</code>.        
   */
  /* WORKER */
  public SimpleNullConstructor(SoftClass softClass, int modifiers, boolean toSuper, Class<?>[] argTypes, Object[] args) {    
    super(softClass,modifiers,"<init>",null,emptyClassList,void.class,emptyClassList);
    
    if ((argTypes != null) && (args != null) && (args.length != argTypes.length)) {
      throw(new IllegalArgumentException("Constructor argument / types mismatch"));
    }
    
    this.toSuper = toSuper;    
    this.argTypes = argTypes != null ? asList(argTypes) : null;
    this.args = args != null ? asList(args) : null;
  }
  
  /* **********************************
   * THE MAIN THING
   */
  public void accept(ClassVisitor vis) {
    
    MethodVisitor code = vis.visitMethod(getModifiers(),getName(),getDescriptor(),getSignature(),
        SoftUtils.stringForTypeList(getThrowsTypes()).toArray(new String[getThrowsTypes().size()])); 
    
    code.visitCode();
    code.visitVarInsn(ALOAD,0);    
    
    // load our args from pool to stack. Deliberately only do 'argTypes' length!!
    if (argTypes != null) { 
      for (int i = 0; i < argTypes.size(); i++) {
        code.visitLdcInsn(args.get(i));        
      }
    }    
    
    // You must use INVOKESPECIAL with method name '<init>' for a ctor.  
    code.visitMethodInsn(INVOKESPECIAL,(toSuper ? getSoftClass().getSuperClassInternalName() : getSoftClass().getInternalName()),"<init>",
        (argTypes != null) ? 
            Type.getMethodDescriptor(Type.VOID_TYPE, SoftUtils.typeForClassList(argTypes).toArray(new Type[argTypes.size()])) :
            "()V");
    
    
    code.visitInsn(RETURN);
    code.visitMaxs(0,0);
    
    code.visitEnd();
  }    
}