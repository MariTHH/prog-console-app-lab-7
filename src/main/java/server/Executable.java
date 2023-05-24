package server;


import common.network.CommandResult;
import common.network.Request;

import java.sql.SQLException;

/**
 * Interface for commands
 */
public interface Executable {
    CommandResult execute(Request<?> request) throws SQLException;
}