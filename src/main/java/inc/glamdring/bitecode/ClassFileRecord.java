package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 1573144
 * <table><tr>
 * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><td> ClassResourceUri</td><td>256</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><td> FileSlotRecord</td><td>256</td><td>256</td><td>{@link inc.glamdring.bitecode.FileSlotRecord}</td></tr>
 * <tr><td> TableRecord</td><td>256</td><td>280</td><td>{@link inc.glamdring.bitecode.TableRecord}</td></tr>
 *
 * @see inc.glamdring.bitecode.ClassFileRecord#ClassResourceUri
 * @see inc.glamdring.bitecode.ClassFileRecord#FileSlotRecord
 * @see inc.glamdring.bitecode.ClassFileRecord#TableRecord
 * </table>
 */
public enum ClassFileRecord { 
ClassResourceUri(0x100),FileSlotRecord(0x18)	{{
		subRecord=inc.glamdring.bitecode.FileSlotRecord.class;
	}}
,TableRecord(0x180000)	{{
		subRecord=inc.glamdring.bitecode.TableRecord.class;
	}}
;
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
        if (subRecord == null) {
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

            }
        }

        for (String vPrefixe1 : new String[]{"_", "", "$"}) {
            if (valueClazz != null) break;
            String vPrefixe = vPrefixe1;
            for (String name1 : new String[]{name().toLowerCase(), name(),}) {
                if (valueClazz != null) break;
                final String trailName = name1;
                if (trailName.endsWith(vPrefixe))
                    for (String aPackage1 : new String[]{"",
                           getClass().getPackage().getName() + ".",
                           "java.lang.",
                           "java.util.",
                    }) {
                        if (valueClazz != null) break;

                        try {
                            valueClazz = Class.forName(aPackage1 + "." + trailName.replaceAll(trailName + vPrefixe, trailName));
                        } catch (ClassNotFoundException e) {
                        }
                    }
            }
        }
        return size;
    }    static void index
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
//@@ #endClassFileRecord