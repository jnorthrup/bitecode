package inc.glamdring.bitecode;
import java.nio.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> Utf8Index</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> AttributeLength</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> MaxStack</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> MaxLocals</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> CodeLength</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> ExceptionTableLength</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> attributes_count</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 *
 * @see CodeAttributeHeader#Utf8Index
 * @see CodeAttributeHeader#AttributeLength
 * @see CodeAttributeHeader#MaxStack
 * @see CodeAttributeHeader#MaxLocals
 * @see CodeAttributeHeader#CodeLength
 * @see CodeAttributeHeader#ExceptionTableLength
 * @see CodeAttributeHeader#attributes_count
 * </table>
 */
public enum CodeAttributeHeader { 
Utf8Index(2),AttributeLength(4),MaxStack(2),MaxLocals(2),CodeLength(4),ExceptionTableLength(2),attributes_count(2);
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
	CodeAttributeHeader (int... dimensions) {
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
        for (CodeAttributeHeader CodeAttributeHeader_ : values()) {
            String hdr = CodeAttributeHeader_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            CodeAttributeHeader_.subIndex(src, register, stack);
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
//@@ #endCodeAttributeHeader