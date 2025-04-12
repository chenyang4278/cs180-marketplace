package server;

import server.handlers.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();

        ServerSocket server = new ServerSocket(8727);

        while (true) {
            Socket socket = server.accept();
            pool.submit(new ClientHandler(socket));
        }
    }
}
