package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> public_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> private_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> protected_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> static_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> final_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> synchronized_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> native_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> abstract_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> strict_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 *
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#public_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#private_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#protected_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#static_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#final_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#synchronized_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#native_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#abstract_
 * @see inc.glamdring.bitecode.classfile.structure.MethodAccessValue#strict_
 * </table>
 */
public  enum MethodAccessValue{
public_,private_,protected_,static_,final_,synchronized_,native_,abstract_,strict_;
	public static int recordLen;
	public int size;
	public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	final static public boolean isRecord=false;
	final static public boolean isValue=true;
	final static public boolean isHeader=false;
	final static public boolean isRef=false;
	final static public boolean isInfo=false;
	MethodAccessValue()	{      
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
        for (MethodAccessValue MethodAccessValue_ : values()) {
            String hdr = MethodAccessValue_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            MethodAccessValue_.subIndex(src, register, stack);
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
//@@ #endMethodAccessValue