/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author Emanuel
 */
public class Server {

  static int i=0;

public static void main(String[] arg) throws IOException {

    ServerSocket ss = null; Socket cs = null;
    Scanner sc = new Scanner(System.in);
    System.out.print("Portul : ");
        ss = new ServerSocket( sc.nextInt() );
    System.out.println("Serverul a pornit");

    while (true) {
        cs = ss.accept();
        System.out.println("\nClient nou. ");
        new Conexiune(cs,++i);
    }
}
}
