package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 8
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> AccessFlagsValue</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.AccessFlagsValue}</td></tr>
 * <tr><th> Utf8Index</th><td>2</td><td>2</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> DescriptorIndex</th><td>2</td><td>4</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> AttributeCount</th><td>2</td><td>6</td><td>{@link java.nio.ByteBuffer}</td></tr>
 *
 * @see inc.glamdring.bitecode.MethodInfo#AccessFlagsValue
 * @see inc.glamdring.bitecode.MethodInfo#Utf8Index
 * @see inc.glamdring.bitecode.MethodInfo#DescriptorIndex
 * @see inc.glamdring.bitecode.MethodInfo#AttributeCount
 * </table>
 */
public enum MethodInfo { 
AccessFlagsValue(0x2)	{{
		subRecord=inc.glamdring.bitecode.AccessFlagsValue.class;
	}}
,Utf8Index(0x2),DescriptorIndex(0x2),AttributeCount(0x2);
	public static int recordLen;
	final public int size;
	final public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=false;
	final static public boolean isValue=false;
	final static public boolean isHeader=false;
	final static public boolean isRef=false;
	final static public boolean isInfo=true;
	MethodInfo (int... dimensions) {
        seek = initRecordLen(size = (dimensions.length > 0 ? dimensions[0] : init()));
    }

    private int initRecordLen(int size) {
        int rl = recordLen;
        recordLen += init() == size ? size : size;
        return rl;
    }

    int init() {
        int size = 0;
        if ( subRecord == null) {
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
        for (MethodInfo MethodInfo_ : values()) {
            String hdr = MethodInfo_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            MethodInfo_.subIndex(src, register, stack);
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
//@@ #endMethodInfo