package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 4:05:54 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum AccessFlagsValue {
    /**
     * Marked or implicitly  in source.
     */
    public_(0x0001),
    /**
     * Marked  in source.
     */
    private_(0x0002),
    /**
     * Marked protected in source.
     */
    protected_(0x0004),
    /**
     * Marked or implicitly static in source.
     */
    static_(0x0008),
    /**
     * Marked     in source.
     */
    final_(0x0010),
    /**
     * Treat superclass methods specially when invoked by the invokespecial instruction.
     */
    super_(0x0020),
    /**
     * Declared volatile; cannot be cached.
     */
    volatile_(0x0040),
    /**
     * Declared transient; not written or read by a persistent object manager.
     */
    transient_(0x0080),
    /**
     * Was an interface in source.
     */
    interface_(0x0200),
    /**
     * Marked or implicitly abstract in source.
     */
    abstract_(0x0400),;
  public   int flag;

    AccessFlagsValue(int flag) {
        this.flag = flag;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {
        int i = src.getShort() & 0xffff;
        AccessFlagsValue[] access_flags1Value = AccessFlagsValue.values();

        String s = "a:";
        for (AccessFlagsValue field_acces : access_flags1Value) {
            int i1 = field_acces.flag;
            if ((i1 & i) != 0) s += "" + field_acces.name();

        }

        System.out.println(s);
    }

}
