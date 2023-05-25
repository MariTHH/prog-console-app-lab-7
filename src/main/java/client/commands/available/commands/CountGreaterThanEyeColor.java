package client.commands.available.commands;

import client.MainClient;
import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;
import server.PersonCollection;

import javax.xml.bind.JAXBException;


/**
 * count_greater_than_eye_color eyeColor
 */
public class CountGreaterThanEyeColor extends Command {

    public CountGreaterThanEyeColor(RequestManager requestManager) {
        super(requestManager);
    }

    /**
     * send command and color to server
     *
     * @param args - color
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
        return "count_greater_than_eye_color";
    }

    @Override
    public String getDescription() {
        return "count_greater_than_eye_color: вывести количество элементов, значение поля eyeColor которых больше заданного";
    }
}

