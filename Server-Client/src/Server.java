import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
     static Server server;
     ServerSocket serversocket;
    Map<Integer,java.net.Socket> clientsmap=new HashMap<Integer,java.net.Socket>();
    int port = 8085;
    int clientscounter =0;
     ExecutorService executor=Executors.newFixedThreadPool(10);    // 10 clients.     

    public static void main(String[] args) throws IOException {
        server=new Server();
        server.runfirst();
    }

    private void runfirst() {        
        try {
    		System.out.println("Creating server socket on port " + port);
            serversocket=new ServerSocket(port); 

            while(true) {
                System.out.println("Waiting for a client to connect");
                try {
                    Socket s=serversocket.accept();
                    clientsmap.put(s.getPort(), s);
                    executor.submit(new clientrequest(s));
                    clientscounter++;
                } catch(IOException error) {
                }
            }
        }catch(IOException error) {
        }
    }

    class clientrequest implements Runnable {

         Socket socket;
         clientrequest(Socket s) {
         socket = s;
        }

        public void run() {
        	try {
        		while (true ) {
        		OutputStream outputsream = socket.getOutputStream();
    			PrintWriter printstream = new PrintWriter(outputsream, true);
    			printstream.println('\n');

    			printstream.println("Type in a message to send");

    			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    			 String string=br.readLine();
    			//System.out.println("a client with port ' "+socket.getPort()+ "' Just said:" + string + '\n');	// test for recieving messages
    			
    			for (Iterator<Integer> iter = clientsmap.keySet().iterator(); iter.hasNext(); )
    			{
    			    int i = iter.next();
    			    java.net.Socket client = clientsmap.get(i);
    			    OutputStream os = client.getOutputStream();
    			    OutputStreamWriter oswriter = new OutputStreamWriter(os);
    			    BufferedWriter buffer = new BufferedWriter(oswriter);
    			    buffer.write("Client number: '"+ clientscounter +"' with port number '"+client.getPort()+"' just said:"+string);
    			    buffer.flush();
    			}
        		}
        	}catch(IOException error) {
            }   	

        }        
    }
}