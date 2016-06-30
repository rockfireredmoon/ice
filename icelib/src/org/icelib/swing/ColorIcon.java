/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

public final class ColorIcon implements Icon {
	private Dimension size;
	private Color color;
	private Color borderColor;

	public ColorIcon() {
		this(null);
	}

	public ColorIcon(Color color) {
		this(color, null);
	}

	public ColorIcon(Color color, Color borderColor) {
		this(color, null, borderColor);
	}

	public ColorIcon(Color color, Dimension size, Color borderColor) {
		setColor(color);
		setSize(size);
		setBorderColor(borderColor);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor((color == null) ? Color.white : color);
		g.fillRect(x, y, getIconWidth(), getIconHeight());

		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRect(x, y, getIconWidth(), getIconHeight());
		}

		if (color == null) {
			g.setColor(Color.black);
			g.drawLine(x, y, x + getIconWidth(), y + getIconHeight());
		}
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	@Override
	public int getIconWidth() {
		return (size == null) ? 16 : size.width;
	}

	@Override
	public int getIconHeight() {
		return (size == null) ? 16 : size.height;
	}

	public Color getColor() {
		return color;
	}
}