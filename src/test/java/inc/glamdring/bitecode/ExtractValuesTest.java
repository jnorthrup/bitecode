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
    private static final String EOLOC = ";\n\t";
    private static final String EOLOC2 = EOLOC + "\t";
    private static final FastMap<CharSequence, String> M = new FastMap<CharSequence, String>();
    private static final String[] ASSOCIATION_STRINGS = new String[]{"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};


    static {

        M.put("recordLen", "public static int recordLen;");
        M.put("size", "final public int size;");
        M.put("seek", "final public int seek;");
        M.put("subRecord", "public Class<? extends Enum> subRecord;");
        M.put("valueClazz", "public java.lang.Class valueClazz;");

    }

    public <T extends Enum<T>> String testGetEnumsStructsForPackage() throws Exception {

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

                if (s1.length() > 4)
                    display += s1 + EOL;

                M.put("isRecord", "final static public boolean isRecord=" + enumClazz.getSimpleName().endsWith("Record") + ';');
                M.put("isValue", "final static public boolean isValue=" + enumClazz.getSimpleName().endsWith("Value") + ';');
                M.put("isHeader", "final static public boolean isHeader=" + enumClazz.getSimpleName().endsWith("Header") + ';');
                M.put("isRef", "final static public boolean isRef=" + enumClazz.getSimpleName().endsWith("Ref") + ';');
                M.put("isInfo", "final static public boolean isInfo=" + enumClazz.getSimpleName().endsWith("Info") + ';');


                for (String field : M.values()) {

                    display += "\t" + field + EOL;
                }

            } catch (Exception e) {
            }

            final String trClass = TableRecord.class.getCanonicalName();

            display += "\t" + enumName + " " +
                    "(int... dimensions) {\n" +
                    "        seek = initRecordLen(size = (dimensions.length > 0 ? dimensions[0] : init()));\n" +
                    "    }\n" +
                    "\n" +
                    "    private int initRecordLen(int size) {\n" +
                    "        int rl = recordLen;\n" +
                    "        recordLen += init() == size ? size : size;\n" +
                    "        return rl;\n" +
                    "    }\n" +
                    "\n" +
                    "    int init() {\n" +
                    "        int size = 0;\n" +
                    "        if ( subRecord == null) {\n" +
                    "            final String[] indexPrefixes = {\"\", \"s\", \"_\", \"Index\", \"Value\", \"Ref\", \"Header\", \"Info\"};\n" +
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
                    "                final String[] vPrefixes = {\"_\", \"\", \"$\"};\n" +
                    "                final String[] names = {name().toLowerCase(), name(),};\n" +
                    "                if (valueClazz == null && (isRef | isValue))\n" +
                    "                    for (int i = 0; valueClazz == null && i < vPrefixes.length; i++)\n" +
                    "                        for (int i1 = 0; valueClazz == null && i1 < names.length; i1++)\n" +
                    "                            if (names[i1].endsWith(vPrefixes[i]))\n" +
                    "                                try {\n" +
                    "                                    valueClazz = Class.forName(names[i1].replaceAll(names[i1] + vPrefixes[i], names[i1]));\n" +
                    "                                } catch (ClassNotFoundException e) {\n" +
                    "                                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "        return size;\n" +
                    "    }\n" +
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

//        final String thePackage = docEnum.getPackage().getName();


        String s = "";


        final Enum[] enums = docEnum.getEnumConstants()/*.invoke(null)*/;
        int size = 0, seek = 0;
        int recordLen = 0;
        try {
            recordLen = (Integer) docEnum.getDeclaredField("recordLen").get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        s += "\n\n/**\n " + "\t<p>recordSize: " + recordLen + "\n * <table><tr>" +
                " * <th>name</th><th>size</th><th>seek</th><th>Sub-Index</th></tr>";

        String name = "";
        for (Enum theSlot : enums) {
            name = theSlot.name();
            Class aClass = null;
            try {
                try {
                    aClass = (Class<ByteBuffer>) theSlot.getDeclaringClass().getDeclaredField("subRecord").get(theSlot);
                } catch (Exception e) {
                    final Pair<Class<? extends Enum>, Integer> record = getSubRecord(name);
                    aClass = (Class) record.getFirst();
                    size = record.getSecond();
                }
            } catch (Exception e) {
            }

            if (aClass == null) {
                try {
                    try {
                        aClass = (Class<ByteBuffer>) theSlot.getDeclaringClass().getDeclaredField("valueClass").get(theSlot);
                    } catch (Exception e) {
                        aClass = getValueClass(name);
                    }
                } catch (Exception e) {
                }
            }
            if (size == 0) {
                try {
                    size = (Integer) theSlot.getDeclaringClass().getDeclaredField("size").get(theSlot);
                } catch (Exception e) {
                }
            }

            try {
                seek = (Integer) theSlot.getDeclaringClass().getDeclaredField("seek").get(theSlot);
            } catch (Exception e) {

            }

            s +=
                    " * <tr>" +
                            "<th> " + name + "</th>" +
                            "<td>" + size + "</td>" +
                            "<td>" + seek + "</td>" +
                            "<td>{@link " + (aClass == null ? ByteBuffer.class : aClass).getCanonicalName() + "}</td>" +
                            "</tr>\n";
        }
        s += " *\n";

        for (Enum theSlot : enums) {
            s += " * @see " + docEnum.getCanonicalName() + "#" + theSlot.name() + '\n';
        }
        s += " * </table>\n";

        s += " */\n";


        return s;
    }


    private Pair<Class<? extends Enum>, Integer> getSubRecord(String name) {
        final String[] indexPrefixes = {"", "s", "_", "Index", "Value", "Ref", "Header", "Info"};
        for (String indexPrefix : indexPrefixes) {
            try {
                return new Pair<Class<? extends Enum>, Integer>(Class.forName(getClass().getPackage().getName() + '.' + name + indexPrefix), Class.forName(getClass().getPackage().getName() + '.' + name + indexPrefix).getField("recordLen").getInt(null));
            } catch (Exception e) {
            }
            break;
        }
        return null;
    }

    private Class<?> getValueClass(String name) {
        final String[] vPrefixes = {"_", "", "$"};
        final String[] packages = {"",
                Object.class.getPackage().getName() + ".",
                List.class.getPackage().getName() + ".",
                getClass().getPackage().getName() + "."
        };
        final String[] names = {name.toLowerCase(), name,};

        Class<?> valueClazz = null;
        for (int i = 0; i < vPrefixes.length; i++)
            for (int i1 = 0; i1 < names.length; i1++)
                if (names[i1].endsWith(vPrefixes[i]))
                    for (String aPackage : packages) {
                        try {
                            valueClazz = Class.forName(aPackage + "." + names[i1].replaceAll(names[i1] + vPrefixes[i], names[i1]));
                            return valueClazz;
                        } catch (ClassNotFoundException e) {
                        }
                    }
        return valueClazz;
    }
}