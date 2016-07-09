/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Enc {

	public static final String MAGIC = "!@ENC/PF_0";
	public static final byte[] HEADER = MAGIC.getBytes();
	public static String CIPHER = "AES/CFB8/NoPadding";
	// static String CIPHER = "AES/CBC/PKCS5Padding";

	public static SecretKeySpec createKey() throws Exception {
		return createKey("password123?", "12345678");
	}

	public static SecretKeySpec createKey(String p, String s) throws Exception {
		return createKey(p.toCharArray(), p.getBytes("UTF-8"));
	}

	public static SecretKeySpec createKey(char[] p, byte[] s) throws Exception {
		/* Derive the key, given password and salt. */
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(p, s, 65536, 128);
		SecretKey tmp = factory.generateSecret(spec);
		return new SecretKeySpec(tmp.getEncoded(), "AES");
	}
}
