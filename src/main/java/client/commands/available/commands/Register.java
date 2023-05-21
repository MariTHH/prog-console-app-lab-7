package client.commands.available.commands;

import client.ReadManager;
import client.RequestManager;
import client.commands.Command;
import common.Confident;
import common.data.User;
import common.network.CommandResult;
import common.network.Request;

import java.util.Scanner;

public class Register extends Login{
    public Register(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    public String getName() {
        return "register";
    }

    public void execute(String[] args) {
        boolean flag = false;
        while (!flag) {
            Scanner scanner = new Scanner(System.in);
            String username = ReadManager.readName(scanner);
            Request<String> request1 = new Request<>("check_register", username, null);
            CommandResult result1 = requestManager.sendRequest(request1);
            System.out.println(result1.message);
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

    @Override
    public String getDescription() {
        return "зарегистрировать пользователя";
    }

    public boolean canExecute() {
        return true;
    }
}
