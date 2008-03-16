package inc.glamdring.bitecode.classfile.structure;

/**
 * The <code>SourceFile</code> attribute is an optional fixed-length attribute in the <code>attributes</code>
 * <p/>
 * table of the <code>FileSlotRecord</code> <a href="FileSlotRecord.doc.html#74353">(§4.1)</a> structure. There can be no more than one
 * <code>SourceFile</code>  attribute in the <code>attributes</code> table of a given <code>FileSlotRecord</code> structure.
 */
 public enum SourceFileAttribute {
    /**
     * The value of the Utf8Index item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure representing the string "SourceFile".
     */
    Utf8Index(2),
    /**
     * The value of the AttributeLength item of a SourceFileAttribute structure must be 2.
     */
    AttributeLength /* {

        void index(    ByteBuffer src,     int[] register,     IntBuffer stack) {

                int len = src.getInt();
                int data = src.position();
            src.position(data + len);
        }

    }*/,
    /**
     * The value of the sourcefileIndex item must
     * be a valid index into the ConstantPoolRecord table. The
     * constant pool entry at that index must be a Utf8_ (§4.4.7) structure representing a string.
     * <p/>
     * The string referenced by the sourcefileIndex item will be interpreted as indicating the name of the source file from which this
     * class file was compiled. It will not be interpreted as indicating the name of a directory containing the file or an absolute path name for the file; such platform-specific additional information must be supplied by the runtime interpreter or development tool at the time the file name is actually used.
     */
    sourcefileIndex(2);
    ;/*
    static void index(    ByteBuffer src,     int[] register,     IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (SourceFileAttribute m : values()) {


                int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
//            FileSlotRecord.genericGetInt(src, m.size );
            m.subIndex(src, register, stack);
        }


    }

    void subIndex(    ByteBuffer src,     int[] register,     IntBuffer stack) {


            int i = src.position();
        src.position(i + size);
    }*/

     public int size;public Class clazz;

    SourceFileAttribute(  int size) {
        this.size = size;
    }

    SourceFileAttribute() {
        size=4;
    }
}
