package inc.glamdring.bitecode;

import java.nio.*;

/**
 * <p>recordSize: 0 <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>public_</td><td>0x0</td><td>0x0</td><td> (byte) public_=src.get(0x0) & 0xff</td><td>{@link
 * MethodAccessValueVisitor#public_(ByteBuffer, int[], IntBuffer)}</td></tr> <tr><td>private_</td><td>0x0</td><td>0x0</td><td>
 * (byte) private_=src.get(0x0) & 0xff</td><td>{@link MethodAccessValueVisitor#private_(ByteBuffer, int[],
 * IntBuffer)}</td></tr> <tr><td>protected_</td><td>0x0</td><td>0x0</td><td> (byte) protected_=src.get(0x0) &
 * 0xff</td><td>{@link MethodAccessValueVisitor#protected_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>static_</td><td>0x0</td><td>0x0</td><td> (byte) static_=src.get(0x0) & 0xff</td><td>{@link
 * MethodAccessValueVisitor#static_(ByteBuffer, int[], IntBuffer)}</td></tr> <tr><td>final_</td><td>0x0</td><td>0x0</td><td>
 * (byte) final_=src.get(0x0) & 0xff</td><td>{@link MethodAccessValueVisitor#final_(ByteBuffer, int[],
 * IntBuffer)}</td></tr> <tr><td>synchronized_</td><td>0x0</td><td>0x0</td><td> (byte) synchronized_=src.get(0x0) &
 * 0xff</td><td>{@link MethodAccessValueVisitor#synchronized_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td>native_</td><td>0x0</td><td>0x0</td><td> (byte) native_=src.get(0x0) & 0xff</td><td>{@link
 * MethodAccessValueVisitor#native_(ByteBuffer, int[], IntBuffer)}</td></tr> <tr><td>abstract_</td><td>0x0</td><td>0x0</td><td>
 * (byte) abstract_=src.get(0x0) & 0xff</td><td>{@link MethodAccessValueVisitor#abstract_(ByteBuffer, int[],
 * IntBuffer)}</td></tr> <tr><td>strictfp_</td><td>0x0</td><td>0x0</td><td> (byte) strictfp_=src.get(0x0) &
 * 0xff</td><td>{@link MethodAccessValueVisitor#strictfp_(ByteBuffer, int[], IntBuffer)}</td></tr>
 *
 * @see inc.glamdring.bitecode.MethodAccessValue#public_
 * @see inc.glamdring.bitecode.MethodAccessValue#private_
 * @see inc.glamdring.bitecode.MethodAccessValue#protected_
 * @see inc.glamdring.bitecode.MethodAccessValue#static_
 * @see inc.glamdring.bitecode.MethodAccessValue#final_
 * @see inc.glamdring.bitecode.MethodAccessValue#synchronized_
 * @see inc.glamdring.bitecode.MethodAccessValue#native_
 * @see inc.glamdring.bitecode.MethodAccessValue#abstract_
 * @see inc.glamdring.bitecode.MethodAccessValue#strictfp_ </table>
 */
public enum MethodAccessValue {
    public_ {{
        docString = "may be accessed from outside its package.";
        flags = 1;
    }}, private_ {{
    docString = "accessible only within the defining class.";
    flags = 2;
}}, protected_ {{
    docString = "may be accessed within subclasses.";
    flags = 4;
}}, static_ {{
    docString = "";
    flags = 8;
}}, final_ {{
    docString = "may not be overridden.";
    flags = 16;
}}, synchronized_ {{
    docString = "invocation is wrapped in a monitor lock.";
    flags = 32;
}}, native_ {{
    docString = "implemented in a language other than Java.";
    flags = 256;
}}, abstract_ {{
    docString = "no implementation is provided.";
    flags = 1024;
}}, strictfp_ {{
    docString = "floating-point mode is FP-strict";
    flags = 2048;
}};
    public java.lang.String docString;
    public int flags;

    /**
     * the length of one record
     */
    public static int recordLen;
    /**
     * the size per field, if any
     */
    public final int size;
    /**
     * the offset from record-start of the field
     */
    public final int seek;
    /**
     * a delegate class wihch will perform sub-indexing on behalf of a field once it has marked its initial stating offset
     * into the stack.
     */
    public Class<? extends Enum> subRecord;
    /**
     * a hint class for bean-wrapper access to data contained.
     */
    public Class valueClazz;
    public static final boolean isRecord = false;
    public static final boolean isValue = true;
    public static final boolean isHeader = false;
    public static final boolean isRef = false;
    public static final boolean isInfo = false;

    /**
     * MethodAccessValue templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    MethodAccessValue(int... dimensions) {
        int[] dim = init(dimensions);
        size = dim[0];
        seek = dim[1];

    }

    int[] init(int... dimensions) {
        int size = dimensions.length > 0 ? dimensions[0] : 0,
                seek = dimensions.length > 1 ? dimensions[1] : 0;

        if (subRecord == null) {
            final String[] indexPrefixes = {"", "s", "_", "Index", "Length", "Ref", "Header", "Info", "Table"};
            for (String indexPrefix : indexPrefixes) {
                try {
                    subRecord = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + indexPrefix);
                    try {
                        size = subRecord.getField("recordLen").getInt(null);
                    } catch (Exception e) {
                    }
                    break;
                } catch (ClassNotFoundException e) {
                }
            }
        }

        for (String vPrefixe1 : new String[]{"_", "", "$", "Value",}) {
            if (valueClazz != null) break;
            String suffix = vPrefixe1;
            for (String name1 : new String[]{name().toLowerCase(), name(),}) {
                if (valueClazz != null) break;
                final String trailName = name1;
                if (trailName.endsWith(suffix)) {
                    for (String aPackage1 : new String[]{"",
                            getClass().getPackage().getName() + ".",
                            "java.lang.",
                            "java.util.",
                    })
                        if (valueClazz == null) break;
                        else
                            try {
                                valueClazz = Class.forName(aPackage1 + name().replace(suffix, ""));
                            } catch (ClassNotFoundException e) {
                            }
                }
            }
        }

        seek = recordLen;
        recordLen += size;

        return new int[]{size, seek};
    }

    /**
     * The struct's top level method for indexing 1 record. Each Enum field will call SubIndex
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    static void index
            (ByteBuffer src, int[] register, IntBuffer stack) {
        for (MethodAccessValue MethodAccessValue_ : values()) {
            String hdr = MethodAccessValue_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            MethodAccessValue_.subIndex(src, register, stack);
        }
    }

    /**
     * Each of the Enums can override thier deault behavior of "seek-past"
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        System.err.println(name() + ":subIndex src:stack" + src.position() + ':' + stack.position());
        int begin = src.position();
        int stackPtr = stack.position();
        stack.put(begin);
        if (isRecord && subRecord != null) {
            try {
                final inc.glamdring.bitecode.TableRecord table = inc.glamdring.bitecode.TableRecord.valueOf(subRecord.getSimpleName());
                if (table != null) {
                    //stow the original location
                    int mark = stack.position();
                    stack.position((register[ClassFileRecord.TableRecord.ordinal()] + table.seek) / 4);
                    subRecord.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);
                    //resume the lower stack activities
                    stack.position(mark);
                }
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }
    }}
//@@ #endMethodAccessValue