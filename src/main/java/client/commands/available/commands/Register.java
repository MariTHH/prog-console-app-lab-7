package client.commands.available.commands;

import client.RequestManager;
import common.data.User;
import common.network.CommandResult;
import common.network.Request;

public class Register extends Login{
    public Register(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    public String getName() {
        return "register";
    }

    public void execute(){
        Request<User> request = new Request<>(getName(), null, null);
        CommandResult result = requestManager.sendRequest(request);
        System.out.println(result.message);
    }

    @Override
    public String getDescription() {
        return "зарегистрировать пользователя";
    }

    public boolean canExecute() {
        return true;
    }
}
