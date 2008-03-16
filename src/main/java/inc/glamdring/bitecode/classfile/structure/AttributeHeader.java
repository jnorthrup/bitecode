package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * Certain attributes are predefined as
 * part of the class file specification.
 * The predefined attributes are the
 * SourceFile (§4.7.7), ConstantValue (§4.7.2), Code (§4.7.3), Exceptions (§4.7.4),
 * InnerClasses (§4.7.5), Synthetic (§4.7.6), LineNumberTable (§4.7.8),
 * LocalVariableTable (§4.7.9), and Deprecated (§4.7.10) attributes.
 * <p/>
 * <p/>
 * Within the
 * context of their use in this specification, that is, in the attributes tables
 * of the class file structures in which they appear,
 * the names of these predefined attributes are reserved.
 */
public enum AttributeHeader {
    /**
     * For all attributes, the <code>Utf8Index</code> must be a valid unsigned 16-bit index into the constant pool of the class.
     */
    Utf8Index(2),
    /**
     * The <code>ConstantPoolRecord</code> entry at <code>Utf8Index</code> must be a  {@link Utf8_}  <a href="ClassFile.doc.html#7963">(§4.4.7)</a> structure representing the name of the attribute. The value of the <code>AttributeLength</code> item indicates the length of the subsequent information in bytes. The length does not include the initial six bytes that contain the <code>Utf8Index</code> and <code>AttributeLength</code> items.
     */
    AttributeLength ;

    AttributeHeader() {
    }
          static public int recordLen;
    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (AttributeHeader m : values()) {


            int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
//            FileSlotRecord.genericGetInt(src, m.size );
            m.subIndex(src, register, stack);
        }


    }

    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {


        int i = src.position();
        src.position(i + size);
    }

    public int size;
    public Class clazz;


    AttributeHeader(int size) {
        this.size = size;
    }
}
