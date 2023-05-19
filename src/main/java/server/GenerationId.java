package server;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A class that creates a unique id for each character
 */
public class GenerationId {

    private static final Set<Integer> arg = new HashSet<>();
    private static final Random random = new Random();

    /**
     * generates a unique id greater than zero
     *
     * @return id (int)
     */
    public static int generateID() {
        int id = 1;
        while (arg.contains(id)) {
            id +=1;
        }
        arg.add(id);
        return id;
    }
}