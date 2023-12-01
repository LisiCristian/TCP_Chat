import java.io.*;
import java.net.*;

public class client extends Thread{

    static Socket client;
    static BufferedReader in;
    static PrintWriter out;
    static boolean off=false;
    

    public client(){
        start();
    }

    @Override
    public void run(){  //thread per la ricezione dei messaggi
        
        while (off==false){
            try {
                System.out.println(in.readLine());
            } catch (IOException e) {
                System.out.println("Errore nella ricezione del messaggio");
                spegni();
                }
        }

    }



    public static void main (String[] args) throws IOException{
        try {
            // creazione socket
            System.out.println("Client avviato...");
            Socket client;
            client = new Socket("localhost",9999);
            // creazione stream di output dal socket
            out= new PrintWriter(client.getOutputStream(), true);
            

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));



            System.out.println("Client Socket: "+ client);
            new client();



            // creazione stream di input da tastiera
            BufferedReader syn = new BufferedReader(new InputStreamReader(System.in));
            boolean first=true;
            while (off==false){   //ciclo fino a quando il client non decide di disconnettersi
                //if (first==false) System.out.print("Scrivi: ");
                //else first=false;
                String messaggio = syn.readLine();
                out.println(messaggio);
                if (messaggio.equals("/esci")){
                    System.out.println("Chiusura...");
                    syn.close();
                    spegni();
                }
            }
        }catch(IOException e){
            spegni();
        }
  
    }


    public static void spegni(){
            off=true;
            try {
                in.close();
                out.close();
                if(client!=null) client.close();
            } catch (IOException e) {/*ignora*/}
        }

   
}
