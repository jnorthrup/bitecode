package inc.glamdring.sublmunge.util;
/**
 *
 */

import com.cyc.tool.subl.jrtl.nativeCode.subLisp.*;
import com.cyc.tool.subl.jrtl.nativeCode.type.core.*;
import com.cyc.tool.subl.util.*;
import junit.framework.*;
import org.objectweb.asm.*;

public class SubLObjectPackageAssemblyUtilTest extends TestCase implements Opcodes {
	SubLObjectPackageAssemblyUtil subLObjectPackageAssemblyUtil;
	private static Thread initThread;

	synchronized static void ensureInitialized() {
		if (!SubLMain.isInitialized()) {
			if (initThread == null) {
				initThread = Thread.currentThread();
				SubLProcess subLProcess = new SubLProcess("ensureInitialized") {
					// final String[] val$args;
					//
					//      SubLProcess(String x0, String[] strings_2) {
					//         val$args = strings_2;
					//    super(x0);
					//    }
					public void safeRun() {
						if (SubLMain.me != null) {
							/* empty */
						}
						SubLMain.initializeSubL(new String[0]);
						if (SubLMain.me != null) {
							/* empty */
						}
						SubLMain.initializeTranslatedSystems();
						SubLFiles.initialize(Keyhashes.class.getName());
						SSS.setDynamicValue(SubLObjectFactory.makeInteger(212));
						SubLMain.setIsInitialized();
					}
				};
				if (initThread instanceof SubLThread) {
					SubLThread subLThread = (SubLThread) initThread;

					subLProcess.safeRun();
				} else {
					SubLThreadPool.getDefaultPool().execute(subLProcess);
				}
			}
		}
	}
	public void testCreateSubLObjectStructSourceFiles() throws Exception {

		ensureInitialized();
		SubLObjectPackageAssemblyUtil.createSubLObjectStructSourceFiles(SubLObject.class);
 
/*
		final SourceInterpreter interpreter = new SourceInterpreter();
		new Analyzer(interpreter);
*/
		
	}
}