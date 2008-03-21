package inc.glamdring.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.*;

public class PackageAssembly {

    public static Map<Class<? extends Enum>, Iterable<? extends Enum>> getEnumsStructsForPackage(final Package package_) throws Exception {
        Map<Class<? extends Enum>, Iterable<? extends Enum>> map = new HashMap<Class<? extends Enum>, Iterable<? extends Enum>>();
        for (Class<? extends Enum> aClass : getClassessOfParent(package_, Enum.class)) {
            Enum[] constants = aClass.getEnumConstants();
            map.put(aClass, Arrays.asList(constants));
        }
        return map;
    }

    public static List<Class<Enum>> getEnumsForPackage(Package package_)
            throws ClassNotFoundException {
        // This will hold a list of directories matching the pckgname.
        //There may be more than one if a package is split over multiple jars/paths
        List<Class<Enum>> classes = new ArrayList<Class<Enum>>();
        ArrayList<File> directories = new ArrayList<File>();
        String pckgname = package_.getName();
        try {

            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL res = resources.nextElement();
                if (res.getProtocol().equalsIgnoreCase("jar")) {
                    JarURLConnection conn = (JarURLConnection) res.openConnection();
                    JarFile jar = conn.getJarFile();

                    for (JarEntry e : Collections.list(jar.entries())) {
                        if (e.getName().startsWith(pckgname.replace('.', '/')) && e.getName().endsWith(".class") && !e.getName().contains("$")) {
                            String className = e.getName().replace("/", ".").substring(0, e.getName().length() - 6);
                            System.out.println(className);
                            classes.add((Class<Enum>) Class.forName(className));
                        }
                    }
                } else
                    directories.add(new File(URLDecoder.decode(res.getPath(), "UTF-8")));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be " +
                    "a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be " +
                    "a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying " +
                    "to get all resources for " + pckgname);
        }

        // For every directory identified capture all the .class files
        for (File directory : directories) {
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (String file : files) {
                    // we are only interested in .class files
                    if (file.endsWith(".class")) {
                        // removes the .class extension
                        classes.add(
                                (Class<Enum>) Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() +
                        ") does not appear to be a valid package");
            }
        }
        return classes;
    }


    public List<Class<Enum>> getClassessOfInterface(Package thePackage, Class theInterface) {
        List<Class<Enum>> classList = new ArrayList<Class<Enum>>();
        try {
            for (Class<Enum> discovered : getEnumsForPackage(thePackage)) {
                if (Arrays.asList(discovered.getInterfaces()).contains(theInterface)) {
                    classList.add(discovered);
                }
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, null, ex);
        }

        return classList;
    }

    public static List<Class<? extends Enum>> getClassessOfParent(Package thePackage, Class<? extends Enum> theParent) {
        List<Class<? extends Enum>> classList = new ArrayList<Class<? extends Enum>>();

        try {
            for (Class discovered : getEnumsForPackage(thePackage)) {

                do {
                    Class parent = discovered.getSuperclass();
                    if (parent != theParent) {
                        discovered = parent;
                    } else {
                        classList.add(discovered);
                        break;
                    }
                } while (discovered != null);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
        }
        return classList;
    }


}
