package common;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password hashing
 */
public class Confident {
    public static String encode(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(Configuration.CONFIG);
            byte[] digest = messageDigest.digest(string.getBytes(StandardCharsets.UTF_8));
            BigInteger num = new BigInteger(1, digest);
            StringBuilder hashedString = new StringBuilder(num.toString(16));
            while (hashedString.length() < 32)
                hashedString.insert(0, "0");
            return hashedString.toString();
        } catch (NoSuchAlgorithmException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }
}
