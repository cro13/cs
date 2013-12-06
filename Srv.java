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
   
   
    
    start(); // pornim threadu-ul
  }
  
 public void fdc(int j){//functia de deconectare fortata a unui utilizator - cand a ales un nume gresit
    try{
    sockete.get(j).writeUTF("fexit");
    sockete.remove(j);
    Srv.i--;
    os.close();
    is.close();
    cs.close();
    }
    catch(IOException e){
     System.out.println("Deconectare esuata");   
    }
  
}
  
  
  
public void dc(int j){//functia de deconectare a unui utilizator
    try{
   
    System.out.println("Clientul :"+users.get(j)+" s-a deconectat !");
    sockete.get(j).writeUTF("exit");
    users.remove(j);
    sockete.remove(j);
    Srv.i--;
    os.close();
    is.close();
    cs.close();
    }
    catch(IOException e){
     System.out.println("Deconectare esuata");   
    }
  
}
  public void run() {
      String curent="";// metoda ce se apleaza la pornirea threadului
    try {
        int i=identitate;
        System.out.println(i);
        String message = is.readUTF(); // citim un mesaj prin fluxul de intrare
        System.out.println("S-a incercat conectarea cu utilizatorul: " + message);
        while(users.contains(message) == true)
        {
         System.out.println("Un user cu acest nume este deja conectat"); 
         fdc(i);
         return;
        }
        this.users.add(message);
        curent = message;
        sockete.get(i).writeUTF("connected"); 
        System.out.println("Utilizatorul cu numele: "+message+" s-a connectat cu succes!");
        
       while (true) { // blocam firul threadului cu un loop ce primeste mesaje de la client
       
           i=users.indexOf(curent);
           
           message = is.readUTF(); // citim un mesaj prin fluxul de intrare
          
               if(message.equals("QUIT"))
               {
                   i = users.indexOf(curent);
                   dc(i);
                   return;
               }
               else
                   if(message.equals("LIST")){
                       i = users.indexOf(curent);
                       System.out.println(i);
                       String allusers = "";
                       for(String x : users)
                       {
                           allusers += x;
                           allusers+="-";
                       }
                       sockete.get(i).writeUTF("USERS");
                       sockete.get(i).writeUTF(allusers);
                   //System.out.println(parts.length);  
                   }
                   else
                       if(message.contains("NICK")){
                           i = users.indexOf(curent);
                           String[] parts = message.split(" ");
                           String parametru;
                           parametru = parts[1];
                           if(users.contains(parametru) == false)
                               users.set(i,parametru);
                           else
                               sockete.get(i).writeUTF("Userul exista deja! Comanda esuata!");
                       }
                       else
                           if(message.contains("BCAST")){
                               //  i=users.indexOf(curent);
                               String[] parts = message.split(" ");
                               String parametru;
                               parametru = parts[1];
                               for(int t = 0 ; t < sockete.size() ; t++)
                                   sockete.get(t).writeUTF(parametru);
                           }
                           else
                               if(message.contains("MSG")){
                                   i = users.indexOf(curent);
                                   String[] parts = message.split(" ");
                                   String utilizator,msg;
                                   utilizator = parts[1];
                                   msg = parts[2];
                                   int ki = users.indexOf(utilizator);
                                   if(ki != -1)
                                       sockete.get(ki).writeUTF("Utilizatorul "+ users.get(i)+" ti-a transmis mesajul: '"+msg+"'");
                                   else
                                       sockete.get(i).writeUTF("Utilizatorul " +utilizator+" nu exista");
                  
                               } 
                               else
                                   if(!message.equals("") && !message.equals(" ") && !message.equals('\n'))              {
                                       i = users.indexOf(curent);
                                       sockete.get(i).writeUTF("Comanda nu exista");
                                       System.out.println("Comanda negasita");
                                   }
               System.out.println(message);  // il afisam
               // for(i=0;i<sockete.size();i++) // apoi parcurgem lista de clienti conectati la server si le trimitem                   
                 //   sockete.get(i).writeUTF(message);    // mesajul ce tocmai a fost primit
                
       }
    }
    catch(Exception e) {
      
    }
  }
}