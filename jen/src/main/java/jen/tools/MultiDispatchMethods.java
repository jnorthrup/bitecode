/* MultiDispatchMethods.java - methods for the multi dispatch proxies.
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
 * File version: $Revision: 1.2 $ $Date: 2005/10/30 23:47:47 $
 * Originated: Oct 29, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import jen.SoftClass;
import jen.SoftMethod;

import static java.util.Collections.emptyList;

import static jen.SoftClass.*;
import static jen.SoftUtils.typeForClassList;

import static jen.tools.Toolbox.CLASS_TYPE;
import static jen.tools.Toolbox.STRING_TYPE;
import static jen.tools.Toolbox.MAP_TYPE;
import static jen.tools.Toolbox.primitiveWrapperType;
import static jen.tools.Toolbox.primitiveTypeIdent;

/* 
 * Package private class that provides methods for the multi dispatch proxies. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.2 $ $Date: 2005/10/30 23:47:47 $ 
 */
final class MultiDispatchMethods
{
  // not copied - just does lazy loading of softclass for this class.
  private static SoftClass mySc;
  private static final Object myScLock = new Object();
  
  static SoftClass getSoftClass() {
    synchronized(myScLock) {
      if (mySc == null) {
        try {
          mySc = new SoftClass(MultiDispatchMethods.class);
        } catch (RuntimeException e) {
          throw(new RuntimeException("Unable to lazy-load dispatch method template class",e));
        }
      }
      
      return mySc;
    }
  }
  
  // not copied, but needed for this to compile
  private HashMap<String,ArrayList<Class[]>> __method_map;
  
  static SoftMethod generateScoreClass(final SoftClass sc) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[] { Class.class, Class.class }));
      }      

      public String getDescriptor() {
        return "(Ljava/lang/Class;Ljava/lang/Class;)I"; 
      }

      public Type getReturnType() {
        return Type.INT_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC, "__score_class", "(Ljava/lang/Class;Ljava/lang/Class;)I", "(Ljava/lang/Class<*>;Ljava/lang/Class<*>;)I", null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(100, l0);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(101, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isPrimitive", "()Z");
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(102, l3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, sc.getInternalName(), "__primitive_wrapper", "(Ljava/lang/Class;)Ljava/lang/Class;");
        mv.visitVarInsn(ASTORE, 0);
        mv.visitLabel(l2);
        mv.visitLineNumber(103, l2);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
        mv.visitVarInsn(ALOAD, 0);
        Label l4 = new Label();
        mv.visitJumpInsn(IF_ACMPNE, l4);
        mv.visitInsn(ICONST_1);
        Label l5 = new Label();
        mv.visitJumpInsn(GOTO, l5);
        mv.visitLabel(l4);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l5);
        mv.visitVarInsn(ISTORE, 3);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(104, l6);
        mv.visitVarInsn(ILOAD, 3);
        Label l7 = new Label();
        mv.visitJumpInsn(IFEQ, l7);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(105, l8);
        Label l9 = new Label();
        mv.visitJumpInsn(GOTO, l9);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitLineNumber(106, l10);
        mv.visitIincInsn(2, 2);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitLineNumber(107, l11);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;");
        mv.visitVarInsn(ASTORE, 1);
        mv.visitLabel(l9);
        mv.visitLineNumber(108, l9);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
        mv.visitJumpInsn(IF_ACMPNE, l10);
        Label l12 = new Label();
        mv.visitLabel(l12);
        mv.visitLineNumber(109, l12);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l7);
        mv.visitLineNumber(110, l7);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        Label l13 = new Label();
        mv.visitJumpInsn(IF_ACMPNE, l13);
        Label l14 = new Label();
        mv.visitLabel(l14);
        mv.visitLineNumber(111, l14);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l13);
        mv.visitLineNumber(112, l13);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;");
        mv.visitVarInsn(ASTORE, 7);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 5);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitVarInsn(ISTORE, 6);
        Label l15 = new Label();
        mv.visitJumpInsn(GOTO, l15);
        Label l16 = new Label();
        mv.visitLabel(l16);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 4);
        Label l17 = new Label();
        mv.visitLabel(l17);
        mv.visitLineNumber(113, l17);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 4);
        Label l18 = new Label();
        mv.visitJumpInsn(IF_ACMPEQ, l18);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z");
        Label l19 = new Label();
        mv.visitJumpInsn(IFEQ, l19);
        mv.visitLabel(l18);
        mv.visitLineNumber(114, l18);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l19);
        mv.visitLineNumber(115, l19);
        mv.visitIincInsn(5, 1);
        mv.visitLabel(l15);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitJumpInsn(IF_ICMPLT, l16);
        Label l20 = new Label();
        mv.visitLabel(l20);
        mv.visitLineNumber(116, l20);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
        Label l21 = new Label();
        mv.visitJumpInsn(IF_ACMPNE, l21);
        Label l22 = new Label();
        mv.visitLabel(l22);
        mv.visitLineNumber(117, l22);
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Incompatible classes!");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitLabel(l21);
        mv.visitLineNumber(118, l21);
        mv.visitIincInsn(2, 1);
        Label l23 = new Label();
        mv.visitLabel(l23);
        mv.visitLineNumber(119, l23);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;");
        mv.visitVarInsn(ASTORE, 1);
        Label l24 = new Label();
        mv.visitLabel(l24);
        mv.visitLineNumber(120, l24);
        mv.visitJumpInsn(GOTO, l7);
        Label l25 = new Label();
        mv.visitLabel(l25);
        mv.visitLocalVariable("declared", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l0, l25, 0);
        mv.visitLocalVariable("actual", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l0, l25, 1);
        mv.visitLocalVariable("score", "I", null, l1, l25, 2);
        mv.visitLocalVariable("declIsObject", "Z", null, l6, l25, 3);
        mv.visitLocalVariable("iface", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l17, l20, 4);
        mv.visitMaxs(3, 8);
        mv.visitEnd();
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC;
      }

      public String getName() {
        return "__score_class";
      }

      public String getSignature() {
        return null;
      }

      public SoftClass getSoftClass() {
        return sc;
      }      
    };
  }
  
  static SoftMethod generateMethodString(final SoftClass sc) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[] { Method.class }));
      }

      public String getDescriptor() {
        return "Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/String;";
      }

      public Type getReturnType() {
        return STRING_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC, "__method_string", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/String;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(400, l0);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ASTORE, 2);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(401, l1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ASTORE, 6);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitVarInsn(ISTORE, 5);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 3);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(402, l4);
        mv.visitVarInsn(ALOAD, 2);
        Label l5 = new Label();
        mv.visitJumpInsn(IFNONNULL, l5);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(403, l6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ASTORE, 2);
        Label l7 = new Label();
        mv.visitJumpInsn(GOTO, l7);
        mv.visitLabel(l5);
        mv.visitLineNumber(404, l5);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ASTORE, 2);
        mv.visitLabel(l7);
        mv.visitLineNumber(405, l7);
        mv.visitIincInsn(4, 1);
        mv.visitLabel(l2);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitJumpInsn(IF_ICMPLT, l3);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(406, l8);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitInsn(ARETURN);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l9, 0);
        mv.visitLocalVariable("pt", "[Ljava/lang/Class;", null, l0, l9, 1);
        mv.visitLocalVariable("b", "Ljava/lang/StringBuilder;", null, l1, l9, 2);
        mv.visitLocalVariable("c", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l4, l8, 3);
        mv.visitMaxs(2, 7);
        mv.visitEnd();
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC;
      }

      public String getName() {
        return "__method_string";
      }

      public String getSignature() {
        return null;
      }

      public SoftClass getSoftClass() {
        return sc;
      }
    };
  }
  
  static SoftMethod generateScoreMethod(final SoftClass sc) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[] { Method.class, Object[].class }));
      }

      public String getDescriptor() {
        return "([Ljava/lang/Class;[Ljava/lang/Object;)I";
      }

      public Type getReturnType() {
        return Type.INT_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC, "__score_method", "([Ljava/lang/Class;[Ljava/lang/Object;)I", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(500, l0);
        mv.visitVarInsn(ALOAD, 1);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNULL, l1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARRAYLENGTH);
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l2);
        mv.visitLabel(l1);
        mv.visitLineNumber(501, l1);
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Incompatible classes!");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitLabel(l2);
        mv.visitLineNumber(502, l2);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(503, l3);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 3);
        Label l4 = new Label();
        mv.visitLabel(l4);
        Label l5 = new Label();
        mv.visitJumpInsn(GOTO, l5);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(504, l6);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 4);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitLineNumber(505, l7);
        mv.visitVarInsn(ALOAD, 4);
        Label l8 = new Label();
        mv.visitJumpInsn(IFNONNULL, l8);
        Label l9 = new Label();
        mv.visitJumpInsn(GOTO, l9);
        mv.visitLabel(l8);
        mv.visitLineNumber(506, l8);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        mv.visitMethodInsn(INVOKESTATIC, sc.getType().getInternalName(), "__score_class", "(Ljava/lang/Class;Ljava/lang/Class;)I");
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 2);
        mv.visitLabel(l9);
        mv.visitLineNumber(507, l9);
        mv.visitIincInsn(3, 1);
        mv.visitLabel(l5);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l6);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitLineNumber(508, l10);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IRETURN);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitLocalVariable("dec", "[Ljava/lang/Class;", null, l0, l11, 0);
        mv.visitLocalVariable("args", "[Ljava/lang/Object;", null, l0, l11, 1);
        mv.visitLocalVariable("score", "I", null, l3, l11, 2);
        mv.visitLocalVariable("i", "I", null, l4, l10, 3);
        mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l7, l9, 4);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC;
      }

      public String getName() {
        return "__score_method";
      }

      public String getSignature() {
        return null;
      }

      public SoftClass getSoftClass() {
        return sc;
      }
    };
  }
  
  static SoftMethod generateFastCompat(final SoftClass sc) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[] { Class[].class, Object[].class }));
      }

      public String getDescriptor() {
        return "([Ljava/lang/Class;[Ljava/lang/Object;)Z";
      }

      public Type getReturnType() {
        return Type.BOOLEAN_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC + ACC_FINAL + ACC_STATIC, "__fast_compat", "([Ljava/lang/Class;[Ljava/lang/Object;)Z", "([Ljava/lang/Class<*>;[Ljava/lang/Object;)Z", null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(200, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l1);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(201, l2);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l1);
        mv.visitLineNumber(202, l1);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);
        Label l3 = new Label();
        mv.visitLabel(l3);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitLineNumber(203, l5);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 3);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(204, l6);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(AALOAD);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
        Label l7 = new Label();
        mv.visitJumpInsn(IF_ACMPEQ, l7);
        mv.visitVarInsn(ALOAD, 3);
        Label l8 = new Label();
        mv.visitJumpInsn(IFNONNULL, l8);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLineNumber(205, l9);
        mv.visitJumpInsn(GOTO, l7);
        mv.visitLabel(l8);
        mv.visitLineNumber(206, l8);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 4);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitLineNumber(207, l10);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isPrimitive", "()Z");
        Label l11 = new Label();
        mv.visitJumpInsn(IFEQ, l11);
        Label l12 = new Label();
        mv.visitLabel(l12);
        mv.visitLineNumber(208, l12);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKESTATIC, sc.getInternalName(), "__primitive_wrapper", "(Ljava/lang/Class;)Ljava/lang/Class;");
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z");
        mv.visitJumpInsn(IFNE, l7);
        Label l13 = new Label();
        mv.visitLabel(l13);
        mv.visitLineNumber(209, l13);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitJumpInsn(GOTO, l7);
        mv.visitLabel(l11);
        mv.visitLineNumber(210, l11);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z");
        mv.visitJumpInsn(IFNE, l7);
        Label l14 = new Label();
        mv.visitLabel(l14);
        mv.visitLineNumber(211, l14);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l7);
        mv.visitLineNumber(212, l7);
        mv.visitIincInsn(2, 1);
        mv.visitLabel(l4);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l5);
        Label l15 = new Label();
        mv.visitLabel(l15);
        mv.visitLineNumber(213, l15);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);
        Label l16 = new Label();
        mv.visitLabel(l16);
        mv.visitLocalVariable("clazz", "[Ljava/lang/Class;", null, l0, l16, 0);
        mv.visitLocalVariable("args", "[Ljava/lang/Object;", null, l0, l16, 1);
        mv.visitLocalVariable("i", "I", null, l3, l15, 2);
        mv.visitLocalVariable("o", "Ljava/lang/Object;", null, l6, l7, 3);
        mv.visitLocalVariable("c", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l10, l7, 4);
        mv.visitMaxs(2, 5);
        mv.visitEnd();
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_FINAL + ACC_SYNTHETIC + ACC_STATIC;
      }

      public String getName() {
        return "__fast_compat";
      }

      public String getSignature() {
        return "([Ljava/lang/Class<*>;[Ljava/lang/Object;)Z";
      }

      public SoftClass getSoftClass() {
        return sc;
      }
    };
  }
  
  static SoftMethod generateMethodMapHelper(final SoftClass sc, final Map<String,List<Method>> methodMap) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[0]));
      }

      public String getDescriptor() {
        return "()Ljava/util/Map;";
      }

      public Type getReturnType() {
        return MAP_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        // MethodVisitor v = cw.visitMethod(ACC_PRIVATE + ACC_FINAL, "__method_map_helper", "()Ljava/util/HashMap;", "()Ljava/util/HashMap<Ljava/lang/String;Ljava/util/ArrayList<[Ljava/lang/Class;>;>;", null);        
        // more Convenient 
        GeneratorAdapter mv = new GeneratorAdapter(ACC_PRIVATE + ACC_SYNTHETIC + ACC_FINAL, org.objectweb.asm.commons.Method.getMethod("java.util.HashMap __method_map_helper()"),"()Ljava/util/Map<Ljava/lang/String;Ljava/util/List<[Ljava/lang/Class;>;>;",null,cw);
        
        // Marks the 'list' local scope later on
        Label l1 = new Label();
        
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(1, l0);
        mv.visitTypeInsn(NEW, "java/util/HashMap");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);
        
        // Mark start of scope for List
        mv.visitLabel(l1);
        mv.visitLineNumber(2, l1);
        
        int nameNum = 0;
        
        // For each base method name (group of overloads)
        for (String methodName : methodMap.keySet()) {

          // Make an arraylist for these methods, add an array list to the method map 
          // for methods with that name if not already, and generate instructions to create the
          // arg array and add it to the list.
          
          //just marks us in here for debugging
          Label l01 = new Label();
          mv.visitLabel(l01);
          mv.visitLineNumber(20 + nameNum, l01);
          mv.visitTypeInsn(NEW, "java/util/ArrayList");
          mv.visitInsn(DUP);
          mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
          mv.visitVarInsn(ASTORE, 2);
          Label l2 = new Label();
          mv.visitLabel(l2);
          mv.visitLineNumber(20 + nameNum, l2);
          mv.visitVarInsn(ALOAD, 1);
          mv.visitLdcInsn(methodName);
          mv.visitVarInsn(ALOAD, 2);
          mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
          mv.visitInsn(POP);
          
          // For each method with that name, add it's argument type array to the
          int methNum = 0;
          
          for (Method m : methodMap.get(methodName)) {
            Class[] pt = m.getParameterTypes();
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(200 + methNum, l3);
            
            // load the arraylist
            mv.visitVarInsn(ALOAD, 2);
            
            // new array of the right size
            mv.push(pt.length);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            
            for (int paramNum = 0; paramNum < pt.length; paramNum++) {
              mv.visitInsn(DUP);
              mv.push(paramNum);
              
              // If this is a primitive type, we must use the static TYPE field
              // from it's appropriate wrapper class.
              if (pt[paramNum].isPrimitive()) {
                // prim type, must get the field
                Type wrapper = primitiveWrapperType(primitiveTypeIdent(Type.getType(pt[paramNum])));
                mv.getStatic(wrapper,"TYPE",CLASS_TYPE);
              } else {                
                // ref type, just use a class literal
                mv.visitLdcInsn(Type.getType(pt[paramNum]));                              
              }
              mv.visitInsn(AASTORE);
            }
            
            // Add the array
            // mark for debugging
            Label l03 = new Label();
            mv.visitLabel(l03);
            mv.visitLineNumber(201 + methNum, l3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
            mv.visitInsn(POP);
            
            methNum += 1;
          }
          
          nameNum += 1;
        }
        
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(21 + nameNum, l4);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        Label l5 = new Label();
        mv.visitLabel(l5);
        
        // Not all locals are marked - no list
        mv.visitLocalVariable("this", getSoftClass().getType().getDescriptor(), null, l0, l5, 0);
        mv.visitLocalVariable("map", "Ljava/util/HashMap;", "Ljava/util/Map<Ljava/lang/String;Ljava/util/List<[Ljava/lang/Class;>;>;", l1, l5, 1);
        mv.visitLocalVariable("list", "Ljava/util/ArrayList;", "Ljava/util/ArrayList<[Ljava/lang/Class;>;", l1, l4, 2);
        
        // need calculate maxs
        mv.visitMaxs(0, 0);
        mv.visitEnd();                
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_SYNTHETIC + ACC_FINAL;
      }

      public String getName() {
        return "__method_map_helper";
      }

      public String getSignature() {
        return "()Ljava/util/Map<Ljava/lang/String;Ljava/util/List<[Ljava/lang/Class;>;>;";
      }

      public SoftClass getSoftClass() {
        return sc;
      }
    };
  }
  
  static SoftMethod generateFindMatchingMethod(final SoftClass sc) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[] { String.class, Object[].class }));
      }

      public String getDescriptor() {
        return "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;";
      }

      public Type getReturnType() {
        return STRING_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC + ACC_FINAL, "__find_matching_method", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(300, l0);
        mv.visitInsn(ACONST_NULL);
        mv.visitTypeInsn(CHECKCAST, "[Ljava/lang/Class;");
        mv.visitVarInsn(ASTORE, 3);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLineNumber(301, l1);
        mv.visitLdcInsn(new Integer(2147483647));
        mv.visitVarInsn(ISTORE, 4);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLineNumber(302, l2);
        mv.visitVarInsn(ALOAD, 0);
        
        mv.visitFieldInsn(GETFIELD, sc.getType().getInternalName(), "__method_map", "Ljava/util/HashMap;");
        
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "java/util/List");
        mv.visitVarInsn(ASTORE, 5);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLineNumber(303, l3);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;");
        mv.visitVarInsn(ASTORE, 7);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, "[Ljava/lang/Class;");
        mv.visitVarInsn(ASTORE, 6);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitLineNumber(304, l6);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, sc.getType().getInternalName(), "__fast_compat", "([Ljava/lang/Class;[Ljava/lang/Object;)Z");
        mv.visitJumpInsn(IFEQ, l4);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitLineNumber(305, l7);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, sc.getType().getInternalName(), "__score_method", "([Ljava/lang/Class;[Ljava/lang/Object;)I");
        mv.visitVarInsn(ISTORE, 8);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(306, l8);
        mv.visitVarInsn(ILOAD, 8);
        Label l9 = new Label();
        mv.visitJumpInsn(IFNE, l9);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKESTATIC, sc.getType().getInternalName(), "__method_string", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/String;");
        mv.visitInsn(ARETURN);
        mv.visitLabel(l9);
        mv.visitLineNumber(307, l9);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IF_ICMPGE, l4);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitLineNumber(308, l10);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitVarInsn(ASTORE, 3);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitLineNumber(309, l11);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitLabel(l4);
        mv.visitLineNumber(310, l4);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
        mv.visitJumpInsn(IFNE, l5);
        Label l12 = new Label();
        mv.visitLabel(l12);
        mv.visitLineNumber(311, l12);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKESTATIC, sc.getType().getInternalName(), "__method_string", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/String;");
        mv.visitInsn(ARETURN);
        Label l13 = new Label();
        mv.visitLabel(l13);
        
        mv.visitLocalVariable("this", sc.getType().getDescriptor(), null, l0, l13, 0);
        mv.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l13, 1);
        mv.visitLocalVariable("args", "[Ljava/lang/Object;", null, l0, l13, 2);
        mv.visitLocalVariable("best", "[Ljava/lang/Class;", null, l1, l13, 3);
        mv.visitLocalVariable("bestScore", "I", null, l2, l13, 4);
        mv.visitLocalVariable("methods", "Ljava/util/ArrayList;", "Ljava/util/ArrayList<[Ljava/lang/Class;>;", l3, l13, 5);
        mv.visitLocalVariable("argTypes", "[Ljava/lang/Class;", null, l6, l12, 6);
        mv.visitLocalVariable("i", "I", null, l8, l4, 8);
        mv.visitMaxs(2, 9);
        mv.visitEnd();
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_SYNTHETIC + ACC_FINAL;
      }

      public String getName() {
        return "__find_matching_method";
      }

      public String getSignature() {
        return null;
      }

      public SoftClass getSoftClass() {
        return sc;
      }
    };
  }
  
  static SoftMethod generatePrimitiveWrapper(final SoftClass sc) {
    return new SoftMethod() {

      public List<Type> getArgumentTypes() {
        return typeForClassList(Arrays.asList(new Class<?>[] { Class.class }));
      }

      public String getDescriptor() {
        return "(Ljava/lang/Class;)Ljava/lang/Class;";
      }

      public Type getReturnType() {
        return CLASS_TYPE;
      }

      public List<Type> getThrowsTypes() {
        return emptyList();
      }

      public void accept(ClassVisitor cw) {
        MethodVisitor mv; 
        mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC + ACC_FINAL + ACC_STATIC, "__primitive_wrapper", "(Ljava/lang/Class;)Ljava/lang/Class;", "(Ljava/lang/Class<*>;)Ljava/lang/Class<*>;", null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(1000, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C");
        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();
        Label l4 = new Label();
        Label l5 = new Label();
        Label l6 = new Label();
        Label l7 = new Label();
        Label l8 = new Label();
        Label l9 = new Label();
        mv.visitLookupSwitchInsn(l9, new int[] { 98, 99, 100, 102, 105, 108, 115, 118 }, new Label[] { l1, l2, l3, l4, l5, l6, l7, l8 });
        mv.visitLabel(l1);
        mv.visitLineNumber(1001, l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C");
        Label l10 = new Label();
        Label l11 = new Label();
        mv.visitLookupSwitchInsn(l2, new int[] { 111, 121 }, new Label[] { l10, l11 });
        mv.visitLabel(l10);
        mv.visitLineNumber(1002, l10);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Boolean;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l11);
        mv.visitLineNumber(1003, l11);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Byte;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l2);
        mv.visitLineNumber(1004, l2);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Character;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l3);
        mv.visitLineNumber(1005, l3);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Double;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l4);
        mv.visitLineNumber(1006, l4);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Float;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l5);
        mv.visitLineNumber(1007, l5);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Integer;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l6);
        mv.visitLineNumber(1008, l6);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Long;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l7);
        mv.visitLineNumber(1009, l7);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Short;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l8);
        mv.visitLineNumber(1010, l8);
        mv.visitLdcInsn(Type.getType("Ljava/lang/Void;"));
        mv.visitInsn(ARETURN);
        mv.visitLabel(l9);
        mv.visitLineNumber(1011, l9);
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("'");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
        mv.visitLdcInsn("' is not a recognised primitive type");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        Label l12 = new Label();
        mv.visitLabel(l12);
        mv.visitLocalVariable("c", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l0, l12, 0);
        mv.visitMaxs(5, 1);
        mv.visitEnd();        
      }

      public int getModifiers() {
        return ACC_PRIVATE + ACC_SYNTHETIC + ACC_FINAL;
      }

      public String getName() {
        return "__primitive_wrapper";
      }

      public String getSignature() {
        return "(Ljava/lang/Class<*>;)Ljava/lang/Class<*>;";
      }

      public SoftClass getSoftClass() {
        return sc;
      }
    };
  }  
  
  /* **********************************
   * ORIGINAL COPIED METHODS
   *
   * The following are the models for the above methods. Please keep 'em up to date.
   */
 
  /*
  private final HashMap<String,ArrayList<Class[]>> __method_map_helper() {
    HashMap<String,ArrayList<Class[]>> map = new HashMap<String,ArrayList<Class[]>>();

    ArrayList<Class[]> l = new ArrayList<Class[]>();
    map.put("methodName", l);

          
    l.add(new Class[] { Object.class, Object.class });

    // ... repeat for each non abstract, non NoDispatch method
    
    return map;
  }
 */
  
  // main method finder - used for invocation
  /*
  private final String __find_matching_method(String name, Object[] args) {
    Class[] best = null;
    int bestScore = Integer.MAX_VALUE;
    
    List<Class[]> methods = __method_map.get(name);
    
    for (Class[] argTypes : methods) {
      if (__fast_compat(argTypes,args)) {
        
        int i = __score_method(argTypes,args);
        
        // special case: 0 is exact match
        if (i == 0) return __method_string(name,argTypes);
        
        if (i < bestScore) {
          best = argTypes;
          bestScore = i;
        }
      }      
    }
    
    return __method_string(name,best);
  }
  */
  
  // unique string key for a method 
  /*
  private static final String __method_string(String name, Class[] pt) {
    StringBuilder b = new StringBuilder().append(name);
    
    for (Class<?> c : pt) {
      if (b == null) {
        b = b.append(c.getName());
      } else {
        b = b.append(c.getName());
      }     
    }
    
    return b.toString();
  }    
  
  private static final int __score_class(Class<?> declared, Class<?> actual) {
    int score = 0;
    
    if (declared.isPrimitive()) {      
      // special case primitives, treat as wrapper (actual arg will be so)
      declared = __primitive_wrapper(declared);
    }
    
    boolean declIsObject = Object.class == declared;
    
    if (declIsObject) {
      // shortcut with object params
      while (actual != Object.class) {
        score += 2;   // 1 for class, 1 for inter
        actual = actual.getSuperclass();
      }
      
      return score;
    }
    
    // If we're here, it's not an Object arg
    
    while (true) {
      if (declared == actual) {
        return score;         
      }
      
      for (Class<?> iface : actual.getInterfaces()) {       
        if (declared == iface || declared.isAssignableFrom(iface)) {
          return score + 1;        
        }       
      }
      
      if (actual == Object.class) {      
        throw(new IllegalArgumentException("Incompatible classes!"));      
      } else {
        score++;
        actual = actual.getSuperclass();             
      }
    }    
  }
  
  private static final int __score_method(Class[] dec, Object[] args) {    
    if (args == null || args.length != dec.length) {
      throw(new IllegalArgumentException("Incompatible classes!"));      
    }
    
    int score = 0;
    
    for (int i = 0; i < dec.length; i++) {
      Object o = args[i];
      if (o == null) continue;
      
      score += __score_class(dec[i],o.getClass());      
    }
    
    return score;    
  }
  
  
  private static final boolean __fast_compat(Class<?>[] clazz, Object[] args) {
    if (clazz.length != args.length) {
      return false;
    }
    
    for (int i = 0; i < args.length; i++) {
      Object o = args[i];
      if (clazz[i] == Object.class || o == null) {
        continue;
      }
      
      Class<?> c = clazz[i];      
      if (c.isPrimitive()) {  
        if (!__primitive_wrapper(c).isAssignableFrom(o.getClass())) {
          return false;
        }
      } else if (!c.isAssignableFrom(o.getClass())) {
        return false;
      }      
    }
    
    return true;
  }    
  
  public static final Class<?> __primitive_wrapper(Class<?> c) {
    switch (c.getName().charAt(0)) {
      case 'b' :
        switch (c.getName().charAt(1)) {
          case 'o':
            return Boolean.class;
          case 'y':
            return Byte.class;          
        }
      case 'c' : 
        return Character.class;
      case 'd' : 
        return Double.class;
      case 'f' : 
        return Float.class;
      case 'i' : 
        return Integer.class;
      case 'l' : 
        return Long.class;
      case 's' : 
        return Short.class;
      case 'v' :
        return Void.class;
      default  : throw(new IllegalArgumentException("'"+c+"' is not a recognised primitive type"));
    }    
  }  
  */
}
