package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 1572864
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> ConstantPoolRecord</th><td>8</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord}</td></tr>
 * <tr><th> interfaces</th><td>262144</td><td>524288</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> fields</th><td>262144</td><td>786432</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> methods</th><td>262144</td><td>1048576</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> attributes</th><td>262144</td><td>1310720</td><td>{@link java.nio.ByteBuffer}</td></tr>
 *
 * @see inc.glamdring.bitecode.classfile.structure.TableRecord#ConstantPoolRecord
 * @see inc.glamdring.bitecode.classfile.structure.TableRecord#interfaces
 * @see inc.glamdring.bitecode.classfile.structure.TableRecord#fields
 * @see inc.glamdring.bitecode.classfile.structure.TableRecord#methods
 * @see inc.glamdring.bitecode.classfile.structure.TableRecord#attributes
 * </table>
 */
public  enum TableRecord{
ConstantPoolRecord	{{
		size=524288;
	}}
,interfaces	{{
		size=262144;
		seek=524288;
	}}
,fields	{{
		size=262144;
		seek=786432;
	}}
,methods	{{
		size=262144;
		seek=1048576;
	}}
,attributes	{{
		size=262144;
		seek=1310720;
	}}
;
	public static int recordLen;
	public int size;
	public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=true;
	final static public boolean isValue=false;
	final static public boolean isHeader=false;
	final static public boolean isRef=false;
	final static public boolean isInfo=false;
	TableRecord()	{      
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
//@@ #endTableRecord