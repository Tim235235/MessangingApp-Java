import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client implements Runnable{
    Socket client;
    PrintWriter out;
    BufferedReader in;
    String username;
    Integer port;
    public String recieverPort;

    public Client(String username) throws IOException {
        client = new Socket("127.0.0.1", 5100);
        this.username = username;
        this.port = client.getLocalPort();
        this.recieverPort = "";
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            if (Server.done){
                in.close();
                out.close();
                client.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
