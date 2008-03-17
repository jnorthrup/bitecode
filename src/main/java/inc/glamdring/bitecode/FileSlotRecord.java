package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 24
 * <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td> magic</td><td>0x4</td><td>0x0</td><td>intmagic src.{@link java.nio.ByteBuffer#getInt}(0)</td><td>{@link FileSlotRecordVisitor#magic(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> minor_version</td><td>0x4</td><td>0x4</td><td>intminor_version src.{@link java.nio.ByteBuffer#getInt}(4)</td><td>{@link FileSlotRecordVisitor#minor_version(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> major_version</td><td>0x4</td><td>0x6</td><td>intmajor_version src.{@link java.nio.ByteBuffer#getInt}(6)</td><td>{@link FileSlotRecordVisitor#major_version(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> ConstantPoolRecord</td><td>0x4</td><td>0x8</td><td>intConstantPoolRecord src.{@link java.nio.ByteBuffer#getInt}(8)</td><td>{@link inc.glamdring.bitecode.ConstantPoolRecord}</td></tr>
 * <tr><td> AccessFlagsValue</td><td>0x4</td><td>0xa</td><td>intAccessFlagsValue src.{@link java.nio.ByteBuffer#getInt}(a)</td><td>{@link inc.glamdring.bitecode.AccessFlagsValue}</td></tr>
 * <tr><td> ClassIndex</td><td>0x4</td><td>0xc</td><td>intClassIndex src.{@link java.nio.ByteBuffer#getInt}(c)</td><td>{@link FileSlotRecordVisitor#ClassIndex(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> SuperClassIndex</td><td>0x4</td><td>0xe</td><td>intSuperClassIndex src.{@link java.nio.ByteBuffer#getInt}(e)</td><td>{@link FileSlotRecordVisitor#SuperClassIndex(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> InterFaceTableRecord</td><td>0x4</td><td>0x10</td><td>intInterFaceTableRecord src.{@link java.nio.ByteBuffer#getInt}(10)</td><td>{@link FileSlotRecordVisitor#InterFaceTableRecord(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> FieldRecord</td><td>0x4</td><td>0x12</td><td>intFieldRecord src.{@link java.nio.ByteBuffer#getInt}(12)</td><td>{@link FileSlotRecordVisitor#FieldRecord(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> MethodsRecord</td><td>0x4</td><td>0x14</td><td>intMethodsRecord src.{@link java.nio.ByteBuffer#getInt}(14)</td><td>{@link FileSlotRecordVisitor#MethodsRecord(ByteBufferer, int[], IntBuffer)}</td></tr>
 * <tr><td> AttributesRecord</td><td>0x4</td><td>0x16</td><td>intAttributesRecord src.{@link java.nio.ByteBuffer#getInt}(16)</td><td>{@link FileSlotRecordVisitor#AttributesRecord(ByteBufferer, int[], IntBuffer)}</td></tr>
 * 
 * @see inc.glamdring.bitecode.FileSlotRecord#magic
 * @see inc.glamdring.bitecode.FileSlotRecord#minor_version
 * @see inc.glamdring.bitecode.FileSlotRecord#major_version
 * @see inc.glamdring.bitecode.FileSlotRecord#ConstantPoolRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#AccessFlagsValue
 * @see inc.glamdring.bitecode.FileSlotRecord#ClassIndex
 * @see inc.glamdring.bitecode.FileSlotRecord#SuperClassIndex
 * @see inc.glamdring.bitecode.FileSlotRecord#InterFaceTableRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#FieldRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#MethodsRecord
 * @see inc.glamdring.bitecode.FileSlotRecord#AttributesRecord
 * </table>
 */
public enum FileSlotRecord { 
magic(0x4),minor_version(0x2),major_version(0x2),ConstantPoolRecord(0x2)	{{
		subRecord=inc.glamdring.bitecode.ConstantPoolRecord.class;
	}}
,AccessFlagsValue(0x2)	{{
		subRecord=inc.glamdring.bitecode.AccessFlagsValue.class;
	}}
,ClassIndex(0x2),SuperClassIndex(0x2),InterFaceTableRecord(0x2),FieldRecord(0x2),MethodsRecord(0x2),AttributesRecord(0x2);
	public java.lang.Class clazz;

	public static int recordLen;
	public final int size;
	public final int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	public static final boolean isRecord=true;
	public static final boolean isValue=false;
	public static final boolean isHeader=false;
	public static final boolean isRef=false;
	public static final boolean isInfo=false;
	FileSlotRecord (int... dimensions) {
        seek = initRecordLen(size = (dimensions.length > 0 ? dimensions[0] : init()));
    }

    private int initRecordLen(int size) {
        int rl = recordLen;
        final int ns = init();
        recordLen += ns == -1 ? size : ns;
        return rl;
    }
    int init() {
        int size = 0;
        if (subRecord == null) {
            final String[] indexPrefixes = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
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

        for (String vPrefixe1 : new String[]{"_", "", "$"}) {
            if (valueClazz != null) break;
            String vPrefixe = vPrefixe1;
            for (String name1 : new String[]{name().toLowerCase(), name(),}) {
                if (valueClazz != null) break;
                final String trailName = name1;
                if (trailName.endsWith(vPrefixe))
                    for (String aPackage1 : new String[]{"",
                           getClass().getPackage().getName() + ".",
                           "java.lang.",
                           "java.util.",
                    }) {
                        if (valueClazz != null) break;

                        try {
                            valueClazz = Class.forName(aPackage1 + "." + trailName.replaceAll(trailName + vPrefixe, trailName));
                        } catch (ClassNotFoundException e) {
                        }
                    }
            }
        }
        return size;
    }    static void index
            (ByteBuffer src, int[] register, IntBuffer stack) {
        for (FileSlotRecord FileSlotRecord_ : values()) {
            String hdr = FileSlotRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            FileSlotRecord_.subIndex(src, register, stack);
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
//@@ #endFileSlotRecord