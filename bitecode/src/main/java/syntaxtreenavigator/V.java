/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Tim Boudreau
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package syntaxtreenavigator;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Tim Boudreau
 */
public class V extends DefaultMutableTreeNode {
    private Object o;
    private String title;
    static boolean showLists = false;

    public V(Object o) {
        super (o);
        this.o = o;
        proc (o);
    }

    public V(String title, Object o) {
        super (o);
        this.o = o;
        this.title = title;
        if ("Class".equals(title)) {
            Thread.dumpStack();
        }
        proc (o);
    }

    public String toString() {
        String nm = title == null ? strippedClassName(o.getClass()) : title;
        StringBuffer result = new StringBuffer (nm.length() + 20);
        result.append ("<html>");
        result.append (nm);
        if (o instanceof MethodTree) {
            result.append ("<font color=#AAAAAA>");
            result.append (' ');
            result.append ('(');
            result.append (((MethodTree) o).getName());
            result.append(')');
        } else if (o instanceof VariableTree) {
            result.append ("<font color=#AAAAAA>");
            result.append (' ');
            result.append ('(');
            result.append (((VariableTree) o).getName());
            result.append(')');
        } else if (o instanceof IdentifierTree) {
            result.append ("<font color=#DD8800>");
            result.append (' ');
            result.append ('(');
            result.append (((IdentifierTree) o).getName());
            result.append(')');
        }
        if (o instanceof Collection) {
            result.insert(0, "<html><b>");
            result.append ("</b> (collection)");
        }
        return result.toString();
    }

    static String strippedClassName(Class clazz) {
        if (clazz == null) {
            return "[null]";
        }
        String s = clazz.getName();
        int ix1 = s.lastIndexOf('.');
        int ix2 = s.lastIndexOf('$');
        int ix = Math.max(ix1, ix2);
        if (ix != s.length() - 1) {
            return s.substring(ix + 1);
        } else {
            return s;
        }
    }

    private class TreeV implements TreeVisitor <Void, Void> {
        public Void visitAnnotation(AnnotationTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitMethodInvocation(MethodInvocationTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitAssert(AssertTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitAssignment(AssignmentTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitCompoundAssignment(CompoundAssignmentTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitBinary(BinaryTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitBlock(BlockTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitBreak(BreakTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitCase(CaseTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitCatch(CatchTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitClass(ClassTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitConditionalExpression(ConditionalExpressionTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitContinue(ContinueTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitDoWhileLoop(DoWhileLoopTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitErroneous(ErroneousTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitExpressionStatement(ExpressionStatementTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitEnhancedForLoop(EnhancedForLoopTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitForLoop(ForLoopTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitIdentifier(IdentifierTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitIf(IfTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitImport(ImportTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitArrayAccess(ArrayAccessTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitLabeledStatement(LabeledStatementTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitLiteral(LiteralTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitMethod(MethodTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitModifiers(ModifiersTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitNewArray(NewArrayTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitNewClass(NewClassTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitParenthesized(ParenthesizedTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitReturn(ReturnTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitMemberSelect(MemberSelectTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitEmptyStatement(EmptyStatementTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitSwitch(SwitchTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitSynchronized(SynchronizedTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitThrow(ThrowTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitCompilationUnit(CompilationUnitTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitTry(TryTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitParameterizedType(ParameterizedTypeTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitArrayType(ArrayTypeTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitTypeCast(TypeCastTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitPrimitiveType(PrimitiveTypeTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitTypeParameter(TypeParameterTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitInstanceOf(InstanceOfTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitUnary(UnaryTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitVariable(VariableTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitWhileLoop(WhileLoopTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitWildcard(WildcardTree arg0, Void arg1) {
            proc (arg0); return null;
        }

        public Void visitOther(Tree arg0, Void arg1) {
            proc (arg0); return null;
        }
    }

    static void clear() {
        visited.clear();
    }

    private static Set visited = new HashSet();
    private void proc (Object o) {
        if (o == null) {
            return;
        }
        if (visited.contains(o)) {
            return;
        }
        visited.add (o);
        if (o instanceof Iterable) {
            procList ((Iterable) o);
            if (o instanceof Tree) {
                ((Tree) o).accept(new TreeV(), null);
            }
        }
        Class clazz = o.getClass();
        Method[] m = clazz.getMethods();
        for (int i = 0; i < m.length; i++) {
            boolean isCollection = Collection.class.isAssignableFrom(m[i].getReturnType());
            int paramCount = m[i].getParameterTypes().length;
            if (isCollection && paramCount == 0 && m[i].getName().startsWith("get")) {
                String title = m[i].getName().substring(3);
                Collection collection;
                try {
                    collection = ((Collection) m[i].invoke(o));
                    if ((collection != null && !collection.isEmpty()) || showLists) {
                        add (new V (title, collection));
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
            boolean isTree = Tree.class.isAssignableFrom(m[i].getReturnType());
            if (isTree && paramCount == 0 && m[i].getName().startsWith("get")) {
                try {
                    Tree tree = (Tree) m[i].invoke(o);
                    if (!visited.contains(tree)) {
                        add (new V(m[i].getName().substring(3), tree));
                    }
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void procList(Iterable list) {
        Iterator i = list.iterator();
//        if (!showLists || !i.hasNext()) {
//            return;
//        }
        while (i.hasNext()) {
            add (new V(i.next()));
        }
    }
}
