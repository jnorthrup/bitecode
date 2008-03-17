package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 48
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> magic</td><td>0x4</td><td>0x0</td><td> (int) magic=src.getInt(0x0)</td><td>{@link FileSlotRecordVisitor#magic(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> minor_version</td><td>0x2</td><td>0x4</td><td> (short) minor_version=src.getShort(0x4) & 0xffff</td><td>{@link FileSlotRecordVisitor#minor_version(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> major_version</td><td>0x2</td><td>0x6</td><td> (short) major_version=src.getShort(0x6) & 0xffff</td><td>{@link FileSlotRecordVisitor#major_version(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> ConstantPoolRecord</td><td>0x26</td><td>0x8</td><td> (byte) ConstantPoolRecord=src.get(0x8) & 0xff</td><td>{@link inc.glamdring.bitecode.ConstantPoolRecord}</td></tr>
 * <tr><td> AccessFlagsValue</td><td>0xfffffff6</td><td>0x2e</td><td> (byte) AccessFlagsValue=src.get(0x2e) & 0xff</td><td>{@link inc.glamdring.bitecode.AccessFlagsValue}</td></tr>
 * <tr><td> ClassIndex</td><td>0x2</td><td>0x24</td><td> (short) ClassIndex=src.getShort(0x24) & 0xffff</td><td>{@link FileSlotRecordVisitor#ClassIndex(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> SuperClassIndex</td><td>0x2</td><td>0x26</td><td> (short) SuperClassIndex=src.getShort(0x26) & 0xffff</td><td>{@link FileSlotRecordVisitor#SuperClassIndex(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> InterFaceTableRecord</td><td>0x2</td><td>0x28</td><td> (short) InterFaceTableRecord=src.getShort(0x28) & 0xffff</td><td>{@link FileSlotRecordVisitor#InterFaceTableRecord(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> FieldRecord</td><td>0x2</td><td>0x2a</td><td> (short) FieldRecord=src.getShort(0x2a) & 0xffff</td><td>{@link FileSlotRecordVisitor#FieldRecord(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> MethodsRecord</td><td>0x2</td><td>0x2c</td><td> (short) MethodsRecord=src.getShort(0x2c) & 0xffff</td><td>{@link FileSlotRecordVisitor#MethodsRecord(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> AttributesRecord</td><td>0x2</td><td>0x2e</td><td> (short) AttributesRecord=src.getShort(0x2e) & 0xffff</td><td>{@link FileSlotRecordVisitor#AttributesRecord(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.FileSlotRecord#magic
 * @see inc.glamdring.bitecode.FileSlotRecord#minor_version
 * @see inc.glamdring.bitecode.FileSlotRecord#major_version
 * @see inc.glamdring.bitecode.FileSlotRecord#ConstantPoolRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#AccessFlagsValue
 * @see inc.glamdring.bitecode.FileSlotRecord#ClassIndex
 * @see inc.glamdring.bitecode.FileSlotRecord#SuperClassIndex
 * @see inc.glamdring.bitecode.FileSlotRecord#InterFaceTableRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#FieldRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#MethodsRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#AttributesRecord
 * </table>
 */
public enum FileSlotRecord { 
magic(0x4),minor_version(0x2),major_version(0x2),ConstantPoolRecord(0x26)	{{
		subRecord=inc.glamdring.bitecode.ConstantPoolRecord.class;
	}}
,AccessFlagsValue(0xfffffff6)	{{
		subRecord=inc.glamdring.bitecode.AccessFlagsValue.class;
	}}
,ClassIndex(0x2),SuperClassIndex(0x2),InterFaceTableRecord(0x2),FieldRecord(0x2),MethodsRecord(0x2),AttributesRecord(0x2);
	public java.lang.Class clazz;

	public static int recordLen;
	public final int size;
	public final int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	public static final boolean isRecord=true;
	public static final boolean isValue=false;
	public static final boolean isHeader=false;
	public static final boolean isRef=false;
	public static final boolean isInfo=false;
    /** FileSlotRecord templated Byte Struct 
     * @param dimensions [0]=size,[1]= forced seek
     */
	FileSlotRecord (int... dimensions) {
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
    }    static void index
            (ByteBuffer src, int[] register, IntBuffer stack) {
        for (FileSlotRecord FileSlotRecord_ : values()) {
            String hdr = FileSlotRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            FileSlotRecord_.subIndex(src, register, stack);
        }
    }

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
//@@ #endFileSlotRecord