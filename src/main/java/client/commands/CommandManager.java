package client.commands;

import client.RequestManager;
import client.commands.available.commands.*;
import server.PersonCollection;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The class is responsible for checking for the correctness of commands and running them
 */
public class CommandManager {
    private PersonCollection personCollection;
    private static boolean isWorking = true;
    private static HashMap<String, Command> commandMap = new HashMap<String, Command>();
    private static String filelink;
    private RequestManager requestManager;

    /**
     * creates a commandMap with commands
     */
    public CommandManager(RequestManager requestManager) {
        commandMap = new HashMap<>();
        this.requestManager = requestManager;
        initializeCommand(new Add(requestManager));
        initializeCommand(new AddIfMax(requestManager));
        initializeCommand(new AddIfMin(requestManager));
        initializeCommand(new Show(requestManager));
        initializeCommand(new Clear(requestManager));
        initializeCommand(new Info(requestManager));
        initializeCommand(new Exit(requestManager));
        initializeCommand(new Help(requestManager));
        initializeCommand(new CountGreaterThanEyeColor(requestManager));
        initializeCommand(new FilterGreaterThanLocation(requestManager));
        initializeCommand(new PrintUniqueLocation(requestManager));
        initializeCommand(new RemoveById(requestManager));
        initializeCommand(new RemoveGreater(requestManager));
        initializeCommand(new Update(requestManager));
        initializeCommand(new ExecuteScript(personCollection));
        initializeCommand(new Login(requestManager));
        initializeCommand(new Register(requestManager));
    }

    /**
     * checks for the correctness of the command and starts
     */
    public void existCommand(String input) {
        String[] args = input.trim().split(" ");
        try {
            String command = args[0];

            String argument;

            if (args.length == 1)
                argument = null;
            else if (args.length == 2)
                argument = args[1];
            else {
                System.out.println("Проблема с аргументом, обратитесь к команде help");
                return;
            }
            Command commanda = commandMap.get(command);
            if (requestManager.getUser()!=null || commanda.canExecute()) {
                if (commandMap.containsKey(command)) {
                    commandMap.get(command).setArgument(argument);
                    commandMap.get(command).execute(args);
                } else {
                    System.out.println("Команды " + args[0] + " не существует");
                }
            } else {
                System.out.println("Пользователь не авторизирован");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Команда введена неверно");
            isWorking = false;
            AtomicBoolean exit = new AtomicBoolean(false);
            exit.set(true);
        }

    }


    public static HashMap<String, Command> getCommandMap() {
        return commandMap;
    }

    /**
     * get command to map
     *
     * @param command - command from client
     */
    void initializeCommand(Command command) {
        if (commandMap.containsKey(command.getName())) {
            throw new IllegalArgumentException("Данная команда уже есть");
        }
        commandMap.put(command.getName(), command);
    }


}




