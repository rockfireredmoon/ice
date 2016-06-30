/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.Serializable;

public class AttachmentRibbon implements Serializable {
	private static final long serialVersionUID = 1L;
	private String material;
	private int maxSegments;
	private float width;
	private float offset;
	private boolean active;
	private RGB initialColor;
	private RGB colorChange;

	public AttachmentRibbon clone() {
		AttachmentRibbon r = new AttachmentRibbon();
		r.material = material;
		r.maxSegments = maxSegments;
		r.width = width;
		r.offset = offset;
		r.active = active;
		r.initialColor = initialColor == null ? null : initialColor.clone();
		r.colorChange = colorChange == null ? null : colorChange.clone();
		return r;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public int getMaxSegments() {
		return maxSegments;
	}

	public void setMaxSegments(int maxSegments) {
		this.maxSegments = maxSegments;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getOffset() {
		return offset;
	}

	public void setOffset(float offset) {
		this.offset = offset;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public RGB getInitialColor() {
		return initialColor;
	}

	public void setInitialColor(RGB initialColor) {
		this.initialColor = initialColor;
	}

	public RGB getColorChange() {
		return colorChange;
	}

	public void setColorChange(RGB colorChange) {
		this.colorChange = colorChange;
	}

}
