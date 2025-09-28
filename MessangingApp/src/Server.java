import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    ServerSocket server;
    public static Boolean done;
    ExecutorService thread_pool;
    List<ClientHandler> client_list;

    @Override
    public void run() {
        done = false;
        thread_pool = Executors.newCachedThreadPool();
        client_list = new ArrayList<>();
        try {
            server = new ServerSocket(5100);
            while(!done){
                Socket client = server.accept();
                ClientHandler new_client = new ClientHandler(client, client.getPort());
                client_list.add(new_client);
                thread_pool.execute(new_client);
                System.out.println(client_list);
            }
            done = true;
            thread_pool.close();
            server.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class ClientHandler implements Runnable {
        Socket client;
        PrintWriter out;
        BufferedReader in;
        String user_port;
        String recieverPort;
        public ClientHandler(Socket client_socket, Integer port){
            this.client = client_socket;
            this.user_port = port.toString();
        }

        @Override
        public void run() {
            try {
                done = false;
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
                String msg = "";
                while (!msg.equals("Over")){
                    msg = in.readLine();
                    if (msg != null){
                        recieverPort = msg.split(">")[1];
                    }
                    if (findUserSocket(recieverPort)!=null){
                        ClientHandler reciever = findUserSocket(recieverPort);
                        System.out.println(msg);
                        System.out.println(reciever);
                        reciever.out.println(msg);
                    }
                }
                done = true;
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public ClientHandler findUserSocket(String recieverPort){
            for (ClientHandler client : client_list){
                if (client.user_port.equals(recieverPort)){
                    return client;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
