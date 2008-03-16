package inc.glamdring.bitecode.classfile.structure;

import static inc.glamdring.util.stdlib.*;

import java.nio.*;

/**
 * 4.4.7    The  {@link Utf8_}  Structure</h3>
 * <a name="1297"></a>
 * The  {@link Utf8_}  structure is used to represent constant string index.
 * <p><a name="3310"></a>
 * UTF-8 strings are encoded so that character sequences that contain only non-null ASCII characters can be represented using only 1 byte per character, but characters of up to 16 bits can be represented. All characters in the range <code>'\u0001'</code> to <code>'\u007F'</code> are represented by a single byte:
 * <p/>
 * </p><p><table border="1">
 * <tbody><tr>
 * <p/>
 * <td><a name="17391"></a>
 * 0
 * </td><td><a name="17393"></a>
 * bits 6-0
 * </td></tr></tbody></table><br></p><p>
 * <p/>
 * <a name="35791"></a>
 * The 7 bits of data in the byte give the value of the character represented. The null
 * character (<code>'\u0000'</code>) and characters in the range <code>'\u0080'</code> to <code>'\u07FF'</code> are represented
 * by a pair of bytes x and y:</p><p>
 * <p/>
 * <a name="28487"></a>
 * x:
 * <table border="1">
 * <tbody><tr><td><a name="28489"></a>
 * 1
 * </td><td><a name="28491"></a>
 * 1
 * </td><td><a name="28493"></a>
 * 0
 * </td><td><a name="28495"></a>
 * bits 10-6
 * <p/>
 * </td></tr></tbody></table><br>
 * <p/>
 * <a name="28497"></a>
 * y:
 * <table border="1">
 * <tbody><tr><td><a name="28499"></a>
 * 1
 * </td><td><a name="28501"></a>
 * 0
 * </td><td><a name="28503"></a>
 * bits 5-0
 * </td></tr></tbody></table><br>
 * <p/>
 * </p><p><a name="35796"></a>
 * The bytes represent the character with the value ((x &amp; <code>0x1f</code>) &lt;&lt; <code>6</code>) + (y &amp; <code>0x3f</code>).
 * </p><p><a name="35797"></a>
 * Characters in the range <code>'\u0800'</code> to <code>'\uFFFF'</code> are represented by 3 bytes x, y, and z:</p><p>
 * <p/>
 * <a name="35800"></a>
 * x:
 * <table border="1">
 * <tbody><tr><td><a name="35802"></a>
 * 1
 * </td><td><a name="35804"></a>
 * 1
 * </td><td><a name="35806"></a>
 * 1
 * </td><td><a name="35808"></a>
 * 0
 * <p/>
 * </td><td><a name="35810"></a>
 * bits 15-12
 * </td></tr></tbody></table><br>
 * <p/>
 * <a name="35812"></a>
 * y:
 * <table border="1">
 * <tbody><tr><td><a name="35814"></a>
 * 1
 * </td><td><a name="35816"></a>
 * 0
 * </td><td><a name="35818"></a>
 * bits 11-6
 * <p/>
 * </td></tr></tbody></table><br>
 * <p/>
 * <a name="35820"></a>
 * z:
 * <table border="1">
 * <tbody><tr><td><a name="35822"></a>
 * 1
 * </td><td><a name="35824"></a>
 * 0
 * </td><td><a name="35826"></a>
 * bits 5-0
 * </td></tr></tbody></table><br></p><p>
 * <p/>
 * <a name="15576"></a>
 * The character with the value ((x &amp; <code>0xf</code>) &lt;&lt; <code>12</code>) + ((y &amp; <code>0x3f</code>) &lt;&lt; <code>6</code>) + (z &amp; <code>0x3f</code>) is
 * represented by the bytes.
 * <p/>
 * </p><p><a name="12887"></a>
 * The bytes of multibyte characters are stored in the <code>class</code> file in big-endian (high byte first) order.</p><p>
 * <a name="6111"></a>
 * There are two differences between this format and the "standard" UTF-8 format. First, the null byte <code>(byte)0</code> is encoded using the 2-byte format rather than the 1-byte format, so that Java virtual machine UTF-8 strings never have embedded nulls. Second, only the 1-byte, 2-byte, and 3-byte formats are used. The Java virtual machine does not recognize the longer UTF-8 formats.</p><p>
 * <a name="14071"></a>
 * For more information regarding the UTF-8 format, see <i>File System Safe UCS Transformation Format (FSS_UTF)</i>, X/Open Preliminary Specification (X/Open Company Ltd., Document Number: P316). This information also appears in ISO/IEC 10646, Annex P.</p><p>
 * <p/>
 * <a name="7715"></a>
 * The  {@link Utf8_}  structure is</p><p>
 * </p><pre><br><a name="1298"></a>&nbsp;&nbsp;&nbsp;&nbsp;<code>Utf8_ {
 * </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u1 tag;
 * </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u2 length;
 * </code>&nbsp;&nbsp;&nbsp;&nbsp;<code>	u1 bytes[length];
 * </code><a name="9399"></a>&nbsp;&nbsp;&nbsp;&nbsp;<code>}
 * </code><br></pre><a name="9402"></a>
 * The items of the  {@link Utf8_}  structure are the following:
 * <p/>
 * <p><a name="9401"></a>
 * </p><dl><dt><code>tag</code>
 * </dt><dd> The <code>tag</code> item of the  {@link Utf8_}  structure has the value <code>CONSTANT_Utf8</code> (<code>1</code>).<p>
 * <p/>
 * <a name="1312"></a>
 * </p></dd><dt><code>length</code>
 * </dt><dd> The value of the <code>length</code> item gives the number of bytes in the <code>bytes</code> array (not the length of the resulting string). The strings in the  {@link Utf8_}  structure are not null-terminated.<p>
 * <a name="2874"></a>
 * </p></dd><dt><code>bytes[]</code>
 * <p/>
 * </dt><dd> The <code>bytes</code> array contains the bytes of the string. No byte may have the value <code>(byte)0</code> or lie in the range <code>(byte)0xf0</code>-<code>(byte)0xff</code>.<p>
 * <a name="2877"></a>
 * </p></dd>
 */
public enum Utf8_ {
    /**
     * The value of the length item gives the number of bytes in the bytes array
     * (not the length of the resulting string). The strings in the
     * Utf8_ structure are not null-terminated.
     */
    length(2),;
     public int size;public Class clazz;

    Utf8_(  int size) {
        this.size = size;
    }

     static void index(ByteBuffer src, int[] registers, IntBuffer stack) {
//            Utf8_.index(src, registers, stack);
        int aShort = (src.getShort() & 0xffff);

        String s = c_str(src, src.position(), src.position() + aShort);
        System.out.println("utf8: " + s);
        src.position(src.position() + aShort);
    }
}
