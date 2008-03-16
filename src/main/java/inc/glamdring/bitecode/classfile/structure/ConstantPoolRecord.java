package inc.glamdring.bitecode.classfile.structure;

/**
 * @version $Id$
 * @user jim
 * @created Mar 12, 2008 12:36:04 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum ConstantPoolRecord {

    ConstantPoolRef(4),
    /**
     * todo: crack fields
     */
    subType(2),

    /**
     * bitmap of shorts from "begin" position which point directly to strings
     */
    utf8Bitmap(1),
    /**
     * the constant pool tag type
     */
    tag(1),;

    /**
     * ctor
     *
     * @param size  bytes in this field
     * @param clazz optional class for setters.
     */
    ConstantPoolRecord(int size, Class... clazz) {

        this.size = size;
         init();

        /**
         * This is used to sew up unspecified setters.
         */

    }


    void init() {
        seek = recordLen;
        recordLen += size;
    }

    /**
     * this holds the length of a record.
     */

    static public int recordLen;

    /**
     * field size.
     */
    public int size;
    public Class clazz;


    /**
     * position from byte 0 of record.
     */
    public int seek;
}