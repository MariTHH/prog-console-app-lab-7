package client;

import client.commands.CommandManager;
import common.Configuration;
import common.network.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public static final Logger logger = LoggerFactory.getLogger("client.logger");

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
                MainClient.logger.error("Не получается спарсить порт.");
            }
        }
        try {
            RequestManager requestManager = new RequestManager(port);
            Scanner scanner = new Scanner(System.in);
            CommandManager commandManager = new CommandManager(requestManager);
            MainClient.logger.info("Клиент запущен! Порт: " + port);
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
                    MainClient.logger.info("login - для входа, register - для регистрации");
                } else {
                    MainClient.logger.info("Введите команду");
                }
                if (!scanner.hasNextLine()) return;
                input = scanner.nextLine();
                if (input.equals("login") || input.equals("register")) {
                    flag = false;
                }
                try {
                    commandManager.existCommand(input);
                } catch (Exception e) {
                    MainClient.logger.info("Сначала авторизируйтесь");
                }
            } while (!input.equals("exit"));
        } catch (ConnectException e) {
            MainClient.logger.warn("Вы не подключены к серверу");
        }
    }
}
