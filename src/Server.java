import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by adrien on 16/06/2016.
 */
public class Server {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Need to give port");
            return;
        }

        int port = Integer.parseInt(args[0]);
        System.out.println("Listening on port " + port);
        new Server(port);
    }

    public Server(int port)
    {
        try {
            ServerSocket s = new ServerSocket(port);
            Socket client;
            while (true) {
                client = s.accept();
                Thread t = new Thread(new Proxy(client));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
