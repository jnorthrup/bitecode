package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;


/**
 * the only thing in the stack if the input offses.
 * except: trackback
 * <p/>
 * the only thing in the registers is the stack.
 *
 * @version $Id$
 * @user jim
 * @created Mar 5, 2008 3:21:06 PM
 * @copyright Glamdring Incorporated Enterprises.  All rights reserved
 * @license this header must remain in this file at all times and credit due to its author may not be removed.
 * Permission is granted for teaching and instructional purposes, learning, and non-commercial use provided that
 * copyright and license notice remain unaltered.  Please contact author for matters of inclusion in commercial or
 * for-profit software and products.
 */
 public enum FileSlotRecord {
    /**
     *
     */
    magic(4),

    /**

     */
    minor_version(2),

    /**
     */
    major_version(2),

    /**
     * we squeeze tag type into:
     * <br>
     * 0-24 - offset (>>8)<br>
     * 25-32 tag          (&0xff)<br>
     */
    ConstantPoolRecord(2)  ,

    /**

     */
    AccessFlagsValue(2)  ,

    /**

     */
    ClassIndex(2),

    /**

     */
    SuperClassIndex(2),

    /**
     * spec says: Each value in the interfaces array must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at each value of interfaces[i], where 0  i < interfaces_count, must be a CONSTANT_Class_info (§4.4.1) structure representing an interface that is a direct superinterface of this class or interface type, in the left-to-right order given in the source for the type.
     */
    InterFaceTableRecord(2) {
        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            int count = (short) (src.getShort() & 0xffff);
            stack.put(count);
            for (int i = 0; i < count; i++) {
                int offset = src.getShort() & 0xffff;
                int cons = register[ConstantPoolRecord.ordinal()];
                int iface = stack.get(cons + offset);
                stack.put(iface);
            }
        }
    },


    /**
     * count,field0,...
     */
    FieldRecord(2) {
        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            int count = (short) (src.getShort() & 0xffff);
            stack.put(count);
            for (int i = 0; i < count; i++) {
                FieldInfoHeader.index(src, register, stack);
            }
        }},

    /**

     */
    MethodsRecord(2) {
        void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
            int count = (short) (src.getShort() & 0xffff);
            stack.put(count);
            for (int i = 0; i < count; i++) {
                MethodInfo.index(src, register, stack);
            }
        }
    },

    /**

     */
    AttributesRecord(2) {
        void next(ByteBuffer src, int[] register, IntBuffer stack) {
            int count = (int) src.getShort() & 0xffff;
            stack.put(count);
            for (int i = 0; i < count; i++) {
                AttributeHeader.index(src, register, stack);
            }
        }


    };


    static void indexClassFile(ByteBuffer src, int[] register, IntBuffer stack) {

        for (FileSlotRecord slotRecord : values()) {

            slotRecord.index(src, register, stack);
        }
    }

    /**
     * @param buf      the input classfile
     * @param register holds stack pointer of each slot
     * @param stack    holds stack position ordinal(), stackPosition(ordinal+1)
     */
    static void index(ByteBuffer src, int[] register, IntBuffer stack) {
        /*  try {

                    for (FileSlotRecord classFileSlot : FileSlotRecord.index()) {

                        System.out.println("indexing:  " + classFileSlot.name());
                        stack.put(genericGetInt(src, classFileSlot.size));

        //                srcUri(256),
        //                indexUri(256),
        //                srcSlotVector(4 * FileSlotRecord.index().length),
        //                indexSlotVector(4 * FileSlotRecord.index().length),
        //                tableOffsets(TableRecord.recordLen),
        //                tabledata(4),


                        classFileSlot.subIndex(src, register, stack);

                    }
        */

        for (FileSlotRecord fileSlotRecord : FileSlotRecord.values()) {

            //notch the slot stackPointer of the src buffer
            int stackPointer = fileSlotRecord.seek + fileSlotRecord.ordinal();
            stack.put(stackPointer, src.position());
            System.err.println(fileSlotRecord.getClass().getCanonicalName() + "." + fileSlotRecord.name() + "->" + fileSlotRecord.getClass().getCanonicalName() + "." + fileSlotRecord.name() + ": srcPos:stacKpos " + src.position() + ":" + stackPointer);

            //each table has a reserved 64k worth of ints
            TableRecord store = null;
            try {
                store = TableRecord.valueOf(fileSlotRecord.name());
            } catch (IllegalArgumentException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            if (store != null) {
                int position = (  /*ClassFileRecord.tabledata.seek + */store.seek) / 4;

                System.err.println(fileSlotRecord.getClass().getCanonicalName() + "." + fileSlotRecord.name() + "->" + store.getClass().getCanonicalName() + "." + fileSlotRecord.name() + ": srcPos:stacKpos " + src.position() + ":" + position);

                stack.position(position);

                //we are the "Index" for this special divided Slot stackPointer tier
                fileSlotRecord.subIndex(src, register, stack);
            }

            //fill the register for later with stack offsets
            register[fileSlotRecord.ordinal()] = stack.position();

        }
    }

    /**
     * write the location of the slotted entry (0-64k) into a stack int pointer -> entry head.
     *
     * @param src
     * @param register
     * @param stack
     */
    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        int stackPtr = stack.position();
        System.err.println(getClass().getCanonicalName() + "." + name() + ": srcPos:stacKpos " + src.position() + ":" + stackPtr);
        stack.put(stackPtr, src.position());

    }


    FileSlotRecord(  int size) {
        this.size = size;
    }

     static int genericGetInt(ByteBuffer src, int len) {
        return len == 4 ? src.getInt() :
                ((len == 2 ? src.getShort() :
                        src.get()) & 0xffff);
    }

     static int genericPeekInt(ByteBuffer src, int len) {
        return len == 4 ? src.getInt(src.position()) :
                ((len == 2 ? src.getShort(src.position()) :
                        src.get(src.position())) & 0xffff);
    }

    /**
     * ctor
     *
     * @param size  bytes in this field
     * @param clazz optional class for setters.
     */
    private FileSlotRecord(  int size , Class... clazz) {

        this.size = size;
        init();

/**
 * This is used to sew up unspecified setters.
 */
    }

    /**
     * this holds the length of a record.
     */
    static public int recordLen;

    /**
     * field size.
     */
     public int size;public Class clazz;

    /**
     * position from byte 0 of record.
     */
     public int seek;


     void init() {
        seek = recordLen;
        recordLen += size;
    }

}
