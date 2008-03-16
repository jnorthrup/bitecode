package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;
/**************************************************************************/

/**

	<p>recordSize: 0

<table><tr><th>name</th><th>offset</th><th>size</th><th>Sub-Index</th></tr><tr><th>

AccessFlagsValue</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.AccessFlagsValue}</td></tr><tr><th>

Utf8Index</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

DescriptorIndex</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

AttributeCount</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr> *
 * @see inc.glamdring.bitecode.classfile.structure.MethodInfo#AccessFlagsValue
 * @see inc.glamdring.bitecode.classfile.structure.MethodInfo#Utf8Index
 * @see inc.glamdring.bitecode.classfile.structure.MethodInfo#DescriptorIndex
 * @see inc.glamdring.bitecode.classfile.structure.MethodInfo#AttributeCount
 *</table>
 */
public  enum MethodInfo{
AccessFlagsValue	{{
		size=2;
	}}
,Utf8Index	{{
		size=2;
	}}
,DescriptorIndex	{{
		size=2;
	}}
,AttributeCount	{{
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

	MethodInfo()	{		 if(  isRecord &&subRecord == null) 		if (subRecord == null) {
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
		for (MethodInfo MethodInfo : values()) {
		    String hdr = MethodInfo.name();
		    System.err.println("hdr:pos " + hdr + ':' + stack.position());
		    MethodInfo.subIndex(src, register, stack);
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
//@@ #endMethodInfo