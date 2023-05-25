package client;

import common.data.*;

import java.util.*;

/**
 * The class is responsible for creating the character
 */
public class ClientManager {
    /**
     * creates a person whose data is recorded in the console
     *
     * @param sc - scanner
     * @return new Person
     */
    public static Person getNewPerson(Scanner sc) {
        String name = ReadManager.readName(sc);
        Long X = ReadManager.readCoordinatesX(sc);
        int Y = ReadManager.readCoordinatesY(sc);
        Double X2 = ReadManager.readLocationX(sc);
        Float Y2 = ReadManager.readLocationY(sc);
        String location = ReadManager.readLocationName(sc);
        Integer height = ReadManager.readHeight(sc);
        Color eyeColor = ReadManager.readColor(sc);
        Color hairColor = ReadManager.readColor(sc);
        Country nationality = ReadManager.readCountry(sc);
        return new Person(name, new Coordinates(X, Y), height, eyeColor, hairColor, nationality, new Location(X2, Y2, location));
    }

    /**
     * creates a person whose data is recorded in the script
     *
     * @param data - characteristics of person from the script
     * @return
     */
    public static Person createPersonFromScript(ArrayList<String> data) {
        try {
            if ((!data.get(0).equals("")) &&
                    (Long.parseLong(data.get(1)) > 0) &&
                    Integer.parseInt(data.get(2)) > 0 &&
                    Double.parseDouble(data.get(3)) != 0 &&
                    (Float.parseFloat(data.get(4)) > 0 && (!data.get(5).equals("") && data.get(5).length() <= 944)) &&
                    (Integer.parseInt(data.get(6)) > 0) &&
                    (ReadManager.doesThisColorTypeExist(data.get(7))) &&
                    (ReadManager.doesThisColorTypeExist(data.get(8))) &&
                    (ReadManager.doesThisCountryTypeExist(data.get(9)))) {
                return new Person(data.get(0),
                        new Coordinates(Long.parseLong(data.get(1)), Integer.parseInt(data.get(2))), Integer.parseInt(data.get(6)),
                        Enum.valueOf(Color.class, data.get(7)),
                        Enum.valueOf(Color.class, data.get(8)),
                        Enum.valueOf(Country.class, data.get(9)),
                        new Location(Double.parseDouble(data.get(3)), Float.parseFloat(data.get(4)), data.get(5)));

            } else {
                MainClient.logger.warn("Неправильно введены данные");
                return null;
            }
        } catch (NumberFormatException e) {
            MainClient.logger.error("Неправильно введены данные");
            return null;
        }

    }


}

