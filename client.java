// Versione finale
import java.io.*;
import java.net.*;
import java.util.Vector;

public class client extends Thread{

    static Socket client;
    static BufferedReader in;
    static PrintWriter out;
    static boolean off=false; //variabile di spegnimento del client
    static Vector<String> nomi= new Vector<String>();

    public client(){
        start();    // Avvia il thread per la ricezione dei messaggi
    }

    @Override
    public void run(){  //thread per la ricezione dei messaggi                     
        while (off==false){
            try {
                System.out.println(in.readLine());  // Visualizza i messaggi ricevuti dal server
            } catch (IOException e) {
                System.out.println("Errore nella ricezione dal server");
                spegni();   // Se si verifica un errore, chiude il client
                }
        }

    }



    public void invio (){

    }


    public static void main (String[] args) throws IOException{
        try {
            // creazione socket
            System.out.println("Client avviato...");
            Socket client= new Socket();
            client.connect(new InetSocketAddress(InetAddress.getLocalHost(),16999));       //getByName("letsgoski.sytes.net")
            // Creazione degli stream di input e output dal socket
            out= new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            System.out.println("Client Socket: "+ client);
            new client();



            boolean scrivi=true;    //booleana per la visualizzazione (o non) sul terminale di "Scrivi:"
            BufferedReader syn = new BufferedReader(new InputStreamReader(System.in));
            boolean first=true;     //booleana per identificare il primo messaggio inviato (nome)
            while (off==false){   //ciclo fino a quando il client non decide di disconnettersi

                if ((first==false) && (scrivi==true)) System.out.println("Scrivi: ");   //se non è il primo messaggio inviato visualizza sul terminale "Scrivi:"

                if ((scrivi==false)) {
                    System.out.print("Storico: \n");
                scrivi=true;
            }
                String messaggio = syn.readLine();
                //controllo validità del nome inserito
                if (first==true) {
                    if ((!messaggio.trim().isEmpty()) && (!messaggio.contains("/"))) {
                        scrivi=true;
                        first=false;
                    }
                }
                out.println(messaggio);
                if (messaggio.toLowerCase().equals("/storico")) {
                    scrivi=false;
                }
                if (messaggio.toLowerCase().equals("/esci")){
                    scrivi=false;
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
                if (in!=null) in.close();
                if (out!=null) out.close();
                if(client!=null) client.close();
                System.out.println("Client disconnesso.");
            } catch (IOException e) {
                //Ignora
            }
        }

   
}
