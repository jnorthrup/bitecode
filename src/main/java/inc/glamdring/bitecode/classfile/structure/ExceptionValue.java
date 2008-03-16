package inc.glamdring.bitecode.classfile.structure;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 8:59:27 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum ExceptionValue {
    /**

     */
    start_pc(2),

    /**

     */
    end_pc(2),

    /**
     */
    handler_pc(2),

    /**

     */
    catch_type(2),;
     public int size;public Class clazz;

    ExceptionValue(  int size) {
        this.size = size;
    }
}    //ExceptionValue[exception_table_length];
