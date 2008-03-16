package inc.glamdring.bitecode.classfile.structure;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 8:59:19 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
public enum ExceptionTableHeader {
    /**

     */
    Utf8Index(2),

    /**

     */
    AttributeLength ,

    /**

     */
    ExceptionsCount(2),;
     public int size;public Class clazz;

    /*	*//**

     *//*
	exceptionIndex_table[number_of_exceptions](2),
*/
    ExceptionTableHeader(  int size) {
        this.size = size;
    }

    ExceptionTableHeader() {
        size=4;
    }
}
