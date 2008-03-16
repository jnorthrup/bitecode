package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * The <code>Deprecated</code> attribute<a href="#78236"><sup>7</sup></a> is an optional fixed-length attribute in the <code>attributes</code>
 * table of <code>FileSlotRecord</code> <a href="FileSlotRecord.doc.html#74353">(§4.1)</a>, <code>FieldInfoHeader</code> <a href="FileSlotRecord.doc.html#2877">(§4.5)</a>, and <code>MethodInfo</code> <a href="FileSlotRecord.doc.html#1513">(§4.6)</a> structures.
 * A class, interface, method, or field may be marked using a <code>Deprecated</code>
 * <p/>
 * attribute to indicate that the class, interface, method, or field has been superseded. A
 * runtime interpreter or tool that reads the <code>class</code> file format, such as a compiler, can
 * use this marking to advise the user that a superseded class, interface, method, or
 * field is being referred to. The presence of a <code>Deprecated</code> attribute does not alter the
 * semantics of a class or interface.
 */
public enum
        DeprecatedAttributeHeader {
    /**
     * The value of the <code>Utf8Index</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="FileSlotRecord.doc.html#7963">(§4.4.7)</a> structure representing the string <code>"Deprecated"</code>.
     */
    Utf8Index(2),

    /**
     * The value of the AttributeLength item is zero.
     */
    AttributeLength ,;
     public int size;public Class clazz;

    DeprecatedAttributeHeader(  int size) {

        this.size = size;
    }

    DeprecatedAttributeHeader() {
        size=4;
    }

    static void next(ByteBuffer src, int[] results, int... key) {

        for (DeprecatedAttributeHeader info : values()) {
            results[info.ordinal()] = FileSlotRecord.genericGetInt(src, info.size);
        }
        int pos = src.position();
        src.position(pos + (results[AttributeLength.ordinal()] * 2));
    }
}
