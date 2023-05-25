package client.commands.available.commands;

import client.MainClient;
import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;


/**
 * Command remove_greater {element} : remove all elements from the collection that exceed the specified
 */
public class RemoveGreater extends Command {

    public RemoveGreater(RequestManager requestManager) {
        super(requestManager);
    }

    /**
     * send a request with a command and height to the server
     *
     * @param args - height
     */
    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            MainClient.logger.warn("Вы неправильно ввели команду");
        } else {
            Request<String> request = new Request<>(getName(), args[1], null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
            } else
                MainClient.logger.warn("Ошибка");
        }
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "remove_greater: удалить из коллекции все элементы, превышающие заданный";
    }
}
