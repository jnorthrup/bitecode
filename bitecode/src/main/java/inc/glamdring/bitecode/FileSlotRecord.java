package inc.glamdring.bitecode;
import java.nio.*;

/**
 * <p>recordSize: 80
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>magic</td><td>0x4</td><td>0x0</td><td>int</td><td>{@link FileSlotRecordVisitor#magic(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>minor_version</td><td>0x2</td><td>0x4</td><td>short</td><td>{@link FileSlotRecordVisitor#minor_version(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>major_version</td><td>0x2</td><td>0x6</td><td>short</td><td>{@link FileSlotRecordVisitor#major_version(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>ConstantPoolRecord</td><td>0x3a</td><td>0x8</td><td>byte[]</td><td>{@link ConstantPoolRecord}</td></tr>
 * <tr><td>AccessFlagsValue</td><td>0x2</td><td>0x42</td><td>short</td><td>{@link AccessFlagsValue}</td></tr>
 * <tr><td>ClassIndex</td><td>0x2</td><td>0x44</td><td>short</td><td>{@link FileSlotRecordVisitor#ClassIndex(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>SuperClassIndex</td><td>0x2</td><td>0x46</td><td>short</td><td>{@link FileSlotRecordVisitor#SuperClassIndex(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>InterFaceTableRecord</td><td>0x2</td><td>0x48</td><td>short</td><td>{@link FileSlotRecordVisitor#InterFaceTableRecord(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>FieldRecord</td><td>0x2</td><td>0x4a</td><td>short</td><td>{@link FileSlotRecordVisitor#FieldRecord(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>MethodsRecord</td><td>0x2</td><td>0x4c</td><td>short</td><td>{@link FileSlotRecordVisitor#MethodsRecord(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 * <tr><td>AttributesRecord</td><td>0x2</td><td>0x4e</td><td>short</td><td>{@link FileSlotRecordVisitor#AttributesRecord(java.nio.ByteBuffer, int[], java.nio.IntBuffer)}</td></tr>
 *
 * @see FileSlotRecord#magic
 * @see FileSlotRecord#minor_version
 * @see FileSlotRecord#major_version
 * @see FileSlotRecord#ConstantPoolRecord
 * @see FileSlotRecord#AccessFlagsValue
 * @see FileSlotRecord#ClassIndex
 * @see FileSlotRecord#SuperClassIndex
 * @see FileSlotRecord#InterFaceTableRecord
 * @see FileSlotRecord#FieldRecord
 * @see FileSlotRecord#MethodsRecord
 * @see FileSlotRecord#AttributesRecord
 *      </table>
 */
public enum FileSlotRecord {
    magic(0x4), minor_version(0x2), major_version(0x2), ConstantPoolRecord(0x3a) {{
    subRecord = ConstantPoolRecord.class;
}}, AccessFlagsValue(0x2) {{
    subRecord = AccessFlagsValue.class;
}}, ClassIndex(0x2), SuperClassIndex(0x2), InterFaceTableRecord(0x2), FieldRecord(0x2), MethodsRecord(0x2), AttributesRecord(0x2);
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
    public static final boolean isRecord = true;
    public static final boolean isValue = false;
    public static final boolean isHeader = false;
    public static final boolean isRef = false;
    public static final boolean isInfo = false;

    /**
     * FileSlotRecord templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    FileSlotRecord(int... dimensions) {
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
        for (FileSlotRecord FileSlotRecord_ : values()) {
            String hdr = FileSlotRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            FileSlotRecord_.subIndex(src, register, stack);
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
//@@ #endFileSlotRecord