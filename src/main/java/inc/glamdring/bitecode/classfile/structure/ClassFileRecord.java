package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * User: jim
 */
public enum ClassFileRecord {

    /**
     * uri     in bytes
     * 0-terminated strings up to 255 len
     */
    ClassResourceUri(256),
    /**
     * the source offsets to the 10 slots -- int[10]
     */

    FileSlotRecord,
    /**
     *
     */
    TableRecord,;
/*
    *//**
     * this holds the length of a record.
     *//*

    static public int recordLen;

    *//**
     * field size.
     *//*
    public int size;
    public Class subRecord;
    public Class valueClazz;


    *//**
     * position from byte 0 of record.
     *//*
    public int seek;
    *//**
     * setter parameter type.
     *//*
    //         Class clazz;
*//**
 * ctor
 *
 * @param size  bytes in this field
 * @param clazz optional class for setters.
 *//*
    ClassFileRecord() {
        this(-1);
    }

    *//**
     * ctor
     *
     * @param size  bytes in this field
     * @param clazz optional class for setters.
     *//*
    ClassFileRecord(int size, Class<? extends Enum>... clazz) {
        if (size == -1) {
            final String packageName = getClass().getPackage().getName();
            try {
                subRecord = Class.forName(packageName + "." + name());
                if (subRecord != null) {
                    size = subRecord.getField("recordLen").getInt(null);
                }
            } catch (Exception e) {
                try {
                    this.size= 4;
                    valueClazz=int.class;
                } catch (Exception e1) {
//                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } else {
            this.size = size;
            nextClass:
            for (Class aClass : clazz) {
                do {

                    Class pClass = aClass.getSuperclass();
                    if (pClass == Enum.class) {
                        subRecord = pClass;
                        continue nextClass;
                    }
                    aClass = pClass;
                } while (aClass != Object.class);
                valueClazz = aClass;
            }
        }
        init();
    }

    void init() {
        seek = recordLen;
        recordLen += size;
    }

    public static void index(MappedByteBuffer src, int[] register, IntBuffer stack) {
    }*/

    ClassFileRecord(int i) {
}

    ClassFileRecord() {
    }
}