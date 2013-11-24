/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package client;
import java.net.*; 
import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
/**
 *
 * @author Emanuel
 */
public class Client {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        System.out.print("Adresa serverului si portul : ");
        Socket cs = new Socket( sc.next(), sc.nextInt() );
        OutputStream os = cs.getOutputStream();
        String msg="Hello Server";
        DataOutputStream ds = new DataOutputStream(cs.getOutputStream());
        ds.writeBytes("Hello from client: "+msg + '\n');
        
        
    }
    
}
