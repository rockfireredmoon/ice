/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 33 or higher
 */
package org.icelib;

import java.io.IOException;

import org.junit.Test;

public class AppearanceTest {

	@Test
	public void test() throws IOException {
		Appearance app = new Appearance(
				"c2:{[\"h\"]=0,[\"c\"]={[\"boots\"]={[\"type\"]=\"Armor-Heavy-Mid3\",[\"colors\"]=[\"d4d4d4\",\"634a12\",\"949494\",\"4c485f\",\"3f0d11\",\"a77d19\"]},[\"chest\"]={[\"type\"]=\"Armor-Heavy-Mid3\",[\"colors\"]=[\"d4d4d4\",\"634a12\",\"949494\",\"4c485f\",\"3f0d11\",\"a77d19\"]},[\"arms\"]={[\"type\"]=\"Armor-Heavy-Mid3\",[\"colors\"]=[\"d4d4d4\",\"634a12\",\"949494\",\"4c485f\",\"3f0d11\",\"a77d19\"]},[\"leggings\"]={[\"type\"]=\"Armor-Light-High1\",[\"colors\"]=[\"8c8eaa\",\"d4d4d4\",\"949494\",\"4c485f\",\"3f0d11\"]},[\"collar\"]={[\"type\"]=\"Armor-Heavy-Mid3\",[\"colors\"]=[\"d4d4d4\",\"634a12\",\"949494\",\"4c485f\",\"3f0d11\",\"a77d19\"]},[\"belt\"]={[\"type\"]=\"Armor-Heavy-Mid3\",[\"colors\"]=[\"d4d4d4\",\"634a12\",\"949494\",\"4c485f\",\"3f0d11\",\"a77d19\"]},[\"gloves\"]={[\"type\"]=\"Armor-Heavy-Mid3\",[\"colors\"]=[\"d4d4d4\",\"634a12\",\"949494\",\"4c485f\",\"3f0d11\",\"a77d19\"]}},[\"b\"]=\"m\",[\"sk\"]={[\"chest_ankles\"]=\"AA8E76\",[\"spots\"]=\"9B8E61\",[\"beak\"]=\"E9E9E9\",[\"beard\"]=\"BFA149\",[\"nails\"]=\"745F56\",[\"mask\"]=\"BFA149\",[\"base\"]=\"AA8E76\",[\"eye_lid\"]=\"BFA149\",[\"neck_belly\"]=\"817650\",[\"eye\"]=\"B11DFF\",[\"eye_brows\"]=\"AA8E76\",[\"head_top\"]=\"BFA149\",[\"hands\"]=\"AA8E76\"},[\"g\"]=\"m\",[\"r\"]=\"n\"}");
		System.out.println("Name: " + app.getName());
		System.out.println("Template: " + app.getBodyTemplate());
		System.out.println("Ear Size: " + app.getEarSize());
		System.out.println("Prop: " + app.getProp());
		System.out.println("Size: " + app.getSize());
		System.out.println("Tail Size: " + app.getTailSize());
		System.out.println("Gender: " + app.getGender());
		System.out.println("Head: " + app.getHead());
		System.out.println("Race: " + app.getRace());
		System.out.println("Body: " + app.getBody());

		System.out.println();

		System.out.println("Attachments");
		for (AttachmentItem it : app.getAttachments()) {
			System.out.println(" Key: " + it.getKey());
			System.out.println(" Effect: " + it.getEffect());
			System.out.println(" Node: " + it.getNode());
			System.out.println(" Colours: " + it.getColors());
			System.out.println();
		}

		System.out.println();
		System.out.println("Clothing");
		for (ClothingItem s : app.getClothing()) {
			System.out.println(" Key: " + s.getKey());
			System.out.println(" Effect: " + s.getEffect());
			System.out.println(" Node: " + s.getNode());
			System.out.println(" Type: " + s.getType());
			System.out.println(" Colours: " + s.getColors());
			System.out.println();

		}
	}

}
