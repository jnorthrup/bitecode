package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 8
 * <table><tr>
 * <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr> * <tr><td> start_pc</td><td>2</td><td>0</td><td>short start_pc src.{@link java.nio.ByteBuffer#getShort}(0) & 0xffff</td><td>{@link ExceptionValueVisitor#start_pc(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> end_pc</td><td>2</td><td>2</td><td>short end_pc src.{@link java.nio.ByteBuffer#getShort}(2) & 0xffff</td><td>{@link ExceptionValueVisitor#end_pc(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> handler_pc</td><td>2</td><td>4</td><td>short handler_pc src.{@link java.nio.ByteBuffer#getShort}(4) & 0xffff</td><td>{@link ExceptionValueVisitor#handler_pc(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> catch_type</td><td>2</td><td>6</td><td>short catch_type src.{@link java.nio.ByteBuffer#getShort}(6) & 0xffff</td><td>{@link ExceptionValueVisitor#catch_type(ByteBufferer, int[], IntBuffer)}</td></tr>
 *
 * @see inc.glamdring.bitecode.ExceptionValue#start_pc
 * @see inc.glamdring.bitecode.ExceptionValue#end_pc
 * @see inc.glamdring.bitecode.ExceptionValue#handler_pc
 * @see inc.glamdring.bitecode.ExceptionValue#catch_type
 * </table>
 */
public enum ExceptionValue { 
start_pc(0x2),end_pc(0x2),handler_pc(0x2),catch_type(0x2);
	public java.lang.Class clazz;

	public static int recordLen;
	public final int size;
	public final int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	public static final boolean isRecord=false;
	public static final boolean isValue=true;
	public static final boolean isHeader=false;
	public static final boolean isRef=false;
	public static final boolean isInfo=false;
	ExceptionValue (int... dimensions) {
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
        for (ExceptionValue ExceptionValue_ : values()) {
            String hdr = ExceptionValue_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ExceptionValue_.subIndex(src, register, stack);
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
//@@ #endExceptionValue