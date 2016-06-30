package org.icetools.modelman;

import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class SkeletonNode extends ConvertableNode {

    public final static Icon SKELETON = new ImageIcon(SkeletonNode.class.getResource("/png/small/skeleton.png"));
    public final static Icon SKELETON_NEEDS_CONVERT = new ImageIcon(
            SkeletonNode.class.getResource("/png/small/skeleton-needs-convert.png"));
    private boolean hasXML = false;
    private final File xmlFile;
    private boolean needsConvert;

    public SkeletonNode(Context context, File root) {
        this(context, root, null);
    }

    public SkeletonNode(Context context, File file, PropNode parent) {
        super(context, file, parent);
        xmlFile = new File(file.getParentFile(), file.getName() + ".xml");
        if (xmlFile.exists()) {
            hasXML = true;
        }
        if (file.exists()) {
            needsConvert = Fixer.isNeedsFix(file);
        }
    }

    @Override
    public void mouseClick(MouseEvent event) {
        // if (event.getClickCount() == 2) {
        // try {
        // Desktop.getDesktop().edit(file);
        // } catch (IOException e) {
        // context.getConsole().error("Failed to open editor.", e);
        // } catch (UnsupportedOperationException uoe) {
        // try {
        // Desktop.getDesktop().open(file);
        // } catch (IOException e) {
        // context.getConsole().error("Failed to open view.", e);
        // }
        // }
        //
        // }
    }

    public boolean isHasXML() {
        return hasXML;
    }

    public Icon getIcon() {
        return hasXML ? SKELETON : SKELETON_NEEDS_CONVERT;
    }

    @Override
    void checkChildren() {
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public File getXML() {
        return xmlFile;
    }

    @Override
    public boolean isNeedsConvert() {
        return needsConvert;
    }
}
