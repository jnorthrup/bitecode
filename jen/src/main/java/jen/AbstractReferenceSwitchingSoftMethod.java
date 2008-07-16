/* AbstractReferenceSwitchingSoftMethod.java - Base reference switching impl
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
 * File version: $Revision: 1.6 $ $Date: 2005/10/29 19:29:12 $
 * Originated: 13-Sep-2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen;

import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/** 
 * An abstract base-class for {@link SoftMethod} implementations that take their
 * source from an existing method. Implementations based upon this class will
 * automatically rename references to properly point to the generated class.
 * <p/>
 * <strong>Note</strong> that while this implementation switches all references
 * to both <code>this</code> and <code>super</code>, it does <strong>not</strong>
 * check that the resulting reference is valid. You must ensure that when switching
 * superclass, for example, any explicit <code>super</code> calls can be satisfied
 * by the new superclass.
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.6 $ $Date: 2005/10/29 19:29:12 $ 
 */
public abstract class AbstractReferenceSwitchingSoftMethod extends AbstractSoftMethod
{
  /* **********************************
   * REFERENCE SWITCHING ADAPTER(S)
   * We have an outer class adapter that exists only to inject the ref switch
   * 
   */
  /*
   * The reference switching adapter. This is implemented in cooperation with the
   * internal implementation of {@link SoftClass}. Reference-switching implementations
   * may insert an instance of this class into the visitor chain to effect the switch.
   *
   * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
   * @version $Revision: 1.6 $ $Date: 2005/10/29 19:29:12 $
   */
  class RefSwitchAdapterHook extends ClassAdapter {
    
    // class names
    private final String oldName;
    private final String newName;    
    private final String oldSuper;
    private final String newSuper;
    
    // method name
    private final String newMethod;
    private final String oldMethod;
    
    private final int newAccess;
        
    /*
     * Create a new switching adapter that wraps the specified visitor, and switches 
     * (possibly implicit) <code>this</code> references to <code>oldName</code> for
     * references to <code>newName</code>, and <code>super</code> references to 
     * <code>oldSuper</code> to <code>newSuper</code>.
     *     
     * Names passed to this constructor are <strong>internal</strong> (i.e. binary)
     * names.
     * 
     * N.B: The new method name is optional, and only needs to be supplied if the
     *      method has been renamed. It allows recursive invocations to be switched.
     *  
     * @param cv The delegate {@link ClassVisitor}.
     * @param oldName The old name of <code>this</code>.
     * @param newName The new name for <code>this</code>.
     * @param oldSuper The old name of <code>super</code>.
     * @param newSuper The new name for <code>super</code>.
     * @param newMethodName The new name for method.
     * @param newAccess New access modifiers for the method (-1 for no change)
     */
    RefSwitchAdapterHook(final ClassVisitor cv, String oldName, String newName, 
                         String oldSuper, String newSuper, String oldMethod, String newMethod, int newAccess) {
      super(cv);
      this.oldName = oldName;
      this.oldSuper = oldSuper;
      this.newName = newName;
      this.newSuper = newSuper;
      this.oldMethod = oldMethod;
      this.newMethod = newMethod;
      this.newAccess = newAccess;
      
      if ((oldMethod != null && newMethod == null) || (newMethod != null && oldMethod == null)) {
        throw(new IllegalArgumentException("Switching adapter: Only one method name specified (specify both or none)"));
      }
    }
    
    RefSwitchAdapterHook(final ClassVisitor cv, String oldName, String newName, 
        String oldSuper, String newSuper, String oldMethod, String newMethod) {
      this(cv,oldName,newName,oldSuper,newSuper,oldMethod,newMethod,-1);
    }
    
    RefSwitchAdapterHook(final ClassVisitor cv, String oldName, String newName, 
        String oldSuper, String newSuper) {
      this(cv,oldName,newName,oldSuper,newSuper,null,null,-1);
    }
    
    // Hook all method visits
    public MethodVisitor visitMethod (int access, String name, String desc, String signature, String[] exceptions) {
      if (oldMethod != null && oldMethod.equals(name)) {
        name = newMethod;
        if (newAccess != -1) access = newAccess;
      }
      
      MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
      
      if ((mv != null) && ((getModifiers() & ACC_ABSTRACT) == 0)) {
        mv = this.new RefSwitchMethodAdapter(mv);
      }      
      return mv;      
    }
    
    class RefSwitchMethodAdapter extends MethodAdapter {
    
      RefSwitchMethodAdapter(final MethodVisitor mv) {
        super(mv);
      }
    
      public void visitTypeInsn (int i, String s) {
        if (oldName.equals(s)) {
          s = newName;
        }
        mv.visitTypeInsn(i, s);
      }
    
      /* all field references should be changed */
      public void visitFieldInsn (int opcode, String owner, String name, String desc) {
        if (oldName.equals(owner)) {
          mv.visitFieldInsn(opcode, newName, name, fixName(desc));
        } else {
          mv.visitFieldInsn(opcode, owner, name, fixName(desc));
        }
      }
    
      public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        mv.visitLocalVariable(name, fixName(desc), fixName(signature),start,end,index);
      }
    
      public void visitMethodInsn (int opcode, String owner, String name, String desc) {
        /* special treatment of super.<init> */
        
        /* NOTE: Behaviour changed at 0.15. Now ALL super references are switched.
         * Granted this could lead to broken classes, but since not switching ended
         * up with classes that failed verification (invokespecial incorrectly)
         * this is probably the best way...
         * 
         * NOTE: Changed again, 0.23, to cope with special case of Object (again).
         *       Basically it's now changing the class only (when the invoked method 
         *       ISN'T and Object method and the class was previously an Object 
         *       subclass) OR (if it's an INVOKESPECIAL).
         *       
         *       Hopefully this will now cover all the places where these calls 
         *       legitimately do need changing, while avoiding those where it
         *       doesnt (i.e. Object locals in your code, Object.getClass(), etc).
         */
        if (!(oldSuper.equals("java/lang/Object") && owner.equals("java/lang/Object")) || opcode == INVOKESPECIAL) {
          // We should change this, it doesn't look like a local (or unrelated) Object invocation
          if (oldSuper.equals(owner)) {
            // superclass, any method
            mv.visitMethodInsn(opcode, newSuper, name, fixSuper(desc));
          } else if (oldName.equals(owner)) {
            if (oldMethod != null && oldMethod.equals(name)) {
              // this class, this method
              mv.visitMethodInsn(opcode, newName, newMethod, fixName(desc));
            } else {
              // this class, other method
              mv.visitMethodInsn(opcode, newName, name, fixName(desc));
            }
          } else {        
            // other class
            mv.visitMethodInsn(opcode, owner, name, fixName(desc));
          }
        }
      }
      
      /* strip these, they'll be wrong anyway after mod */
      public void visitLineNumber(int num, Label label) { }
      
      // The replace('$','%') business is about the regexp classes taking
      // $ as a group specifier (on replaceAll call).
      private String fixName(String s) {
        if (s != null) {
          String name = oldName;
          if (s.indexOf(name) != -1) {
            s = s.replaceAll(name.replace('$','%'), newName.replace('$','%')).replace('%','$');
          }
        }
        return s;
      }
      private String fixSuper(String s) {
        if (s != null) {
          String name = oldSuper;
          if (s.indexOf(name) != -1) {
            s = s.replaceAll(name.replace('$','%'), newSuper.replace('$','%')).replace('%','$');
          }
        }
        return s;
      }    
    }
  }
  
  
  public AbstractReferenceSwitchingSoftMethod(SoftClass softClass) {
    super(softClass);
  }

  public AbstractReferenceSwitchingSoftMethod(SoftClass softClass, int modifiers, String name, String signature, List<Class<?>> argumentTypes, Class returnType, List<Class<?>> throwsTypes) {
    super(softClass, modifiers, name, signature, argumentTypes, returnType, throwsTypes);    
  }

  public AbstractReferenceSwitchingSoftMethod(SoftClass softClass, int modifiers, String name, String signature, List<Type> argumentTypes, Type returnType, List<Type> throwsTypes) {
    super(softClass, modifiers, name, signature, argumentTypes, returnType, throwsTypes);    
  }

  /* (non-Javadoc)
   * @see jen.AbstractSoftMember#accept(org.objectweb.asm.ClassVisitor)
   */
  public abstract void accept(ClassVisitor vis);

  /* 
   * Create a new adapter around the specified visitor that will switch
   * references appropriately for this {@link SoftMethod}. This method
   * is provided specifically for ASMSoftMethod. 
   */
  ClassVisitor switchingAdapter(ClassVisitor visitor) {
    SoftClass sc = getSoftClass();
    return this.new RefSwitchAdapterHook(visitor,sc.originalName(),
                                                 sc.getInternalName(),
                                                 sc.originalSuper(),
                                                 sc.getSuperClassInternalName());    
  }
  
  /**
   * Create a new adapter around the specified visitor that will switch
   * references appropriately for this {@link SoftMethod}, including
   * switching recursive invocations on the (renamed) method itself.
   * 
   * @see #switchingAdapter(ClassVisitor, String, String, String, String, String, int)
   */
  protected ClassVisitor switchingAdapter(ClassVisitor visitor, String oldName, String newName, String oldSuper, String newSuper) {
    return this.new RefSwitchAdapterHook(visitor,
                                         SoftUtils.javaToBinary(oldName),
                                         SoftUtils.javaToBinary(newName),    
                                         SoftUtils.javaToBinary(oldSuper),
                                         SoftUtils.javaToBinary(newSuper));    
  }
  
  /**
   * Create a new adapter around the specified visitor that will switch
   * references appropriately for this {@link SoftMethod}, including
   * switching recursive invocations on the (renamed) method itself.
   * 
   * @see #switchingAdapter(ClassVisitor, String, String, String, String, String, int)
   */
  protected ClassVisitor switchingAdapter(ClassVisitor visitor, String oldName, String newName, String oldSuper, String newSuper, String oldMethod, String newMethod) {
    return this.new RefSwitchAdapterHook(visitor,
                                         SoftUtils.javaToBinary(oldName),
                                         SoftUtils.javaToBinary(newName),    
                                         SoftUtils.javaToBinary(oldSuper),
                                         SoftUtils.javaToBinary(newSuper),
                                         oldMethod,
                                         newMethod);    
  }
  
  /**
   * Create a new adapter around the specified visitor that will switch
   * references appropriately for this {@link SoftMethod}. Subclasses 
   * should call this method from {@link SoftMember#accept(ClassVisitor)}
   * before passing the visitor on.
   * <p/>
   * You must specify both old and new names for both <code>this</code>
   * and the <code>super</code> class, which are used basically to 
   * 'find and replace' references in the generated bytecode. It is 
   * acceptable for either <code>old</code> to equal it's <code>new</code>,
   * though this has the potential to generate invalid classes if 
   * improperly used.
   * <p/>
   * The names specified are language names - they are converted internally
   * to binary names as required.
   * 
   * @param visitor The {@link ClassVisitor} that is to be adapted.
   * @param oldName The original class name 
   * @param newName The new class name
   * @param oldSuper The original superclass name
   * @param newSuper The new superclass name 
   * @param oldMethod The original method name
   * @param newMethod The new method name 
   * 
   * @return A new delegating visitor wrapping that supplied, with hooks to modify 
   *         references in generated method bodies.
   */
  protected ClassVisitor switchingAdapter(ClassVisitor visitor, String oldName, String newName, String oldSuper, String newSuper, String oldMethod, String newMethod, int newAccess) {
    return this.new RefSwitchAdapterHook(visitor,
                                         SoftUtils.javaToBinary(oldName),
                                         SoftUtils.javaToBinary(newName),    
                                         SoftUtils.javaToBinary(oldSuper),
                                         SoftUtils.javaToBinary(newSuper),
                                         oldMethod,
                                         newMethod,
                                         newAccess);    
  }
}
