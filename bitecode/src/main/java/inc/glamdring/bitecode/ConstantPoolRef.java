package inc.glamdring.bitecode;
import java.nio.*;

/**
 * <p>recordSize: 54
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>INVALID</td><td>0x4</td><td>0x0</td><td>int</td><td>{@link ConstantPoolRefVisitor#INVALID(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Utf8_</td><td>0x2</td><td>0x4</td><td>short</td><td>{@link Utf8_}</td></tr>
 * <tr><td>UNUSED</td><td>0x4</td><td>0x6</td><td>int</td><td>{@link ConstantPoolRefVisitor#UNUSED(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Integer_</td><td>0x4</td><td>0xa</td><td>int</td><td>{@link ConstantPoolRefVisitor#Integer_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Float_</td><td>0x4</td><td>0xe</td><td>int</td><td>{@link ConstantPoolRefVisitor#Float_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Long_</td><td>0x8</td><td>0x12</td><td>long</td><td>{@link ConstantPoolRefVisitor#Long_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Double_</td><td>0x8</td><td>0x1a</td><td>long</td><td>{@link ConstantPoolRefVisitor#Double_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Class_</td><td>0x2</td><td>0x22</td><td>short</td><td>{@link ConstantPoolRefVisitor#Class_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>String_</td><td>0x2</td><td>0x24</td><td>short</td><td>{@link ConstantPoolRefVisitor#String_(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>Field_</td><td>0x4</td><td>0x26</td><td>int</td><td>{@link Field_}</td></tr>
 * <tr><td>Method_</td><td>0x4</td><td>0x2a</td><td>int</td><td>{@link Method_}</td></tr>
 * <tr><td>InterfaceMethod_</td><td>0x4</td><td>0x2e</td><td>int</td><td>{@link InterfaceMethod_}</td></tr>
 * <tr><td>NameAndType_</td><td>0x4</td><td>0x32</td><td>int</td><td>{@link NameAndType_}</td></tr>
 *
 * @see ConstantPoolRef#INVALID
 * @see ConstantPoolRef#Utf8_
 * @see ConstantPoolRef#UNUSED
 * @see ConstantPoolRef#Integer_
 * @see ConstantPoolRef#Float_
 * @see ConstantPoolRef#Long_
 * @see ConstantPoolRef#Double_
 * @see ConstantPoolRef#Class_
 * @see ConstantPoolRef#String_
 * @see ConstantPoolRef#Field_
 * @see ConstantPoolRef#Method_
 * @see ConstantPoolRef#InterfaceMethod_
 * @see ConstantPoolRef#NameAndType_
 *      </table>
 */
public enum ConstantPoolRef {
    INVALID(0x4), Utf8_(0x2) {{
    subRecord = Utf8_.class;
}}, UNUSED(0x4), Integer_(0x4), Float_(0x4), Long_(0x8), Double_(0x8), Class_(0x2), String_(0x2), Field_(0x4) {{
    subRecord = Field_.class;
}}, Method_(0x4) {{
    subRecord = Method_.class;
}}, InterfaceMethod_(0x4) {{
    subRecord = InterfaceMethod_.class;
}}, NameAndType_(0x4) {{
    subRecord = NameAndType_.class;
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
    public static final boolean isRef = true;
    public static final boolean isInfo = false;

    /**
     * ConstantPoolRef templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    ConstantPoolRef(int... dimensions) {
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
        for (ConstantPoolRef ConstantPoolRef_ : values()) {
            String hdr = ConstantPoolRef_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ConstantPoolRef_.subIndex(src, register, stack);
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
//@@ #endConstantPoolRef