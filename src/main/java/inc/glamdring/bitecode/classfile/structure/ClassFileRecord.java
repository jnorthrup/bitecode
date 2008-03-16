package inc.glamdring.bitecode.classfile.structure;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 0
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> ClassResourceUri</th><td>0</td><td>0</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> FileSlotRecord</th><td>0</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.FileSlotRecord}</td></tr>
 * <tr><th> TableRecord</th><td>1572864</td><td>0</td><td>{@link inc.glamdring.bitecode.classfile.structure.TableRecord}</td></tr>
 *
 * @see inc.glamdring.bitecode.classfile.structure.ClassFileRecord#ClassResourceUri
 * @see inc.glamdring.bitecode.classfile.structure.ClassFileRecord#FileSlotRecord
 * @see inc.glamdring.bitecode.classfile.structure.ClassFileRecord#TableRecord
 * </table>
 */
public  enum ClassFileRecord{
ClassResourceUri{{size=256;}},FileSlotRecord,TableRecord;
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
	ClassFileRecord()	{
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
        for (ClassFileRecord ClassFileRecord_ : values()) {
            String hdr = ClassFileRecord_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            ClassFileRecord_.subIndex(src, register, stack);
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
//@@ #endClassFileRecord