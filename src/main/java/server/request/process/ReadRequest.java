package server.request.process;

import common.network.CommandResult;
import common.network.Request;
import server.MainServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class read client requests
 */
public class ReadRequest implements Runnable {
    private SocketChannel socketChannel;
    private RequestProcess requestProcess;
    private final ExecutorService requestThread = Executors.newCachedThreadPool();
    private final ExecutorService requestThread1 = Executors.newFixedThreadPool(10);

    public ReadRequest(SocketChannel socketChannel, RequestProcess requestProcess) {
        this.socketChannel = socketChannel;
        this.requestProcess = requestProcess;
    }

    /**
     * read request with command
     */
    @Override
    public void run() {
        Request<?> request;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socketChannel.socket().getInputStream());
            request = (Request<?>) objectInputStream.readObject();
            System.out.println(socketChannel.getRemoteAddress() + ": " + request.command);
            CommandResult result = requestProcess.processRequest(request, requestThread);
            requestThread1.submit(new Reply(socketChannel, result));
        } catch (IOException | ClassNotFoundException exception) {
            MainServer.logger.error("Запрос не выполнен");
        }
    }
}
