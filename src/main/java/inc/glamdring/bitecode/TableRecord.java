package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 1572864
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> ConstantPoolRecord</td><td>0x80000</td><td>0x0</td><td>byte[]ConstantPoolRecord src.{@link java.nio.ByteBuffer#get}(0) & 0xff</td><td>{@link inc.glamdring.bitecode.ConstantPoolRecord}</td></tr>
 * <tr><td> interfaces</td><td>0x80000</td><td>0x80000</td><td>byte[]interfaces src.{@link java.nio.ByteBuffer#get}(80000) & 0xff</td><td>{@link TableRecordVisitor#interfaces(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> fields</td><td>0x80000</td><td>0xc0000</td><td>byte[]fields src.{@link java.nio.ByteBuffer#get}(c0000) & 0xff</td><td>{@link TableRecordVisitor#fields(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> methods</td><td>0x80000</td><td>0x100000</td><td>byte[]methods src.{@link java.nio.ByteBuffer#get}(100000) & 0xff</td><td>{@link TableRecordVisitor#methods(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> attributes</td><td>0x80000</td><td>0x140000</td><td>byte[]attributes src.{@link java.nio.ByteBuffer#get}(140000) & 0xff</td><td>{@link TableRecordVisitor#attributes(ByteBufferer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.TableRecord#ConstantPoolRecord
 * @see inc.glamdring.bitecode.TableRecord#interfaces
 * @see inc.glamdring.bitecode.TableRecord#fields
 * @see inc.glamdring.bitecode.TableRecord#methods
 * @see inc.glamdring.bitecode.TableRecord#attributes
 * </table>
 */
public enum TableRecord { 
ConstantPoolRecord(0x80000)	{{
		subRecord=inc.glamdring.bitecode.ConstantPoolRecord.class;
	}}
,interfaces(0x40000),fields(0x40000),methods(0x40000),attributes(0x40000);
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
	TableRecord (int... dimensions) {
        seek = initRecordLen(size = (dimensions.length > 0 ? dimensions[0] : init()));
    }

    private int initRecordLen(int size) {
        int rl = recordLen;
        final int ns = init();
        recordLen += ns == -1 ? size : ns;
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
        for (TableRecord TableRecord_ : values()) {
            String hdr = TableRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            TableRecord_.subIndex(src, register, stack);
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
//@@ #endTableRecord