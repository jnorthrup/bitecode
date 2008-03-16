package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * The <code>Synthetic</code> attribute<a href="#88570"><sup>6</sup></a> is a fixed-length attribute in the <code>attributes</code> table of
 * <code>FileSlotRecord</code> <a href="FileSlotRecord.doc.html#74353">(§4.1)</a>, <code>FieldInfoHeader</code> <a href="FileSlotRecord.doc.html#2877">(§4.5)</a>, and <code>MethodInfo</code> <a href="FileSlotRecord.doc.html#1513">(§4.6)</a> structures. A
 * class member that does not appear in the source code must be marked using a <code>Synthetic
 * <p/>
 * </code> attribute.
 */
public enum SyntheticAttribute {
    /**
     * The value of the <code>Utf8Index</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index must be a  {@link Utf8_}  <a href="FileSlotRecord.doc.html#7963">(§4.4.7)</a> structure representing the string <code>"Synthetic"</code>.
     */
    Utf8Index(2),

    /**
     * The value of the AttributeLength item is zero.
     */
    AttributeLength  ;

    SyntheticAttribute() {
        size=4;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (SyntheticAttribute m : values()) {


            int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
//            FileSlotRecord.genericGetInt(buf, m.size );
            m.subIndex(src, register, stack);
        }
    }

    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        int i = src.position();
        src.position(i + size);
    }

     public int size;public Class clazz;

    SyntheticAttribute(  int size) {
        this.size = size;
    }
}
