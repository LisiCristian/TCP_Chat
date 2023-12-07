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



    public static void main (String[] args) throws IOException{
        try {
            // creazione socket
            System.out.println("Client avviato...");
            Socket client= new Socket();
            client.connect(new InetSocketAddress(InetAddress.getLocalHost(),16999));       //getByName("letsgoski.sytes.net")
            // Creazione degli stream di input e output dal socket
            out= new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedReader syn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Client Socket: "+ client);
            System.out.println("Benvenuto nella chat, puoi usare i comandi:\n/esci per disconetterti.\n/storico per visualizzare lo storico dei messaggi.");
            System.out.println("Inserire un nome: ");
            String messaggio;
            do{
                messaggio = syn.readLine();
                out.println(messaggio);
                messaggio=in.readLine();
                if (messaggio.equals("usato")) System.out.println("Nome già usato, reinserire: ");
                if (messaggio.equals("invalido")) System.out.println("Nome non valido, reinserire: ");
            }while (!messaggio.equals("true"));
           

            new client();
            
            



            boolean scrivi=true;    //booleana per la visualizzazione (o non) sul terminale di "Scrivi:"
            while (off==false){   //ciclo fino a quando il client non decide di disconnettersi

                if (scrivi==true) System.out.println("Scrivi: ");

                if ((scrivi==false)) {
                    System.out.print("Storico: \n");
                scrivi=true;
            }
                messaggio = syn.readLine();
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
