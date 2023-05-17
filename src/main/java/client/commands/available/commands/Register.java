package client.commands.available.commands;

import client.RequestManager;

public class Register extends Login{
    public Register(RequestManager requestManager) {
        super(requestManager);
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "зарегистрировать пользователя";
    }

    public boolean canExecute() {
        return true;
    }
}
