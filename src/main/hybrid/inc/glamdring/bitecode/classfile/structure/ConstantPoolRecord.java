package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;
/**************************************************************************/

/**

	<p>recordSize: 8

<table><tr><th>name</th><th>offset</th><th>size</th><th>Sub-Index</th></tr><tr><th>

ConstantPoolRef</th><td>4</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.ConstantPoolRef}</td></tr><tr><th>

subType</th><td>2</td><td>4</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

utf8Bitmap</th><td>1</td><td>6</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

tag</th><td>1</td><td>7</td><td>{@link java.nio.ByteBuffer}</td></tr> *
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord#ConstantPoolRef
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord#subType
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord#utf8Bitmap
 * @see inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord#tag
 *</table>
 */
public  enum ConstantPoolRecord{
ConstantPoolRef	{{
		recordLen=8;
		size=4;
	}}
,subType	{{
		recordLen=8;
		size=2;
		seek=4;
	}}
,utf8Bitmap	{{
		recordLen=8;
		size=1;
		seek=6;
	}}
,tag	{{
		recordLen=8;
		size=1;
		seek=7;
	}}
;
	public java.lang.Class clazz;

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

	ConstantPoolRecord()	{		 if(  isRecord &&subRecord == null) 		if (subRecord == null) {
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
		for (ConstantPoolRecord ConstantPoolRecord : values()) {
		    String hdr = ConstantPoolRecord.name();
		    System.err.println("hdr:pos " + hdr + ':' + stack.position());
		    ConstantPoolRecord.subIndex(src, register, stack);
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
//@@ #endConstantPoolRecord