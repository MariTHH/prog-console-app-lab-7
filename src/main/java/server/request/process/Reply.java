package server.request.process;

import common.network.CommandResult;
import server.MainServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

/**
 * Class ask for reply to client
 */
public class Reply implements Runnable {
    private SocketChannel socketChannel;
    private CommandResult result;

    public Reply(SocketChannel socketChannel, CommandResult result) {
        this.socketChannel = socketChannel;
        this.result = result;
    }

    /**
     * pass result to client
     */
    @Override
    public void run() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException exception) {
            MainServer.logger.error("Ответ на запрос отсутствует");
        }
    }
}
