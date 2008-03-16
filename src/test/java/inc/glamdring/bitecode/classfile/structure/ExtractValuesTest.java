package inc.glamdring.bitecode.classfile.structure;


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
    private static final FastMap M = new FastMap();


    static {

        M.put("recordLen", "public static int recordLen;");
        M.put("size", "public int size;");
        M.put("seek", "public int seek;");
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

            display += "public  enum " + enumName + "{" + EOL;


            boolean first = true;

            Map<Enum, Class> subRecords = new EnumMap(enumClazz);
            Map<Enum, Integer> sizes = new EnumMap(enumClazz);
            for (Enum instance : parentEnum) {
                String instanceSimpleName = null;


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

                            if (field.getType() != enumClazz) {
                                final Object o = field.get(instance);
                                if (o != null && !o.equals(0))
                                    s1 += "\n\t\t" + z + "=" + o + ";";
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

                final boolean isRecord = enumClazz.getSimpleName().endsWith("Record");
                M.put("isRecord", "final static public boolean isRecord=" + isRecord + ';');
                final boolean isValue = enumClazz.getSimpleName().endsWith("Value");
                M.put("isValue", "final static public boolean isValue=" + isValue + ';');
                final boolean isHeader = enumClazz.getSimpleName().endsWith("Header");
                M.put("isHeader", "final static public boolean isHeader=" + isHeader + ';');
                final boolean isRef = enumClazz.getSimpleName().endsWith("Ref");
                M.put("isRef", "final static public boolean isRef=" + isRef + ';');
                final boolean isInfo = enumClazz.getSimpleName().endsWith("Info");
                M.put("isInfo", "final static public boolean isInfo=" + isInfo + ';');


                for (Object field : M.values()) {

                    display += "\t" + field + EOL;
                }

            } catch (Exception e) {
            }

            final String trClass = TableRecord.class.getCanonicalName();
            String constructor = "\n" +


                    "\t" + enumName + "()\t{";
            constructor += "\t\t if(  isRecord &&subRecord == null) " +
                    "\t\tif (subRecord == null) {\n" +
                    "\t\t    try {\n" +
                    "\t\t        subRecord =   Class.forName(getClass().getPackage() +'.'+ name()) "    +
                    "\t\t        size=subRecord.getField(\"recordLen\").getInt(null);\n" +
                    "\t\t    } catch ( Exception e) {\n" +
                    "\t\t    }\n";
            constructor += "\t\tinit();\n" +
                    "\t}}\n" +
                    "\n" + "";
            display += constructor;
            display += ((String) "\tvoid init() {\n" +
                    "\t\tseek = recordLen;\n" +
                    "\t\trecordLen += size;\n" +
                    "\t}\n" +
                    "\n" + "");
            final String indexMethod = "\tstatic void index(ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                    "\t\tfor (" + enumName + " " + enumName + " : values()) {\n" +
                    "\t\t    String hdr = " + enumName + ".name();\n" +
                    "\t\t    System.err.println(\"hdr:pos \" + hdr + ':' + stack.position());\n" +
                    "\t\t    " + enumName + ".subIndex(src, register, stack);\n" +
                    "\t\t}\n" +
                    "\t}\n" +
                    "\n" + "";
            display += indexMethod;
            final String subIndexMethod = "\tprivate void subIndex(ByteBuffer src, int[] register, IntBuffer stack) {\n" +
                    "\t\t" + "\t\t   System.err.println(name()+\":subIndex src:stack\" + src.position()+ ':' + stack.position());" + EOL +//
                    "\n" +
                    "\t\t int begin = src.position();\n" +
                    "\t\t int stackPtr = stack.position();\n" +
                    "\t\tstack.put(begin);\n" +
                    "\n" +
                    "\t\tif (isRecord&&subRecord != null) {\n" +
                    "//            if(subRecord!=" + trClass + ")\n" +
                    "//            else\n" +
                    "\t\t    try {\n" +
                    "\t\t        final " + TableRecord.class.getCanonicalName() + " table = TableRecord.valueOf(subRecord.getSimpleName());" + EOL +
                    "\t\t        if (table != null) {\n" +
                    "\t\t            //stow the original location\n" +
                    "\t\t            int mark = stack.position();\n" +
                    "\t\t            stack.position((register[ClassFileRecord.TableRecord.ordinal()] + table.seek)/4);\n" +
                    "\t\t            final Method method = subRecord.getMethod(\"index\", ByteBuffer.class, int[].class, IntBuffer.class);\n" +
                    "\t\t            //resume the lower stack activities\n" +
                    "\t\t            stack.position(mark);\n" +
                    "\t\t\t\t}\n" +
                    "\t\t\t}catch ( Exception e) {\n" +
                    "\t\t\t\tthrow new Error(e.getMessage());\n" +
                    "\t\t\t}\n" +
                    "\t\t}\n" +
                    "\t}\n";
            display += subIndexMethod + "";
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

    private <T extends Enum<T>> String createConstructor(String enumName) {
        return "\n" +


                "\t" + enumName + "\t{" +
                "\t\t if(  this.isRecord=name().endsWith(\"Record\"&&subRecord == null);" +
                "\t\tif (subRecord == null) {\n" +
                "\t\t    try {\n" +
                "\t\t        subRecord = (Class<? extends Enum>) Class.forName(getClass().getCanonicalName());\n" +
                "\t\t        size=subRecord.getField(\"recordLen\").getInt(subRecord);\n" +
                "\t\t    } catch ( Exception e) {\n" +
                "\t\t    }\n" +

                "\t\tinit();\n" +
                "\t}}\n" +
                "\n";
    }

    public static String genHeader(Class<? extends Enum> docEnum) {


        final String thePackage = docEnum.getPackage().getName();


        String s = "";


        Enum[] enums = null;
        enums = docEnum.getEnumConstants()/*.invoke(null)*/;
        int seek = 0;
        int recordLen = 0;
        try {
            recordLen = (Integer) docEnum.getField("recordLen").get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        s += "\n/**************************************************************************/" + "\n\n/**\n\n" + "\t<p>recordSize: " + recordLen + "\n\n<table><tr>" +
                "<th>name</th><th>offset</th><th>size</th><th>Sub-Index</th></tr>";
        for (Enum theSlot : enums) {
            int size = 0;
            try {
                size = (Integer) theSlot.getDeclaringClass().getField("size").get(theSlot);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchFieldException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            try {
                seek = (Integer) theSlot.getDeclaringClass().getField("seek").get(theSlot);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NoSuchFieldException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            final String name = theSlot.name();
            Class<?> subRecord = ByteBuffer.class;
            try {
                subRecord = Class.forName(thePackage + '.' + name);
            } catch (ClassNotFoundException e) {
            }

            s +=
                    "<tr>" +
                            "<th>\n\n" + name + "</th>" +
                            "<td>" + size + "</td>" +
                            "<td>" + seek + "</td>" +
                            "<td>{@link " + subRecord.getCanonicalName() + "}</td>" +
                            "</tr>";

        }
        s += " *\n";
        for (Enum theSlot : enums) {
            s += " * @see " + docEnum.getCanonicalName() + "#" + theSlot.name() + '\n';
        }
        s += " *</table>\n";

        s += " */\n";


        return s;
    }
} 