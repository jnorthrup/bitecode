package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * <h2>4.6    Methods</h2> <a name="1514"></a> Each method, including each instance initialization method <a href="Overview.doc.html#12174">(§3.9)</a> and the class or interface initialization method <a href="Overview.doc.html#12174">(§3.9)</a>, is described by a <code>MethodInfo</code> structure. No two methods in one <code>class</code> file may have the same name and descriptor <a href="ClassFile.doc.html#7035">(§4.3.3)</a>.  <p><a name="84874"></a> The structure has the following format:</p><p> </p><pre><br><a name="1515"></a>&nbsp;&nbsp;&nbsp;&nbsp;<code>MethodInfo { </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 access_flags; </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 nameIndex; </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 DescriptorIndex; </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 attributes_count; </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	AttributeHeader attributes[attributes_count]; </code><a name="1521"></a>&nbsp;&nbsp;&nbsp;&nbsp;<code>} </code><br></pre><a name="9412"></a> The items of the <code>MethodInfo</code> structure are as follows:  <p><a name="1522"></a> </p><dl><dt><code>access_flags</code> </dt><dd> The value of the <code>access_flags</code> item is a mask of flags used to denote access permission to and properties of this method. The interpretation of each flag, when set, is as shown in <a href="ClassFile.doc.html#75568">Table 4.5</a>. <a name="75568"></a><p><table border="1"> <tbody><tr><td><a name="75497"></a> <strong>Flag Name</strong> </td><td><a name="75499"></a> <strong>Value</strong>  </td><td><a name="75501"></a> <strong>Interpretation</strong>  </td></tr><tr><td><a name="75504"></a> <code>ACC_PUBLIC</code> </td><td><a name="75506"></a> <code>0x0001</code> </td><td><a name="75508"></a> Declared <code></code>; may be accessed from outside its package.  </td></tr><tr><td><a name="75511"></a> <code>ACC_PRIVATE</code>  </td><td><a name="75513"></a> <code>0x0002</code> </td><td><a name="75515"></a> Declared <code></code>; accessible only within the defining class.  </td></tr><tr><td><a name="75518"></a> <code>ACC_PROTECTED</code> </td><td><a name="75520"></a> <code>0x0004</code> </td><td><a name="75522"></a> Declared <code>protected</code>; may be accessed within subclasses.   </td></tr><tr><td><a name="75525"></a> <code>ACC_STATIC</code> </td><td><a name="75527"></a> <code>0x0008</code> </td><td><a name="75529"></a> Declared <code>static</code>.  </td></tr><tr><td><a name="75533"></a> <code>ACC_FINAL</code> </td><td><a name="75535"></a> <code>0x0010</code> </td><td><a name="75537"></a>  Declared <code>   </code>; may not be overridden.  </td></tr><tr><td><a name="75541"></a> <code>ACC_SYNCHRONIZED</code> </td><td><a name="75543"></a> <code>0x0020</code> </td><td><a name="75545"></a> Declared <code>synchronized</code>; invocation is wrapped in a monitor lock.  </td></tr><tr><td><a name="75549"></a> <code>ACC_NATIVE</code> </td><td><a name="75551"></a>  <code>0x0100</code> </td><td><a name="75553"></a> Declared <code>native</code>; implemented in a language other than Java.  </td></tr><tr><td><a name="75555"></a> <code>ACC_ABSTRACT</code> </td><td><a name="75557"></a> <code>0x0400</code> </td><td><a name="75559"></a> Declared <code>abstract</code>; no implementation is provided.  </td></tr><tr><td><a name="75561"></a>  <code>ACC_STRICT</code> </td><td><a name="75563"></a> <code>0x0800</code> </td><td><a name="75565"></a> Declared <code>strictfp</code>; floating-point mode is FP-strict  </td></tr></tbody></table><br><br> </p><p> Methods of classes may set any of the flags in <a href="ClassFile.doc.html#75568">Table 4.5</a>. However, a specific method of a class may have at most one of its <code>ACC_PRIVATE</code>, <code>ACC_PROTECTED</code>, and <code>ACC_PUBLIC </code>flags set <a href="Concepts.doc.html#18914">(§2.7.4)</a>. If such a method has its <code>ACC_ABSTRACT</code> flag set it may not have any of its <code>ACC_FINAL</code>, <code>ACC_NATIVE</code>, <code>ACC_PRIVATE</code>, <code>ACC_STATIC</code>, <code>ACC_STRICT</code>, or <code>ACC_SYNCHRONIZED</code> flags set <a href="Concepts.doc.html#20648">(§2.13.3.2)</a>.</p><p>  All interface methods must have their <code>ACC_ABSTRACT</code> and <code>ACC_PUBLIC</code> flags set and may not have any of the other flags in <a href="ClassFile.doc.html#75568">Table 4.5</a> set <a href="Concepts.doc.html#20648">(§2.13.3.2)</a>.</p><p>  A specific instance initialization method <a href="Overview.doc.html#12174">(§3.9)</a> may have at most one of its <code>ACC_PRIVATE</code>, <code>ACC_PROTECTED</code>, and <code>ACC_PUBLIC</code> flags set and may also have its <code>ACC_STRICT</code> flag set, but may not have any of the other flags in <a href="ClassFile.doc.html#75568">Table 4.5</a> set.</p><p>  Class and interface initialization methods <a href="Overview.doc.html#12174">(§3.9)</a> are called implicitly by the Java virtual machine; the value of their <code>access_flags</code> item is ignored except for the settings of the <code>ACC_STRICT </code>flag.</p><p>  All bits of the <code>access_flags</code> item not assigned in <a href="ClassFile.doc.html#75568">Table 4.5</a> are reserved for future use. They should be set to zero in generated <code>class</code> files and should be ignored by Java virtual machine implementations.</p><p>  <a name="1529"></a> </p></dd><dt><code>nameIndex</code> </dt><dd> The value of the <code>nameIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing either one of the special method names <a href="Overview.doc.html#12174">(§3.9)</a>, <code>&lt;init&gt;</code> or <code>&lt;clinit&gt;</code>, or a valid method name in the Java programming language <a href="Concepts.doc.html#21272">(§2.7)</a>, stored as a simple name <a href="Concepts.doc.html#21410">(§2.7.1)</a>.<p>  <a name="1531"></a> </p></dd><dt><code>DescriptorIndex</code> </dt><dd> The value of the <code>DescriptorIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing a valid method descriptor <a href="ClassFile.doc.html#7035">(§4.3.3)</a>.<p>  <a name="1533"></a> </p></dd><dt><code>attributes_count</code> </dt><dd> The value of the <code>attributes_count</code> item indicates the number of additional attributes <a href="ClassFile.doc.html#43817">(§4.7)</a> of this method.<p> <a name="1535"></a> </p></dd><dt><code>attributes[]</code> </dt><dd> Each value of the <code>attributes</code> table must be an attribute structure <a href="ClassFile.doc.html#43817">(§4.7)</a>. A method can have any number of optional attributes associated with it. <p>  The only attributes defined by this specification as appearing in the <code>attributes</code> table of a <code>MethodInfo</code> structure are the <code>Code</code> <a href="ClassFile.doc.html#1546">(§4.7.3)</a>, <code>Exceptions</code> <a href="ClassFile.doc.html#3129">(§4.7.4)</a>, <code>Synthetic</code> <a href="ClassFile.doc.html#80128">(§4.7.6)</a>, and <code>Deprecated</code> <a href="ClassFile.doc.html#78232">(§4.7.10)</a> attributes. </p><p>  A Java virtual machine implementation must recognize and correctly read <code>Code</code> <a href="ClassFile.doc.html#1546">(§4.7.3)</a> and <code>Exceptions</code> <a href="ClassFile.doc.html#3129">(§4.7.4)</a> attributes found in the <code>attributes</code> table of a <code>MethodInfo</code> structure. A Java virtual machine implementation is required to silently ignore any or all other attributes in the <code>attributes</code> table of a <code>MethodInfo</code> structure that it does not recognize. Attributes not defined in this specification are not allowed to affect the semantics of the <code>class</code> file, but only to provide additional descriptive information <a href="ClassFile.doc.html#16733">(§4.7.1)</a>.</p>  * @version $Id$
 *
 * @user jim
 * @created Mar 10, 2008 8:29:51 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
 public enum MethodInfo {
    /**
     * @see MethodAccessValue
     */
    AccessFlagsValue(2) {

        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            MethodAccessValue.subIndex(src, register, stack);
        }
    },

    /**
     * The value of the nameIndex item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure representing either one of the special method names (§3.9), <init> or <clinit>, or a valid method name in the Java programming language (§2.7), stored as a simple name (§2.7.1).
     */
    Utf8Index(2) {
 
    },

    /**
     */
    DescriptorIndex(2),

    /**
     */
    AttributeCount(2) {

        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            int count = (int) src.getShort() & 0xffff;
            stack.put(count);
            for (int i = 0; i < count; i++) {
                AttributeHeader.index(src, register, stack);
            }

        }},;
     public int size;public Class clazz;

    MethodInfo(  int size) {
        this.size = size;
    }

     static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (MethodInfo m : values()) {


            int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
//            FileSlotRecord.genericGetInt(buf, m.size );
            m.subIndex(src, register, stack);
        }


    }

    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        int i = src.position();
        src.position(i + size);
    }
}

