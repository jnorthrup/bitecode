package inc.glamdring.bitecode;
import java.nio.*;

/**
 * <p>recordSize: 8
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>InnerClassInfoIndex</td><td>0x2</td><td>0x0</td><td>short</td><td>{@link InnerClassInfoVisitor#InnerClassInfoIndex(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>OuterClassInfoIndex</td><td>0x2</td><td>0x2</td><td>short</td><td>{@link InnerClassInfoVisitor#OuterClassInfoIndex(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>inner_nameIndex</td><td>0x2</td><td>0x4</td><td>short</td><td>{@link InnerClassInfoVisitor#inner_nameIndex(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>AccessFlagsValue</td><td>0x2</td><td>0x6</td><td>short</td><td>{@link AccessFlagsValue}</td></tr>
 *
 * @see InnerClassInfo#InnerClassInfoIndex
 * @see InnerClassInfo#OuterClassInfoIndex
 * @see InnerClassInfo#inner_nameIndex
 * @see InnerClassInfo#AccessFlagsValue
 *      </table>
 */
public enum InnerClassInfo {
    InnerClassInfoIndex(0x2), OuterClassInfoIndex(0x2), inner_nameIndex(0x2), AccessFlagsValue(0x2) {{
    subRecord = AccessFlagsValue.class;
}};
    public Class clazz;

    /**
     * the length of one record
     */
    public static int recordLen;
    /**
 * the size per field, if any
 */
    public final int ___size___;
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
    public static final boolean isValue = false;
    public static final boolean isHeader = false;
    public static final boolean isRef = false;
    public static final boolean isInfo = true;

    /**
     * InnerClassInfo templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    InnerClassInfo(int... dimensions) {
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

        //;
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
        for (InnerClassInfo InnerClassInfo_ : values()) {
            String hdr = InnerClassInfo_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            InnerClassInfo_.subIndex(src, register, stack);
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
                    //register[TopLevelRecord.TableRecord.ordinal()] + ___table.seek___) / 4);
                    subRecord.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);
                    //resume the lower stack activities
                    stack.position(mark);
                }
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }
    }
}
//@@ #endInnerClassInfo