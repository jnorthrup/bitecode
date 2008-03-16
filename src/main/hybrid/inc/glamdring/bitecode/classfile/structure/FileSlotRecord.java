package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;
/**************************************************************************/

/**

	<p>recordSize: 0

<table><tr><th>name</th><th>offset</th><th>size</th><th>Sub-Index</th></tr><tr><th>

magic</th><td>4</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

minor_version</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

major_version</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

ConstantPoolRecord</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord}</td></tr><tr><th>

AccessFlagsValue</th><td>2</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.AccessFlagsValue}</td></tr><tr><th>

ClassIndex</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

SuperClassIndex</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

InterFaceTableRecord</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

FieldRecord</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

MethodsRecord</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr><tr><th>

AttributesRecord</th><td>2</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr> *
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#magic
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#minor_version
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#major_version
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#ConstantPoolRecord
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#AccessFlagsValue
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#ClassIndex
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#SuperClassIndex
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#InterFaceTableRecord
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#FieldRecord
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#MethodsRecord
 * @see inc.glamdring.bitecode.classfile.structure.FileSlotRecord#AttributesRecord
 *</table>
 */
public  enum FileSlotRecord{
magic	{{
		size=4;
	}}
,minor_version	{{
		size=2;
	}}
,major_version	{{
		size=2;
	}}
,ConstantPoolRecord	{{
		size=2;
	}}
,AccessFlagsValue	{{
		size=2;
	}}
,ClassIndex	{{
		size=2;
	}}
,SuperClassIndex	{{
		size=2;
	}}
,InterFaceTableRecord	{{
		size=2;
	}}
,FieldRecord	{{
		size=2;
	}}
,MethodsRecord	{{
		size=2;
	}}
,AttributesRecord	{{
		size=2;
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

	FileSlotRecord()	{		 if(  isRecord &&subRecord == null) 		if (subRecord == null) {
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
		for (FileSlotRecord FileSlotRecord : values()) {
		    String hdr = FileSlotRecord.name();
		    System.err.println("hdr:pos " + hdr + ':' + stack.position());
		    FileSlotRecord.subIndex(src, register, stack);
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
//@@ #endFileSlotRecord