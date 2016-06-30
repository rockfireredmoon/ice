package org.icetools.modelman;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

@SuppressWarnings("serial")
public class ModelMan extends JFrame implements Context {

    private ConvertAction convert;
    private UnconvertAction unconvert;
    private JTree tree;
    private FixAction fix;
    private UnfixAction unfix;
    private Console console;
    private PropTreeModel treeModel;
    private FileNodeBuilder fnb;
    private CloneAction clone;
    private ReloadAction reload;
    private final JCheckBox remove;

    public ModelMan(File file) {
        super("ModelMan");

        // Console
        console = new Console() {

			@Override
			public void refresh(final File file) {

	            // Update the parent node
	            SwingUtilities.invokeLater(new Runnable() {
	                @Override
	                public void run() {
	                    PropNode parent = treeModel.getNodeForFile(file.getParentFile());
	                    parent.reload();
	                    treeModel.reload(parent);
	                }
	            });
				
			}
        	
        	
        };

        fnb = new FileNodeBuilder(this);
        fnb.add(new MeshNodeFactory());
        fnb.add(new ComponentNodeFactory());

        // final File file = new File(System.getProperty("user.dir"));
        PropNode root = fnb.create(null, file);
        treeModel = new PropTreeModel(root);

        // Tree
        tree = new JTree(treeModel);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                setAvailableActions();
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final PropNode selectedPropNode = getSelectedPropNode();
                if (selectedPropNode != null) {
                    selectedPropNode.mouseClick(e);
                }
            }
        });
        tree.setRowHeight(18);
        tree.setCellRenderer(new FileNodeTreeCellRenderer());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.add(fix = new FixAction());
        toolBar.add(unfix = new UnfixAction());
        toolBar.add(convert = new ConvertAction());
        toolBar.add(unconvert = new UnconvertAction());
        toolBar.add(clone = new CloneAction(this, treeModel));
        toolBar.add(reload = new ReloadAction());
        toolBar.add(remove = new JCheckBox("Remote other"));

        // Split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JLabel());
        split.setOneTouchExpandable(true);

        // Console split
        JSplitPane consoleSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split, console);

        // Main
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(consoleSplit, BorderLayout.CENTER);

        // Initial
        setAvailableActions();
    }

    private List<ConvertableNode> findNeedsFix(PropNode n, List<ConvertableNode> l) {
        if (n != null) {
            if (n instanceof ConvertableNode && ((ConvertableNode) n).isNeedsConvert()) {
                l.add((ConvertableNode) n);
            }
            if (!n.isLeaf()) {
                for (int i = 0; i < n.getChildCount(); i++) {
                    findNeedsFix((PropNode) n.getChildAt(i), l);
                }
            }
        }
        return l;
    }

    private List<ConvertableNode> findNeedsUnfix(PropNode n, List<ConvertableNode> l) {
        if (n != null) {
            if (n instanceof ConvertableNode && ((ConvertableNode) n).getFile().exists() && !((ConvertableNode) n).isHasXML() && !((ConvertableNode) n).isNeedsConvert()) {
                l.add((ConvertableNode) n);
            }
            if (!n.isLeaf()) {
                for (int i = 0; i < n.getChildCount(); i++) {
                    findNeedsUnfix((PropNode) n.getChildAt(i), l);
                }
            }
        }
        return l;
    }

    private List<ConvertableNode> findNeedsXMLUnconvert(PropNode n, List<ConvertableNode> l) {
        if (n != null) {
            if (n instanceof ConvertableNode && !((ConvertableNode) n).isNeedsConvert() && ((ConvertableNode) n).isHasXML()) {
                l.add((ConvertableNode) n);
            }
            if (!n.isLeaf()) {
                for (int i = 0; i < n.getChildCount(); i++) {
                    findNeedsXMLUnconvert((PropNode) n.getChildAt(i), l);
                }
            }
        }
        return l;
    }

    private List<ConvertableNode> findNeedsXMLConvert(PropNode n, List<ConvertableNode> l) {
        if (n != null) {
            if (n instanceof ConvertableNode && !((ConvertableNode) n).isNeedsConvert() && !((ConvertableNode) n).isHasXML()) {
                l.add((ConvertableNode) n);
            }
            if (!n.isLeaf()) {
                for (int i = 0; i < n.getChildCount(); i++) {
                    findNeedsXMLConvert((PropNode) n.getChildAt(i), l);
                }
            }
        }
        return l;
    }

    public void setAvailableActions() {
        PropNode propNode = getSelectedPropNode();

        List<ConvertableNode> l = findNeedsFix(propNode, new ArrayList<ConvertableNode>());
        List<ConvertableNode> f = findNeedsUnfix(propNode, new ArrayList<ConvertableNode>());
        List<ConvertableNode> x = findNeedsXMLConvert(propNode, new ArrayList<ConvertableNode>());
        List<ConvertableNode> u = findNeedsXMLUnconvert(propNode, new ArrayList<ConvertableNode>());
        fix.setEnabled(!l.isEmpty());
        unfix.setEnabled(!f.isEmpty());
        convert.setEnabled(!x.isEmpty());
        unconvert.setEnabled(!u.isEmpty());
        clone.setEnabled(propNode instanceof ComponentNode);
        reload.setEnabled(propNode != null);
    }

    public PropNode getSelectedPropNode() {
        PropNode propNode = (PropNode) tree.getLastSelectedPathComponent();
        return propNode;
    }

    public static void main(String[] args) {
    	File file = null;
    	if(args.length == 0) {
    		JFileChooser jfx = new JFileChooser(new File(".").getAbsolutePath());
    		jfx.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    		if(jfx.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    			file = jfx.getSelectedFile();
    		}
    		else
    			System.exit(0);
    	}
    	else
    		file = new File(args[0]);
        ModelMan jf = new ModelMan(file);
        jf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        jf.pack();
        jf.setSize(640, 480);
        jf.setVisible(true);
    }

    class FixAction extends AbstractAction {

        FixAction() {
            super("Fix");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Fixer fixer = new Fixer(console, Fixer.Type.FIX);
            for (ConvertableNode l : findNeedsFix(getSelectedPropNode(), new ArrayList<ConvertableNode>())) {
                fixer.addFile(l.getFile());
            }
            new Thread(fixer).start();
        }
    }

    class UnfixAction extends AbstractAction {

        UnfixAction() {
            super("Unfix");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Fixer fixer = new Fixer(console, Fixer.Type.UNFIX);
            for (ConvertableNode l : findNeedsUnfix(getSelectedPropNode(), new ArrayList<ConvertableNode>())) {
                fixer.addFile(l.getFile());
            }
            new Thread(fixer).start();
        }
    }

    class UnconvertAction extends AbstractAction {

        UnconvertAction() {
            super("Unconvert");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Converter converter = new Converter(console, treeModel, Converter.Type.XML_TO_BINARY);
            converter.setRemoveOther(remove.isSelected());
            for (ConvertableNode l : findNeedsXMLUnconvert(getSelectedPropNode(), new ArrayList<ConvertableNode>())) {
                converter.addFile(l.getXML());
            }
            final Thread thread = new Thread(converter);
            thread.setUncaughtExceptionHandler(console);
            thread.start();
        }
    }

    class ConvertAction extends AbstractAction {

        ConvertAction() {
            super("Convert");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Converter converter = new Converter(console, treeModel, Converter.Type.BINARY_TO_XML);
            for (ConvertableNode l : findNeedsXMLConvert(getSelectedPropNode(), new ArrayList<ConvertableNode>())) {
                converter.addFile(l.getFile());
            }
            final Thread thread = new Thread(converter);
            thread.setUncaughtExceptionHandler(console);
            thread.start();
        }
    }

    class ReloadAction extends AbstractAction {

        ReloadAction() {
            super("Reload");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PropNode pn = getSelectedPropNode();
            pn.reload();
            treeModel.reload(pn);
        }
    }

    @Override
    public Console getConsole() {
        return console;
    }

    @Override
    public FileNodeBuilder getBuilder() {
        return fnb;
    }

    @Override
    public PropTreeModel getTreeModel() {
        return treeModel;
    }
}
