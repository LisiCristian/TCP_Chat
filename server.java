import java.io.*;
import java.net.*;
import java.util.*;

public class server extends Thread{
    static Vector<Socket> connessioni = new Vector<Socket>();
    int id=0;

        @Override
        public void run() {
            BufferedReader in;
            //output sul client
            PrintWriter out;
            String nome;
            Socket client;
            client = connessioni.elementAt(id);
            id++;
            
            try{
                out=new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Inserire un nome: ");
                nome = in.readLine();
                System.out.println(nome + " connesso");
                while (true){
                    String messaggio= in.readLine();
                    for (int i=0; i<connessioni.size();i++){
                        Socket g= new Socket();
                        g=connessioni.elementAt(i);
                        //broadcast a tutti i client eccetto il mittente
                        if ((g!=null)&&(g!=client)) {
                            out.println(nome+": "+messaggio);
                            out.flush();
                        }
                    }
                }
            }catch(IOException e) {}
        }


    
    public static void main (String [] args) throws IOException{
        try {
            ServerSocket socketBenvenuto = new ServerSocket();
            socketBenvenuto.bind(new InetSocketAddress("localhost", 9999));
            System.out.println("Server in ascolto sulla porta locale di benvenuto: "+ socketBenvenuto.getLocalPort());
            
            

            while (true) {
                server t= new server();
                Socket client = socketBenvenuto.accept();
                connessioni.add(client);
                System.out.println("Connessione accettata, socket client: " + client);
                t.start();
            }    
        } catch (IOException e) { 
            System.out.println("Accept fallito");
            System.exit(1);
            }



/**
 * //scrittura file
        try{
            FileWriter scrittore= new FileWriter("storico.txt");
            PrintWriter scrivi= new PrintWriter(scrittore);
            scrivi.println("ciao\nciao");
            scrittore.close();
        }catch (Exception e){}


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



    



}