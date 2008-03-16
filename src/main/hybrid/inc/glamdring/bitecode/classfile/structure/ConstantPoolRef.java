package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> INVALID</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Utf8_</th><td>0</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.Utf8_}</td></tr>
 * <tr><th> UNUSED</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Integer_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Float_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Long_</th><td>8</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Double_</th><td>8</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Class_</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> String_</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> Field_</th><td>4</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.Field_}</td></tr>
 * <tr><th> Method_</th><td>4</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.Method_}</td></tr>
 * <tr><th> InterfaceMethod_</th><td>0</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.InterfaceMethod_}</td></tr>
 * <tr><th> NameAndType_</th><td>4</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.NameAndType_}</td></tr>
 *
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#INVALID
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Utf8_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#UNUSED
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Integer_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Float_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Long_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Double_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Class_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#String_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Field_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#Method_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#InterfaceMethod_
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRef#NameAndType_
 * </table>
 */
public  enum ConstantPoolRef{
INVALID,Utf8_,UNUSED,Integer_,Float_,Long_	{{
		size=8;
	}}
,Double_	{{
		size=8;
	}}
,Class_	{{
		size=2;
	}}
,String_	{{
		size=2;
	}}
,Field_	{{
		size=4;
	}}
,Method_,InterfaceMethod_,NameAndType_	{{
		size=4;
	}}
;
	public java.lang.Class clazz;

	public static int recordLen;
	public int size;
	public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=false;
	final static public boolean isValue=false;
	final static public boolean isHeader=false;
	final static public boolean isRef=true;
	final static public boolean isInfo=false;
	ConstantPoolRef()	{      
            init();
            if (subRecord == null) {
            final String[] strings = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
            for (String string : strings) {
                try {
                    subRecord = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + string);
                    try {
                        size = subRecord.getField("recordLen").getInt(null);
                    } catch (IllegalAccessException e) {
                    } catch (NoSuchFieldException e) {
                    }
                    break;
                } catch (ClassNotFoundException
                        e) {
                }
            }
        }
    }

    void init() {
        seek = recordLen;
        recordLen += size;
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
                final inc.glamdring.bitecode.classfile.structure.TableRecord table = inc.glamdring.bitecode.classfile.structure.TableRecord.valueOf(subRecord.getSimpleName());
                if (table != null) {
                    //stow the original location
                    int mark = stack.position();
                    stack.position((register[ClassFileRecord.TableRecord.ordinal()] + table.seek) / 4);
                    final Method method = subRecord.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class);
                    //resume the lower stack activities
                    stack.position(mark);
                }
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }
    }}
//@@ #endConstantPoolRef