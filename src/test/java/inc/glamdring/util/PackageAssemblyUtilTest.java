package inc.glamdring.util;

/**
 *
 */

import inc.glamdring.bitecode.*;
import junit.framework.*;

public class PackageAssemblyUtilTest extends TestCase {
    PackageAssemblyUtil packageAssemblyUtil = new PackageAssemblyUtil();

    public void testGetEnumsStructsForPackage() throws Exception {
        packageAssemblyUtil.createEnumStructSourceFiles(TableRecord.class);
    }
}


