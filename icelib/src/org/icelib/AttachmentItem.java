/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.List;

import org.icesquirrel.runtime.SquirrelTable;

@SuppressWarnings("serial")
public class AttachmentItem extends AbstractEntityItem<EntityKey> {

	public AttachmentItem(AbstractEntityItem<EntityKey> original) {
		super(original);
	}

	public AttachmentItem(EntityKey asset, String effect, List<RGB> colors, AttachmentPoint node) {
		super(asset, effect, colors, node);
	}

	public AttachmentItem(EntityKey asset, String effect, List<RGB> colors) {
		super(asset, effect, colors);
	}

	public AttachmentItem(EntityKey asset) {
		super(asset);
	}

	public static AttachmentItem createAttachment(EntityKey asset, String effect, List<RGB> colors) {
		return createAttachment(asset, effect, colors, null);
	}

	public static AttachmentItem createAttachment(EntityKey asset, String effect, List<RGB> colors, AttachmentPoint node) {
		return new AttachmentItem(asset, effect, colors, node);
	}

	@SuppressWarnings("unchecked")
	public static AttachmentItem createAttachment(SquirrelTable oo) {
		String nodeName = ((String) oo.get("node"));
		AttachmentPoint ap = Icelib.isNotNullOrEmpty(nodeName) ? AttachmentPoint.valueOf(nodeName.toUpperCase()) : null;
		return createAttachment(new EntityKey((String) oo.get("type")), (String) oo.get("effect"),
				Icelib.toRGBList((List<String>) oo.get("colors")), ap);
	}
}