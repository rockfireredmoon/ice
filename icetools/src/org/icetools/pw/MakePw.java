package org.icetools.pw;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.icelib.Hash;

public class MakePw {
    public static void main(String[] args)throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Username: ");
        String username = br.readLine();
        System.out.print("Password: ");
        String pw = br.readLine();
        System.out.print("Server (y/n): ");
        String server = br.readLine();
        System.out.println("Hashed: " + Hash.hash(pw, username, server.equalsIgnoreCase("Y")));
    }
}
