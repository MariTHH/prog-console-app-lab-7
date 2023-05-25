package client.commands.available.commands;

import client.MainClient;
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
        return "login: авторизация пользователя";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            MainClient.logger.warn("Вы неправильно ввели команду");
        } else {
            boolean flag = false;
            while (!flag) {
                Scanner scanner = new Scanner(System.in);
                String username = ReadManager.readName(scanner);
                Request<String> request1 = new Request<>("check_user", username, null);

                CommandResult result1 = requestManager.sendRequest(request1);
                if (result1.status) {
                    flag = true;
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
        }
    }

    public boolean canExecute() {
        return true;
    }
}
