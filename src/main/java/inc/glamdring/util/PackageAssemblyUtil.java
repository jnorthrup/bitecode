package inc.glamdring.util;


import inc.glamdring.bitecode.*;
import static inc.glamdring.util.PackageAssembly.*;
import javolution.util.*;

import java.io.*;
import static java.lang.Package.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.*;

public class PackageAssemblyUtil {
    private static final String EOL = "\n";
    private static final FastMap<CharSequence, String> INTRINSICS = new FastMap<CharSequence, String>();
    private static final String[] ISAREFS = new String[]{"Record", "Value", "Header", "Ref", "Info"};
    private static final String ISA_MODS = Modifier.toString(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC);
    static Map<Class<?>, Pair<String, Pair<String, String>>> bBufWrap = new LinkedHashMap<Class<?>, Pair<String, Pair<String, String>>>();

    static {
        bBufWrap.put(char.class, new Pair<String, Pair<String, String>>("Char", new Pair<String, String>("char", "")));
        bBufWrap.put(int.class, new Pair<String, Pair<String, String>>("Int", new Pair<String, String>("int", "")));
        bBufWrap.put(long.class, new Pair<String, Pair<String, String>>("Long", new Pair<String, String>("long", "")));
        bBufWrap.put(short.class, new Pair<String, Pair<String, String>>("Short", new Pair<String, String>("short", " & 0xffff")));
        bBufWrap.put(double.class, new Pair<String, Pair<String, String>>("Double", new Pair<String, String>("double", "")));
        bBufWrap.put(float.class, new Pair<String, Pair<String, String>>("Float", new Pair<String, String>("float", "")));
        bBufWrap.put(byte[].class, new Pair<String, Pair<String, String>>("", new Pair<String, String>("byte", " & 0xff")));
        bBufWrap.put(byte.class, new Pair<String, Pair<String, String>>("", new Pair<String, String>("byte", " & 0xff")));
        INTRINSICS.put("recordLen",
                new String("/**\n" +
                        "     * the length of one record\n" +
                        "     */\n\t") +
                        Modifier.toString(Modifier.STATIC | Modifier.PUBLIC) + " int recordLen;");
        INTRINSICS.put("size",
                new String("/**\n" +
                        "     * the size per field, if any\n" +
                        "     */\n\t") +
                        Modifier.toString(Modifier.FINAL | Modifier.PUBLIC) + " int size;");
        INTRINSICS.put("seek",
                new String("/**\n" +
                        "     * the offset from record-start of the field\n" +
                        "     */\n\t") +
                        Modifier.toString(Modifier.FINAL | Modifier.PUBLIC) + " int seek;");
        INTRINSICS.put("subRecord",
                new String("/**\n" +
                        "     * a delegate class wihch will perform sub-indexing on behalf of a field once it has marked its initial stating\n" +
                        "     * offset into the stack.\n" +
                        "     */\n") +
                        "\tpublic Class<? extends Enum> subRecord;");
        INTRINSICS.put("valueClazz",
                new String("/**\n" +
                        "     * a hint class for bean-wrapper access to data contained.\n" +
                        "     */\n") +
                        "\tpublic Class valueClazz;");
        for (String isaref : ISAREFS)
            INTRINSICS.put("is" + isaref, "");
    }

    public String getEnumsStructsForPackage() throws Exception {
        return createEnumStructSourceFiles(TableRecord.class);
    }

    public String createEnumStructSourceFiles(final Class<TableRecord> tableRecordClass) throws Exception {

        Map<Class<? extends Enum>, Iterable<? extends Enum>> map = inc.glamdring.util.PackageAssembly.getEnumsStructsForPackage(tableRecordClass.getPackage());
        Set<Entry<Class<? extends Enum>, Iterable<? extends Enum>>> entries = map.entrySet();

        String display = "";
        String enumName = "";
        for (Entry<Class<? extends Enum>, Iterable<? extends Enum>> entry : entries)
            display += genMiddle(tableRecordClass, entry);
        return display;
    }

    String genMiddle(Class<TableRecord> tableRecordClass, Entry<Class<? extends Enum>, Iterable<? extends Enum>> entry) throws IOException {

        String display = "";
        String enumName;
        Class<? extends Enum> enumClazz = entry.getKey();
        Iterable<? extends Enum> parentEnum = entry.getValue();
        enumName = enumClazz.getSimpleName();
        String fn = ("src/main/hybrid/" + tableRecordClass.getPackage().getName() + "/" + enumName).replace(".", "/") + ".java";
        System.err.println("attempting to open " + fn);
        final File file = new File(fn);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter ostream = new FileWriter(file);
        System.err.println("*** Dumping " + file.getCanonicalPath() + "     " + file.toURI().toASCIIString());

        display += "public enum " + enumName + " { " + EOL;


        display += getConstantFields(enumClazz) + ";\n";
        String result = getBaseEnumFields(enumClazz);

        final String trClass = TableRecord.class.getCanonicalName();
        display += result + "    /** " + enumName + " templated Byte Struct \n" +
                "     * @param dimensions [0]=size,[1]= forced seek\n" +
                "     */\n";


        display += "\t" + enumName + " ";

        display += "(int... dimensions) {\n" +
                "        int[] dim = init(dimensions);\n" +
                "        size = dim[0];\n" +
                "        seek = dim[1];\n" +
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
                "\n" +
                "    /**\n" +
                "     * The struct's top level method for indexing 1 record. Each Enum field will call SubIndex\n" +
                "     *\n" +
                "     * @param src      the ByteBuffer of the input file\n" +
                "     * @param register array holding values pointing to Stack offsets\n" +
                "     * @param stack    A stack of 32-bit pointers only to src positions\n" +
                "     */\n" +
                "    static void index\n" +
                "            (ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                "        for (" + enumName + " " + enumName + "_ : values()) {\n" +
                "            String hdr = " + enumName + "_.name();\n" +
                "            System.err.println(\"hdr:pos \" + hdr + ':' + stack.position());\n" +
                "            " + enumName + "_.subIndex(src, register, stack);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Each of the Enums can override thier deault behavior of \"seek-past\"\n" +
                "     *\n" +
                "     * @param src      the ByteBuffer of the input file\n" +
                "     * @param register array holding values pointing to Stack offsets\n" +
                "     * @param stack    A stack of 32-bit pointers only to src positions\n" +
                "     */\n" +
                "    private void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                "        System.err.println(name() + \":subIndex src:stack\" + src.position() + ':' + stack.position());\n" +
                "        int begin = src.position();\n" +
                "        int stackPtr = stack.position();\n" +
                "        stack.put(begin);\n" +
                "        if (isRecord && subRecord != null) { \n" +
                "            try {\n" +
                "                final " + tableRecordClass.getCanonicalName() + " table = " + tableRecordClass.getCanonicalName() + ".valueOf(subRecord.getSimpleName());\n" +
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
        return display;
    }

    private String getBaseEnumFields(Class<? extends Enum> enumClazz) {
        String result = "";

        try {

            final Field[] fields = enumClazz.getFields();

            String s1 = "";
            for (Field field : fields) {
                String z = field.toGenericString().replaceAll(enumClazz.getCanonicalName() + ".", "");
                if (field.getType() != enumClazz && !INTRINSICS.containsKey(field.getName()))
                    s1 += "\t" + z + ";" + EOL;
            }

            if (s1.length() > 4)
                result += s1 + EOL;

            for (String isaref : ISAREFS) {
                INTRINSICS.put("is" + isaref, ISA_MODS + " boolean " + "is" + isaref + "=" + enumClazz.getSimpleName().endsWith(isaref) + ';');
            }


            for (String field : INTRINSICS.values()) {

                result += "\t" + field + EOL;
            }

        } catch (Exception e) {
        }
        return result;
    }

    String getConstantFields(Class<? extends Enum> enumClazz) {
        boolean first = true;

        String result = "";
        String pname = enumClazz.getPackage().getName();
        for (Enum instance : enumClazz.getEnumConstants()) {
            try {
                String symbol = instance.name();


                result += (first ? "" : ",") + symbol.replaceAll(pname + ".", "(");
                first = false;
                try {
                    final Field[] fields = enumClazz.getFields();
                    String tmpString = "";
                    for (Field field : fields) {
                        String attrName = field.getName().replaceAll(enumClazz.getCanonicalName(), "");
                        if (attrName == "size") {
                            final Integer integer = (Integer) field.get(instance);
                            if (integer != 0)
                                result = result + "(0x" + Integer.toHexString(integer) + ")";
                        } else {
                            if (field.getType() != enumClazz && (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                                final Object o = field.get(instance);
                                if (o != null && !o.equals(0))
                                    tmpString += "\n\t\t" + attrName + "=" + (field.getType() == Class.class
                                            ? ((Class) o).getCanonicalName() + ".class" :
                                            field.getType() == String.class
                                                    ? '"' + String.valueOf(o).trim() + '"'  :
                                                    String.valueOf(o) )+ ";";
                            }
                        }
                    }
                    if (tmpString.length() > 4)
                        result += "\t{{" + tmpString + "\n\t}}" + EOL;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String genHeader(Class<? extends Enum> docEnum) {

        String display = "";
        final Enum[] enums = docEnum.getEnumConstants();

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
                    "<td>" + name + "</td>" +
                    "<td>0x" + Integer.toHexString(size) + "</td>" +
                    "<td>0x" + Integer.toHexString(seek) + "</td>" +
                    "<td>" + ((valClazz == null) ? (" (" + pair.getSecond().getFirst() + ") " +
                    name + "=src.get" + pair.getFirst()
                    + "(0x" + Integer.toHexString(seek) + ")"
                    + pair.getSecond().getSecond()) : (valClazz.getCanonicalName())) + "</td>" +
                    "<td>{@link "
                    + (aClass == null ? theSlot.getDeclaringClass().getSimpleName()

                    + "Visitor#" + name + "(ByteBuffer, int[], IntBuffer)" : aClass.getCanonicalName()) + "}</td>" +
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


    private Object[] getSubRecord(Enum enum_) {
        final String[] suffixes = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
        for (String indexPrefix : suffixes) {
            try {
                final String p = enum_.getDeclaringClass().getPackage().getName();
                final String name = p + '.' + enum_.name() + indexPrefix;
                final Class<?> aClass = Class.forName(name);
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

    public void testPackage(Package... p) throws Exception {
        String packageName = (p.length > 0 ? p[0] : getClass().getPackage()).getName();
        for (Class<? extends Enum> aClass : getClassessOfParent(getPackage(packageName), Enum.class)) {

            Field[] fields = aClass.getFields();
            String[] fn = new String[fields.length];

            for (int i = 0; i < fn.length; i++)
                fn[i] = fields[i].toGenericString();

            System.err.println(aClass.getSimpleName() + Arrays.toString(fn).replaceAll(",", ",\n\t").replaceAll(packageName + ".", ""));
        }
    }

}