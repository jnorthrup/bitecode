package inc.glamdring.bitecode;
import java.nio.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> ClassResourceUri</th><td>256</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> FileSlotRecord</th><td>256</td><td>0</td><td>{@link FileSlotRecord}</td></tr>
 * <tr><th> TableRecord</th><td>256</td><td>0</td><td>{@link TableRecord}</td></tr>
 *
 * @see ClassFileRecord#ClassResourceUri
 * @see ClassFileRecord#FileSlotRecord
 * @see ClassFileRecord#TableRecord
 * </table>
 */
public enum ClassFileRecord { 
ClassResourceUri(0x100),FileSlotRecord	{{
		subRecord= FileSlotRecord.class;
	}}
,TableRecord(0x180000)	{{
		subRecord= TableRecord.class;
	}}
;
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
	ClassFileRecord (int... dimensions) {
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
        for (ClassFileRecord ClassFileRecord_ : values()) {
            String hdr = ClassFileRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ClassFileRecord_.subIndex(src, register, stack);
        }
    }

    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
        System.err.println(name() + ":subIndex src:stack" + src.position() + ':' + stack.position());
        int begin = src.position();
        int stackPtr = stack.position();
        stack.put(begin);
        if (isRecord && subRecord != null) { 
            try {
                final inc.glamdring.bitecode.TableRecord table =inc.glamdring.bitecode. TableRecord.valueOf(subRecord.getSimpleName());
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
//@@ #endClassFileRecord