/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icesquirrel.interpreter.SquirrelInterpretedTable;
import org.icesquirrel.runtime.SquirrelArray;
import org.icesquirrel.runtime.SquirrelPrintWriter;
import org.icesquirrel.runtime.SquirrelPrinter;
import org.icesquirrel.runtime.SquirrelTable;

@SuppressWarnings("serial")
public class Appearance implements Serializable {
	final static Logger LOG = Logger.getLogger(Appearance.class.getName());

	public static class ClothingList extends ArrayList<ClothingItem> {

		public ClothingItem getItemForType(ClothingType type) {
			for (ClothingItem t : this) {
				if (t.getType().equals(type)) {
					return t;
				}
			}
			return null;
		}
	}

	public static class SkinElement implements Serializable, Comparable<SkinElement> {

		private String name;
		private RGB color;

		public SkinElement(String name, RGB color) {
			super();
			this.name = name;
			this.color = color;
		}

		public final String getName() {
			return name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		public final RGB getColor() {
			return color;
		}

		public final void setColor(RGB color) {
			this.color = color;
		}

		@Override
		public int compareTo(SkinElement o) {
			return name.compareTo(o.name);
		}

		@Override
		public String toString() {
			return "SkinElement{" + "name=" + name + ", color=" + color + '}';
		}
	}

	public enum Name {

		/**
		 * Biped
		 */
		C2,
		/**
		 * Creature
		 */
		N4,
		/**
		 * Prop
		 */
		P1
	}

	public enum ClothingType {

		CHEST, LEGGINGS, BELT, BOOTS, ARMS, GLOVES, COLLAR, HELMET;

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}

		public String getDefaultIcon() {
			switch (this) {
			case BOOTS:
				return "Icon-32-C_Armor-Feet01.png";
			case CHEST:
				return "Icon-32-C_Armor-Chest01.png";
			case ARMS:
				return "Icon-32-Armor-Arms01.png";
			case LEGGINGS:
				return "Icon-32-C_Armor-Legs01.png";
			case COLLAR:
				return "Icon-32-Armor-Neck01.png";
			case BELT:
				return "Icon-32-Armor-Belts01.png";
			case GLOVES:
				return "Icon-32-C_Armor-Hands01.png";
			}
			return null;
		}

		public EquipType toEquipType() {
			switch (this) {
			case BOOTS:
				return EquipType.FEET;
			case CHEST:
				return EquipType.CHEST;
			case ARMS:
				return EquipType.ARMS;
			case LEGGINGS:
				return EquipType.LEGS;
			case COLLAR:
				return EquipType.COLLAR;
			case BELT:
				return EquipType.BELT;
			case GLOVES:
				return EquipType.HANDS;
			case HELMET:
				return EquipType.HEAD;
			}
			return null;
		}
	}

	public enum Gender {

		MALE, FEMALE;

		public static Gender fromCode(char code) {
			if (code == 'f') {
				return FEMALE;
			}
			return MALE;
		}

		public char toCode() {
			return name().toLowerCase().charAt(0);
		}

		@Override
		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	public enum Body {

		NORMAL('n'), MUSCULAR('m'), ROTUND('r');
		private char code;

		private Body(char code) {
			this.code = code;
		}

		public static Body fromCode(char c) {
			for (Body b : values()) {
				if (b.getCode() == c) {
					return b;
				}
			}
			return null;
		}

		public String getIcon() {
			switch (this) {
			case MUSCULAR:
				return "Icon-CC-BodyType-Muscular.png";
			case ROTUND:
				return "Icon-CC-BodyType-Rotund.png";
			default:
				return "Icon-CC-BodyType-Normal.png";
			}
		}

		public char getCode() {
			return code;
		}

		@Override
		public String toString() {
			return Icelib.toEnglish(name(), true);
		}

		public String getMeshSuffix() {
			switch (this) {
			case MUSCULAR:
				return "-Muscular";
			case ROTUND:
				return "-Rotund";
			default:
				return "";
			}
		}
	}

	public enum Head {

		NORMAL(0), SERIOUS(1), WISE(2);
		private int code;

		private Head(int code) {
			this.code = code;
		}

		public static Head fromCode(int c) {
			for (Head b : values()) {
				if (b.getCode() == c) {
					return b;
				}
			}
			return null;
		}

		public String getIcon() {
			switch (this) {
			case WISE:
				return "Icon-CC-FaceStyle-Old.png";
			case SERIOUS:
				return "Icon-CC-FaceStyle-Intense.png";
			default:
				return "Icon-CC-FaceStyle-Normal.png";
			}
		}

		public int getCode() {
			return code;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	public enum Race {
		// TODO - Not sure about Sylvan

		ANURA('a'), ATAVIAN('v'), ANUBIAN('i'), BANDICOON('d'), BOUNDER('b'), BROCCAN('r'), CAPRICAN('c'), DAEMON('e'), DRYAD(
				'q'), FANGREN('g'), FELINE('f'), FOXEN('x'), HART('h'), LISIAN('l'), LONGTAIL('o'), NOCTARI(
						'n'), TAURIAN('t'), TROBLIN('s'), TUSKEN('k'), URSINE('u'), YETI('y'), CLOCKWORK('w'), CYCLOPS('p');
		private char code;

		private Race(char code) {
			this.code = code;
		}

		public char getCode() {
			return code;
		}

		public static Race fromCode(char c) {
			for (Race b : values()) {
				if (b.getCode() == c) {
					return b;
				}
			}
			return null;
		}

		public String toString() {
			return Icelib.toEnglish(name(), true);
		}
	}

	private Name name;
	private SquirrelTable ob;

	public Appearance() {
		ob = new SquirrelTable();
		ob.insert("sz", 1.0d);
		setName(Name.C2);
		setRace(Race.HART);
		setBody(Body.NORMAL);
		setGender(Gender.MALE);
		setHead(Head.WISE);
	}

	@Override
	public Appearance clone() {
		Appearance a = new Appearance();
		try {
			a.parse(toString());
		} catch (IOException ex) {
			Logger.getLogger(Appearance.class.getName()).log(Level.SEVERE, null, ex);
		}
		return a;
	}

	public Appearance(String appearance) throws IOException {
		parse(appearance);
	}

	public void parse(String appearance) throws IOException {
		int idx = appearance.indexOf(':');
		name = null;
		if (idx != -1) {
			String nameText = appearance.substring(0, idx);
			for (Name n : Name.values()) {
				if (n.name().toLowerCase().equals(nameText)) {
					name = n;
					break;
				}
			}
			if (name != null) {
				appearance = appearance.substring(idx + 1);
			}
		}
		ob = SquirrelInterpretedTable.table(appearance);
	}

	public String getBodyTemplate() {
		Object object = ob.get("c");
		if (object instanceof String) {
			return (String) object;
		}
		return null;
	}

	public void setBodyTemplate(String bodyTemplate) {
		if (bodyTemplate == null) {
			ob.remove("c");
		} else {
			ob.insert("c", bodyTemplate);
		}
	}

	@SuppressWarnings("unchecked")
	public ClothingList getClothing() {
		ClothingList clothes = new ClothingList();
		Object object = ob.get("c");
		if (object != null && object instanceof SquirrelTable) {
			SquirrelTable eo = (SquirrelTable) object;
			for (Object key : eo.keySet()) {
				SquirrelTable val = (SquirrelTable) eo.get(key);
				final ClothingItem e = new ClothingItem(ClothingType.valueOf(((String) key).toUpperCase()),
						new ClothingTemplateKey((String) val.get("type")), (String) val.get("effect"),
						Icelib.toRGBList((List<String>) val.get("colors")));
				clothes.add(e);
			}
		}
		return clothes;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public void addOrUpdateSkinElement(SkinElement element) {
		SquirrelTable root = ob;
		SquirrelTable object = (SquirrelTable) root.get("sk");
		if (object == null) {
			object = new SquirrelTable();
			root.insert("sk", object);
		}
		object.insert(element.getName(), Icelib.toHexString(element.getColor()));
	}

	public SkinElement getSkinElement(String name) {
		for (SkinElement el : getSkinElements()) {
			if (el.getName().equals(name)) {
				return el;
			}
		}
		return null;
	}

	public List<SkinElement> getSkinElements() {
		List<SkinElement> skinElements = new ArrayList<Appearance.SkinElement>();
		SquirrelTable object = (SquirrelTable) ob.get("sk");
		if (object != null) {
			for (Object key : object.keySet()) {
				Color color = Color.BLACK;
				try {
					color = new Color((String) object.get(key));
				} catch (IllegalArgumentException iae) {
					LOG.warning(String.format("Invalid name/colour pair %s", key));
				}
				skinElements.add(new SkinElement((String) key, color));
			}
		}
		return skinElements;
	}

	public void setSkinElements(List<SkinElement> skinElements) {
		SquirrelTable eo = new SquirrelTable();
		for (SkinElement el : skinElements) {
			eo.insert(el.getName(), Icelib.toHexNumber(el.getColor()));
		}
		ob.insert("sk", eo);
	}

	public void setClothing(ClothingList clothing) {
		SquirrelTable eo = new SquirrelTable();
		for (ClothingItem el : clothing) {
			SquirrelTable i = new SquirrelTable();
			i.insert("type", el.getKey().getName());
			SquirrelArray a = new SquirrelArray();
			if (el.getColors() != null) {
				for (RGB rgb : el.getColors()) {
					a.add(Icelib.toHexNumber(rgb).toLowerCase());
				}
			}
			i.insert("colors", a);
			eo.insert(el.getType().name().toLowerCase(), i);
		}
		ob.insert("c", eo);
	}

	public void setProp(String prop) {
		ob.insert("a", prop);
	}

	public String getProp() {
		if (Name.P1.equals(getName())) {
			SquirrelTable root = ob;
			return (String) root.get("a");
		}
		return null;
	}

	public void removeAttachment(AttachmentItem item) {
		List<AttachmentItem> a = getAttachments();
		a.remove(item);
		setAttachments(a);
	}

	public void addAttachment(AttachmentItem item) {
		List<AttachmentItem> a = getAttachments();
		a.add(item);
		setAttachments(a);
	}

	public List<AttachmentItem> getAttachments() {
		List<AttachmentItem> l = new ArrayList<AttachmentItem>();
		SquirrelTable root = (SquirrelTable) ob;
		Object object = root.get("a");
		if (object != null && object instanceof SquirrelArray) {
			for (Object o : ((SquirrelArray) object)) {
				l.add(AttachmentItem.createAttachment((SquirrelTable) o));
			}
		}
		return l;
	}

	public void setAttachments(List<AttachmentItem> attachments) {
		SquirrelTable root = (SquirrelTable) ob;
		SquirrelArray object = (SquirrelArray) root.get("a");
		if (object == null) {
			object = new SquirrelArray();
			root.insert("a", object);
		}
		object.clear();
		for (AttachmentItem ai : attachments) {
			object.add(ai.toSerializable());
		}
	}

	public float getSize() {
		Object object = ob.get("sz");
		return object == null ? 1 : Float.parseFloat(object.toString());
	}

	public void setSize(float size) {
		ob.insert("sz", String.valueOf(size));
	}

	public void setBody(Body body) {
		ob.insert("b", String.valueOf(body.getCode()));
	}

	public Body getBody() {
		String object = (String) ob.get("b");
		return object == null || object.length() == 0 ? null : Body.fromCode(object.charAt(0));
	}

	public void setHead(Head head) {
		ob.insert("h", Long.valueOf(head.getCode()));
	}

	public Race getRace() {
		String object = (String) ob.get("r");
		return object == null || object.length() == 0 ? null : Race.fromCode(object.charAt(0));
	}

	public void setRace(Race race) {
		ob.insert("r", String.valueOf(race.getCode()));
	}

	public Head getHead() {
		Long object = (Long) ob.get("h");
		return object == null ? null : Head.fromCode(object.intValue());
	}

	public Gender getGender() {
		String object = (String) ob.get("g");
		return object == null || object.length() == 0 ? null : Gender.fromCode(object.toLowerCase().charAt(0));
	}

	public float getEarSize() {
		return Float.parseFloat(String.valueOf(ob.get("es", "1")));
	}

	public float getTailSize() {
		return Float.parseFloat(String.valueOf(ob.get("ts", "1")));
	}

	public void setTailSize(float tailSize) {
		ob.insert("ts", String.valueOf(tailSize));
	}

	public void setEarSize(float earSize) {
		ob.insert("es", String.valueOf(earSize));
	}

	public void setGender(Gender gender) {
		ob.insert("g", String.valueOf(gender.toCode()));
	}

	public String toString() {
		StringWriter sw = new StringWriter();
		SquirrelPrintWriter w = new SquirrelPrintWriter(sw);
		SquirrelPrinter.format(w, ob, -1);
		return (name == null ? "" : (name.name().toLowerCase() + ":")) + sw.toString();
	}

	public Map<String, RGB> getSkinMap() {
		Map<String, RGB> m = new LinkedHashMap<String, RGB>();
		for (SkinElement el : getSkinElements()) {
			m.put(el.getName(), el.getColor());
		}
		return m;
	}
}
