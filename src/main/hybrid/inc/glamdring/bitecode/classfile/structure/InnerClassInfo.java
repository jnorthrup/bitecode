package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> InnerClassInfoIndex</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> OuterClassInfoIndex</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> inner_nameIndex</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> AccessFlagsValue</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.AccessFlagsValue}</td></tr>
 *
 * @see inc.glamdring.bitecode.classfile.structure.InnerClassInfo#InnerClassInfoIndex
 * @see inc.glamdring.bitecode.classfile.structure.InnerClassInfo#OuterClassInfoIndex
 * @see inc.glamdring.bitecode.classfile.structure.InnerClassInfo#inner_nameIndex
 * @see inc.glamdring.bitecode.classfile.structure.InnerClassInfo#AccessFlagsValue
 * </table>
 */
public  enum InnerClassInfo{
InnerClassInfoIndex	{{
		size=2;
	}}
,OuterClassInfoIndex	{{
		size=2;
	}}
,inner_nameIndex	{{
		size=2;
	}}
,AccessFlagsValue	{{
		size=2;
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
	final static public boolean isRef=false;
	final static public boolean isInfo=true;
	InnerClassInfo()	{      
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
        for (InnerClassInfo InnerClassInfo_ : values()) {
            String hdr = InnerClassInfo_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            InnerClassInfo_.subIndex(src, register, stack);
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
//@@ #endInnerClassInfo