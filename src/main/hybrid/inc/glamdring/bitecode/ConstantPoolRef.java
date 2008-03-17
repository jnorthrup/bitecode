package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 38
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> INVALID</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Utf8_</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.Utf8_}</td></tr>
 * <tr><th> UNUSED</th><td>2</td><td>2</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Integer_</th><td>2</td><td>2</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Float_</th><td>2</td><td>2</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Long_</th><td>2</td><td>2</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Double_</th><td>2</td><td>10</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Class_</th><td>2</td><td>18</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> String_</th><td>2</td><td>20</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Field_</th><td>2</td><td>22</td><td>{@link inc.glamdring.bitecode.Field_}</td></tr>
 * <tr><th> Method_</th><td>2</td><td>26</td><td>{@link inc.glamdring.bitecode.Method_}</td></tr>
 * <tr><th> InterfaceMethod_</th><td>2</td><td>30</td><td>{@link inc.glamdring.bitecode.InterfaceMethod_}</td></tr>
 * <tr><th> NameAndType_</th><td>2</td><td>34</td><td>{@link inc.glamdring.bitecode.NameAndType_}</td></tr>
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
INVALID,Utf8_(0x2)	{{
		subRecord=inc.glamdring.bitecode.Utf8_.class;
	}}
,UNUSED,Integer_,Float_,Long_(0x8),Double_(0x8),Class_(0x2),String_(0x2),Field_(0x4)	{{
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
	public static final boolean isRef=false;
	public static final boolean isInfo=false;
	ConstantPoolRef (int... dimensions) {
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