package common;

import common.network.CommandResult;
import common.network.Request;

/**
 * abstract class with commands
 */
public abstract class DataManager {
    public abstract CommandResult add(Request<?> request);

    public abstract CommandResult addIfMax(Request<?> request);

    public abstract CommandResult addIfMin(Request<?> request);

    public abstract CommandResult show(Request<?> request);

    public abstract CommandResult clear(Request<?> request);

    public abstract CommandResult info(Request<?> request);

    public abstract CommandResult help(Request<?> request);
    public abstract CommandResult exit();

    public abstract CommandResult countEyeColor(Request<?> request);

    public abstract CommandResult filterGreater(Request<?> request);

    public abstract CommandResult printUniqueLocation(Request<?> request);

    public abstract CommandResult remove_by_id(Request<?> request);

    public abstract CommandResult removeGreater(Request<?> request);

    public abstract CommandResult update(Request<?> request);

    public void save(String filename) {
    }
}
