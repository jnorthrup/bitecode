package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 8:42:09 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum MethodAccessValue {
    /**
     * Declared ; may be accessed from outside its package.
     */public_(0x0001),
    /**
     * Declared ; accessible only within the defining class.
     */private_(0x0002),
    /**
     * Declared protected; may be accessed within subclasses.
     */protected_(0x0004),
    /**
     * Declared static.
     */static_(0x0008),
    /**
     * Declared    ; may not be overridden.
     */final_(0x0010),
    /**
     * Declared synchronized; invocation is wrapped in a monitor lock.
     */synchronized_(0x0020),
    /**
     * Declared native; implemented in a language other than Java.
     */native_(0x0100),
    /**
     * Declared abstract; no implementation is provided.
     */abstract_(0x0400),
    /**
     * Declared strictfp; floating-point mode is FP-strict
     */strict_(0x0800),;
     int flag;

    MethodAccessValue(int flag) {

        this.flag = flag;
    }

    static void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        int i = src.getShort() & 0xffff;

        String s = "ma:";
        for (MethodAccessValue acces : MethodAccessValue.values()) {
            int i1 = acces.flag;
            if ((i1 & i) != 0) s += "" + acces.name();

        }

        System.out.println(s);
    }
}
