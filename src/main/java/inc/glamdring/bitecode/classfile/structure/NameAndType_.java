package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * The  {@link NameAndType_}  structure is used to represent a field or
 * method, without indicating which class or interface type it belongs to:
 * <p/>
 * <p></p><pre><br><a name="1328"></a>&nbsp;&nbsp;&nbsp;&nbsp;<code>NameAndType_ {
 * </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u1 tag;
 * </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 nameIndex;
 * </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 DescriptorIndex;
 * </code><a name="1332"></a>&nbsp;&nbsp;&nbsp;&nbsp;<code>}
 * </code><br></pre><a name="9390"></a>
 * The items of the  {@link NameAndType_}  structure are as follows:
 * <p><a name="1334"></a>
 * </p><dl><dt><code>tag</code>
 * </dt><dd> The <code>tag</code> item of the  {@link NameAndType_}  structure has the value <code>CONSTANT_NameAndType</code> (<code>12</code>).<p>
 * <p/>
 * <a name="1336"></a>
 * </p></dd><dt><code>nameIndex</code>
 * </dt><dd> The value of the <code>nameIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing either a valid field or method name <a href="Concepts.doc.html#21272">(§2.7)</a> stored as a simple name <a href="Concepts.doc.html#21410">(§2.7.1)</a>, that is, as a Java programming language identifier <a href="Concepts.doc.html#25339">(§2.2)</a> or as the special method name <code>&lt;init&gt;</code> <a href="Overview.doc.html#12174">(§3.9)</a>.<p>
 * <p/>
 * <a name="1338"></a>
 * </p></dd><dt><code>DescriptorIndex</code>
 * </dt><dd> The value of the <code>DescriptorIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing a valid field descriptor <a href="ClassFile.doc.html#14152">(§4.3.2)</a> or method descriptor <a href="ClassFile.doc.html#7035">(§4.3.3)</a>.<p>
 * <p/>
 * <a name="7963"></a>
 * </p></dd></dl>
 * <h3>4.4.7    The  {@link Utf8_}  Structure</h3>
 *
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:13:05 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum NameAndType_ {


    /**
     * The value of the <code>nameIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing either a valid field or method name <a href="Concepts.doc.html#21272">(§2.7)</a> stored as a simple name <a href="Concepts.doc.html#21410">(§2.7.1)</a>, that is, as a Java programming language identifier <a href="Concepts.doc.html#25339">(§2.2)</a> or as the special method name <code>&lt;init&gt;</code> <a href="Overview.doc.html#12174">(§3.9)</a>.
     */
    Utf8Index(2),

    /**
     * The value of the <code>DescriptorIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing a valid field descriptor <a href="ClassFile.doc.html#14152">(§4.3.2)</a> or method descriptor <a href="ClassFile.doc.html#7035">(§4.3.3)</a>.
     */
    DescriptorIndex(2),;
     public int size;public Class clazz;

    NameAndType_(  int size) {

        this.size = size;
    }

    /**
     * @param buf     the buffer
     * @param results return vlaues which will be
     *                Utf8Index,
     *                AttributeLength,
     *                line_number_table_length
     * @param key     const table (optional)
     */
     static void next(ByteBuffer src, int[] results, IntBuffer key) {
        for (NameAndType_ info : values()) {
            results[info.ordinal()] = FileSlotRecord.genericGetInt(src, info.size);
        }
    }
}
