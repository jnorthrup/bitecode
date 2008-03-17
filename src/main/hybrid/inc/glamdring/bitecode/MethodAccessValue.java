package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: -9
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> public_</td><td>0xffffffff</td><td>0x0</td><td> (byte) public_=src.{@link java.nio.ByteBuffer#get}(0x0) & 0xff</td><td>{@link MethodAccessValueVisitor#public_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> private_</td><td>0xffffffff</td><td>0xffffffff</td><td> (byte) private_=src.{@link java.nio.ByteBuffer#get}(0xffffffff) & 0xff</td><td>{@link MethodAccessValueVisitor#private_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> protected_</td><td>0xffffffff</td><td>0xfffffffe</td><td> (byte) protected_=src.{@link java.nio.ByteBuffer#get}(0xfffffffe) & 0xff</td><td>{@link MethodAccessValueVisitor#protected_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> static_</td><td>0xffffffff</td><td>0xfffffffd</td><td> (byte) static_=src.{@link java.nio.ByteBuffer#get}(0xfffffffd) & 0xff</td><td>{@link MethodAccessValueVisitor#static_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> final_</td><td>0xffffffff</td><td>0xfffffffc</td><td> (byte) final_=src.{@link java.nio.ByteBuffer#get}(0xfffffffc) & 0xff</td><td>{@link MethodAccessValueVisitor#final_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> synchronized_</td><td>0xffffffff</td><td>0xfffffffb</td><td> (byte) synchronized_=src.{@link java.nio.ByteBuffer#get}(0xfffffffb) & 0xff</td><td>{@link MethodAccessValueVisitor#synchronized_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> native_</td><td>0xffffffff</td><td>0xfffffffa</td><td> (byte) native_=src.{@link java.nio.ByteBuffer#get}(0xfffffffa) & 0xff</td><td>{@link MethodAccessValueVisitor#native_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> abstract_</td><td>0xffffffff</td><td>0xfffffff9</td><td> (byte) abstract_=src.{@link java.nio.ByteBuffer#get}(0xfffffff9) & 0xff</td><td>{@link MethodAccessValueVisitor#abstract_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> strict_</td><td>0xffffffff</td><td>0xfffffff8</td><td> (byte) strict_=src.{@link java.nio.ByteBuffer#get}(0xfffffff8) & 0xff</td><td>{@link MethodAccessValueVisitor#strict_(ByteBufferer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.MethodAccessValue#public_
 * @see inc.glamdring.bitecode.MethodAccessValue#private_
 * @see inc.glamdring.bitecode.MethodAccessValue#protected_
 * @see inc.glamdring.bitecode.MethodAccessValue#static_
 * @see inc.glamdring.bitecode.MethodAccessValue#final_
 * @see inc.glamdring.bitecode.MethodAccessValue#synchronized_
 * @see inc.glamdring.bitecode.MethodAccessValue#native_
 * @see inc.glamdring.bitecode.MethodAccessValue#abstract_
 * @see inc.glamdring.bitecode.MethodAccessValue#strict_
 * </table>
 */
public enum MethodAccessValue { 
public_(0xffffffff),private_(0xffffffff),protected_(0xffffffff),static_(0xffffffff),final_(0xffffffff),synchronized_(0xffffffff),native_(0xffffffff),abstract_(0xffffffff),strict_(0xffffffff);
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
    /** MethodAccessValue templated Byte Struct 
     * @param dimensions [0]=size,[1]= forced seek
     */
	MethodAccessValue (int... dimensions) {
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
//@@ #endMethodAccessValue