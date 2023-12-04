import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class server extends Thread{
    static Vector<Socket> connessioni = new Vector<Socket>();
    static Vector<String> nomi = new Vector<String>();
    BufferedReader in;  //ricezione messaggi
    PrintWriter out;    //output sul client
    static int id=0;

    public server(Socket client){
        connessioni.addElement(client);
       try{
        out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
       }catch (IOException e) {}
        start();    // Avvio del thread
    }

        @Override
        public void run() {     //thread che gestisce l'invio dei messaggi di ogni client
            String nome;
            Socket client;
            boolean chiudi=false;
            int myId = id++;    // Memorizza l'id del thread corrente e incrementiamo la variabile id 
            client = connessioni.elementAt(myId);   //prendiamo dal vettore il client che invia il messaggio
            try{ 
                out.println("Benvenuto nella chat, puoi usare i comandi:\n/esci per disconetterti.\n/storico per visualizzare lo storico dei messaggi.");
                out.println("Inserire un nome: "); //se non viene inserito un nome valido non si può accedere alla chat
                nome = in.readLine();
                while ((nome.trim().isEmpty())||(nome.contains("/"))||(nomi.contains(nome))){      // Verifica se il nome è vuoto o contiene /
                    if (!nomi.contains(nome)) out.println("Inserire un nome valido che non contenga \"/\": ");
                    else out.println("Nome già in uso, reinserire: ");
                    nome = in.readLine();
                }
                nomi.addElement(nome);
                System.out.println(nome + " connesso");
                while (chiudi==false){  //ciclo fino a quando il client non si disconnette
                    String messaggio= in.readLine();
                    if (messaggio.equals("/esci")){
                        chiudi=true;
                        break;
                    }
                    if (messaggio.toLowerCase().equals("/storico")){
                        storico(out);
                    }
                    else{
                        for (int i=0; i<connessioni.size(); i++){
                            Socket destinatario= new Socket();
                            destinatario=connessioni.elementAt(i);
                            //broadcast a tutti i client eccetto il mittente
                            if (destinatario!=client) {
                                new PrintWriter(destinatario.getOutputStream(),true).println(nome + ": " + messaggio);
                            }
                        }
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy").withZone(ZoneId.of("Europe/Rome")).withLocale(Locale.ITALY);
                        LocalDateTime dataora = LocalDateTime.of(LocalDate.now(), LocalTime.now());
                        if(!messaggio.toLowerCase().equals("/storico"))  salvataggio(nome + ": " + messaggio +" <"+dataora.format(formatter)+">");
                    } 
                }
                if (chiudi==true) {
                    System.out.println(nome+" disconnesso");
                    chiudiClient(client,in,out,myId);
                }
            }catch(Exception e) {
                chiudiClient(client,in,out,myId);
            }
        }


        public void uscitaChat (int myId){
            connessioni.removeElementAt(myId); //Rimuove il client dal vettore delle connessioni 
            nomi.removeElementAt(myId);
            id--;   //Decrementa l'id per mantenere la coerenza nel vettore
        }


        public void chiudiClient (Socket client, BufferedReader in, PrintWriter out,int myId){
            uscitaChat(myId);
            try{
                // Chiude gli stream di input e output e il socket del client
                if (in!=null) in.close();
                if (out!=null) out.close();
                if (client!=null) client.close();
            }catch (Exception e){
                //ignora
            }
        }


    public static void main (String [] args) throws IOException{
        try {
            ServerSocket socketBenvenuto = new ServerSocket();
            socketBenvenuto.bind(new InetSocketAddress("localhost", 5678));
            System.out.println("Server in ascolto sulla porta locale di benvenuto: "+ socketBenvenuto.getLocalPort());
            
            
            while (true) {
                
                Socket client = socketBenvenuto.accept();
                
                System.out.println("Connessione accettata, socket client: " + client);
                new server(client);
            }    
        } catch (IOException e) { 
            System.out.println("Accept fallito");
            System.exit(1);
            }
    }//fine main


    //scrittura file
    public static void salvataggio (String messaggio){
        try{
            FileWriter scrittore= new FileWriter("storico.txt",true);
            PrintWriter scrivi= new PrintWriter(scrittore);
            scrivi.println(messaggio);
            scrittore.close();
        }catch (Exception e){}
    }

    
    //lettura file
    public static void storico(PrintWriter out){
        try{
            BufferedReader in=new BufferedReader(new FileReader("storico.txt"));
            String line = in.readLine();
            //ciclo fino a quando il file non è vuoto
            while (line!=null){
                out.println(line);
                line = in.readLine();
            }  
            in.close();
            out.println("\nScrivi: ");      
        }catch (Exception e){}
    }


}
