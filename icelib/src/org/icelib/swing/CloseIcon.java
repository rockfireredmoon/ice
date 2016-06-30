/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.UIManager;

public class CloseIcon implements Icon {

	private int w;
	private int h;

	public CloseIcon(int s) {
		this(s, s);
	}

	public CloseIcon(int w, int h) {
		this.w = w;
		this.h = h;
	}

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(UIManager.getColor("TabbedPane.foreground"));
		int thickness = 1;

		for (int i = 0; i < thickness; i++) {
			g.drawRoundRect(x + i, y + i, w - (i * 2), h - (i * 2), 2, 2);
			g.drawLine(x + i + thickness + 1, y + thickness + 1, x + w - thickness - thickness + i, y + h - thickness - 1);
			g.drawLine(x + i + thickness + 1, y + h - thickness - 1, x + w - thickness - thickness + i, y + thickness + 1);
		}
	}

	@Override
	public int getIconWidth() {
		return w;
	}

	@Override
	public int getIconHeight() {
		return h;
	}
}
