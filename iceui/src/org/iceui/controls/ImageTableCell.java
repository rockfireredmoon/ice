package org.iceui.controls;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;

import icetone.controls.table.TableCell;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.Element;

public class ImageTableCell extends TableCell {
	public final static Logger LOG = Logger.getLogger(ImageTableCell.class.getName());
	private Element img;
	private float cellHeight;

	public ImageTableCell(BaseScreen screen, String name, float cellHeight) {
		this(screen, name, cellHeight, null);
	}

	public ImageTableCell(BaseScreen screen, String name, float cellHeight, String imagePath) {
		super(screen, name);
		this.cellHeight = cellHeight;
		setPreferredDimensions(new Size(cellHeight, cellHeight));

		img = new Element(screen, new Size(cellHeight / 2f, cellHeight / 2f));
		img.setStyleClass("image");
		img.setIgnoreMouse(true);
		addElement(img);

		if (imagePath != null)
			setImagePath(imagePath);
	}

	public void setImagePath(String imagePath) {
		try {
			Texture loadTexture = screen.getApplication().getAssetManager().loadTexture(imagePath);
			setImage(loadTexture.getImage());
		} catch (Exception anfe) {
			setText("Error.");
			LOG.log(Level.SEVERE, "Failed to load image.", anfe);
		}
	}

	public void setImage(Image image) {
		if (image == null) {
			img.setPreferredDimensions(getPreferredDimensions());
		} else {
			img.setTexture(image);
			float w = img.getElementTexture().getImage().getWidth();
			float h = img.getElementTexture().getImage().getHeight();
			float wr = cellHeight / 2f / w;
			float wh = cellHeight / 2f / h;
			float r = Math.max(wr, wh);
			final Size cellPref = new Size(w * r, h * r);
			img.setPreferredDimensions(cellPref);
		}
	}

	public void setImageTexture(Texture texture) {
		if (texture == null) {
			img.setPreferredDimensions(getPreferredDimensions());
		} else {
			img.setTexture(texture);
			float w = img.getElementTexture().getImage().getWidth();
			float h = img.getElementTexture().getImage().getHeight();
			float wr = cellHeight / 2f / w;
			float wh = cellHeight / 2f / h;
			float r = Math.max(wr, wh);
			final Size cellPref = new Size(w * r, h * r);
			img.setPreferredDimensions(cellPref);
		}
	}

	public Element getImage() {
		return img;
	}
}
