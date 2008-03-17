package inc.glamdring.bitecode;
import java.nio.*;
import java.lang.reflect.*;

/**
 	<p>recordSize: 1572864
 * <table><tr> * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr> * <tr><th> ConstantPoolRecord</th><td>524288</td><td>0</td><td>{@link inc.glamdring.bitecode.ConstantPoolRecord}</td></tr>
 * <tr><th> interfaces</th><td>524288</td><td>524288</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> fields</th><td>524288</td><td>786432</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> methods</th><td>524288</td><td>1048576</td><td>{@link java.nio.ByteBuffer}</td></tr>
 * <tr><th> attributes</th><td>524288</td><td>1310720</td><td>{@link java.nio.ByteBuffer}</td></tr>
 *
 * @see inc.glamdring.bitecode.TableRecord#ConstantPoolRecord
 * @see inc.glamdring.bitecode.TableRecord#interfaces
 * @see inc.glamdring.bitecode.TableRecord#fields
 * @see inc.glamdring.bitecode.TableRecord#methods
 * @see inc.glamdring.bitecode.TableRecord#attributes
 * </table>
 */
public enum TableRecord { 
ConstantPoolRecord(0x80000)	{{
		subRecord=inc.glamdring.bitecode.ConstantPoolRecord.class;
	}}
,interfaces(0x40000),fields(0x40000),methods(0x40000),attributes(0x40000);
	public static int recordLen;
	final public int size;
	final public int seek;
	public Class<? extends Enum> subRecord;
	public java.lang.Class valueClazz;
	TableRecord (int... dimensions) {
        seek = initRecordLen(size = (dimensions.length > 0 ? dimensions[0] : dimensions[0]   ));
    }

    private int initRecordLen(int size) {
        int rl = recordLen;
        recordLen += size;
        return rl;
    }}
