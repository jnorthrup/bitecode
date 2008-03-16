package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:12:38 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum Field_ {

    /**

     */
    ClassIndex(2),

    /**

     */
    NameAndTypeIndex(2),;
     public int size;public Class clazz;

    Field_(  int size) {

        this.size = size;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (Field_ m : values()) {


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
