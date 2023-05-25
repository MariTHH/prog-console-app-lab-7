package client.commands.available.commands;

import client.MainClient;
import client.RequestManager;
import client.commands.Command;
import common.network.CommandResult;
import common.network.Request;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * exit : terminate the program (without saving to a file)
 */
public class Exit extends Command {
    public Exit() {
        super();
    }

    /**
     * send exit to server
     */
    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            MainClient.logger.warn("Вы неправильно ввели команду");
        } else {
            Request<?> request = new Request<String>(getName(), null, null);
            CommandResult result = requestManager.sendRequest(request);
            if (result.status) {
                System.out.println((result.message));
            } else {
                MainClient.logger.warn("Ошибка");
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
