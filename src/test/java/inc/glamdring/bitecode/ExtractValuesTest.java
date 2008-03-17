package inc.glamdring.bitecode;


import inc.glamdring.util.*;
import javolution.util.*;
import junit.framework.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.util.*;
import java.util.Map.*;

public class ExtractValuesTest extends TestCase {


    ExtractValues extractValues = new ExtractValues();
    private static final String EOL = "\n";
    private static final FastMap<CharSequence, String> M = new FastMap<CharSequence, String>();
    private static final String[] ISAREFS = new String[]{"Record", "Value", "Header", "Ref", "Info"};
    private static final String ISA_MODS = Modifier.toString(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC);
    static Map<Class<?>, Pair<String, Pair<String, String>>> bBufWrap = new LinkedHashMap<Class<?>, Pair<String, Pair<String, String>>>();

    static {

        {
            bBufWrap.put(char.class, new Pair<String, Pair<String, String>>("Char", new Pair("char", "")));
            bBufWrap.put(int.class, new Pair<String, Pair<String, String>>("Int", new Pair("int", "")));
            bBufWrap.put(long.class, new Pair<String, Pair<String, String>>("Long", new Pair("long", "")));
            bBufWrap.put(short.class, new Pair<String, Pair<String, String>>("Short", new Pair("short", " & 0xffff")));
            bBufWrap.put(double.class, new Pair<String, Pair<String, String>>("Double", new Pair("double", "")));
            bBufWrap.put(float.class, new Pair<String, Pair<String, String>>("Float", new Pair("float", "")));
            bBufWrap.put(byte[].class, new Pair<String, Pair<String, String>>("", new Pair("byte", " & 0xff")));
            bBufWrap.put(byte.class, new Pair<String, Pair<String, String>>("", new Pair("byte", " & 0xff")));


            M.put("recordLen", Modifier.toString(Modifier.STATIC | Modifier.PUBLIC) + " int recordLen;");
            M.put("size", Modifier.toString(Modifier.FINAL | Modifier.PUBLIC) + " int size;");
            M.put("seek", Modifier.toString(Modifier.FINAL | Modifier.PUBLIC) + " int seek;");
            M.put("subRecord", "public Class<? extends Enum> subRecord;");
            M.put("valueClazz", "public java.lang.Class valueClazz;");

            for (String isaref : ISAREFS) {
                M.put("is" + isaref, "");
            }
        }

    }

    public String testGetEnumsStructsForPackage() throws Exception {

        Map<Class<? extends Enum>, Iterable<? extends Enum>> map = extractValues.getEnumsStructsForPackage();

        Set<Entry<Class<? extends Enum>, Iterable<? extends Enum>>> entries = map.entrySet();

        String display = "";
        String enumName = "";
        for (Entry<Class<? extends Enum>, Iterable<? extends Enum>> entry : entries) {

            Class<? extends Enum> enumClazz = entry.getKey();
            Iterable<? extends Enum> parentEnum = entry.getValue();
            enumName = enumClazz.getSimpleName();
            String fn = ("src/main/hybrid/" + getClass().getPackage().getName() + "/" + enumName).replace(".", "/") + ".java";
            System.err.println("attempting to open " + fn);
            final File file = new File(fn);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter ostream = new FileWriter(file);
            System.err.println("*** Dumping " + file.getCanonicalPath() + "     " + file.toURI().toASCIIString());

            display += "public enum " + enumName + " { " + EOL;


            boolean first = true;

            for (Enum instance : parentEnum) {
                try {
                    String pname = getClass().getPackage().getName();
                    String symbol = instance.name();


                    display += (first ? "" : ",") + symbol.replaceAll(pname + ".", "(");
                    first = false;
                    try {
                        final Field[] fields = enumClazz.getFields();
                        String s1 = "";
                        for (Field field : fields) {
                            String z = field.getName().replaceAll(enumClazz.getCanonicalName(), "");
                            if (z == "size") {
                                final Integer integer = (Integer) field.get(instance);
                                if (integer != 0) display = display + "(0x" + Integer.toHexString(integer) + ")";
                                continue;
                            }
                            if (field.getType() != enumClazz && (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                                final Object o = field.get(instance);
                                if (o != null && !o.equals(0))
                                    s1 += "\n\t\t" + z + "=" + (field.getType() == Class.class ? ((Class) o).getCanonicalName() + ".class" : String.valueOf(o)) + ";";
                            }
                        }
                        if (s1.length() > 4)
                            display += "\t{{" + s1 + "\n\t}}" + EOL;
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                }

            }

            display += ";\n";

            try {

                final Field[] fields = enumClazz.getFields();

                String s1 = "";
                for (Field field : fields) {
                    String z = field.toGenericString().replaceAll(enumClazz.getCanonicalName() + ".", "");
                    if (field.getType() != enumClazz && !M.containsKey(field.getName()))
                        s1 += "\t" + z + ";" + EOL;
                }

/*
                M.put("isRecord", "final static public boolean is
                M.put("isValue", "final static public boolean isValue=" + enumClazz.getSimpleName().endsWith("Value") + ';');
                M.put("isHeader", "final static public boolean isHeader=" + enumClazz.getSimpleName().endsWith("Header") + ';');
                M.put("isRef", "final static public boolean isRef=" + enumClazz.getSimpleName().endsWith("Ref") + ';');
                M.put("isInfo", "final static public boolean isInfo=" + enumClazz.getSimpleName().endsWith("Info") + ';');

*/
                if (s1.length() > 4)
                    display += s1 + EOL;

                for (String isaref : ISAREFS) {
                    M.put("is" + isaref, ISA_MODS + " boolean " + "is" + isaref + "=" + enumClazz.getSimpleName().endsWith(isaref) + ';');
                }


                for (String field : M.values()) {

                    display += "\t" + field + EOL;
                }

            } catch (Exception e) {
            }

            final String trClass = inc.glamdring.bitecode.TableRecord.class.getCanonicalName();
            display += "    /** " + enumName + " templated Byte Struct \n" +
                    "     * @param dimensions [0]=size,[1]= forced seek\n" +
                    "     */\n";


            display += "\t" + enumName + " ";

            display += "(int... dimensions) {\n" +
                    "        int[] dim = init(dimensions);\n" +
                    "        size = dim[0];\n" +
                    "        seek = dim[1];\n" +
                    "\n" +
                    "\n" +
                    "    }\n" +
                    "\n" +
                    "    int[] init(int... dimensions) {\n" +
                    "        int size = dimensions.length > 0 ? dimensions[0] : 0,\n" +
                    "                seek = dimensions.length > 1 ? dimensions[1] : 0;\n" +
                    "\n" +
                    "        if (subRecord == null) {\n" +
                    "            final String[] indexPrefixes = {\"\", \"s\", \"_\", \"Index\", \"Length\", \"Ref\", \"Header\", \"Info\", \"Table\"};\n" +
                    "            for (String indexPrefix : indexPrefixes) {\n" +
                    "                try {\n" +
                    "                    subRecord = (Class<? extends Enum>) Class.forName(getClass().getPackage().getName() + '.' + name() + indexPrefix);\n" +
                    "                    try {\n" +
                    "                        size = subRecord.getField(\"recordLen\").getInt(null);\n" +
                    "                    } catch (Exception e) {\n" +
                    "                    }\n" +
                    "                    break;\n" +
                    "                } catch (ClassNotFoundException e) {\n" +
                    "                }\n" +
                    "\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        for (String vPrefixe1 : new String[]{\"_\", \"\", \"$\", \"Value\",}) {\n" +
                    "            if (valueClazz != null) break;\n" +
                    "            String suffix = vPrefixe1;\n" +
                    "            for (String name1 : new String[]{name().toLowerCase(), name(),}) {\n" +
                    "                if (valueClazz != null) break;\n" +
                    "                final String trailName = name1;\n" +
                    "                if (trailName.endsWith(suffix)) {\n" +
                    "                    for (String aPackage1 : new String[]{\"\",\n" +
                    "                            getClass().getPackage().getName() + \".\",\n" +
                    "                            \"java.lang.\",\n" +
                    "                            \"java.util.\",\n" +
                    "                    })\n" +
                    "                        if (valueClazz == null) break;\n" +
                    "                        else\n" +
                    "                            try {\n" +
                    "                                valueClazz = Class.forName(aPackage1 + name().replace(suffix, \"\"));\n" +
                    "                            } catch (ClassNotFoundException e) {\n" +
                    "                            }\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "        seek = recordLen;\n" +
                    "        recordLen += size;\n" +
                    "\n" +
                    "        return new int[]{size, seek};\n" +
                    "    }" +
                    "    static void index\n" +
                    "            (ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                    "        for (" + enumName + " " + enumName + "_ : values()) {\n" +
                    "            String hdr = " + enumName + "_.name();\n" +
                    "            System.err.println(\"hdr:pos \" + hdr + ':' + stack.position());\n" +
                    "            " + enumName + "_.subIndex(src, register, stack);\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                    "        System.err.println(name() + \":subIndex src:stack\" + src.position() + ':' + stack.position());\n" +
                    "        int begin = src.position();\n" +
                    "        int stackPtr = stack.position();\n" +
                    "        stack.put(begin);\n" +
                    "        if (isRecord && subRecord != null) { \n" +
                    "            try {\n" +
                    "                final " + TableRecord.class.getCanonicalName() + " table = " + TableRecord.class.getCanonicalName() + ".valueOf(subRecord.getSimpleName());\n" +
                    "                if (table != null) {\n" +
                    "                    //stow the original location\n" +
                    "                    int mark = stack.position();\n" +
                    "                    stack.position((register[ClassFileRecord.TableRecord.ordinal()] + table.seek) / 4);\n" +
                    "                    subRecord.getMethod(\"index\", ByteBuffer.class, int[].class, IntBuffer.class).invoke(null);\n" +
                    "                    //resume the lower stack activities\n" +
                    "                    stack.position(mark);\n" +
                    "                }\n" +
                    "            } catch (Exception e) {\n" +
                    "                throw new Error(e.getMessage());\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }";

            final String postScript = display += "}\n" +
                    "//@@ #end" + enumName + "";

            try {

                String t = "";
                t += "package " + "" + enumClazz.getPackage().getName() + ";";
                t += "\n" + "import java.nio.*;";
                t += "\n" + "import java.lang.reflect.*;";


                display = t + genHeader(enumClazz) + display;
            } catch (Exception e) {
                throw new IOError(e);
            }

            ostream.write(display);
            ostream.close();
            display = "";
        }
        return display;
    }

    public String genHeader(Class<? extends Enum> docEnum) {

        String display = "";
        final Enum[] enums = docEnum.getEnumConstants()/*.invoke(null)*/;

        int recordLen = 0;
        try {
            recordLen = (Integer) docEnum.getDeclaredField("recordLen").get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        display += "\n\n/**\n * <p>recordSize: " + recordLen + "\n * <table><tr> " +
                "<th>name</th>" +
                "<th>size</th>" +
                "<th>seek</th>" +
                "<th>Value Class</th>" +
                "<th>Sub-Index</th>" +
                "</tr>\n";

        String name = "";
        for (Enum theSlot : enums) {
            int size = 0, seek = 0;
            name = theSlot.name();
            Class aClass = null;
            Class bClass = null;
            try {
                aClass = (Class) theSlot.getDeclaringClass().getDeclaredField("subRecord").get(theSlot);
            } catch (Exception e) {

            }
            try {
                bClass = (Class) theSlot.getDeclaringClass().getDeclaredField("valueClazz").get(theSlot);
            } catch (Exception e) {
            }
            try {
                size = (Integer) theSlot.getDeclaringClass().getDeclaredField("size").get(theSlot);
            } catch (Exception e) {
            }

            try {
                seek = (Integer) theSlot.getDeclaringClass().getDeclaredField("seek").get(theSlot);
            } catch (Exception e) {
            }


            Class valClazz = null;

            if (bClass == null) {
                bClass = guessIntTypes(size);
            }

            final Pair<String, Pair<String, String>> pair = bBufWrap.get(bClass);
            display += " * <tr>" +
                    "<td> " + name + "</td>" +
                    "<td>0x" + Integer.toHexString(size) + "</td>" +
                    "<td>0x" + Integer.toHexString(seek) + "</td>" +
                    "<td>" + ((valClazz == null) ? (" (" + pair.getSecond().getFirst() + ") " + name + "=src.get" + pair.getFirst() + "(0x" + Integer.toHexString(seek) + ")" + pair.getSecond().getSecond()) : (valClazz.getCanonicalName())) + "</td>" +
                    "<td>{@link " + (aClass == null ? theSlot.getDeclaringClass().getSimpleName()

                    +"Visitor#" + name + "(ByteBuffer, int[], IntBuffer)" : aClass.getCanonicalName()) + "}</td>" +
                    "</tr>\n";
        }
        display += " * \n";

        for (Enum theSlot : enums) {
            display += " * @see " + docEnum.getCanonicalName() + "#" + theSlot.name() + '\n';
        }
        display += " * </table>\n";

        display += " */\n";


        return display;
    }


    private Object[] getSubRecord(String name) {
        final String[] indexPrefixes = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
        for (String indexPrefix : indexPrefixes) {
            try {
                final String p = getClass().getPackage().getName();
                final String name1 = p + '.' + name + indexPrefix;
                final Class<?> aClass = Class.forName(name1);
                final int anInt = aClass.getField("recordLen").getInt(null);
                if (aClass != null)
                    return new Object[]{aClass, anInt};
            } catch (Exception e) {
            }
        }
        return null;
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

}