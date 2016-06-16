import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by adrien on 16/06/2016.
 */
public class Server {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Need to give address and port");
            return;
        }

        String address = args[0];
        int port = Integer.parseInt(args[1]);
        System.out.println("Listening on " + address + ":" + port);
        new Server(port, address);
    }

    public Server(int port, String address)
    {
        try {
            ServerSocket s = new ServerSocket(port, 50, InetAddress.getByName(address));
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
