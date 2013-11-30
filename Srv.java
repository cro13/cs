/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srvcln;

import java.io.*;
import java.net.*;
import java.util.*;

class Srv {  
    
  static int i=-1;   // numarul de conexiuni de la deschiderea serverului
  
  public static void main(String[] arg) throws IOException {
      
    /*   In ArrayList-ul "sockete" vom stoca toate DataOutputStreamurile ce se conecteaza
     * la serverul nostru.
     *   DataOutputStream-ul este clasa ce se ocupa de fluxul de iesire al clientului
     * care s-a conecatat la server. In DataOutputStream-ul fiecarui socket ( client )
     * serverul va scrie mesajul ce doreste sa il transmita.
     */     
    ArrayList<DataOutputStream> sockete = new ArrayList<DataOutputStream>();
    ArrayList<String> users=new ArrayList<String>();
    // Obiectele de tip ServerSocket si Socket sunt cele care instantiaza conexiunea dintre Server si Client 
    ServerSocket ss = null; Socket cs = null;
    Scanner sc = new Scanner(System.in); 
    System.out.print("Portul : ");
    ss = new ServerSocket( sc.nextInt() );  // instantiam ServerSocket-ul la portul citit de la tastatura 
    System.out.println("Serverul a pornit");

    while (true) { // Blocam firul principal al serverului cu un loop care asteapta 
        // conexiuni de la clienti si pe fiecare iteratie face urmatoarele:
      cs = ss.accept(); // serverul asteaptea urmatoare conexiune de la un socket, iar cand aceasta se intampla
      // stocheaza valoarea in obiectul cs, de tip socket, pe server
      sockete.add(new DataOutputStream(cs.getOutputStream())); // dupa conexiune adaugam DataOutputStream-ul
      // al socket-ului abea conectat, in lista noastra din server
      System.out.println("\nClient nou. ");
      new Conexiune(cs,++i,sockete,users); // deschidem un thread nou pentru clientul conectat unde dam ca parametrul
      // socket-ul abea conectat, indicele sau si lista cu ceilalti clienti conectati
    }
  }
  
}

class Conexiune extends Thread { // Clasa Conexiune extinde Thread si presupune un proces ce va rula in paralel
    // cu cel principal al serverului, si se va ocupa de primirea si trimiterea mesajelor ce tin
    // de UN SINGUR client ( astfel mai jos regasim variabilele ce ar tine de socket-ul unui client )
    // si lista cu toate DataOutputStreamurile clientilor conectat - > pentru a le putea transmite mesajele mai departe
  int identitate; Socket cs = null; DataInputStream is = null;  DataOutputStream os =null  ;
  static ArrayList<DataOutputStream> sockete;
  static ArrayList<String> users;
  public Conexiune(Socket client, int i,ArrayList<DataOutputStream> sockete,ArrayList<String> users) // constructorul clasei in care primim parametrii
           throws IOException {    // pe care i-am trimis mai sus
    cs = client; identitate = i;  // atribui socket-ul primit de la server celui local threadu-ului, si identitatea
    is = new DataInputStream(cs.getInputStream());  // fluxul de intrare
    this.sockete=sockete;
    this.users=users;
    os = new DataOutputStream(cs.getOutputStream());// cel de iesire
   
    String message = is.readUTF(); // citim un mesaj prin fluxul de intrare
    System.out.println("S-a incercat conectarea cu utilizatorul: "+message);
       while(users.contains(message) == true)
        {
          sockete.get(i).writeUTF("try again");
           message = is.readUTF();
           System.out.println("S-a incercat conectarea cu utilizatorul: "+message);
        }
        this.users.add(message);
        sockete.get(i).writeUTF("connected"); 
        System.out.println("Utilizatorul cu numele: "+message+" s-a connectat cu succes!");
    
    start(); // pornim threadu-ul
  }
public void dc(int j){//functia de deconectare a unui utilizator
    try{
   
    System.out.println("Clientul :"+users.get(j)+" s-a deconectat !");
    sockete.get(j).writeUTF("exit");
    users.remove(j);
    sockete.remove(j);
    Srv.i--;
    //os.close();
   // is.close();
   // cs.close();
    }
    catch(IOException e){
     System.out.println("Deconectare esuata");   
    }
  
}
  public void run() { // metoda ce se apleaza la pornirea threadului
    try {
       while (true) { // blocam firul threadului cu un loop ce primeste mesaje de la client
           int j=identitate;
           int k;
           String curent=users.get(j);
           String message = is.readUTF(); // citim un mesaj prin fluxul de intrare
                     
               if(message.equals("QUIT"))
               {
                   dc(j);
                   return;
               }
               
               if(message.equals("LIST"))
               {
                   k=users.indexOf(curent);
                   System.out.println(k);
                   String allusers="";
                   for(String x:users)
                   {allusers+=x;
                    allusers+="-";
                   }
                   sockete.get(k).writeUTF("USERS");
                   sockete.get(k).writeUTF(allusers);
                   //System.out.println(parts.length);
               
               }
               
               if(message.contains("NICK")){
                   k=users.indexOf(curent);
                   String[] parts = message.split(" ");
                   String cmd,parametru;
                   cmd = parts[0];
                   parametru = parts[1];
                   if(users.contains(parametru)==false)
                         users.set(k,parametru);
                   else
                       sockete.get(k).writeUTF("Userul exista deja! Comanda esuata!");
               }
               
               
                System.out.println(message);  // il afisam
                for(int i=0;i<sockete.size();i++) // apoi parcurgem lista de clienti conectati la server si le trimitem                   
                    sockete.get(i).writeUTF(message);    // mesajul ce tocmai a fost primit
                
            }
    }
    catch(Exception e) { }
  }
}