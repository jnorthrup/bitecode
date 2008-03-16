package inc.glamdring.bitecode.classfile.structure;

import java.nio.*;

/**
 * The <code>ConstantValue</code> attribute is a fixed-length attribute used in the <code>attributes</code>
 * table of the <code>FieldInfoHeader</code> <a href="ClassFile.doc.html#2877">(§4.5)</a> structures. A <code>ConstantValue</code> attribute represents
 * the value of a constant field that must be (explicitly or implicitly) <code>static</code>; that is, the
 * <p/>
 * <code>ACC_STATIC</code> bit (<a href="ClassFile.doc.html#88358">Table 4.4</a>) in the <code>flags</code> item of the <code>FieldInfoHeader</code> structure must be
 * set. There can be no more than one <code>ConstantValue</code> attribute in the <code>attributes</code>
 * table of a given <code>FieldInfoHeader</code> structure. The constant field represented by the
 * <p/>
 * <code>FieldInfoHeader</code> structure is assigned the value referenced by its <code>ConstantValue</code>
 * attribute as part of the initialization of the class or interface declaring the constant
 * field <a href="Concepts.doc.html#19075">(§2.17.4)</a>. This occurs immediately prior to the invocation of the class or interface
 * initialization method <a href="Overview.doc.html#12174">(§3.9)</a> of that class or interface.
 * <p><a name="36278"></a>
 * If a <code>FieldInfoHeader</code> structure representing a non-<code>static</code> field has a <code>ConstantValue</code> attribute, then that attribute must silently be ignored. Every Java virtual machine implementation must recognize <code>ConstantValue</code> attributes.
 */
 public enum ConstantValueAttribute {
    /**
     * The value of the Utf8Index item must be a valid index into the ConstantPoolRecord table. The ConstantPoolRecord entry at that index must be a Utf8_ (§4.4.7) structure representing the string "ConstantValue".
     */
    Utf8Index(2),

    /**
     * The value of the AttributeLength item of a ConstantValueAttribute structure must be 2.
     */
    AttributeLength ,

    /**
     * The value of the <code>constantvalueIndex</code> item must be a valid index into the <code>ConstantPoolRecord</code> table. The <code>ConstantPoolRecord</code> entry at that index gives the constant value represented by this attribute. The <code>ConstantPoolRecord</code> entry must be of a type appropriate to the field, as shown by <a href="ClassFile.doc.html#36388">Table 4.6</a>.
     * <a name="36388"></a><p><table border="1">
     * <tbody><tr><td><b>Field Type</b>
     * <p/>
     * </td><td><a name="36365"></a>
     * <b>Entry Type</b>
     * <p/>
     * </td></tr><tr><td><code>long</code>
     * </td><td><a name="36369"></a>
     * <code>CONSTANT_Long</code>
     * <p/>
     * </td></tr><tr><td><code>float</code>
     * </td><td><a name="36373"></a>
     * <code>CONSTANT_Float</code>
     * <p/>
     * </td></tr><tr><td><code>double</code>
     * <p/>
     * </td><td><a name="36377"></a>
     * <code>CONSTANT_Double</code>
     * <p/>
     * </td></tr><tr><td><code>int</code>, <code>short</code>, <code>char</code>, <code>byte</code>, <code>boolean
     * </code>
     * </td><td><a name="36381"></a>
     * <code>CONSTANT_Integer</code>
     * <p/>
     * </td></tr><tr><td><code>String</code>
     * </td><td><a name="36385"></a>
     * <code>CONSTANT_String</code>
     * <p/>
     * </td></tr></tbody></table>
     */
    constantvalueIndex(2),;
     public int size;public Class clazz;

    ConstantValueAttribute(  int size) {
        this.size = size;
    }

    ConstantValueAttribute() {
        size=4;
    }

    static void index(ByteBuffer src, int[] register, IntBuffer stack) {

//        Logger.getAnonymousLogger().info("Stack: " + stack.position());

        for (ConstantValueAttribute m : values()) {


            int i = stack.position();
            System.out.println("sp:offset\t" + i + ":" + (i - register[FileSlotRecord.ConstantPoolRecord.ordinal()]) + "\ts:v:a " + m.size + ":" + FileSlotRecord.genericPeekInt(src, m.size) + ":\t" + m.name());
//            FileSlotRecord.genericGetInt(src, m.size );
            m.subIndex(src, register, stack);
        }


    }

    void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {


        int i = src.position();
        src.position(i + size);
    }

}
