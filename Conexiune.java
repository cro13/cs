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
public class Conexiune extends Thread{
    int identitate; 
    Socket cs = null;
    InputStream is = null;
    public Conexiune(Socket client, int i)throws IOException {
        cs = client; 
    identitate = i;
    is = cs.getInputStream();
    start();
    }
    
    public void run() {
        try {
            
          BufferedReader rd=new BufferedReader(new InputStreamReader(is));
          String msg=rd.readLine();
          
            System.out.println("Clientul " + identitate +" a transmis:");
          System.out.println(msg);
        }
        catch(Exception e)
        {
        }
}
}
