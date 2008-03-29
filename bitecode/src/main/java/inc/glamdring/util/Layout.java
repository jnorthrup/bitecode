package inc.glamdring.util;

import static javax.swing.JOptionPane.*;
import javax.swing.*;
import java.text.*;
import java.util.*;

/**
 * User: jim
 */
public enum Layout {
    /**
     * non-zero indicates non-binary node
     */
    hash(1),
    /**
     * pascal string allows 0-255 tokens. todo: should length==0 have a special meaning? if so what meaning?
     */
    span(4),
    /**
     * block alignment
     */
    padding4(4 - (span.seek % 4) - 1),
    /**
     * The position of the key in its text file up to 4 gigs from start of ByteBuffer.
     * <p/>
     * Key files can be larger than 4 gigs, but the segment of that file being indexed or mmap'd uses only 32 bits
     * address space with nio.
     */
    offset(4),
    /**
     * graph node
     */
    left(4),
    /**
     * graph node
     */
    right(4),

    /**
     * domain specific value for bayes classifiers.
     */
    value(4);
    /**
     * this holds the length of a record.
     */

    static public int recordLen;

    /**
     * field size.
     */
    public int size;
    public Class clazz;


    /**
     * position from byte 0 of record.
     */
    public int seek;

    /**
     * ctor
     *
     * @param size  bytes in this field
     * @param clazz optional class for setters.
     */
    Layout(int size, Class... clazz) {

        this.size = size;
        this.clazz = guessIntTypes(size, clazz);
        init();

        /**
         * This is used to sew up unspecified setters.
         */

    }


    public void init() {
        seek = recordLen;
        recordLen += size;
    }

    public static Class guessIntTypes(int size, Class... clazz) {
        Class layout_clazz;
        if (clazz.length == 0) {
            switch (size) {
                case 1:
                    layout_clazz = byte.class;
                    break;
                case 2:
                    layout_clazz = short.class;
                    break;
                case 4:
                    layout_clazz = int.class;
                    break;
                case 8:
                    layout_clazz = long.class;
                    break;
                default:
                    layout_clazz = byte[].class;
                    break;
            }
        } else {
            layout_clazz = clazz[0];
        }
        return layout_clazz;
    }

    /**
     * small stub for bean stuff handy when you rework any layout details.
     */

    static public void main(String[] a) {
        HashMap<Class<?>, Pair<String, String>> nc = new LinkedHashMap<Class<?>, Pair<String, String>>();

        {
            nc.put(char.class, new Pair<String, String>("Char", "char"));
            nc.put(int.class, new Pair<String, String>("Int", "int"));
            nc.put(long.class, new Pair<String, String>("Long", "long"));
            nc.put(short.class, new Pair<String, String>("Short", "short"));
            nc.put(double.class, new Pair<String, String>("Double", "double"));
            nc.put(byte[].class, new Pair<String, String>("", "byte[]"));
            nc.put(byte.class, new Pair<String, String>("", "byte"));
        }


        String pkgName = Layout.class.getPackage().getName();
        String vname = Layout.class.getCanonicalName();

        //        String s = genHeader(pkgName, vname, null);
        //        s += genRecordClass(nc, vname, null);
        //
        String baseName = showInputDialog("please type in the Basename", "XRecord");
        if (baseName.length() < 4)
            return;


        String s = genHeader(pkgName, vname, baseName);
        s += genRecordInterface(nc, vname, baseName);
        JTextPane message;
        message = new JTextPane();
        message.setText(s);
        String s1 = showInputDialog(message);


        s = genHeader(pkgName, vname, baseName);
        s += genRecordClass(nc, vname, baseName);
        message = new JTextPane();
        message.setText(s);
        String x = showInputDialog(message);


    }

    public static String genHeader(String pkgName, String vname, String baseName) {
        String s = "" + "\n\n\npackage " + "" + pkgName + ";" + "\n\nimport static " + vname + ".*;" + "\n" + "\n" + "import java.nio.*;";

        s += MessageFormat.format("\n/**************************************************************************/" +
                "\n\n/**\n\n" +
                "\t<p>total size record: {1}\n\n<table><tr>", pkgName, Layout.recordLen);


        s += MessageFormat.format("<th>name</th><th>offset</th><th>size</th><th>Class</th></tr>", s);

        for (Layout layout : Layout.values()) {
            s += MessageFormat.format("<tr>" +
                    "<th>\n\n{0}</th>" +
                    "<td>{1}</td>" +
                    "<td>{2}</td>" +
                    "<td>{3}</td>" +
                    "</tr>",
                    layout.name(),
                    layout.seek,
                    layout.size,
                    layout.clazz.getName());
        }
        s += "\t</table>\n\n\n\n";


        for (Enum layout : Layout.values()) {
            s += "\t\n@see " + vname + "#" + layout.name();
        }

        s += "\n\n*/";
        return s;
    }

    public static String genRecordClass(Map<Class<?>, Pair<String, String>> nc, String vname, String baseName) {
        String s = "\n\npublic class " + baseName + "Instance implements " + baseName + "Iface<" + baseName + "Instance> {\n" + "\tByteBuffer buffer;\n";
        s += "\n\tpublic " + baseName + "Instance (ByteBuffer buffer){this.buffer=buffer;}";

        for (Layout layout : values()) {
            String lname = layout.name();
            char cap = lname.toUpperCase().charAt(0);
            String cn = cap + lname.substring(1);
            String getterName = "get" + cn;
            String setterName = "set" + cn;

            String typeName = layout.clazz.getCanonicalName();


            String upType = nc.get(layout.clazz).getFirst();
            String primType = nc.get(layout.clazz).getSecond();


            s += "\n\t/** @see " + vname + "#" + layout.name() + " */";
            if (layout.clazz == byte[].class)
                s += MessageFormat.format("\n\tpublic byte[] {0}() {1}\n\t\tbyte {2}[] = new byte[Layout.{2}.size/*" + layout.size + "*/];\n\t\tbuffer.get({2}, buffer.position() + Layout.{2}.seek, Layout.{2}.size);\n\t\treturn {2};\n\t}", getterName, '{', lname);
            else
                s += MessageFormat.format("\n\tpublic {0} {1}() {5}\n\t\treturn buffer.get{2}(buffer.position() + Layout.{3}.seek /*{4}*/);\n\t}\n", primType, getterName, upType, lname, layout.seek, '{');

            s += "\n\t/** @see " + vname + "#" + layout.name() + " */";
            if (layout.clazz == byte[].class) {
                s += MessageFormat.format("\n\tpublic " + baseName + "Instance {0}(    byte[] {1}) {2}\n\t\tbuffer.put({3}, buffer.position() + Layout.{4}.seek, Layout.{5}.size);\n\t\treturn this;\n\t}\n", setterName, lname, '{', lname, lname, lname);
            } else {
                s += MessageFormat.format("\n\tpublic " + baseName + "Instance {0}(    {1} {2}) {3}\n\t\tbuffer.put{4}(buffer.position() + Layout.{5}.seek,  {6});\n\t\treturn this;\n\t}\n", setterName, primType, lname, '{', upType, lname, lname);
                //                    String s1 = MessageFormat.format("buffer.position() + {0}.{1}.seek, {2}", vname, lname, lname);
                //                s += MessageFormat.format("\tpublic\t"+baseName+"Instance\t{0}(   \t{1}\t{2})\t{\n\tbuffer.put{3}({4});\n\t\treturn\tthis;\n\t}\n", setterName, primType, lname, upType, s1);
            }
        }

        return s += "\n}\n";
    }

    public static String genRecordInterface(Map<Class<?>, Pair<String, String>> nc, String vname, String baseName) {
        String name1 = baseName + "Iface";
        String s = "\n\n  interface " + name1 + "<T extends " + name1 + " >{\n";
        //        s += "\n\t  " + baseName + "  (ByteBuffer buffer) ;";

        for (Layout layout : values()) {

            String lname = layout.name();
            char cap = lname.toUpperCase().charAt(0);
            String cn = cap + lname.substring(1);
            String getterName = "get" + cn;
            String setterName = "set" + cn;

            String typeName = layout.clazz.getCanonicalName();


            String upType = nc.get(layout.clazz).getFirst();
            String primType = nc.get(layout.clazz).getSecond();


            s += "\n\t/** @see " + vname + "#" + layout.name() + " */";
            if (layout.clazz == byte[].class)
                s += MessageFormat.format("\n\t  byte[] {0}();", getterName, '{', lname);
            else
                s += MessageFormat.format("\n\t  {0} {1}() ;", primType, getterName, upType, lname, layout.seek, '{');

            s += "\n\t/** @see " + vname + "#" + layout.name() + " */";
            if (layout.clazz == byte[].class) {
                s += MessageFormat.format("\n\t  T  {0}(    byte[] {1});", setterName, lname, '{', lname, lname, lname);
            } else {
                s += MessageFormat.format("\n\t  T  {0}(    {1} {2});", setterName, primType, lname, '{', upType, lname, lname);
            }
        }
        s += "\n\t}";
        return s;
    }
}