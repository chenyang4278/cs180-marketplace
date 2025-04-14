package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server
 * <p>
 * Simple server that sends new socket connections to a
 * thread pool using ClientHandler.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class Server implements IServer {
    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();

        ServerSocket server = new ServerSocket(8727);

        while (true) {
            Socket socket = server.accept();
            pool.submit(new ClientHandler(socket));
        }
    }
}
