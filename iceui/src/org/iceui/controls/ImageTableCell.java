package org.iceui.controls;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.Icelib;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

import icetone.controls.lists.Table.TableCell;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.UIDUtil;

public class ImageTableCell extends TableCell {
	public final static Logger LOG = Logger.getLogger(ImageTableCell.class.getName());
	private Element img;
	private float cellHeight;

	public ImageTableCell(ElementManager screen, String name, float cellHeight) {
		super(screen, name);
		this.cellHeight = cellHeight;
		setPreferredDimensions(new Vector2f(cellHeight, cellHeight));
	}

	public ImageTableCell(ElementManager screen, String name, float cellHeight, String imagePath) {
		this(screen, name, cellHeight);
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
		if (img == null) {
			img = new Element(screen, UIDUtil.getUID(), new Vector2f(cellHeight / 2f, cellHeight / 2f), Vector4f.ZERO, null);
			img.setIgnoreMouse(true);
			addChild(img);
		}
		if (image == null) {
			img.setPreferredDimensions(getPreferredDimensions());
		} else {
			img.setColorMap(image);
			float w = img.getElementTexture().getImage().getWidth();
			float h = img.getElementTexture().getImage().getHeight();
			float wr = cellHeight / 2f / w;
			float wh = cellHeight / 2f / h;
			float r = Math.max(wr, wh);
			final Vector2f cellPref = new Vector2f(w * r, h * r);
			img.setPreferredDimensions(cellPref);
		}
	}

	public void setImageTexture(Texture texture) {
		if (img == null) {
			img = new Element(screen, UIDUtil.getUID(), new Vector2f(cellHeight / 2f, cellHeight / 2f), Vector4f.ZERO, null);
			img.setIgnoreMouse(true);
			addChild(img);
		}
		if (texture == null) {
			img.setPreferredDimensions(getPreferredDimensions());
		} else {
			img.setTexture(texture);
			float w = img.getElementTexture().getImage().getWidth();
			float h = img.getElementTexture().getImage().getHeight();
			float wr = cellHeight / 2f / w;
			float wh = cellHeight / 2f / h;
			float r = Math.max(wr, wh);
			final Vector2f cellPref = new Vector2f(w * r, h * r);
			img.setPreferredDimensions(cellPref);
		}
	}
}
