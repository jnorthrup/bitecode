package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 1048918
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> ClassResourceUri</td><td>0x100</td><td>0x0</td><td> (byte) ClassResourceUri=src.get(0x0) & 0xff</td><td>{@link ClassFileRecordVisitor#ClassResourceUri(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> FileSlotRecord</td><td>0x30</td><td>0x100</td><td> (byte) FileSlotRecord=src.get(0x100) & 0xff</td><td>{@link inc.glamdring.bitecode.FileSlotRecord}</td></tr>
 * <tr><td> TableRecord</td><td>0x100026</td><td>0x130</td><td> (byte) TableRecord=src.get(0x130) & 0xff</td><td>{@link inc.glamdring.bitecode.TableRecord}</td></tr>
 * 
 * @see inc.glamdring.bitecode.ClassFileRecord#ClassResourceUri
 * @see inc.glamdring.bitecode.ClassFileRecord#FileSlotRecord
 * @see inc.glamdring.bitecode.ClassFileRecord#TableRecord
 * </table>
 */
public enum ClassFileRecord { 
ClassResourceUri(0x100),FileSlotRecord(0x30)	{{
		subRecord=inc.glamdring.bitecode.FileSlotRecord.class;
	}}
,TableRecord(0x100026)	{{
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
    /** ClassFileRecord templated Byte Struct 
     * @param dimensions [0]=size,[1]= forced seek
     */
	ClassFileRecord (int... dimensions) {
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