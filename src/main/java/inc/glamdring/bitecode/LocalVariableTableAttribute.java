package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 8
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> Utf8Index</td><td>0x2</td><td>0x0</td><td>shortUtf8Index src.{@link java.nio.ByteBuffer#getShort}(0) & 0xffff</td><td>{@link LocalVariableTableAttributeVisitor#Utf8Index(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> AttributeLength</td><td>0x2</td><td>0x2</td><td>shortAttributeLength src.{@link java.nio.ByteBuffer#getShort}(2) & 0xffff</td><td>{@link LocalVariableTableAttributeVisitor#AttributeLength(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> local_variable_table_length</td><td>0x2</td><td>0x6</td><td>shortlocal_variable_table_length src.{@link java.nio.ByteBuffer#getShort}(6) & 0xffff</td><td>{@link LocalVariableTableAttributeVisitor#local_variable_table_length(ByteBufferer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.LocalVariableTableAttribute#Utf8Index
 * @see inc.glamdring.bitecode.LocalVariableTableAttribute#AttributeLength
 * @see inc.glamdring.bitecode.LocalVariableTableAttribute#local_variable_table_length
 * </table>
 */
public enum LocalVariableTableAttribute { 
Utf8Index(0x2),AttributeLength(0x4),local_variable_table_length(0x2);
	public java.lang.Class clazz;

	public static int recordLen;
	public final int size;
	public final int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	public static final boolean isRecord=false;
	public static final boolean isValue=false;
	public static final boolean isHeader=false;
	public static final boolean isRef=false;
	public static final boolean isInfo=false;
	LocalVariableTableAttribute (int... dimensions) {
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
        for (LocalVariableTableAttribute LocalVariableTableAttribute_ : values()) {
            String hdr = LocalVariableTableAttribute_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            LocalVariableTableAttribute_.subIndex(src, register, stack);
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
//@@ #endLocalVariableTableAttribute