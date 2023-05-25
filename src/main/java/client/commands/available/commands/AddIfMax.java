package client.commands.available.commands;

import client.ClientManager;
import client.MainClient;
import client.RequestManager;
import client.commands.Command;
import common.data.Person;
import common.network.CommandResult;
import common.network.Request;

import java.util.Scanner;


/**
 * add_if_max {element}
 */
public class AddIfMax extends Command {

    public AddIfMax(RequestManager requestManager) {
        super(requestManager);
    }

    /**
     * add a new element to a collection if its value exceeds the value of the largest element of that collection
     *
     * @param args - height
     */
    @Override
    public void execute(String[] args) {
        int height = 0;
        Person newPerson;
        try {
            if (args.length > 2) {
                MainClient.logger.warn("Вы неправильно ввели команду");
            } else {
                height = Integer.parseInt(args[1]);
                Request<Integer> request = new Request<>(null, height, null);
                CommandResult result = requestManager.sendRequest(request);
                if (result.status) {
                    if (ExecuteScript.getFlag()) {
                        ExecuteScript.getPersonList().set(6, args[1]);
                        newPerson = ClientManager.createPersonFromScript(ExecuteScript.getPersonList());
                    } else {
                        newPerson = ClientManager.getNewPerson(new Scanner(System.in));
                    }
                    assert newPerson != null;
                    newPerson.setHeight(height);
                    Request<Person> request1 = new Request<>(getName(), newPerson, null);
                    CommandResult result1 = requestManager.sendRequest(request1);

                    if (result1.status) {
                        System.out.println((result.message));
                        MainClient.logger.info("Ваш персонаж теперь в коллекции");
                    } else
                        MainClient.logger.warn("Ошибка");
                } else {
                    MainClient.logger.warn("Ваш персонаж не самый высокий!!");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            MainClient.logger.warn("Недостаточно аргументов, обратитесь к команде help");
        }
    }


    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getDescription() {
        return "add_if_max: добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }

}