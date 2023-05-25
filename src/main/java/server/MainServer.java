package server;


import client.commands.available.commands.Exit;
import common.Configuration;
import common.DataManager;
import server.request.process.ReadRequest;
import server.request.process.RequestProcess;

import java.io.*;
import java.net.InetSocketAddress;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class accepts requests from the client, connects to it, and starts executing commands
 */
public class MainServer {
    public static final Logger logger = LoggerFactory.getLogger("server.logger");
    private static int port = Configuration.PORT;
    private static final ExecutorService readRequestThreadPool = Executors.newCachedThreadPool();

    /**
     * Start server, connect to the client and get requests with commands, execute them
     *
     * @param args - port
     */
    public static void main(String[] args) {
        MainServer.logger.info("The program started.");
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception exception) {
                MainServer.logger.error("Не получается спарсить порт");
            }
        }
        String[] loginData = Parser.getLoginData();

        DBManager dbManager;
        try {
            dbManager = new DBManager(Configuration.jdbcLocal, loginData[0], loginData[1]);
            dbManager.connectDB();
        } catch (Exception exception) {
            MainServer.logger.error("Не удалось выполнить подключение к базе данных");
            return;
        }

        DataManager dataManager;
        try {
            dataManager = new PersonCollection(dbManager);
        } catch (Exception exception) {
            MainServer.logger.warn("Ошибка во время подключения к бд");
            return;
        }

        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            MainServer.logger.info("Сервер запущен. Порт: " + port);
        } catch (IOException exception) {
            MainServer.logger.warn("Ошибка запуска сервера!");
            return;
        }

        Service service = new Service(dataManager, dbManager);

        AtomicBoolean exit = new AtomicBoolean(false);
        getUserInputHandler(exit).start();

        while (!exit.get()) {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                RequestProcess requestProcess = new RequestProcess(service);
                readRequestThreadPool.submit(new ReadRequest(socketChannel, requestProcess));
            } catch (NullPointerException | IOException | RejectedExecutionException exception) {
                MainServer.logger.warn("Ошибка, канал пуст");
            }
        }
    }

    /**
     * Running two commands on the server
     * save and exit
     *
     * @return
     */
    private static Thread getUserInputHandler(AtomicBoolean exit) {
        return new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {

                if (scanner.hasNextLine()) {
                    String serverCommand = scanner.nextLine();
                    Exit exit1 = new Exit();

                    if (serverCommand.equals(exit1.getName())) {
                        try {
                            readRequestThreadPool.shutdown();
                            throw new RejectedExecutionException();
                        } catch (RejectedExecutionException e) {
                            MainServer.logger.error("Все пока");
                            exit.set(true);
                            return;
                        }

                    } else {
                        MainServer.logger.info("Такой команды нет");
                        return;
                    }
                }
            }
        });
    }
}

