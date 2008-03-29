package inc.glamdring.util;
/**
 *
 */

import junit.framework.*;
import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.util.*;

import java.io.*;
import java.util.*;

public class AsmToolTest extends TestCase {
    public void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
                try {
                    final AsmToolTest test = new AsmToolTest();
//                    test./**/
                } catch (Exception e) {
                    e.printStackTrace();  //todo: verify for a purpose
                }
            }
        }).start();

    }

    public void testFoo() throws Exception {
        new AsmToolTest().run();
    }

    public TestResult run() {

        ClassReader cr = null;
        try {
            cr = new ClassReader(getClass().getCanonicalName());
        } catch (IOException e) {
            e.printStackTrace();  //todo: verify for a purpose
        }
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.EXPAND_FRAMES/*|ClassReader.SKIP_DEBUG*/);

        for (MethodNode method : cn.methods) {
            final InsnList instructions = method.instructions;
            if (instructions.size() > 0) {
                try {
                    if (!analyze(cn, method)) {
//                        Analyzer a = new Analyzer(new BasicVerifier());

//                        final BasicVerifier b  = new BasicVerifier()   ;
                        Interpreter b;/*
                        b = new BasicVerifier();
                        b = new SimpleVerifier(); */
                        b = new BasicVerifier();
                        //b = new SourceInterpreter();
                        Analyzer a = new Analyzer(b);
                        try {
                            a.analyze(cn.name, method);
                        } catch (AnalyzerException e) {
                            e.printStackTrace();  //todo: verify for a purpose
                        } catch (Exception ignored) {
                        }
                        final Frame[] frames = a.getFrames();

                        MethodVisitor mv = new MyTraceMethodVisitor(frames);
                        for (AbstractInsnNode insn : instructions) {

                            (insn).accept(mv);

                        }
                        mv.visitMaxs(0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();  //todo: verify for a purpose
                }
            }
        }
        return createResult();
    }

    /*
    * Detects unused xSTORE instructions, i.e. xSTORE instructions without at
    * least one xLOAD corresponding instruction in their successor instructions
    * (in the control flow graph).
    */
    public boolean analyze(final ClassNode c, final MethodNode m) throws Exception {

        Analyzer a = new Analyzer(new SourceInterpreter());
        Frame[] frames = a.analyze(c.name, m);

        // for each xLOAD instruction, we find the xSTORE instructions that can
        // produce the value loaded by this instruction, and we put them in
        // 'stores'
        Collection stores = new HashSet();


        int i = 0;
        for (Object insn : m.instructions.toArray()) {
//            Object insn = m.instructions.get(i);
            int opcode = ((AbstractInsnNode) insn).getOpcode();
            if ((opcode >= ILOAD && opcode <= ALOAD) || opcode == IINC) {
                int var;
                if (opcode == IINC) var = ((IincInsnNode) insn).var;
                else var = ((VarInsnNode) insn).var;
                Frame f = frames[i++];
                if (f != null) {
                    Set s = ((SourceValue) f.getLocal(var)).insns;
                    for (Object value : s) {
                        insn = value;
                        if (insn instanceof VarInsnNode) {
                            stores.add(insn);


                        }
                    }
                }
            }
        }

        // we then find all the xSTORE instructions that are not in 'stores'
        boolean ok = true;
        for (i = 0; i < m.instructions.size(); ++i) {
            AbstractInsnNode insn = m.instructions.get(i);
            if (insn != null) {
                int opcode = (insn).getOpcode();
                if (opcode >= ISTORE && opcode <= ASTORE) {
                    if (!stores.contains(insn)) {
                        ok = false;
                        System.err.println("method " + m.name
                                + ", instruction " + i
                                + ": useless store instruction");
                    }
                }
            }
        }
        visitT(ok);
        return ok;
    }

    /*
    * Test for the above method, with three useless xSTORE instructions.
    */
    public int test(int i, int j) {
        i = i + 1; // ok, because i can be read after this point
        if (j != 0) {
            try {
                j = j--; // ok, because j can be accessed in the catch
                int k = 0;
                if (i > 0) {
                    k = i - 1;
                }
                return k;
            } catch (Exception e) { // useless ASTORE (e is never used)
                j++; // useless
            }
        } else {
            j = 0x1; // useless
        }
        this.visitT(this);
        return 0;
    }

    private static class MyTraceMethodVisitor extends TraceMethodVisitor {
        private final Frame[] frames;

        public MyTraceMethodVisitor(Frame... frames) {this.frames = frames;}

        public void visitMaxs(
                final int maxStack,
                final int maxLocals) {
            for (int i = 0; i < text.size(); ++i) {
                String s = frames[i] == null
                        ? "null"
                        : frames[i].toString();
                while (s.length() < Math.max(20, maxStack + maxLocals + 1)) {
                    s += " ";
                }
                System.err.print(Integer.toString(i + 1000)
                        .substring(1)
                        + " " + s + " : " + text.get(i));
            }
            System.err.println();
        }

    }

    public <T> void visitT(T foo) {

        System.err.println("happygenericDumping " +
                foo.getClass().getCanonicalName());
    }


}