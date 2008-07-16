/* SoftDebug.java - Useful debugging utilities
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
 * File version: $Revision: 1.1 $ $Date: 2005/10/10 02:52:37 $
 * Originated: Oct 10, 2005
 * Author: Ross Bamford (rosco<at>roscopeco.co.uk)
 */

package jen.tools;

import java.io.PrintStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/** 
 * Provides convenient static methods to generate pieces of code (perhaps equivalent
 * to one or two individual language statements) that are useful when debugging 
 * generated classes. All methods of this class that generate code can generate 
 * to any {@link org.objectweb.asm.MethodVisitor}. Each method may place constraints
 * on the stack state when the code it generates is entered, and make assertions 
 * about the stack state when the code it generates completes. None of these 
 * methods may generate a {@code RETURN} of any kind, and nor will they generate 
 * {@code JSR}, {@code JSW_W} or {@code RET} instructions. They may, however, 
 * generate {@code ATHROW}.
 * <p/>
 * This is a purely static class with no instances or shared state. 
 *
 * @author Ross Bamford (rosco&lt;at&gt;roscopeco.co.uk)
 * @version $Revision: 1.1 $ $Date: 2005/10/10 02:52:37 $
 */
public class SoftDebug
{
  static final Type SYSTEM_TYPE = Type.getType(System.class);  
  static final Type PRINTSTREAM_TYPE = Type.getType(PrintStream.class);
  
  static final String voidChar = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(char.class) });
  static final String voidCharArray = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(char[].class) });
  static final String voidDouble = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(double.class) });
  static final String voidFloat = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(float.class) });
  static final String voidInt = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(int.class) });
  static final String voidLong = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(long.class) });
  static final String voidBoolean = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(boolean.class) });
  static final String voidObject = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(Object.class) });
  static final String voidObjectArray = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(Object[].class) });
  static final String voidString = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(String.class) });
  static final String voidStringArray = Type.getMethodDescriptor(Type.VOID_TYPE,new Type[] { Type.getType(String[].class) });
  
  /* base implementation of the print tops */
  static void basePrintTop(MethodVisitor gen, String method, String desc) {    
    gen.visitInsn(DUP);
    gen.visitFieldInsn(GETSTATIC,SYSTEM_TYPE.getInternalName(),"out",PRINTSTREAM_TYPE.getDescriptor());
    gen.visitInsn(SWAP);
    gen.visitMethodInsn(INVOKEVIRTUAL,PRINTSTREAM_TYPE.getInternalName(),method,desc);    
  }
  
  /**
   * Generates instructions to pass the {@link Object} on top of the stack to 
   * {@link PrintStream#print(java.lang.Object)} on {@link System#out}.
   * At entry to the code block, the top stack element must contain a reference.
   * The stack is unchanged after the print completes.
   * 
   * @param gen The {@link MethodVisitor} to which the instructions should be generated.
   */
  public static void printTopObject(MethodVisitor gen) {
    basePrintTop(gen,"print",voidObject);    
  }
  
  /**
   * Generates instructions to pass the {@link Object} on top of the stack to 
   * {@link PrintStream#println(java.lang.Object)} on {@link System#out}.
   * At entry to the code block, the top stack element must contain an object
   * reference. The stack is unchanged after the print completes.
   * 
   * @param gen The {@link MethodVisitor} to which the instructions should be generated.
   */
  public static void printlnTopObject(MethodVisitor gen) {
    basePrintTop(gen,"println",voidObject);    
  }

  /**
   * Equivalent of {@link #printTopObject(MethodVisitor)} for {@code char[]}.
   */
  public static void printTopCharArray(MethodVisitor gen) {
    basePrintTop(gen,"print",voidCharArray);    
  }
  
  /**
   * Equivalent of {@link #printlnTopObject(MethodVisitor)} for {@code char[]}.
   */
  public static void printlnTopCharArray(MethodVisitor gen) {
    basePrintTop(gen,"println",voidCharArray);    
  }

  /**
   * Equivalent of {@link #printTopObject(MethodVisitor)} for {@code char}.
   */
  public static void printTopChar(MethodVisitor gen) {
    basePrintTop(gen,"print",voidChar);    
  }
  
  /**
   * Equivalent of {@link #printlnTopObject(MethodVisitor)} for {@code char}.
   */
  public static void printlnTopChar(MethodVisitor gen) {
    basePrintTop(gen,"println",voidChar);    
  }

  /**
   * Equivalent to {@link #printTopObject(MethodVisitor)} for {@code double}.
   */
  public static void printTopDouble(MethodVisitor gen) {
    basePrintTop(gen,"print",voidDouble);    
  }
  
  /**
   * Equivalent to {@link #printlnTopObject(MethodVisitor)} for {@code double}.
   */
  public static void printlnTopDouble(MethodVisitor gen) {
    basePrintTop(gen,"println",voidDouble);    
  }

  /**
   * Equivalent to {@link #printTopObject(MethodVisitor)} for {@code float}.
   */
  public static void printTopFloat(MethodVisitor gen) {
    basePrintTop(gen,"print",voidFloat);    
  }
  
  /**
   * Equivalent to {@link #printlnTopObject(MethodVisitor)} for {@code float}.
   */
  public static void printlnTopFloat(MethodVisitor gen) {
    basePrintTop(gen,"println",voidFloat);    
  }

  /**
   * Equivalent to {@link #printTopObject(MethodVisitor)} for {@code int}.
   */
  public static void printTopInt(MethodVisitor gen) {
    basePrintTop(gen,"print",voidInt);    
  }
  
  /**
   * Equivalent to {@link #printlnTopObject(MethodVisitor)} for {@code int}.
   */
  public static void printlnTopInt(MethodVisitor gen) {
    basePrintTop(gen,"println",voidInt);    
  }
  
  /**
   * Equivalent to {@link #printTopObject(MethodVisitor)} for {@code long}.
   */
  public static void printTopLong(MethodVisitor gen) {
    basePrintTop(gen,"print",voidLong);    
  }
  
  /**
   * Equivalent to {@link #printlnTopObject(MethodVisitor)} for {@code long}.
   */
  public static void printlnTopLong(MethodVisitor gen) {
    basePrintTop(gen,"println",voidLong);    
  }

  /**
   * Equivalent to {@link #printTopObject(MethodVisitor)} for {@code boolean}.
   */
  public static void printTopBoolean(MethodVisitor gen) {
    basePrintTop(gen,"print",voidBoolean);    
  }
  
  /**
   * Equivalent to {@link #printlnTopObject(MethodVisitor)} for {@code boolean}.
   */
  public static void printlnTopBoolean(MethodVisitor gen) {
    basePrintTop(gen,"println",voidBoolean);    
  }

  /**
   * Equivalent to {@link #printTopObject(MethodVisitor)} for {@link String}
   */
  public static void printTopString(MethodVisitor gen) {
    basePrintTop(gen,"print",voidString);
  }
  
  /**
   * Equivalent to {@link #printlnTopObject(MethodVisitor)} for {@link String}
   */
  public static void printlnTopString(MethodVisitor gen) {
    basePrintTop(gen,"println",voidString);    
  }
  
  /* base impl of printString */
  static void basePrintString(MethodVisitor gen, String method, String desc, String s) {    
    gen.visitFieldInsn(GETSTATIC,SYSTEM_TYPE.getInternalName(),"out",PRINTSTREAM_TYPE.getDescriptor());
    gen.visitLdcInsn(s);
    gen.visitMethodInsn(INVOKEVIRTUAL,PRINTSTREAM_TYPE.getInternalName(),method,desc);    
  }
  
  /**
   * Generates instructions to print the specified constant string with
   * {@link PrintStream#print(java.lang.String)} on {@link System#out}.
   * The stack is left unchanged after the print.
   * 
   * @param gen The {@link MethodVisitor} the instructions should be generated to.
   * @param str The constant {@link String} to output.
   */
  public static void printString(MethodVisitor gen, String str) {
    basePrintString(gen,"print",voidString,str);
  }
  
  /**
   * Generates instructions to print the specified constant string with
   * {@link PrintStream#println(java.lang.String)} on {@link System#out}.
   * The stack is left unchanged after the print.
   * 
   * @param gen The {@link MethodVisitor} the instructions should be generated to.
   * @param str The constant {@link String} to output.
   */
  public static void printlnString(MethodVisitor gen, String str) {
    basePrintString(gen,"println",voidString,str);
  }
}
