package inc.glamdring.util;

import java.nio.*;

/**
 * @version $Id$
 * @user jim
 * @created Mar 10, 2008 11:43:52 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */

public class stdlib {

    static public String c_str(ByteBuffer view, int... params) {
        int end = params.length > 1 ? params[1] : view.limit();
        int begin = params.length > 0 ? params[0] : view.position();
        CharBuffer charBuffer = CharBuffer.allocate(end - begin);
        for (int i = begin; i < end; i++) {
            charBuffer.put((char) (view.get(i) & 0xff));
        }
        String s = charBuffer.flip().toString();
        return s;
    }

}




