/* NullConstructorVisitor.java - *very* simple ASM adapter to add null ctor
 *
 * This file is part of an example, and is donated to the public domain.
 * You may freely do with this code as you wish, ENTIRELY AT YOUR OWN RISK.
 *
 * File version: $Revision: 1.2 $ $Date: 2005/10/03 17:59:42 $
 * Originated: 02-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.util.List;

import jen.AbstractSoftMethod;
import jen.SoftClass;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static java.util.Collections.emptyList;

/*
 * this is a copy of the most basic behaviour from the members 'simplenullctor' class.
 * It's here to allow us basic testing without actually depending on members.  
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/03 17:59:42 $
 */
public final class ExampleNullConstructor extends AbstractSoftMethod
{
  private static final List<Type> EMPTY_TYPE_LIST = emptyList();
  
  private final Object arg;  
  private final boolean toSuper;
  
  public ExampleNullConstructor(SoftClass softClass, Object arg) {
    this(softClass, ACC_PUBLIC, arg, false);      
  }
  
  public ExampleNullConstructor(SoftClass softClass, int modifiers, Object arg, boolean toSuper) {
    super(softClass,modifiers,"<init>",null,EMPTY_TYPE_LIST,Type.VOID_TYPE,EMPTY_TYPE_LIST);
    this.toSuper = toSuper;
    this.arg = arg;
  }
  
  /* **********************************
   * THE MAIN THING
   */
  public void accept(ClassVisitor vis) {
    
    MethodVisitor code = vis.visitMethod(getModifiers(),getName(),getDescriptor(),getSignature(),
        SoftUtils.stringForTypeList(getThrowsTypes()).toArray(new String[getThrowsTypes().size()])); 
    
    code.visitCode();
    code.visitVarInsn(ALOAD,0);    
    
    // setup our arg as a stack constant
    if (arg != null) code.visitLdcInsn(arg);
    
    // You must use INVOKESPECIAL with method name '<init>' for a ctor.  
    code.visitMethodInsn(INVOKESPECIAL,(toSuper ? getSoftClass().getSuperClassInternalName() : getSoftClass().getInternalName()),"<init>",
        (arg != null) ? 
            Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] { Type.getType(arg.getClass()) }) :
            "()V");
    
    
    code.visitInsn(RETURN);
    code.visitMaxs(0,0);
    
    code.visitEnd();
  }    
}