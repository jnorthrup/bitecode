package inc.glamdring.bitecode;
import java.nio.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> magic</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> minor_version</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> major_version</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> ConstantPoolRecord</th><td>4</td><td>0</td><td>{@link ConstantPoolRecord}</td></tr>
 * <tr><th> AccessFlagsValue</th><td>4</td><td>0</td><td>{@link AccessFlagsValue}</td></tr>
 * <tr><th> ClassIndex</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> SuperClassIndex</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> InterFaceTableRecord</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> FieldRecord</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> MethodsRecord</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> AttributesRecord</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
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
 * </table>
 */
public enum FileSlotRecord { 
magic(4),minor_version(2),major_version(2),ConstantPoolRecord(2)	{{
		subRecord= ConstantPoolRecord.class;
	}}
,AccessFlagsValue(2)	{{
		subRecord= AccessFlagsValue.class;
	}}
,ClassIndex(2),SuperClassIndex(2),InterFaceTableRecord(2),FieldRecord(2),MethodsRecord(2),AttributesRecord(2);
	public java.lang.Class clazz;

	public static int recordLen;
	final public int size;
	final public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=true;
	final static public boolean isValue=false;
	final static public boolean isHeader=false;
	final static public boolean isRef=false;
	final static public boolean isInfo=false;
	FileSlotRecord (int... dimensions) {
        seek = initRecordLen(size = (dimensions.length > 0 ? dimensions[0] : init()));
    }

    private int initRecordLen(int size) {
        int rl = recordLen;
        recordLen += init() == size ? size : size;
        return rl;
    }

    int init() {
        int size = 0;
        if (/*isRecord&&*/subRecord == null) {
            final String[] indexPrefixes = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
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
                final String[] vPrefixes = {"_", "", "$"};
                final String[] names = {name().toLowerCase(), name(),};
                if (valueClazz == null && (isRef | isValue))
                    for (int i = 0; valueClazz == null && i < vPrefixes.length; i++)
                        for (int i1 = 0; valueClazz == null && i1 < names.length; i1++)
                            if (names[i1].endsWith(vPrefixes[i]))
                                try {
                                    valueClazz = Class.forName(names[i1].replaceAll(names[i1] + vPrefixes[i], names[i1]));
                                } catch (ClassNotFoundException e) {
                                }
            }
        }
        return size;
    }
    static void index
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
                final TableRecord table = TableRecord.valueOf(subRecord.getSimpleName());
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