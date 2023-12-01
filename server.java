import java.io.*;
import java.net.*;
import java.util.*;

public class server extends Thread{
    static Vector<Socket> connessioni = new Vector<Socket>();
    //ricezione messaggi
    BufferedReader in;
    //output sul client
    PrintWriter out;
    static int id=0;

    public server(Socket client){
        connessioni.addElement(client);
        System.out.println("Grandezza dell'array: "+connessioni.size());
       try{
        out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
       }catch (IOException e) {}
        start();
    }

        @Override
        public void run() {
            String nome;
            Socket client;
            boolean chiudi=false;
            //prendiamo dal vettore il client che invia il messaggio e incrementiamo la variabile id 
            client = connessioni.elementAt(id++); 
                //System.out.println(client + " " + id);
            try{ 
                out.println("Inserire un nome: "); //se non viene inserito un nome valido non si può accedere alla chat
                nome = in.readLine();
                System.out.println(nome + " si è connesso");
                while (chiudi==false){
                    String messaggio= in.readLine();
                    if (messaggio.equals("/esci")){
                        chiudi=true;
                        break;
                    }
                    for (int i=0; i<connessioni.size(); i++){
                        Socket destinatario= new Socket();
                        destinatario=connessioni.elementAt(i);
                                    //System.out.println("Numero i: "+i+" Socket: "+ destinatario);
                        //broadcast a tutti i client eccetto il mittente
                        if (destinatario!=client) {
                           new PrintWriter(destinatario.getOutputStream(),true).println(nome + ": " + messaggio);
                            salvataggio(nome + ": " + messaggio);
                        }
                    }
                }
                if (chiudi==true) {
                    System.out.println("Chiuso");
                    chiudiClient(client,in,out);
                }
            }catch(Exception e) {
                chiudiClient(client,in,out);
            }
            
        }


        public void uscitaChat (){
            connessioni.remove(this);
        }



        public void chiudiClient (Socket client, BufferedReader in, PrintWriter out){
            uscitaChat();
            try{
                if (in!=null) in.close();
                if (out!=null) out.close();
                if (client!=null) client.close();
            }catch (Exception e){
                //TODO: gestire 
            }
        }


    
    public static void main (String [] args) throws IOException{
        try {
            ServerSocket socketBenvenuto = new ServerSocket();
            socketBenvenuto.bind(new InetSocketAddress("localhost", 9999));
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







/**
 * //scrittura file
        


//lettura file
        try{
            BufferedReader in=new BufferedReader(new FileReader("storico.txt"));
            String line = in.readLine();
            //ciclo fino a qunado il file non è vuoto
            while (line!=null){
                System.out.println(line);
                line = in.readLine();

            }  
            in.close();      
        }catch (Exception e){}
 */
    }//main

    public static void salvataggio (String messaggio){
            try{
            FileWriter scrittore= new FileWriter("storico.txt");
            PrintWriter scrivi= new PrintWriter(scrittore);
            scrivi.println(messaggio);
            scrittore.close();
        }catch (Exception e){}
            }

    



}
