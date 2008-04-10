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

import com.sun.source.tree.CompilationUnitTree;

import java.awt.Font;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;

/**
 * @author Tim Boudreau
 */
public class SyntaxTreePanel extends javax.swing.JPanel implements DiagnosticListener, TreeSelectionListener {

	/**
	 * Creates new form SyntaxTreePanel
	 */
	public SyntaxTreePanel() {
		initComponents();
		tree.addTreeSelectionListener(this);
		TreeSelectionModel smdl = new DefaultTreeSelectionModel();
		smdl.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setSelectionModel(smdl);
		smdl.addTreeSelectionListener(this);
		tree.setRootVisible(false);
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		Font f = filenameLabel.getFont();
		f = f.deriveFont(Font.BOLD);
		filenameLabel.setFont(f);
//        status.setText (" ");
		filenameLabel.setText(" ");
		//try to warm up jfilechooser
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		statussep = new javax.swing.JSeparator();
		jSplitPane1 = new javax.swing.JSplitPane();
		jPanel2 = new javax.swing.JPanel();
		filenameLabel = new javax.swing.JLabel();
		treepane = new javax.swing.JScrollPane();
		tree = new javax.swing.JTree();
		jCheckBox1 = new javax.swing.JCheckBox();
		vp = new ViewPanel();
		status = new javax.swing.JLabel();

		setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 300;
		gridBagConstraints.ipady = 3;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
		add(statussep, gridBagConstraints);

		jSplitPane1.setDividerLocation(50);

		jPanel2.setLayout(new java.awt.GridBagLayout());

		filenameLabel.setText("[no file loaded]");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
		jPanel2.add(filenameLabel, gridBagConstraints);

		treepane.setViewportView(tree);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 100;
		gridBagConstraints.weightx = 0.75;
		gridBagConstraints.weighty = 0.75;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		jPanel2.add(treepane, gridBagConstraints);

		jCheckBox1.setText("Show empty lists");
		jCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
		jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCheckBox1ActionPerformed(evt);
			}
		});
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
		jPanel2.add(jCheckBox1, gridBagConstraints);

		jSplitPane1.setLeftComponent(jPanel2);

		vp.setLayout(null);
		jSplitPane1.setRightComponent(vp);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 100;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(jSplitPane1, gridBagConstraints);

		status.setText("Use File | Open to open a Java source file");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(status, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
//    V.showLists = jCheckBox1.isSelected();
//    if (lastFile != null && lastFile.isFile()) {
//        try {
//            openFile(lastFile);
//        } catch (IOException ex) {
//            status.setText(ex.getLocalizedMessage());
//            ex.printStackTrace();
//        }
//    }
	}//GEN-LAST:event_jCheckBox1ActionPerformed

	public void addNotify() {
		super.addNotify();
		jSplitPane1.setDividerLocation(0.5D);
	}

	public V init(Iterator<? extends CompilationUnitTree> it) {
		root = new DefaultMutableTreeNode("Root");
		V first = null;
		while (it.hasNext()) {
			CompilationUnitTree unit = it.next();
			V v = new V(unit);
			if (first == null) {
				first = v;
			}
			root.add(v);
		}
		DefaultTreeModel mdl = new DefaultTreeModel(root);
		tree.setModel(mdl);
		tree.expandRow(0);
		if (first != null) {
			tree.getSelectionModel().setSelectionPath(new TreePath(new Object[]{mdl.getRoot(),
			                                                                    first}));
		}
		return first;
	}

	public void report(Diagnostic diagnostic) {
		status.setText(diagnostic.toString());
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode nd = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		status.setText(nd.getUserObject() == null ?
				"[null]" :
				nd.getUserObject().toString());
		Object o = nd.getUserObject();
		((ViewPanel) vp).setObject(o);
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JLabel filenameLabel;
	private javax.swing.JCheckBox jCheckBox1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JLabel status;
	private javax.swing.JSeparator statussep;
	private javax.swing.JTree tree;
	private javax.swing.JScrollPane treepane;
	private javax.swing.JPanel vp;
	// End of variables declaration//GEN-END:variables

	DefaultMutableTreeNode root;

	JLabel getFilenameLabel() {
		return filenameLabel;
	}

	void setStatusText(String txt) {
		status.setText(txt);
	}

	void expandPath(TreePath path) {
		tree.expandPath(path);
	}

	JTree getTree() {
		return tree;
	}
}
