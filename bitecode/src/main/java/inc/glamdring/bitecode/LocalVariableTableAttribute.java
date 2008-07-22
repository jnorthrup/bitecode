package inc.glamdring.bitecode;
import java.nio.*;

/**
 * <p>recordSize: 8
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>Utf8Index</td><td>0x2</td><td>0x0</td><td>short</td><td>{@link LocalVariableTableAttributeVisitor#Utf8Index(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>AttributeLength</td><td>0x4</td><td>0x2</td><td>int</td><td>{@link LocalVariableTableAttributeVisitor#AttributeLength(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>local_variable_table_length</td><td>0x2</td><td>0x6</td><td>short</td><td>{@link LocalVariableTableAttributeVisitor#local_variable_table_length(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 *
 * @see LocalVariableTableAttribute#Utf8Index
 * @see LocalVariableTableAttribute#AttributeLength
 * @see LocalVariableTableAttribute#local_variable_table_length
 *      </table>
 */
public enum LocalVariableTableAttribute {
    Utf8Index(0x2), AttributeLength(0x4), local_variable_table_length(0x2);
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
    public static final boolean isInfo = false;

    /**
     * LocalVariableTableAttribute templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    LocalVariableTableAttribute(int... dimensions) {
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
        for (LocalVariableTableAttribute LocalVariableTableAttribute_ : values()) {
            String hdr = LocalVariableTableAttribute_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            LocalVariableTableAttribute_.subIndex(src, register, stack);
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
//@@ #endLocalVariableTableAttribute