package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 34
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> INVALID</td><td>0xffffffff</td><td>0x0</td><td> (byte) INVALID=src.get(0x0) & 0xff</td><td>{@link ConstantPoolRefVisitor#INVALID(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Utf8_</td><td>0x2</td><td>0xffffffff</td><td> (short) Utf8_=src.getShort(0xffffffff) & 0xffff</td><td>{@link inc.glamdring.bitecode.Utf8_}</td></tr>
 * <tr><td> UNUSED</td><td>0xffffffff</td><td>0x1</td><td> (byte) UNUSED=src.get(0x1) & 0xff</td><td>{@link ConstantPoolRefVisitor#UNUSED(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Integer_</td><td>0xffffffff</td><td>0x0</td><td> (byte) Integer_=src.get(0x0) & 0xff</td><td>{@link ConstantPoolRefVisitor#Integer_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Float_</td><td>0xffffffff</td><td>0xffffffff</td><td> (byte) Float_=src.get(0xffffffff) & 0xff</td><td>{@link ConstantPoolRefVisitor#Float_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Long_</td><td>0x8</td><td>0xfffffffe</td><td> (long) Long_=src.getLong(0xfffffffe)</td><td>{@link ConstantPoolRefVisitor#Long_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Double_</td><td>0x8</td><td>0x6</td><td> (long) Double_=src.getLong(0x6)</td><td>{@link ConstantPoolRefVisitor#Double_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Class_</td><td>0x2</td><td>0xe</td><td> (short) Class_=src.getShort(0xe) & 0xffff</td><td>{@link ConstantPoolRefVisitor#Class_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> String_</td><td>0x2</td><td>0x10</td><td> (short) String_=src.getShort(0x10) & 0xffff</td><td>{@link ConstantPoolRefVisitor#String_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> Field_</td><td>0x4</td><td>0x12</td><td> (int) Field_=src.getInt(0x12)</td><td>{@link inc.glamdring.bitecode.Field_}</td></tr>
 * <tr><td> Method_</td><td>0x4</td><td>0x16</td><td> (int) Method_=src.getInt(0x16)</td><td>{@link inc.glamdring.bitecode.Method_}</td></tr>
 * <tr><td> InterfaceMethod_</td><td>0x4</td><td>0x1a</td><td> (int) InterfaceMethod_=src.getInt(0x1a)</td><td>{@link inc.glamdring.bitecode.InterfaceMethod_}</td></tr>
 * <tr><td> NameAndType_</td><td>0x4</td><td>0x1e</td><td> (int) NameAndType_=src.getInt(0x1e)</td><td>{@link inc.glamdring.bitecode.NameAndType_}</td></tr>
 * 
 * @see inc.glamdring.bitecode.ConstantPoolRef#INVALID
 * @see inc.glamdring.bitecode.ConstantPoolRef#Utf8_
 * @see inc.glamdring.bitecode.ConstantPoolRef#UNUSED
 * @see inc.glamdring.bitecode.ConstantPoolRef#Integer_
 * @see inc.glamdring.bitecode.ConstantPoolRef#Float_
 * @see inc.glamdring.bitecode.ConstantPoolRef#Long_
 * @see inc.glamdring.bitecode.ConstantPoolRef#Double_
 * @see inc.glamdring.bitecode.ConstantPoolRef#Class_
 * @see inc.glamdring.bitecode.ConstantPoolRef#String_
 * @see inc.glamdring.bitecode.ConstantPoolRef#Field_
 * @see inc.glamdring.bitecode.ConstantPoolRef#Method_
 * @see inc.glamdring.bitecode.ConstantPoolRef#InterfaceMethod_
 * @see inc.glamdring.bitecode.ConstantPoolRef#NameAndType_
 * </table>
 */
public enum ConstantPoolRef { 
INVALID(0xffffffff),Utf8_(0x2)	{{
		subRecord=inc.glamdring.bitecode.Utf8_.class;
	}}
,UNUSED(0xffffffff),Integer_(0xffffffff),Float_(0xffffffff),Long_(0x8),Double_(0x8),Class_(0x2),String_(0x2),Field_(0x4)	{{
		subRecord=inc.glamdring.bitecode.Field_.class;
	}}
,Method_(0x4)	{{
		subRecord=inc.glamdring.bitecode.Method_.class;
	}}
,InterfaceMethod_(0x4)	{{
		subRecord=inc.glamdring.bitecode.InterfaceMethod_.class;
	}}
,NameAndType_(0x4)	{{
		subRecord=inc.glamdring.bitecode.NameAndType_.class;
	}}
;
	public java.lang.Class clazz;

	public static int recordLen;
	public final int size;
	public final int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	public static final boolean isRecord=false;
	public static final boolean isValue=false;
	public static final boolean isHeader=false;
	public static final boolean isRef=true;
	public static final boolean isInfo=false;
    /** ConstantPoolRef templated Byte Struct 
     * @param dimensions [0]=size,[1]= forced seek
     */
	ConstantPoolRef (int... dimensions) {
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
        for (ConstantPoolRef ConstantPoolRef_ : values()) {
            String hdr = ConstantPoolRef_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ConstantPoolRef_.subIndex(src, register, stack);
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
//@@ #endConstantPoolRef