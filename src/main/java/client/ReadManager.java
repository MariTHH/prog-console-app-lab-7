package client;

import common.data.Color;
import common.data.Country;
import java.io.Console;

import java.util.*;

/**
 * The class is responsible for what the user enters
 */
public class ReadManager {

    public static String takePassword(Scanner scanner) {
        String password;
        Console console = System.console();
        while (true) {
            System.out.println("Введите пароль:");
            if (console != null) {
                char[] letters = console.readPassword();
                if (letters == null) continue;
                password = String.valueOf(letters);
            } else password = scanner.nextLine();
            if (password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[_0-9a-zA-Z!@#$%^&*]{5,25}")){
                return password;
            }
            else {
                System.out.println("В пароле обязательно от 5 до 25 символов (минимум 1 цифра, 1 заглавная и 1 строчная буква латинского алфавита)");
            }
        }
    }


    /**
     * method checks if the name is entered correctly, it contains only letters or not
     *
     * @param sc
     * @return name
     */

    public static String readName(Scanner sc) throws IllegalArgumentException {
        System.out.println("Введите имя ");
        String name = sc.nextLine().trim();
        while (true) {
            if (name.equals("")) {
                System.out.println("Имя не может быть пустой строкой, введите имя");
                name = sc.nextLine().trim();
            } else if (!name.matches("^[a-zA-Z-А-Яа-я]*$")) {
                System.out.println("Имя не может быть иными знаками кроме букв");
                name = sc.nextLine().trim();
            } else {
                return name;
            }
        }
    }

    /**
     * checks if the entered coordinate is correct, whether a number is entered or not
     *
     * @param sc
     * @return X
     */
    public static Long readCoordinatesX(Scanner sc) {
        System.out.println("Введите координату X");
        while (true) {
            try {
                String X_string = sc.nextLine().trim();
                long X = Long.parseLong(X_string);
                if (!X_string.equals("")) {
                    return X;
                } else {
                    System.out.println("Вы должны ввести число, а не пустую строку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");
            }
        }
    }

    /**
     * checks if the entered coordinate is correct, whether a number is entered or not
     *
     * @param sc
     * @return Y
     */
    public static int readCoordinatesY(Scanner sc) {
        System.out.println("Введите координату Y");
        while (true) {
            try {
                String Y_string = sc.nextLine().trim();
                int Y = Integer.parseInt(Y_string);
                if (!Y_string.equals("")) {
                    return Y;
                } else {
                    System.out.println("Вы должны ввести число, а не пустую строку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");
            }
        }
    }

    /**
     * checks the correctness of the entered growth, whether the number is entered or not, check for zero
     *
     * @param sc
     * @return height
     */
    public static Integer readHeight(Scanner sc) {
        System.out.println("Введите рост персонажа(не больше 2 147 483 647)");
        while (true) {
            String height_string = sc.nextLine().trim();
            try {
                int height = Integer.parseInt(height_string);
                if (height > 0) {
                    return height;
                } else {
                    System.out.println("Рост должен быть больше 0");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");
            }
        }
    }

    /**
     * checks if the location name is correct, it must be less than 944 and not null
     *
     * @param sc
     * @return location
     */
    public static String readLocationName(Scanner sc) {
        System.out.println("Введите название локации");
        String location = sc.nextLine().trim();
        while (true) {
            if (location.length() >= 944 || location.equals("")) {
                while (location.length() >= 944) {
                    System.out.println("Слишком длинное название");
                    location = sc.nextLine().trim();
                }
                while (location.equals("")) {
                    System.out.println("Название не может быть пустой строкой");
                    location = sc.nextLine().trim();
                }
            } else if (!location.matches("^[a-zA-Z-А-Яа-я]*$")) {
                System.out.println("Имя не может быть иными знаками кроме букв");
                location = sc.nextLine().trim();
            } else {
                return location;
            }

        }
    }

    public static Double readLocationX(Scanner sc) {
        System.out.println("Введите координату X для локации");
        while (true) {
            try {
                String X_s = sc.nextLine().trim();
                double X = Double.parseDouble(X_s);
                if (!X_s.equals("")) {
                    return X;
                } else {
                    System.out.println("Вы должны ввести число, а не пустую строку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");
            }
        }
    }

    public static Float readLocationY(Scanner sc) {
        System.out.println("Введите координату Y для локации");
        while (true) {
            try {
                String Y_s = sc.nextLine().trim();
                float Y = Float.parseFloat(Y_s);
                if (!Y_s.equals("")) {
                    return Y;
                } else {
                    System.out.println("Вы должны ввести число, а не пустую строку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");
            }
        }
    }

    /**
     * checks the existence of the entered color
     *
     * @param colors
     * @return boolean true or false
     */
    public static boolean doesThisColorTypeExist(String colors) {
        for (Color ourColor : Color.values()) {
            if (ourColor.name().equals(colors)) {
                return true;
            }
        }
        return false;
    }

    public static Color readColor(Scanner sc) {
        System.out.println("Выберите любой цвет из предложенных " + "GREEN(1) RED(2) BLACK(3) BLUE(4) YELLOW(5) ORANGE(6) WHITE(7)");
        while (true) {
            try {
                String type = sc.nextLine().trim();
                int code = Integer.parseInt(type);
                if (!type.equals("")) {
                    for (Color ourColor : Color.values()) {
                        if (ourColor.getCode() == code) {
                            String color = String.valueOf(ourColor);
                            return Enum.valueOf(Color.class, color);
                        }
                    }
                    {
                        System.out.println("Данного цвета не существует. Выберите цвет из списка!!");
                    }

                } else {
                    System.out.println("Вы должны ввести число, а не пустую строку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");

            }
        }
    }

    /**
     * checks the existence of the entered country
     *
     * @param country
     * @return
     */
    public static boolean doesThisCountryTypeExist(String country) {
        for (Country ourCountry : Country.values()) {
            if (ourCountry.name().equals(country)) {
                return true;
            }
        }
        return false;
    }

    public static Country readCountry(Scanner sc) {
        System.out.println("Выберите любую страну из предложенных " + "USA(1) SPAIN(2) CHINA(3) ITALY(4) JAPAN(5)");
        while (true) {
            try {
                String type = sc.nextLine().trim();
                int code = Integer.parseInt(type);
                if (!type.equals("")) {
                    for (Country country : Country.values()) {
                        if (country.getCode() == code) {
                            String ourCountry = String.valueOf(country);
                            return Enum.valueOf(Country.class, ourCountry);

                        }
                    }
                    {
                        System.out.println("Данной страны не существует. Выберите страну из списка!!");
                    }
                } else {
                    System.out.println("Вы должны ввести число, а не пустую строку");
                }
            } catch (NumberFormatException e) {
                System.out.println("Число введено неверно");
            }
        }
    }
}
