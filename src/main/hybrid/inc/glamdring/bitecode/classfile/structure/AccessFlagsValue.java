package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;
/**************************************************************************/

/**

	<p>recordSize: 0

<table><tr><th>name</th><th>offset</th><th>size</th><th>Sub-Index</th></tr><tr><th>

public_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

private_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

protected_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

static_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

final_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

super_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

volatile_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

transient_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

interface_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

abstract_</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr> *
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#public_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#private_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#protected_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#static_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#final_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#super_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#volatile_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#transient_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#interface_
 * @see inc.glamdring.bitecode.classfile.structure.AccessFlagsValue#abstract_
 *</table>
 */
public  enum AccessFlagsValue{
public_	{{
		flag=1;
	}}
,private_	{{
		flag=2;
	}}
,protected_	{{
		flag=4;
	}}
,static_	{{
		flag=8;
	}}
,final_	{{
		flag=16;
	}}
,super_	{{
		flag=32;
	}}
,volatile_	{{
		flag=64;
	}}
,transient_	{{
		flag=128;
	}}
,interface_	{{
		flag=512;
	}}
,abstract_	{{
		flag=1024;
	}}
;
	public int flag;

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

	AccessFlagsValue()	{		 if(  isRecord &&subRecord == null) 		if (subRecord == null) {
		    try {
		        subRecord =   Class.forName(getClass().getPackage() +'.'+ name()) 		        size=subRecord.getField("recordLen").getInt(null);
		    } catch ( Exception e) {
		    }
		init();
	}}

	void init() {
		seek = recordLen;
		recordLen += size;
	}

	static void index(ByteBuffer src, int[] register, IntBuffer stack) {
		for (AccessFlagsValue AccessFlagsValue : values()) {
		    String hdr = AccessFlagsValue.name();
		    System.err.println("hdr:pos " + hdr + ':' + stack.position());
		    AccessFlagsValue.subIndex(src, register, stack);
		}
	}

	private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {
				   System.err.println(name()+":subIndex src:stack" + src.position()+ ':' + stack.position());

		 int begin = src.position();
		 int stackPtr = stack.position();
		stack.put(begin);

		if (isRecord&&subRecord != null) {
//            if(subRecord!=inc.glamdring.bitecode.classfile.structure.TableRecord)
//            else
		    try {
		        final inc.glamdring.bitecode.classfile.structure.TableRecord table = TableRecord.valueOf(subRecord.getSimpleName());
		        if (table != null) {
		            //stow the original location
		            int mark = stack.position();
		            stack.position((register[ClassFileRecord.TableRecord.ordinal()] + table.seek)/4);
		            final Method method = subRecord.getMethod("index", ByteBuffer.class, int[].class, IntBuffer.class);
		            //resume the lower stack activities
		            stack.position(mark);
				}
			}catch ( Exception e) {
				throw new Error(e.getMessage());
			}
		}
	}
}
//@@ #endAccessFlagsValue