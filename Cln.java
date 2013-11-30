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
    String st="";
    System.out.print("Adresa serverului si portul : ");
    
    Socket cs = new Socket( sc.next(), sc.nextInt() ); // instantiem un socket la adresa si portul citit de la tastatura
    // socket-ul va fi obiectul ce se va conecta la server si prin intermediul caruia vom transmite si primi date
    
    DataOutputStream os = new DataOutputStream( cs.getOutputStream()); // declaram un obiect de tipul DataOutputStream
    // ce va lucra cu fluxul de iesire al socket-ului
     
     os.writeUTF(nick);
    boolean fin=true;
    final DataInputStream is = new DataInputStream( cs.getInputStream()); // analog pentru fluxul de intrare
    String reply="";
    
    while(fin==true){
        try{
            reply=is.readUTF();
         }
        catch(IOException e){ continue;}
    while(reply.equals("try again"))
    {
        System.out.println("Nick deja folosit!Introdu un alt nick");
        nick=sc.nextLine();
         while(nick.equals(""))
         {
             nick=sc.nextLine();
         }
        os.writeUTF(nick);
        reply=is.readUTF();
       
    }
    if(reply.equals("connected"))
        fin=false;
     System.out.println(reply);
    }
    
    Thread T= new Thread(new Runnable(){  // declaram si instantiam Thread-ul ce se va ocupa cu primirea
        // mesajelor de la server
             public void run() { // metoda ce va fi apelata cand thread-ul este pornit
              while (true) {// blocam firul printr-un loop infinit ce primeste mesaje de la server
                  String s = ""; // 
                  try {
                      s = is.readUTF();   // primim mesajul de la server
                      
                      if(s.equals("exit")){System.out.println("Deconectare reusita!");
                      System.exit(0);}
                                            
                      if(s.equals("USERS")){
                          String allusers=is.readUTF();
                          String[] parts = allusers.split("-");
                          for(int p=0;p<parts.length;p++)
                             System.out.println(parts[p]);
                          
                      }
                      
                     // System.out.println(s); // afisam ce am primit
                  }
                  catch (IOException ex) {
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