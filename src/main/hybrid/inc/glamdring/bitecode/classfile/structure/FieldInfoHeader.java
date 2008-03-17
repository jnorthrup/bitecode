package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 6
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> AccessFlagsValue</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.AccessFlagsValue}</td></tr>
 * <tr><th> Utf8Index</th><td>2</td><td>2</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> DescriptorIndex</th><td>2</td><td>4</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> attributes_count</th><td>2</td><td>4</td><td>{@link java.nio.ByteBuffer}</td></tr>
 *
 * @see inc.glamdring.bitecode.classfile.structure.FieldInfoHeader#AccessFlagsValue
 * @see inc.glamdring.bitecode.classfile.structure.FieldInfoHeader#Utf8Index
 * @see inc.glamdring.bitecode.classfile.structure.FieldInfoHeader#DescriptorIndex
 * @see inc.glamdring.bitecode.classfile.structure.FieldInfoHeader#attributes_count
 * </table>
 */
public enum FieldInfoHeader { 
AccessFlagsValue(2)	{{
		subRecord=inc.glamdring.bitecode.classfile.structure.AccessFlagsValue.class;
	}}
,Utf8Index(2),DescriptorIndex,attributes_count(2);
	public java.lang.Class clazz;

	public static int recordLen;
	final public int size;
	final public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=false;
	final static public boolean isValue=false;
	final static public boolean isHeader=true;
	final static public boolean isRef=false;
	final static public boolean isInfo=false;
	FieldInfoHeader (int... dimensions) {
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
        for (FieldInfoHeader FieldInfoHeader_ : values()) {
            String hdr = FieldInfoHeader_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            FieldInfoHeader_.subIndex(src, register, stack);
        }
    }

    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        System.err.println(name() + ":subIndex src:stack" + src.position() + ':' + stack.position());
        int begin = src.position();
        int stackPtr = stack.position();
        stack.put(begin);
        if (isRecord && subRecord != null) { 
            try {
                final inc.glamdring.bitecode.classfile.structure.TableRecord table = inc.glamdring.bitecode.classfile.structure.TableRecord.valueOf(subRecord.getSimpleName());
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
//@@ #endFieldInfoHeader