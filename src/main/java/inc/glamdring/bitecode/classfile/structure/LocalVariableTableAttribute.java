package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:16:14 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum LocalVariableTableAttribute {
    /**
     * The value of the Utf8Index item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure representing the string "LocalVariableTable".
     */
    Utf8Index(2),
    /**
     * The value of the AttributeLength item indicates the length of the attribute, excluding the initial six bytes.
     */
    AttributeLength ,
    /**
     * The value of the local_variable_table_length item indicates the number of entries in the local_variable_table array.
     */
    local_variable_table_length(2);
     public int size;public Class clazz;

    LocalVariableTableAttribute(  int size) {
        //To change body of created methods use File | Settings | File Templates.
        this.size = size;
    }

    LocalVariableTableAttribute() {
        size=4;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (LocalVariableTableAttribute m : values()) {


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
