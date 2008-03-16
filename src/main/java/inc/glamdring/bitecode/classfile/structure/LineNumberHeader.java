package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * The <code>LineNumberTable</code> attribute is an optional variable-length attribute in the
 * <code>attributes</code> table of a <code>Code</code> <a href="FileSlotRecord.doc.html#1546">(§4.7.3)</a> attribute. It may be used by debuggers to
 * determine which part of the Java virtual machine <code>code</code> array corresponds to a given
 * line number in the original source file. If <code>LineNumberTable</code> attributes are present
 * in the <code>attributes</code> table of a given <code>Code</code> attribute, then they may appear in any
 * order. Furthermore, multiple <code>LineNumberTable</code> attributes may together represent a
 * given line of a source file; that is, <code>LineNumberTable</code> attributes need not be one-to-one with source lines.
 */
public enum LineNumberHeader {
    Utf8Index(2),
    AttributeLength ,

    /**
     *
     */
    LineNumberCount(2) {


        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            int aShort = src.getShort() & 0xffff;
            for (int i = 0; i < aShort; i++)
                LineNumberValue.index(src, register, stack);
        }
    };


     public int size;public Class clazz;


    LineNumberHeader(  int size) {
        this.size = size;
    }

    LineNumberHeader() {
        //To change body of created methods use File | Settings | File Templates.
    }


    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (LineNumberHeader m : values()) {


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
}
