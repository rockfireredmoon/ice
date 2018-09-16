package org.icetools.pw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.icelib.Hash;

public class MakePw {

	static String hash(String username, String pw, char mode) {
		switch (mode) {
		case 'Y':
		case 'y':
			return Hash.hash(pw, username, true);
		case 'N':
		case 'n':
			return Hash.hash(pw, username, false);
		case 'S':
		case 's':
			return Hash.generateSalt(Hash.hash(pw, username, false));
		}
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Username: ");
			String username = br.readLine();
			System.out.print("Password: ");
			String pw = br.readLine();
			System.out.print("Server (y/n/s): ");
			String server = br.readLine();
			System.out.println("Hashed: " + hash(username, pw, server.charAt(0)));
		} else {
			System.out.println(hash(args[0], args[1], args[2].charAt(0)));

		}
	}
}
