package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: -10
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> public_</td><td>0xffffffff</td><td>0x0</td><td> (byte) public_=src.get(0x0) & 0xff</td><td>{@link AccessFlagsValueVisitor#public_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> private_</td><td>0xffffffff</td><td>0xffffffff</td><td> (byte) private_=src.get(0xffffffff) & 0xff</td><td>{@link AccessFlagsValueVisitor#private_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> protected_</td><td>0xffffffff</td><td>0xfffffffe</td><td> (byte) protected_=src.get(0xfffffffe) & 0xff</td><td>{@link AccessFlagsValueVisitor#protected_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> static_</td><td>0xffffffff</td><td>0xfffffffd</td><td> (byte) static_=src.get(0xfffffffd) & 0xff</td><td>{@link AccessFlagsValueVisitor#static_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> final_</td><td>0xffffffff</td><td>0xfffffffc</td><td> (byte) final_=src.get(0xfffffffc) & 0xff</td><td>{@link AccessFlagsValueVisitor#final_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> super_</td><td>0xffffffff</td><td>0xfffffffb</td><td> (byte) super_=src.get(0xfffffffb) & 0xff</td><td>{@link AccessFlagsValueVisitor#super_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> volatile_</td><td>0xffffffff</td><td>0xfffffffa</td><td> (byte) volatile_=src.get(0xfffffffa) & 0xff</td><td>{@link AccessFlagsValueVisitor#volatile_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> transient_</td><td>0xffffffff</td><td>0xfffffff9</td><td> (byte) transient_=src.get(0xfffffff9) & 0xff</td><td>{@link AccessFlagsValueVisitor#transient_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> interface_</td><td>0xffffffff</td><td>0xfffffff8</td><td> (byte) interface_=src.get(0xfffffff8) & 0xff</td><td>{@link AccessFlagsValueVisitor#interface_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * <tr><td> abstract_</td><td>0xffffffff</td><td>0xfffffff7</td><td> (byte) abstract_=src.get(0xfffffff7) & 0xff</td><td>{@link AccessFlagsValueVisitor#abstract_(ByteBuffer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.AccessFlagsValue#public_
 * @see inc.glamdring.bitecode.AccessFlagsValue#private_
 * @see inc.glamdring.bitecode.AccessFlagsValue#protected_
 * @see inc.glamdring.bitecode.AccessFlagsValue#static_
 * @see inc.glamdring.bitecode.AccessFlagsValue#final_
 * @see inc.glamdring.bitecode.AccessFlagsValue#super_
 * @see inc.glamdring.bitecode.AccessFlagsValue#volatile_
 * @see inc.glamdring.bitecode.AccessFlagsValue#transient_
 * @see inc.glamdring.bitecode.AccessFlagsValue#interface_
 * @see inc.glamdring.bitecode.AccessFlagsValue#abstract_
 * </table>
 */
public enum AccessFlagsValue { 
public_(0xffffffff)	{{
		flag=1;
	}}
,private_(0xffffffff)	{{
		flag=2;
	}}
,protected_(0xffffffff)	{{
		flag=4;
	}}
,static_(0xffffffff)	{{
		flag=8;
	}}
,final_(0xffffffff)	{{
		flag=16;
	}}
,super_(0xffffffff)	{{
		flag=32;
	}}
,volatile_(0xffffffff)	{{
		flag=64;
	}}
,transient_(0xffffffff)	{{
		flag=128;
	}}
,interface_(0xffffffff)	{{
		flag=512;
	}}
,abstract_(0xffffffff)	{{
		flag=1024;
	}}
;
	public int flag;

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
    /** AccessFlagsValue templated Byte Struct 
     * @param dimensions [0]=size,[1]= forced seek
     */
	AccessFlagsValue (int... dimensions) {
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
        for (AccessFlagsValue AccessFlagsValue_ : values()) {
            String hdr = AccessFlagsValue_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            AccessFlagsValue_.subIndex(src, register, stack);
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
//@@ #endAccessFlagsValue