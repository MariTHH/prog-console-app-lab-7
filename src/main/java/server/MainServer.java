package server;

import common.Configuration;
import common.DataManager;
import common.data.Person;
import common.network.CommandResult;
import common.network.Request;
import server.request.process.ReadRequest;
import server.request.process.RequestProcess;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The class accepts requests from the client, connects to it, and starts executing commands
 */
public class MainServer {
    private static int port = Configuration.PORT;
    private static final ExecutorService readRequestThreadPool = Executors.newCachedThreadPool();

    /**
     * Start server, connect to the client and get requests with a collection and commands, execute them
     *
     * @param args - port
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception exception) {
                System.out.println("Не получается спарсить порт. Используется " + port);
            }
        }
        String[] loginData = Parser.getLoginData();
        if (loginData == null) {
            return;
        }

        DBManager dbManager;
        try {
            dbManager = new DBManager(Configuration.jdbcLocal, loginData[0], loginData[1]);
            dbManager.connectDB();
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        DataManager dataManager;
        try {
            dataManager = new PersonCollection(dbManager);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            System.out.println("Сервер запущен. Порт: " + port);
        } catch (IOException exception) {
            System.out.println("Ошибка запуска сервера!");
            System.out.println(exception.getMessage());
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Выход");
            save(dataManager, "s");
        }));

        Service service = new Service(dataManager, dbManager);

        AtomicBoolean exit = new AtomicBoolean(false);
        getUserInputHandler(dataManager, exit).start();

        while (!exit.get()) {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null) continue;
                RequestProcess requestProcess = new RequestProcess(service);
                readRequestThreadPool.submit(new ReadRequest(socketChannel, requestProcess));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }

    /**
     * Running two commands on the server
     * save and exit
     *
     * @param dataManager - class with commands
     * @param exit        - command exit
     * @return
     */
    private static Thread getUserInputHandler(DataManager dataManager, AtomicBoolean exit) {
        return new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                if (scanner.hasNextLine()) {
                    String serverCommand = scanner.nextLine();
                    if (serverCommand.contains("save")) {
                        serverCommand = serverCommand.split(" ")[1];
                        save(dataManager, String.valueOf(serverCommand));
                        return;
                    }
                    if (serverCommand.equals("exit")) {
                        exit.set(true);
                        return;
                    } else {
                        System.out.println("Такой команды нет");
                    }
                } else {
                    exit.set(true);
                }
            }
        });
    }

    /**
     * save collection to file
     *
     * @param filename - file in which server load collection, s - default
     */
    private static void save(DataManager dataManager, String filename) {
        dataManager.save(filename);
    }

}
