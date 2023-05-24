package server;

import common.Configuration;

import javax.xml.bind.*;
import java.io.*;
import java.util.Scanner;

/**
 * Parse collection to XML and Java Object
 */
public class Parser {
    public Parser() {
    }

    /**
     * converts JavaObject to XML file
     *
     * @param Path - file in which to load the collection
     */
    public static void convertToXML(PersonCollection collection, String Path) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonCollection.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(Path));
            marshaller.marshal(collection, bufferedOutputStream);
            bufferedOutputStream.close();
        } catch (IOException | JAXBException e) {
            System.out.println("Права к файлу ограничены");
        }
    }

    /**
     * converts XML to JavaObject
     *
     * @param file - file from which we get the collection
     * @return unmarshal file
     */
    public static PersonCollection convertToJavaObject(File file) throws JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonCollection.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (PersonCollection) unmarshaller.unmarshal(file);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Указанный файл не найден");
        } catch (JAXBException e) {
            System.out.println("С файлом что-то не так, либо он пуст. В коллекции ничего нет");
            return new PersonCollection();
        } catch (NullPointerException e) {
            throw new RuntimeException();
        }
    }

    /**
     * get name and password for db
     *
     * @return string[] with data
     */
    public static String[] getLoginData() {
        try {
            Scanner scanner = new Scanner(new FileReader("biba"));
            return new String[]{scanner.nextLine(), scanner.nextLine()};
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        }
        return null;
    }
}
