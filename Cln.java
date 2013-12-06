/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package srvcln;


import java.net.*; import java.util.*; import java.io.*;

class Cln {  
    // Logic nu se pot face ambele simultan pe acelasi fir de executie, astfel
    // in programul de mai jos vom pastra firul principal pentru a trimite mesaje catre server
    // si vom crea un Thread nou pentru a primi mesaje de la server
    
  public static void main(String[] args) throws Exception {
    Scanner sc = new Scanner(System.in); 
    System.out.print("Alegeti  nickul : ");
    String nick = "";
    nick = sc.nextLine();
    String st = "";
    System.out.print("Adresa serverului si portul : ");
    String ip = sc.next();
    int port = sc.nextInt();
    Socket cs = new Socket( ip, port ); // instantiem un socket la adresa si portul citit de la tastatura
    // socket-ul va fi obiectul ce se va conecta la server si prin intermediul caruia vom transmite si primi date
    
    DataOutputStream os = new DataOutputStream( cs.getOutputStream()); // declaram un obiect de tipul DataOutputStream
    // ce va lucra cu fluxul de iesire al socket-ului
     
     os.writeUTF(nick);
   final DataInputStream is = new DataInputStream( cs.getInputStream()); // analog pentru fluxul de intrare
    try{
        String reply = "";
        reply = is.readUTF();
        if(reply.equals("connected")){
            System.out.println("Connectare reusita");
        }
        else
            if(reply.equals("fexit")){
                System.out.println("Un utilizator cu acest nume este deja conectat! Alegeti alt nume");
                System.exit(0);
            }
            
    }
    catch (Exception e){}
    
    Thread T= new Thread(new Runnable(){  // declaram si instantiam Thread-ul ce se va ocupa cu primirea
        // mesajelor de la server
             public void run() { // metoda ce va fi apelata cand thread-ul este pornit
                
                 
                 
              while (true) {// blocam firul printr-un loop infinit ce primeste mesaje de la server
                  String s = ""; // 
                  try {
                      s = is.readUTF();   // primim mesajul de la server
                      System.out.println(s);
                      if(s.equals("exit")){
                          System.out.println("Deconectare reusita!");
                          System.exit(0);
                      }
                                          
                      if(s.equals("USERS")){
                          String allusers = is.readUTF();
                          String[] parts = allusers.split("-");
                          for(int p = 0 ; p < parts.length ; p++)
                             System.out.println(parts[p]);
                          }
                      
                     // System.out.println(s); // afisam ce am primit
                  }
                  catch (IOException ex) {
                      System.out.println("Serverul s-a deconectat");
                      System.exit(0);
                  }
                 
              }
          }
      });
      T.start(); // pornim threadul 

      while (true) { // blocam firul principal cu un loop infinit care citeste de la tastatura mesaje
          // si le trimite prin DataOutputStream in fluxul de iesire al socket-ului catre server
          st = sc.nextLine();
          os.writeUTF(st);
          
      }
    
    
  }
}