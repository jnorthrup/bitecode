package inc.glamdring.util;
/**
 *
 */

import com.cyc.tool.subl.jrtl.nativeCode.type.core.*;
import junit.framework.*;

public class SubLObjectPackageAssemblyUtilTest extends TestCase {
    SubLObjectPackageAssemblyUtil subLObjectPackageAssemblyUtil;

    public void testCreateSubLObjectStructSourceFiles() throws Exception {
        try {
            SubLObjectPackageAssemblyUtil.createSubLObjectStructSourceFiles(SubLObject.class);
//            fail("Should have thrown an Exception");
        }
        catch (Exception ex) {}
    }
}