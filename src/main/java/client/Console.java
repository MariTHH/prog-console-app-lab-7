package client;

import common.network.Request;
import server.PersonCollection;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static server.Parser.convertToJavaObject;

/**
 * Reads the file for the main
 */
public class Console {
    PersonCollection collection = new PersonCollection();
    RequestManager requestManager = new RequestManager();

    /**
     * Request the file again if it does not exist
     */
    public void fileRead() {
        boolean flag = false;
        try {
            while (!flag) {
                System.out.println("Введите название файла еще раз");
                Scanner scanner = new Scanner(System.in);
                File file = new File(scanner.nextLine());
                if (file.exists() && !file.isDirectory()) {
                    flag = true;
                    collection.setCollection(convertToJavaObject(file).getCollection());
                    Request<PersonCollection> request = new Request<>(null, collection, collection);
                    PersonCollection result = requestManager.sendCollection(request);
                    result.getCollection();
                    collection.setCollection(result.getCollection());
                }
            }
        } catch (IllegalArgumentException | JAXBException | IOException | ClassNotFoundException e) {
            System.out.println("Файл не найден");
        }
    }
}






