package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 12, 2008 11:41:52 AM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */

public enum LineNumberValue {
    /**
     * start_pc
     * The value of the start_pc item must indicate the index into the code array at which the code for a new line in the original source file begins. The value of start_pc must be less than the value of the code_length item of the Code attribute of which this LineNumberTable is an attribute.
     */
    start_pc(2),

    /**
     * line_number
     * The value of the line_number item must give the corresponding line number in the original source file.
     */
    line_number(2),;
     public int size;public Class clazz;

    LineNumberValue(  int size) {


        this.size = size;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (LineNumberValue m : values()) {


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
