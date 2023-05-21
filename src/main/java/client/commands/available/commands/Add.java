package client.commands.available.commands;

import client.*;
import client.commands.Command;
import common.data.Person;
import common.network.*;
import server.PersonCollection;

import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Command add {element}
 */
public class Add extends Command {


    public Add(RequestManager requestManager) {
        super(requestManager);


    }


    /**
     * add a new element to the collection
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("Вы неправильно ввели команду");
        } else if (ExecuteScript.getFlag()) {
            Person newPerson = ClientManager.createPersonFromScript(ExecuteScript.getPersonList());
            Request<Person> request = new Request<>(getName(), newPerson, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
                System.out.println("Ваш персонаж теперь в коллекции");
            } else {
                System.out.println("Ошибка");
            }
        } else {
            Scanner sc = new Scanner(System.in);
            Person newPerson = ClientManager.getNewPerson(sc);
            Request<Person> request = new Request<>(getName(), newPerson, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
                System.out.println("Ваш персонаж теперь в коллекции");
            } else {
                System.out.println("Ошибка");
            }
        }
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "add : добавить новый элемент в коллекцию";
    }


}
