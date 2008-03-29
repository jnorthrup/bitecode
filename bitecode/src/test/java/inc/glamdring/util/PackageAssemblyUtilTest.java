package inc.glamdring.util;

/**
 *
 */

import com.cyc.tool.subl.jrtl.nativeCode.type.core.*;
import inc.glamdring.bitecode.*;
import junit.framework.*;

public class PackageAssemblyUtilTest extends TestCase {

   public void testGetSublObject() throws Exception {
        SubLObjectPackageAssemblyUtil.createSubLObjectStructSourceFiles(SubLObject.class);
    }
    public void testGetEnumsStructsForPackage() throws Exception {
        EnumPackageAssemblyUtil.createEnumStructSourceFiles(TableRecord.class);
    }
}


