package client.commands.available.commands;

import client.MainClient;
import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;

import javax.xml.bind.JAXBException;

/**
 * filter_greater_than_location location :
 * display elements whose location field value is greater than the given one
 */
public class FilterGreaterThanLocation extends Command {

    public FilterGreaterThanLocation(RequestManager requestManager) {
        super(requestManager);
    }

    /**
     * send command and coordinate x to server
     *
     * @param args - coordinate x
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
        return "filter_greater_than_location";
    }

    @Override
    public String getDescription() {
        return "filter_greater_than_location: вывести элементы, значение поля location которых больше заданного";
    }
}
