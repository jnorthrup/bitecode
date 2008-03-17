package inc.glamdring.bitecode;
import java.nio.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> INVALID</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Utf8_</th><td>0</td><td>0</td><td>{@link Utf8_}</td></tr>
 * <tr><th> UNUSED</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Integer_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Float_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Long_</th><td>8</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Double_</th><td>8</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Class_</th><td>8</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> String_</th><td>8</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Field_</th><td>8</td><td>0</td><td>{@link Field_}</td></tr>
 * <tr><th> Method_</th><td>8</td><td>0</td><td>{@link Method_}</td></tr>
 * <tr><th> InterfaceMethod_</th><td>8</td><td>0</td><td>{@link InterfaceMethod_}</td></tr>
 * <tr><th> NameAndType_</th><td>8</td><td>0</td><td>{@link NameAndType_}</td></tr>
 *
 * @see ConstantPoolRef#INVALID
 * @see ConstantPoolRef#Utf8_
 * @see ConstantPoolRef#UNUSED
 * @see ConstantPoolRef#Integer_
 * @see ConstantPoolRef#Float_
 * @see ConstantPoolRef#Long_
 * @see ConstantPoolRef#Double_
 * @see ConstantPoolRef#Class_
 * @see ConstantPoolRef#String_
 * @see ConstantPoolRef#Field_
 * @see ConstantPoolRef#Method_
 * @see ConstantPoolRef#InterfaceMethod_
 * @see ConstantPoolRef#NameAndType_
 * </table>
 */
public enum ConstantPoolRef { 
INVALID,Utf8_	{{
		subRecord= Utf8_.class;
	}}
,UNUSED,Integer_,Float_,Long_(8),Double_(8),Class_(2),String_(2),Field_(4)	{{
		subRecord= Field_.class;
	}}
,Method_	{{
		subRecord= Method_.class;
	}}
,InterfaceMethod_	{{
		subRecord= InterfaceMethod_.class;
	}}
,NameAndType_(4)	{{
		subRecord= NameAndType_.class;
	}}
;
	public java.lang.Class clazz;

	public static int recordLen;
	final public int size;
	final public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=false;
	final static public boolean isValue=false;
	final static public boolean isHeader=false;
	final static public boolean isRef=true;
	final static public boolean isInfo=false;
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
                final TableRecord table = TableRecord.valueOf(subRecord.getSimpleName());
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