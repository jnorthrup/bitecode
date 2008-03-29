package inc.glamdring.bitecode;

import java.nio.*;
import java.lang.reflect.*;

/**
 * <p>recordSize: 8 <table><tr> <th>name</th><th>size</th><th>seek</th><th>Value Class</th><th>Sub-Index</th></tr>
 * <tr><td>AccessFlagsValue</td><td>0x2</td><td>0x0</td><td> (short) AccessFlagsValue=src.getShort(0x0) &
 * 0xffff</td><td>{@link inc.glamdring.bitecode.AccessFlagsValue}</td></tr> <tr><td>Utf8Index</td><td>0x2</td><td>0x2</td><td>
 * (short) Utf8Index=src.getShort(0x2) & 0xffff</td><td>{@link FieldInfoHeaderVisitor#Utf8Index(ByteBuffer, int[],
 * IntBuffer)}</td></tr> <tr><td>DescriptorIndex</td><td>0x2</td><td>0x4</td><td> (short)
 * DescriptorIndex=src.getShort(0x4) & 0xffff</td><td>{@link FieldInfoHeaderVisitor#DescriptorIndex(ByteBuffer, int[],
 * IntBuffer)}</td></tr> <tr><td>attributes_count</td><td>0x2</td><td>0x6</td><td> (short)
 * attributes_count=src.getShort(0x6) & 0xffff</td><td>{@link FieldInfoHeaderVisitor#attributes_count(ByteBuffer, int[],
 * IntBuffer)}</td></tr>
 *
 * @see inc.glamdring.bitecode.FieldInfoHeader#AccessFlagsValue
 * @see inc.glamdring.bitecode.FieldInfoHeader#Utf8Index
 * @see inc.glamdring.bitecode.FieldInfoHeader#DescriptorIndex
 * @see inc.glamdring.bitecode.FieldInfoHeader#attributes_count </table>
 */
public enum FieldInfoHeader {
    AccessFlagsValue(0x2) {{
        subRecord = inc.glamdring.bitecode.AccessFlagsValue.class;
    }}, Utf8Index(0x2), DescriptorIndex(0x2), attributes_count(0x2);
    public java.lang.Class clazz;

    /**
     * the length of one record
     */
    public static int recordLen;
    /**
     * the size per field, if any
     */
    public final int size;
    /**
     * the offset from record-start of the field
     */
    public final int seek;
    /**
     * a delegate class wihch will perform sub-indexing on behalf of a field once it has marked its initial stating offset
     * into the stack.
     */
    public Class<? extends Enum> subRecord;
    /**
     * a hint class for bean-wrapper access to data contained.
     */
    public Class valueClazz;
    public static final boolean isRecord = false;
    public static final boolean isValue = false;
    public static final boolean isHeader = true;
    public static final boolean isRef = false;
    public static final boolean isInfo = false;

    /**
     * FieldInfoHeader templated Byte Struct
     *
     * @param dimensions [0]=size,[1]= forced seek
     */
    FieldInfoHeader(int... dimensions) {
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
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();  //todo: verify for a purpose
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();  //todo: verify for a purpose
                    } catch (SecurityException e) {
                        e.printStackTrace();  //todo: verify for a purpose
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //todo: verify for a purpose
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
    }

    /**
     * The struct's top level method for indexing 1 record. Each Enum field will call SubIndex
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    static void index
            (ByteBuffer src, int[] register, IntBuffer stack) {
        for (FieldInfoHeader FieldInfoHeader_ : values()) {
            String hdr = FieldInfoHeader_.name();
            System.err.println("hdr:pos " + hdr + ':' + stack.position());
            FieldInfoHeader_.subIndex(src, register, stack);
        }
    }

    /**
     * Each of the Enums can override thier deault behavior of "seek-past"
     *
     * @param src      the ByteBuffer of the input file
     * @param register array holding values pointing to Stack offsets
     * @param stack    A stack of 32-bit pointers only to src positions
     */
    private void subIndex(Buffer src, int[] register, IntBuffer stack) {
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
            } catch (IllegalArgumentException e) {
                e.printStackTrace();  //todo: verify for a purpose
            } catch (InvocationTargetException e) {
                e.printStackTrace();  //todo: verify for a purpose
            } catch (SecurityException e) {
                e.printStackTrace();  //todo: verify for a purpose
            } catch (NoSuchMethodException e) {
                e.printStackTrace();  //todo: verify for a purpose
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //todo: verify for a purpose
            } catch (Exception e) {
                throw new Error(e.getMessage());
            }
        }
    }}
//@@ #endFieldInfoHeader