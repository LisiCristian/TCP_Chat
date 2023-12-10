//Versione finale 2
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
    Socket client;

    public server(Socket client){
        this.client = client;
        try{
            out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream())),true);
            in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        }catch (IOException e) { System.out.println(e); }
        start();    // Avvio del thread
    }

        @Override
        public void run() {     //thread che gestisce l'invio dei messaggi di ogni client
            String nome = null;
            try{
                nome = in.readLine();
                while (nomi.contains(nome)||(nome.trim().isEmpty())||(nome.contains("/"))){
                    if (nomi.contains(nome)) out.println("usato");
                    else if ((nome.trim().isEmpty())||(nome.contains("/"))) out.println("invalido");
                    nome=in.readLine();
                }  
                out.println("true");
                connessioni.add(client); //una volta effettuato l'accesso possiamo memorizzare il client nella lista dei socket    
                nomi.addElement(nome);

                System.out.println(nome + " connesso");
                
                broadcast( (nome+" connesso"), client);
                while (true){  //ciclo fino a quando il client non si disconnette
                    String messaggio= in.readLine();
                    if (messaggio.toLowerCase().equals("/esci")) break;
                    
                    else if (messaggio.toLowerCase().equals("/storico")) storico(out);
                    else{
                        if(!messaggio.startsWith("/")){
                            messaggio = nome + ": " + messaggio;
                            broadcast (messaggio,client);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss dd/MM/yyyy").withZone(ZoneId.of("Europe/Rome")).withLocale(Locale.ITALY);
                            LocalDateTime dataora = LocalDateTime.of(LocalDate.now(), LocalTime.now());
                            //memorizza tutti i messaggi che non iniziano con "/"
                            salvataggio(messaggio +" <"+dataora.format(formatter)+">");
                        }
                    } 
                }
                System.out.println(nome+" disconnesso");
                broadcast(nome+" ha lasciato la chat",client);
                chiudiClient(client,in,out, nome);
            }catch(SocketException e) { 
                chiudiClient(client,in,out,nome); 
                if(nome != null) System.out.println(nome+" disconneesso");
            }
            catch (IOException e){ System.out.println(e);}
        }

        public void uscitaChat (String nome){
            connessioni.removeElement(client); //Rimuove il client dal vettore delle connessioni 
            connessioni.trimToSize();
            nomi.removeElement(nome); //Rimuove il nome dalla lista dei nomi
            nomi.trimToSize();
        }
        public void chiudiClient (Socket client, BufferedReader in, PrintWriter out, String nome){
            uscitaChat(nome);
            try{
                // Chiude gli stream di input e output e il socket del client
                if (in!=null) in.close();
                if (out!=null) out.close();
                if (client!=null) client.close();
            }catch (Exception e){ System.out.println(e);}
        }


        public static void broadcast (String messaggio, Socket client){
            for (int i=0; i<connessioni.size(); i++){
                Socket destinatario = connessioni.elementAt(i);
                //broadcast a tutti i client eccetto il mittente
                if (destinatario!=client) {
                    try {
                        new PrintWriter(destinatario.getOutputStream(),true).println(messaggio);
                    } catch (IOException e) { System.out.println(e); }
                }
            }
        }

    public static void main (String [] args) throws IOException{
        ServerSocket socketBenvenuto = new ServerSocket();
        try {
            socketBenvenuto.bind(new InetSocketAddress(InetAddress.getLocalHost(),16999));
            System.out.println("Server in ascolto sulla porta locale di benvenuto: "+ socketBenvenuto.getLocalPort());
            
            while (true) {
                try {
                    Socket client = socketBenvenuto.accept();
                    System.out.println("Connessione accettata, socket client: " + client);
                    new server(client);
                } catch (IOException e) { System.out.println("Accept fallito"); }
            } 
        } catch (Exception e) { 
            System.out.println(e);
            socketBenvenuto.close();
        }
    }//fine main


    //scrittura file
    public static void salvataggio (String messaggio){
        try{
            FileWriter scrittore= new FileWriter("storico.txt",true);
            PrintWriter scrivi= new PrintWriter(scrittore);
            scrivi.println(messaggio);
            scrittore.close();
        }catch (Exception e){ System.out.println(e); }
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
        }catch (Exception e){ System.out.println(e); }
    }
}
