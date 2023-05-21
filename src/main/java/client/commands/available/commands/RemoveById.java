package client.commands.available.commands;

import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;

import javax.xml.bind.JAXBException;
import java.util.Scanner;

/**
 * Command remove_by_id id : remove an item from the collection by its id
 */
public class RemoveById extends Command {

    public RemoveById(RequestManager requestManager) {
        super(requestManager);
    }

    /**
     * If the command is in the script, then the id is asked to enter
     * if not, the id argument
     * send the id and the command
     *
     * @param args - id
     */
    @Override
    public void execute(String[] args) {
        String id;
        if (args.length > 2) {
            System.out.println("Вы неправильно ввели команду");
        } else {
            if (args.length == 1) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Введите ID");
                id = scanner.nextLine();
            } else {
                id = args[1];
            }
            Request<String> request = new Request<>(getName(), id, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
            } else {
                System.out.println("Ошибка");
                System.out.println(result.message);
            }
        }
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getDescription() {
        return "remove_by_id: удалить элемент из коллекции по его id";
    }
}

