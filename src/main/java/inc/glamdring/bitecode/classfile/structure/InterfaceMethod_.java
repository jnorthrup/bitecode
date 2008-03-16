package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:12:50 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum InterfaceMethod_ {

    /**
     * The value of the classIndex item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a CONSTANT_Class_info (§4.4.1) structure representing the class or interface type that contains the declaration of the field or method.
     */
    ClassIndex(2),

    /**
     * The value of the name_and_typeIndex item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a NameAndType_ (§4.4.6) structure. This ConstantPoolRecord entry indicates the name and descriptor of the field or method. In a Field_ the indicated descriptor must be a field descriptor (§4.3.2). Otherwise, the indicated descriptor must be a method descriptor (§4.3.3).
     */
    NameAndTypeIndex(2),;
     public int size;public Class clazz;

    InterfaceMethod_(  int size) {
        this.size = size;
    }

     static void next(ByteBuffer src, int[] results, IntBuffer key) {
        for (InterfaceMethod_ info : values()) {
            results[info.ordinal()] = FileSlotRecord.genericGetInt(src, info.size);
        }
    }
}
