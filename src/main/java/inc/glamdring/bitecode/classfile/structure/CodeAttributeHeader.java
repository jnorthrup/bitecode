package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 8:59:32 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum CodeAttributeHeader {
    /**
     * The value of the Utf8Index item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure representing the string "Code".
     */
    Utf8Index(2),
    /**
     * The value of the AttributeLength item indicates the length of the attribute, excluding the initial six bytes.
     */
    AttributeLength,

    /**
     * The value of the max_stack item gives the maximum depth (§3.6.2) of the operand stack of this method at any point during execution of the method.
     */
    MaxStack(2),

    /**
     * The value of the max_locals item gives the number of local variables in the local variable array allocated upon invocation of this method, including the local variables used to pass parameters to the method on its invocation.
     * <p/>
     * The greatest local variable index for a value of type long or double is max_locals-2. The greatest local variable index for a value of any other type is max_locals-1.
     */
    MaxLocals(2),

    /**
     * The value of the code_length item gives the number of bytes in the code array for this method. The value of code_length must be greater than zero; the code array must not be empty.
     */
    CodeLength
   ,


    /**

     */
    ExceptionTableLength(2),

    /**

     */
    attributes_count(2),;
     public int size,seek;
    static public int recordLen;

    CodeAttributeHeader(int size ) {

        this.size = size;
    }

    CodeAttributeHeader() {
        size=4;

    }
}
