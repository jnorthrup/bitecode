package inc.glamdring.bitecode;
import java.nio.*;

/**
 * <p>recordSize: 1048970
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>ClassResourceUri</td><td>0x100</td><td>0x0</td><td>byte[]</td><td>{@link ClassFileRecordVisitor#ClassResourceUri(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>FileSlotRecord</td><td>0x50</td><td>0x100</td><td>byte[]</td><td>{@link FileSlotRecord}</td></tr>
 * <tr><td>TableRecord</td><td>0x10003a</td><td>0x150</td><td>byte[]</td><td>{@link TableRecord}</td></tr>
 *
 * @see inc.glamdring.bitecode.TopLevelRecord#ClassResourceUri
 * @see inc.glamdring.bitecode.TopLevelRecord#FileSlotRecord
 * @see inc.glamdring.bitecode.TopLevelRecord#TableRecord
 *      </table>
 */
public enum TopLevelRecord {
    ClassResourceUri(0x100), FileSlotRecord(0x50) {{
    subRecord = FileSlotRecord.class;
}}, TableRecord(0x10003a) {{
    subRecord = TableRecord.class;
}};
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
    public static final boolean isRecord = true;
    public static final boolean isValue = false;
    public static final boolean isHeader = false;
    public static final boolean isRef = false;
    public static final boolean isInfo = false;

    /**
     * ClassFileRecord templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    TopLevelRecord(int... dimensions) {
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
                        size= subRecord.getField("___recordlen___").getInt(null);
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

        seek = recordLen;
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
        for (TopLevelRecord TopLevelRecord_ : values()) {
            String hdr = TopLevelRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            TopLevelRecord_.subIndex(src, register, stack);
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
                final TopLevelRecord table;
                table = TableRecord.valueOf(subRecord.getSimpleName());
                if (table != null) {
                    //stow the original location
                    int mark = stack.position();
//                    //register[TopLevelRecord.TableRecord.ordinal()] + ___table.seek___) / 4);
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
//@@ #endClassFileRecord