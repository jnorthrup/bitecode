package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:13:13 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
 public enum FieldInfoHeader {
    /**
     * The value of the <code>access_flags</code> item is a mask of flags used to denote method_access_flags permission to and properties of this field. The interpretation of each flag, when set, is as shown in <a href="ClassFile.doc.html#88358">Table 4.4</a>.<p>
     * Fields of classes may set any of the flags in <a href="ClassFile.doc.html#88358">Table 4.4</a>. However, a specific field of a class may have at most one of its <code>ACC_PRIVATE</code>, <code>ACC_PROTECTED</code>, and <code>ACC_PUBLIC</code> flags set <a href="Concepts.doc.html#18914">(§2.7.4)</a> and may not have both its <code>ACC_FINAL</code> and <code>ACC_VOLATILE</code> flags set <a href="Concepts.doc.html#29882">(§2.9.1)</a>.
     * <p/>
     * </p></dd><dd><p><table border="1">
     * <tbody><tr><td><a name="88358"></a>
     * <strong>Flag Name</strong>
     * </td><td><a name="88360"></a>
     * <strong>Value</strong>
     * </td><td><a name="88362"></a>
     * <strong>Interpretation</strong>
     * <p/>
     * </td></tr><tr><td><a name="88365"></a>
     * <code>ACC_PUBLIC</code>
     * </td><td><a name="88367"></a>
     * <code>0x0001</code>
     * <p/>
     * </td><td><a name="88369"></a>
     * Declared <code></code>; may be accessed from outside its package.
     * <p/>
     * </td></tr><tr><td><a name="88373"></a>
     * <code>ACC_PRIVATE</code>
     * </td><td><a name="88375"></a>
     * <code>0x0002</code>
     * </td><td><a name="88377"></a>
     * Declared <code></code>; usable only within the defining class.
     * <p/>
     * </td></tr><tr><td><a name="88381"></a>
     * <code>ACC_PROTECTED</code>
     * <p/>
     * </td><td><a name="88383"></a>
     * <code>0x0004</code>
     * </td><td><a name="88385"></a>
     * Declared <code>protected</code>; may be accessed within subclasses.
     * <p/>
     * </td></tr><tr><td><a name="88389"></a>
     * <code>ACC_STATIC</code>
     * </td><td><a name="88391"></a>
     * <code>0x0008</code>
     * </td><td><a name="88393"></a>
     * Declared <code>static</code>.
     * <p/>
     * <p/>
     * </td></tr><tr><td><a name="88396"></a>
     * <code>ACC_FINAL</code>
     * </td><td><a name="88398"></a>
     * <code>0x0010</code>
     * </td><td><a name="88400"></a>
     * Declared <code>   </code>; no further assignment after initialization.
     * <p/>
     * </td></tr><tr><td><a name="88404"></a>
     * <code>ACC_VOLATILE</code>
     * </td><td><a name="88406"></a>
     * <code>0x0040</code>
     * </td><td><a name="88408"></a>
     * <p/>
     * Declared <code>volatile</code>; cannot be cached.
     * <p/>
     * </td></tr><tr><td><a name="88412"></a>
     * <code>ACC_TRANSIENT</code>
     * </td><td><a name="88414"></a>
     * <code>0x0080</code>
     * </td><td><a name="88416"></a>
     * Declared <code>transient</code>; not written or read by a persistent object manager.
     * <p/>
     * </td></tr></tbody></table><br><br></p><p>
     * Fields of classes may set any of the flags in <a href="ClassFile.doc.html#88358">Table 4.4</a>. However, a specific field of a class may have at most one of its <code>ACC_PRIVATE</code>, <code>ACC_PROTECTED</code>, and <code>ACC_PUBLIC</code> flags set <a href="Concepts.doc.html#18914">(§2.7.4)</a> and may not have both its <code>ACC_FINAL</code> and <code>ACC_VOLATILE</code> flags set <a href="Concepts.doc.html#29882">(§2.9.1)</a>.</p><p>
     * <p/>
     * All fields of interfaces must have their <code>ACC_PUBLIC</code>, <code>ACC_STATIC</code>, and <code>ACC_FINAL</code> flags set and may not have any of the other flags in <a href="ClassFile.doc.html#88358">Table 4.4</a> set <a href="Concepts.doc.html#18349">(§2.13.3.1)</a>. </p><p>
     * <p/>
     * All bits of the <code>access_flags</code> item not assigned in <a href="ClassFile.doc.html#88358">Table 4.4</a> are reserved for future use. They should be set to zero in generated <code>class</code> files and should be ignored by Java virtual machine implementations.</p>
     */
    AccessFlagsValue(2) {

    },
    /**
     * The value of the <code>nameIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure which must represent a valid field name <a href="Concepts.doc.html#21272">(§2.7)</a> stored as a simple name <a href="Concepts.doc.html#21410">(§2.7.1)</a>, that is, as a Java programming language identifier <a href="Concepts.doc.html#25339">(§2.2)</a>.
     */
    Utf8Index(2),
    /**
     * The value of the DescriptorIndex item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure that must represent a valid field descriptor (§4.3.2).
     */
    DescriptorIndex(2, Utf8_.class),
    /**
     * The value of the attributes_count item indicates the number of additional attributes (§4.7) of this method.
     */
    attributes_count(2) {


        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            short attr = src.getShort();
            for (int i = 0; i < attr; i++) {
                AttributeHeader.index(src, register, stack);
            }
        }

    };
     public int size;public Class clazz;

    FieldInfoHeader(  int size) {
        this.size = size;
    }

    FieldInfoHeader(int i, Class<Utf8_> utf8_class) {

    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (FieldInfoHeader m : values()) {


            int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
//            FileSlotRecord.genericGetInt(src, m.size );
            m.subIndex(src, register, stack);
        }


    }

    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {


        int i = src.position();
        src.position(i + size);
    }
}