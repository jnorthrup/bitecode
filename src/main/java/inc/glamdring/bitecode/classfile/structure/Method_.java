package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:12:44 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum Method_ {

    /**
     * The classIndex item of a Method_ structure must be a class type, not an interface type.
     */
    ClassIndex(2),

    /**
     * The value of the <code>name_and_typeIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a <code>{@link NameAndType_}</code> <a href="ClassFile.doc.html#1327">(§4.4.6)</a> structure. This <code>ConstantPoolRecord</code> entry indicates the name and descriptor of the field or method.
     * <p/>
     * If the name of the method of a <code>Method_</code> structure begins with a<code>'  &lt;'</code> (<code>'\u003c'</code>), then the name must be the special name <code>&lt;init&gt;</code>, representing an instance initialization method <a href="Overview.doc.html#12174">(§3.9)</a>. Such a method must return no value.
     */
    NameAndTypeIndex(2);

    public int size;
    public Class clazz;
    public static int  recordLen;
    private int seek;

    Method_(int size) {

        this.size = size;
        init();
    }

    private void init() {
        seek=recordLen;
        recordLen+=size;
    }
}
