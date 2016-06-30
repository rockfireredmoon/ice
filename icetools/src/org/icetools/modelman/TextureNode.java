package org.icetools.modelman;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.icelib.XDesktop;

public class TextureNode extends PropNode {
	public final static Icon TEXTURE = new ImageIcon(TextureNode.class.getResource("/png/small/texture.png"));

	public TextureNode(Context context, File root) {
		this(context, root, null);
	}

	public TextureNode(Context context, File file, PropNode parent) {
		super(context, file, parent);

	}

	@Override
	public void mouseClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			try {
				XDesktop.getDesktop().edit(file);
			} catch (IOException e) {
				context.getConsole().error("Failed to open editor.", e);
			} catch (UnsupportedOperationException uoe) {
				try {
					XDesktop.getDesktop().open(file);
				} catch (IOException e) {
					context.getConsole().error("Failed to open view.", e);
				}
			}

		}
	}

	public Icon getIcon() {
		return TEXTURE;
	}

	@Override
	void checkChildren() {
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

}
