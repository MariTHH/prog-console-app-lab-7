package client.commands.available.commands;

import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * exit : terminate the program (without saving to a file)
 */
public class Exit extends Command {
    public Exit(RequestManager requestManager) {
        super(requestManager);
    }

    /**
     * send exit to server
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            System.out.println("Вы неправильно ввели команду");
        } else {
            Request<?> request = new Request<String>(getName(), null, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
            } else {
                System.out.println("Ошибка");
            }

        }
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "exit: завершить программу (без сохранения в файл)";
    }
}
