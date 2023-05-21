package server.request.process;

import common.network.CommandResult;
import common.network.Request;
import server.Service;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class RequestProcess {
    private Service service;

    public RequestProcess(Service service) {
        this.service = service;
    }

    public CommandResult processRequest(Request<?> request, ExecutorService requestThread) {
        RequestExecutor requestExecutor = new RequestExecutor(request);
        Future<CommandResult> commandResultFuture = requestThread.submit(requestExecutor);
        while (true) {
            if (commandResultFuture.isDone()) {
                try {
                    return commandResultFuture.get();
                } catch (InterruptedException | ExecutionException exception) {
                }
            }
        }
    }

    private class RequestExecutor implements Callable<CommandResult> {

        private Request<?> request;

        public RequestExecutor(Request<?> request) {
            this.request = request;
        }

        @Override
        public CommandResult call() throws AccessDeniedException, SQLException {
            CommandResult result = service.executeCommand(request);
            if (result.status)
                System.out.println("Команда выполнена успешно");
            else
                System.out.println("Команда выполнена неуспешно");
            return result;
        }
    }
}
