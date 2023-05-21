package client;

import client.commands.CommandManager;
import common.Configuration;
import common.network.Request;
import server.PersonCollection;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.ConnectException;
import java.util.Scanner;

import static server.Parser.convertToJavaObject;

/**
 * The class starts the client, passes requests to the server
 */
public class MainClient {
    private static int port = Configuration.PORT;

    /**
     * Start client, send collection and commands to server
     *
     * @param args - port and file with collection
     */
    public static void main(String[] args) throws IOException, JAXBException, ClassNotFoundException {
        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception exception) {
                System.out.println("Не получается спарсить порт.");
            }
        }
        try {
            RequestManager requestManager = new RequestManager(port);
            Scanner scanner = new Scanner(System.in);
            CommandManager commandManager = new CommandManager(requestManager);
            System.out.println("Клиент запущен! Порт: " + port);
            PersonCollection collection = new PersonCollection();

            if (args.length == 2) {
                File file = new File(args[1]);
                if (file.exists() && !file.isDirectory()) {
                    collection.setCollection(convertToJavaObject(file).getCollection());
                    Request<PersonCollection> request = new Request<>(null, collection, collection);
                    PersonCollection result = requestManager.sendCollection(request);
                    result.getCollection();
                    collection.setCollection(result.getCollection());
                } else {
                    Console console = new Console();
                    console.fileRead();
                }
            }
            String input;
            boolean flag = true;
            do {
                if (flag) {
                    System.out.println("Введите команду:(login - для входа, register - для регистрации)");
                } else {
                    System.out.println("Введите команду");
                }
                if (!scanner.hasNextLine()) return;
                input = scanner.nextLine();
                if (input.equals("login") || input.equals("register")) {
                    flag = false;
                }
                try {
                    commandManager.existCommand(input);
                } catch (Exception e) {
                    System.out.println("Ошибка");
                }
            } while (!input.equals("exit"));
        } catch (ConnectException e) {
            System.out.println("Вы не подключены к серверу");
        }
    }
}
