package client;

import common.Configuration;
import common.network.Request;
import common.network.CommandResult;
import server.PersonCollection;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

/**
 * The class creates requests for the server
 */
public class RequestManager {
    private int port = Configuration.PORT;
    protected final int max_attempts = 5;

    public RequestManager() {

    }

    public RequestManager(int port) {
        this.port = port;
    }

    /**
     * method sends the collection to the server and gets the result from the server
     *
     * @param request request - collection read from file
     * @return result
     */
    public PersonCollection sendCollection(Request<?> request) throws IOException, ClassNotFoundException {
        Socket socket1 = new Socket(Configuration.IP, port);

        OutputStream send = socket1.getOutputStream();
        ObjectOutputStream objectSend = new ObjectOutputStream(send);
        objectSend.writeObject(request);

        InputStream receive = socket1.getInputStream();
        ObjectInputStream objectReceive = new ObjectInputStream(receive);
        CommandResult result = (CommandResult) objectReceive.readObject();
        return result.getPersonCollection();
    }

    /**
     * The method gets the command and the arguments that the client entered the console, passes it to the server
     * for execution, tries to connect for 30 seconds
     *
     * @param request request - command entered by the client and its arguments
     * @return result
     */
    public CommandResult sendRequest(Request<?> request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос является null");
        }

        int attempt = 0;
        while (attempt < max_attempts) {
            try {
                Socket socket = new Socket(Configuration.IP, port);

                OutputStream send = socket.getOutputStream();
                ObjectOutputStream objectSend = new ObjectOutputStream(send);
                objectSend.writeObject(request);

                InputStream receive = socket.getInputStream();
                ObjectInputStream objectReceive = new ObjectInputStream(receive);
                CommandResult result = (CommandResult) objectReceive.readObject();

                if (attempt != 0) {
                    System.out.println("Подключение установлено");
                }
                attempt = max_attempts;
                return result;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Не удалось подключиться к серверу. Пробуем еще.");
                attempt++;
                try {
                    Thread.sleep(6000);
                } catch (Exception ignored) {
                }
            }
        }
        return new CommandResult(false, "Прошло 30 секунд, сервер не отвечает.");
    }
}
