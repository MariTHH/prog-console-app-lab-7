package common.data;

import java.io.Serial;

/**
 * Enum with countries and codes for it
 */
public enum Country {
    USA(1),
    SPAIN(2),
    CHINA(3),
    ITALY(4),
    JAPAN(5);
    private final int code;
    @Serial
    private static final long serialVersionUID = 0xBABA;

    Country(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
