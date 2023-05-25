package client.commands.available.commands;

import client.MainClient;
import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;

import javax.xml.bind.JAXBException;

/**
 * Command show. Output to the standard output stream all elements of the collection in string representation
 */
public class Show extends Command {

    public Show(RequestManager requestManager) {
        super(requestManager);

    }

    /**
     * send a request with the show command
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            MainClient.logger.warn("Вы неправильно ввели команду");
        } else {
            Request<String> request = new Request<>(getName(), null, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
            } else
                MainClient.logger.warn("Ошибка");
        }
    }


    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "show: вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }

}
