package inc.glamdring.bitecode;
import java.nio.*;

/**
 * <p>recordSize: 2
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>public_</td><td>0x2</td><td>0x0</td><td>short</td><td>{@link AccessFlagsValueVisitor#public_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>private_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#private_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>protected_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#protected_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>static_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#static_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>final_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#final_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>super_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#super_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>volatile_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#volatile_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>transient_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#transient_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>interface_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#interface_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>abstract_</td><td>0x0</td><td>0x2</td><td>byte[]</td><td>{@link AccessFlagsValueVisitor#abstract_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 *
 * @see AccessFlagsValue#public_
 * @see AccessFlagsValue#private_
 * @see AccessFlagsValue#protected_
 * @see AccessFlagsValue#static_
 * @see AccessFlagsValue#final_
 * @see AccessFlagsValue#super_
 * @see AccessFlagsValue#volatile_
 * @see AccessFlagsValue#transient_
 * @see AccessFlagsValue#interface_
 * @see AccessFlagsValue#abstract_
 *      </table>
 */
public enum AccessFlagsValue {
    public_(0x2) {{
        flag = 1;
    }}, private_ {{
    flag = 2;
}}, protected_ {{
    flag = 4;
}}, static_ {{
    flag = 8;
}}, final_ {{
    flag = 16;
}}, super_ {{
    flag = 32;
}}, volatile_ {{
    flag = 64;
}}, transient_ {{
    flag = 128;
}}, interface_ {{
    flag = 512;
}}, abstract_ {{
    flag = 1024;
}};
    public int flag;

    /**
     * the length of one record
     */
    public static int recordLen;
    /**
 * the size per field, if any
 */
    public int ___size___;
    /**
 * the offset from record-start of the field
 */
    public final int ___seek___;
    /**
     * a delegate class wihch will perform sub-indexing on behalf of a field once it has marked its initial stating
     * offset into the stack.
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
     * AccessFlagsValue templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    AccessFlagsValue(int... dimensions) {
        int[] dim = init(dimensions);
        ___size___ = dim[0];
        ___seek___ = dim[1];

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
                        //.getField("___recordlen___").getInt(null);
                    } catch (Exception e) {
                    }
                    break;
                } catch (Exception e) {
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
                    for (String aPackage1 : new String[]{
                            "",
                            getClass().getPackage().getName() + ".",
                            "java.lang.",
                            "java.util.",
                    })
                        if (valueClazz == null) break;
                        else
                            try {
                                valueClazz = Class.forName(aPackage1 + name().replace(suffix, ""));
                            } catch (Exception e) {
                            }
                }
            }
        }

//        //
        recordLen += ___size___;

        return new int[]{___size___, ___seek___};
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
        for (AccessFlagsValue AccessFlagsValue_ : values()) {
            String hdr = AccessFlagsValue_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            AccessFlagsValue_.subIndex(src, register, stack);
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
                final TableRecord table = TableRecord.valueOf(subRecord.getSimpleName());
                if (table != null) {
                    //stow the original location
                    int mark = stack.position();
                    //                    subRecord.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);
                    //resume the lower stack activities
                    stack.position(mark);
                }
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }
    }
}
//@@ #endAccessFlagsValue