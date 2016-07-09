/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.icesquirrel.interpreter.SquirrelInterpretedArray;
import org.icesquirrel.interpreter.SquirrelInterpretedTable;
import org.icesquirrel.runtime.SquirrelArray;
import org.icesquirrel.runtime.SquirrelPrinter;
import org.icesquirrel.runtime.SquirrelTable;

@SuppressWarnings("serial")
public class ItemAppearance implements Serializable {

	private SquirrelArray ob;

	public ItemAppearance() {
		ob = new SquirrelArray();
	}

	public ItemAppearance(String appearance) throws IOException {
		this();
		appearance = appearance.trim();
		if (!StringUtils.isBlank(appearance)) {
			if (appearance.startsWith("{")) {
				ob = new SquirrelArray();
				ob.add(SquirrelInterpretedTable.table(appearance));
			} else {
				ob = SquirrelInterpretedArray.array(appearance);
			}
			if (ob.size() < 2) {
				ob.add(null);
			}
		}

	}

	public ItemAppearance(ItemAppearance appearance) {
		ob = SquirrelInterpretedArray.array(appearance.ob.toString());
	}

	public String toCopyString() {
		SquirrelTable eo = findMap("c");
		if (eo == null) {
			List<AttachmentItem> as = getAttachments();
			if (as.size() > 0) {
				SquirrelArray oe = new SquirrelArray();
				writeAttachment(0, as, oe);
				return oe.toString();
			}
			return null;
		}
		return "{c=" + SquirrelPrinter.format(eo) + "}";
	}

	public String getClothingType() {
		SquirrelTable eo = findMap("c");
		if (eo == null) {
			return null;
		}
		return (String) eo.get("type");
	}

	public void setClothingAsset(String clothingType) {
		SquirrelTable eo = findOrCreateClothing();
		eo.insert("type", clothingType);
	}

	@SuppressWarnings("unchecked")
	public List<RGB> getClothingColor() {
		SquirrelTable eo = findMap("c");
		if (eo == null || !eo.containsKey("colors")) {
			return null;
		}
		return Collections.unmodifiableList(Icelib.toRGBList((List<String>) eo.get("colors")));
	}

	public List<AttachmentItem> getAttachments() {
		List<AttachmentItem> l = new ArrayList<AttachmentItem>();
		for (Object v : ob) {
			if (v instanceof SquirrelTable && ((SquirrelTable) v).containsKey("a")) {
				l.add(AttachmentItem.createAttachment((SquirrelTable) ((SquirrelTable) v).get("a")));
			}
		}
		return Collections.unmodifiableList(l);
	}

	public ItemAppearance setAttachments(AttachmentItem... attachments) {
		setAttachments(Arrays.asList(attachments));
		return this;
	}

	public void addAttachment(AttachmentItem attachment) {
		List<AttachmentItem> al = new ArrayList<AttachmentItem>(getAttachments());
		al.add(attachment);
		setAttachments(al);
	}

	public void setAttachments(List<AttachmentItem> attachments) {
		int firstIndex = -1;
		int index = 0;
		for (Object v : new ArrayList<Object>(ob)) {
			if (v instanceof SquirrelTable && ((SquirrelTable) v).containsKey("a")) {
				ob.remove(v);
				if (firstIndex == -1) {
					firstIndex = index;
				}
			}
			index++;
		}
		List<AttachmentItem> ri = new ArrayList<AttachmentItem>(attachments);
		Collections.reverse(ri);
		writeAttachment(firstIndex, ri, ob);
	}

	private void writeAttachment(int firstIndex, List<AttachmentItem> ri, SquirrelArray arr) {
		for (AttachmentItem ai : ri) {
			SquirrelTable to = new SquirrelTable();
			to.insert("type", ai.getKey().getName());
			if (ai.getColors() != null) {
				to.insert("colors", Icelib.createColorArray(ai.getColors()));
			}
			if (ai.getEffect() != null && !ai.getEffect().equals("")) {
				to.insert("effect", ai.getEffect());
			}
			if (ai.getNode() != null && !ai.getNode().equals("")) {
				to.insert("node", ai.getNode().name().toLowerCase());
			}
			SquirrelTable eo = new SquirrelTable();
			eo.insert("a", to);
			arr.add(Math.max(firstIndex, 0), eo);
		}
	}

	public void setClothingColor(List<? extends RGB> colors) {
		findOrCreateClothing().insert("colors", Icelib.createColorArray(colors));
	}

	public String toString() {
		return ob.toString();
	}

	private SquirrelTable findOrCreateClothing() {
		SquirrelTable eo = findMap("c");
		if (eo == null) {
			SquirrelTable xo = new SquirrelTable();
			ob.add(xo);
			eo = new SquirrelTable();
			xo.insert("c", eo);
		}
		return eo;
	}

	private SquirrelTable findMap(String n) {
		for (Object v : ob) {
			if (v instanceof SquirrelTable && ((SquirrelTable) v).containsKey(n)) {
				return (SquirrelTable) ((SquirrelTable) v).get("c");
			}
		}
		return null;
	}
}
