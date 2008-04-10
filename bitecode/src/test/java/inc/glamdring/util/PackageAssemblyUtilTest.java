package inc.glamdring.util;

import com.thoughtworks.xstream.*;
import inc.glamdring.bitecode.*;
import static javolution.lang.MathLib.*;
import junit.framework.*;
import org.dom4j.io.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.xml.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.zip.*;

public class PackageAssemblyUtilTest extends TestCase {
	private XStream xStream = new XStream();
	private static final Logger LOG = Logger.getAnonymousLogger();


	public void testGetEnumsStructsForPackage() throws Exception {
		Map<Class<? extends Enum>, Iterable<? extends Enum>> loMap1 = new HashMap<Class<? extends Enum>, Iterable<? extends Enum>>();
		for (Class<? extends Enum> aClass : PackageAssembly.getClassessOfParent(TableRecord.class.getPackage(), Enum.class)) {
			Enum[] constants = aClass.getEnumConstants();
			loMap1.put(aClass, Arrays.asList(constants));
		}
		Set<Map.Entry<Class<? extends Enum>, Iterable<? extends Enum>>> loEntries = loMap1.entrySet();
		String lstrDisplay = "";
		String lstrEnumName = "";
		for (Map.Entry<Class<? extends Enum>, Iterable<? extends Enum>> entry : loEntries)
			lstrDisplay += EnumPackageAssemblyUtil.genEnumMiddle(TableRecord.class, entry);
	}

	private void createClassReader(SortedMap<CharSequence, ByteBuffer> charSequenceByteBufferSortedMap) {
		for (ByteBuffer buffer : charSequenceByteBufferSortedMap.values()) {

			if (!buffer.hasArray()) {
				final ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
				buf.put((ByteBuffer) buffer.rewind());
				buffer = buf;
			}
			final ClassReader classReader = new ClassReader(buffer.array());
			// 	final String s = classReader.getClassName()+Arrays.toString(classReader.getInterfaces());
			//			Logger.getAnonymousLogger().info(s);
			//               ClassReader cr = new ClassReader(source);
			ClassNode cn = new ClassNode();
			classReader.accept(cn, ClassReader.EXPAND_FRAMES);

			final List methods = cn.methods;
			for (Object method : methods) {
				MethodNode m = (MethodNode) method;

			}
		}
	}

	private void buildnodes(final SortedMap<CharSequence, ByteBuffer> poOut, final List<Callable<String>> poCallme, File... poaFileCollection) throws IOException {

		for (final File file1 : poaFileCollection) {
			if (file1.isDirectory()) {
				buildnodes(poOut, poCallme, file1.listFiles());
			} else if (file1.getName().endsWith(".class")) {
				Callable<String> callable = new Callable<String>() {
					public String call() throws Exception {
						poOut.put(file1.getCanonicalPath(), new RandomAccessFile(file1, "rw").getChannel().map(FileChannel.MapMode.PRIVATE, 0, file1.length()));
						return file1.getPath();
					}
				};
				poCallme.add(callable);
			} else if (file1.getName().endsWith(".jar") || file1.getName().endsWith(".zip")) {
				final ZipFile loZ1 = new ZipFile(file1);
				final Enumeration<? extends ZipEntry> loEnumeration = loZ1.entries();
				while (loEnumeration.hasMoreElements()) {
					final ZipEntry loZe = loEnumeration.nextElement();
					Callable callable = new Callable() {
						public Object call() throws Exception {
							final String lstrClname = loZe.getName();
							if (lstrClname.endsWith(".class")) {
								final long lnStreamsize = loZe.getSize();
								ByteBuffer loBuffer = ByteBuffer.allocate((int) lnStreamsize);
								final ReadableByteChannel loChannel = Channels.newChannel(loZ1.getInputStream(loZe));
								while (loBuffer.hasRemaining() && loChannel.read(loBuffer) != -1)
									;
								poOut.put(lstrClname, loBuffer);
							}

							return lstrClname;
						}
					};
					poCallme.add(callable);
				}
			}
		}
	}

	public void testPackage() throws Exception {
		final File loFile = new File("target/classes");
		final File[] loaFiles = loFile.listFiles();
		SortedMap<CharSequence, ByteBuffer> charSequenceByteBufferSortedMap = new ConcurrentSkipListMap<CharSequence, ByteBuffer>();
		List<Callable<String>> loCallme = new ArrayList<Callable<String>>();
		buildnodes(charSequenceByteBufferSortedMap, loCallme, loFile/*, new File("../../cyc/system/src/main/resources/lib/cyc.jar")*/);


		final ExecutorService loExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		final List<Future<String>> loFutures = loExecutorService.invokeAll(loCallme);
		{
			final long l = System.currentTimeMillis();

			for (Future<String> future : loFutures) {
				LOG.info(future.get());
			}
			LOG.info(new StringBuilder("ms:").append((System.currentTimeMillis() - l)).toString());
		}

		{
			final long l = System.currentTimeMillis();

			createClassReader(charSequenceByteBufferSortedMap);
			LOG.info(new StringBuilder("ms:").append((System.currentTimeMillis() - l)).toString());
		}

		final int i = charSequenceByteBufferSortedMap.size();

		final Random random = new Random();

		final int i1 = abs(random.nextInt()) % i;
		final Collection<ByteBuffer> byteBuffers = charSequenceByteBufferSortedMap.values();

		//random-populace
		final ByteBuffer[] rpop = byteBuffers.toArray(new ByteBuffer[byteBuffers.size()]);

		ByteBuffer buffer = rpop[ i1 ];
		{
			final long l = System.currentTimeMillis();

/*
			for (ByteBuffer buffer : charSequenceByteBufferSortedMap.values()) {

*/
			if (!buffer.hasArray()) {
				final ByteBuffer buf = ByteBuffer.allocate(buffer.limit());
				buf.put((ByteBuffer) buffer.rewind());
				buffer = buf;
			}

			final ClassReader classReader = new ClassReader(buffer.array());
			// 	final String s = classReader.getClassName()+Arrays.toString(classReader.getInterfaces());
			//			Logger.getAnonymousLogger().info(s);
			//               ClassReader cr = new ClassReader(source);
			final SAXEventRecorder eventRecorder = new SAXEventRecorder();
			classReader.accept(new SAXClassAdapter(new XMLWriter(System.out, OutputFormat.createPrettyPrint()), true), ClassReader.EXPAND_FRAMES);

/*
			eventRecorder.writeExternal((ObjectOutput) new DataOutputStream(System.out));
*/

			
			LOG.info(new StringBuilder("ms:").append((System.currentTimeMillis() - l)).toString());
		}


		return;
		//////////////////////////////////////////////
		//////////////////////////////////////////////
		//////////////////////////////////////////////
		//////////////////////////////////////////////
		//////////////////////////////////////////////
		//////////////////////////////////////////////
		//////////////////////////////////////////////
		//////////////////////////////////////////////
	}
}