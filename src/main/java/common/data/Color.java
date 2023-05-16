package common.data;

import java.io.Serial;

/**
 * Enum with colors and codes for it
 */
public enum Color {
    GREEN(1),
    RED(2),
    BLACK(3),
    BLUE(4),
    YELLOW(5),
    ORANGE(6),
    WHITE(7);
    @Serial
    private static final long serialVersionUID = 0xABAB;
    private final int code;

    Color(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
