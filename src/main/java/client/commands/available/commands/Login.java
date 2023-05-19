package client.commands.available.commands;

import client.ReadManager;
import client.RequestManager;
import client.commands.Command;
import common.Confident;
import common.data.User;
import common.network.CommandResult;
import common.network.Request;

import java.util.Scanner;

public class Login extends Command {

    public Login(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getDescription() {
        return "проверка пользователя";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("Вы неправильно ввели команду");
        } else {
            Scanner scanner = new Scanner(System.in);
            String username = ReadManager.readName(scanner);
            String password = ReadManager.takePassword(scanner);
            password = Confident.encode(password);
            User user = new User(username, password);
            Request<User> request = new Request<>(getName(), user, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                requestManager.setUser(user);
            }
            System.out.println(result.message);
        }
    }
    public boolean canExecute() {
        return true;
    }
}
