package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * <span class="mw-headline">The constant pool</span></h3>  	 <p>The constant pool table is where most of the literal constant index are stored. This includes index such as numbers of all sorts, strings, identifier names, references to classes and methods, and type descriptors. All indexes, or references, to specific constants in the constant pool table are given by 16-bit (type u2) numbers, where index value 1 refers to the first constant in the table (index value 0 is invalid).</p>  	 <p>Due to historic choices made during the file format development, the number of constants in the constant pool table is not actually the same as the constant pool count which precedes the table. First, the table is indexed starting at 1 (rather than 0), so the count should actually be interpreted as the maximum index. Additionally a couple types of constants, namely longs and doubles, take up two consecutive slots in the table, although the second such slot is a phantom index that is never directly used.</p>  	 <p>The type of each item (constant) in the constant pool is identified by an initial byte <i>tag</i>. The number of bytes following this tag and their interpretation are then dependent upon the tag value. The legal constant types and their tag index are:</p>
 * <p/>  	 <table class="wikitable">  	 <tbody><tr>  	 <th>tag byte</th>  	 <th>additional bytes</th>  	 <th>description of constant</th>  	 </tr>  	 <tr>  	 <td>1</td>
 * <td>2+<i>PrefixTree</i> bytes<br>
 * <p/>  	 (variable)</td>
 * <td>UTF-8 (Unicode) string: a character string prefixed by a 16-bit number (type u2) indicating the number of bytes in the encoded string which immediately follows (which may be different than the number of characters). Note that the encoding used is not actually <a href="/wiki/UTF-8" title="UTF-8">UTF-8</a>, but involves a slight modification of the Unicode standard encoding form.</td>
 * </tr>  	 <tr>  	 <td>3</td>
 * <td>4 bytes</td>
 * <td>Integer: a signed 32-bit <a href="/wiki/Two%27s_complement" title="Two's complement">two's complement</a> number in big-endian format</td>
 * <p/>
 * <p/>  	 </tr>  	 <tr>  	 <td>4</td>
 * <td>4 bytes</td>
 * <td>Float: a 32-bit single-precision <a href="/wiki/IEEE_754" class="mw-redirect" title="IEEE 754">IEEE 754</a> floating-point number</td>
 * </tr>  	 <tr>  	 <td>5</td>
 * <td>8 bytes</td>
 * <p/>
 * <p/>  	 <td>Long: a signed 64-bit two's complement number in big-endian format (takes two slots in the constant pool table)</td>
 * </tr>  	 <tr>  	 <td>6</td>
 * <td>8 bytes</td>
 * <td>Double: a 64-bit double-precision IEEE 754 floating-point number (takes two slots in the constant pool table)</td>
 * </tr>  	 <tr>  	 <td>7</td>
 * <td>2 bytes</td>
 * <td>Class reference: an index within the constant pool to a UTF-8 string containing the fully-qualified class name (in <i>internal format</i>)</td>
 * <p/>
 * <p/>  	 </tr>  	 <tr>  	 <td>8</td>
 * <td>2 bytes</td>
 * <td>String reference: an index within the constant pool to a UTF-8 string</td>
 * </tr>  	 <tr>  	 <td>9</td>
 * <td>4 bytes</td>
 * <td>Field reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor.</td>
 * </tr>
 * <p/>  	 <tr>  	 <td>10</td>
 * <td>4 bytes</td>
 * <td>Method reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor.</td>
 * </tr>  	 <tr>  	 <td>11</td>
 * <td>4 bytes</td>
 * <td>Interface method reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor.</td>
 * </tr>  	 <tr>
 * <p/>  	 <td>12</td>
 * <td>4 bytes</td>
 * <td>Name and type descriptor: two indexes to UTF-8 strings within the constant pool, the first representing a name (identifier) and the second a specially-encoded type descriptor.</td>
 * </tr>
 * </tbody></table>
 * <p>There are only two integral constant types, integer and long. Other integral types appearing in the high-level language, such as boolean, byte, and short must be represented as an integer constant.</p>  	 <p>Class names in Java, when fully qualified, are traditionally dot-separated, such as "java.lang.Object". However within the low-level Class reference constants, an internal form appears which uses slashes instead, such as "java/lang/Object".</p>  	 <p>The Unicode strings, despite the moniker "UTF-8 string", are not actually encoded according to the Unicode standard, although it is similar. There are two differences (see
 * <a href="/wiki/UTF-8#Java" title="UTF-8">UTF-8</a> for a complete discussion). The first is that the codepoint U+0000 is encoded as the two-byte sequence
 * <code>C0 80</code> (in hex) instead of the standard single-byte encoding
 * <code>00</code>. The second difference is that supplementary characters (those outside the
 * <a href="/wiki/Basic_Multilingual_Plane" class="mw-redirect" title="Basic Multilingual Plane">BMP</a> at U+10000 and above) are encoded using a surrogate-pair construction similar to
 * <a href="/wiki/UTF-16" class="mw-redirect" title="UTF-16">UTF-16</a> rather than being directly encoded using UTF-8. In this case each of the two surrogates is encoded separately in UTF-8. For example U+1D11E is encoded as the 6-byte sequence
 * <code>ED A0 B4 ED B4 9E</code>, rather than the correct 4-byte UTF-8 encoding of
 * <code>f0 9d 84 9e</code>.</p>
 */
public enum ConstantPoolRef {

    INVALID(Error.class, (byte) 0) {
        void subIndex(ByteBuffer srcf, int[] registers, IntBuffer stack) {
            throw new UnsupportedOperationException();
        }},

    /**
     * UTF-8 (Unicode) string: a character string prefixed by a 16-bit number (type u2) indicating the number of bytes in the encoded string which immediately follows (which may be different than the number of characters). Note that the encoding used is not actually UTF-8, but involves a slight modification of the Unicode standard encoding form.
     */
    Utf8_ ,
    UNUSED(Error.class, (byte) 0) {
        void subIndex(ByteBuffer srcf, int[] registers, IntBuffer stack) {
            throw new UnsupportedOperationException();
        }},
    Integer_ ,
    Float_ ,
    Long_(long.class, (byte) 0, 8),
    Double_(double.class, (byte) 0, 8),
    /**
     * refrences the utf8 class name
     */
    Class_(2),
    /**
     * stringIndex
     * The value of the stringIndex item must be a valid index into the ConstantPoolRecord table. The
     * ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure
     * representing the sequence of characters to which the String object is to be initialized.
     */
    String_(String.class, (byte) 1, 2),
    /**
     * Field reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor.
     */
    Field_(Field_.class, (byte) 0, 4) {
        void subIndex(ByteBuffer src, int[] registers, IntBuffer stack) {
            Field_.index(src, registers, stack);
        }},
    /**
     * Method reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor.
     */
    Method_,

    /**
     * Interface method reference: two indexes within the constant pool, the first pointing to a Class reference, the second to a Name and Type descriptor.
     */
    InterfaceMethod_((byte) 0) { },
    /**
     * Name and type descriptor: two indexes to UTF-8 strings within the constant pool, the first representing a name (identifier) and the second a specially-encoded type descriptor.
     */
    NameAndType_(NameAndType_.class, (byte) 3, 4) {
    },;

    public int size;
    public Class clazz;
    Class valueType;

    /**
     * this provides the "bits" corresponding to the first 7 short(s) which contain a pointer to utf8.
     * <p/>
     * certain internal struct may contain more than the field tags defined at this level to be reached (score!).
     */
    byte utf8Bitmap;

    ConstantPoolRef(Class valueType, byte utf8Bitmap, int... size) {
        this.valueType = valueType;
        this.utf8Bitmap = utf8Bitmap;
        this.size = size.length == 0 ? 0 : size[0];
    }

    ConstantPoolRef(int size) {
        this.size = size;
    }

    ConstantPoolRef() {
    }

    Class getValueType() {
        return valueType;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {
        for (ConstantPoolRef m : values()) {
            int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
            m.subIndex(src, register, stack);
        }
    }

    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        int stackPtr = src.position();

        src.position(stackPtr + size);
    }
}