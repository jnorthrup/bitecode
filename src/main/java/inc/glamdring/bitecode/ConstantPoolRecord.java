package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 8
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> ConstantPoolRef</td><td>0x4</td><td>0x0</td><td> (int) ConstantPoolRef=src.getInt(0x0)</td><td>{@link inc.glamdring.bitecode.ConstantPoolRef}</td></tr>
 * <tr><td> subType</td><td>0x2</td><td>0x4</td><td> (short) subType=src.getShort(0x4) & 0xffff</td><td>{@link ConstantPoolRecordVisitor#subType(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> utf8Bitmap</td><td>0x1</td><td>0x6</td><td> (byte) utf8Bitmap=src.get(0x6) & 0xff</td><td>{@link ConstantPoolRecordVisitor#utf8Bitmap(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> tag</td><td>0x1</td><td>0x7</td><td> (byte) tag=src.get(0x7) & 0xff</td><td>{@link ConstantPoolRecordVisitor#tag(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.ConstantPoolRecord#ConstantPoolRef
 * @see inc.glamdring.bitecode.ConstantPoolRecord#subType
 * @see inc.glamdring.bitecode.ConstantPoolRecord#utf8Bitmap
 * @see inc.glamdring.bitecode.ConstantPoolRecord#tag
 * </table>
 */
public enum ConstantPoolRecord { 
ConstantPoolRef(0x4)	{{
		subRecord=inc.glamdring.bitecode.ConstantPoolRef.class;
	}}
,subType(0x2),utf8Bitmap(0x1),tag(0x1);
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
    /** ConstantPoolRecord templated Byte Struct 
     * @param dimensions [0]=size,[1]= forced seek
     */
	ConstantPoolRecord (int... dimensions) {
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
        for (ConstantPoolRecord ConstantPoolRecord_ : values()) {
            String hdr = ConstantPoolRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ConstantPoolRecord_.subIndex(src, register, stack);
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
//@@ #endConstantPoolRecord