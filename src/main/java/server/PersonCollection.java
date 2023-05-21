package server;

import client.RequestManager;
import client.commands.CommandManager;
import common.DataManager;
import common.data.Person;
import common.network.CommandResult;
import common.network.Request;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A class that implements collection related methods
 */
@XmlRootElement(
        name = "persons"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonCollection extends DataManager {
    @XmlElement(name = "Person")
    private Parser parser;
    private ConcurrentSkipListSet<Person> treeSet = new ConcurrentSkipListSet<>();
    private static Date creationDate = new Date();
    private DBManager dbManager;

    public PersonCollection(Parser parser) throws JAXBException {
        this.parser = parser;
    }

    public PersonCollection(DBManager dbManager) {
        this.dbManager = dbManager;
        this.parser = new Parser();

        loadCollectionFromDB();
    }

    public PersonCollection() {

    }

    /**
     * upload the collection to the server
     *
     * @param request - collection
     */
    public void loadCollection(ConcurrentSkipListSet<Person> request) {
        setCollection(request);
    }

    public void loadCollectionFromDB() {
        setCollection(dbManager.readCollection());
    }

    public ConcurrentSkipListSet<Person> getCollection() {
        return treeSet;
    }

    /**
     * displays information about the character with all fields
     *
     * @param person from client
     */
    public String personInfo(Person person) {
        return ("ID: " + person.getId() +
                "\nИмя персонажа: " + person.getName() +
                "\nКоординаты: X=" + person.getCoordinates().getX() + ", Y=" + person.getCoordinates().getY() +
                "\nВремя создания: " + person.getCreationDate() +
                "\nРост: " + person.getHeight() +
                "\nЦвет глаз: " + person.getEyeColor() +
                "\nЦвет волос: " + person.getHairColor() +
                "\nСтрана: " + person.getNationality() +
                "\nЛокация: " + "X: " + person.getLocation().getX() + " Y: " + person.getLocation().getY() + " Название: " + person.getLocation().getLocationName() + "\n");

    }

    /**
     * add person to collection
     *
     * @param request - person from client
     */
    public CommandResult add(Request<?> request) {
        try {
            Person person = (Person) request.type;
            return addPerson(person, request);
        } catch (Exception exception) {
            return new CommandResult(false, "Передан аргумент другого типа");
        }
    }

    private CommandResult addPerson(Person person, Request<?> request) throws SQLException {
        boolean ok = dbManager.addPerson(person, request.user.getUsername());
        if (ok) {
            treeSet.add(person);
            return new CommandResult(true, "Новый элемент успешно добавлен");
        }
        return new CommandResult(false, "Не удалось добавить элемент");
    }


    /**
     * displays information about each person
     */
    public String information() {
        if (treeSet.isEmpty()) {
            return "В коллекции ничего нет";
        }
        StringBuilder info = new StringBuilder();
        for (Person person : this.treeSet) {
            info.append(personInfo(person));

        }
        return info.toString();
    }

    /**
     * @return information about tree set
     */
    public CommandResult show(Request<?> request) {
        return new CommandResult(true, information());
    }

    /**
     * method which compares the characters' height
     *
     * @param height_int - height which client entered
     * @return true or false
     */
    public boolean toHeight(int height_int) {
        boolean flag = true;
        for (Person person : treeSet) {
            if (height_int > person.getHeight()) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * add person to tree set
     *
     * @param request - person which client create
     */
    public CommandResult addIfMax(Request<?> request) {
        try {
            Person person = (Person) request.type;
            addPerson(person, request);
            return new CommandResult(true, "Персонаж добавлен");
        } catch (SQLException e) {
            return new CommandResult(false, "Ошибка на сервере, связанная с бд");
        }
    }

    /**
     * add person to tree set
     *
     * @param request - person which client create
     */
    public CommandResult addIfMin(Request<?> request) {
        try {
            Person person = (Person) request.type;
            addPerson(person, request);
            return new CommandResult(true, "Персонаж добавлен");
        } catch (SQLException e) {
            return new CommandResult(false, "Ошибка на сервере, связанная с бд");
        }
    }


    /**
     * clears the collection
     */
    /**
     * public CommandResult clear(Request<?> request) {
     * treeSet.clear();
     * return new CommandResult(true, "Элементы удалены");
     * }
     */
    public CommandResult clear(Request<?> request) {
        String username = request.user.getUsername();
        if (!dbManager.deleteAllOwned(username)) {
            return new CommandResult(false, "Вы не можете удалить персонажей");
        } else if (treeSet.isEmpty()) {
            return new CommandResult(false, "Коллекция пустая");
        } else {
            treeSet.removeIf(e -> e.getOwnerUsername().equals(username));
            return new CommandResult(true, "Элементы, добавленные пользователем, удалены");
        }
    }

    /**
     * check id
     *
     * @param ID could be int
     * @return true or false
     */
    public boolean existID(int ID, String username) throws AccessDeniedException, SQLException {
        for (Person person : treeSet) {
            if (person.getId() == ID) {
                boolean update = dbManager.updatePerson(person.getId(), person, username);
                if (update) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check if the character exists, and if so, remove it from the tree set
     *
     * @param request - id which client entered
     */
    public CommandResult remove_by_id(Request<?> request) {
        String message = null;
        int ID;
        try {
            ID = Integer.parseInt((String) request.type);
            if (treeSet.stream().noneMatch(person -> person.getId() == (ID))) {
                return new CommandResult(false, "Персонажа с таким ID не существует");
            }
            boolean remove = dbManager.removeById(ID, request.user.getUsername());
            if (remove) {
                treeSet.removeIf(person -> person.getId() == (ID));
                return new CommandResult(true, "Персонаж удален");
            } else {
                return new CommandResult(false, "Вы не можете удалить данного персонажа");
            }
        } catch (NumberFormatException e) {
            message = "Вы неправильно ввели ID";
        } catch (SQLException e) {
            message = "Ошибка на сервере, связанная с бд";
        }
        return new CommandResult(true, message);
    }


    /**
     * removes the highest person
     */
    public CommandResult removeGreater(Request<?> request) {
        String message = null;
        try {
            int height = Integer.parseInt((String) request.type);
            if (height > 0 || height == 0) {
                for (Person person : treeSet) {
                    if (person.getHeight() > height && Objects.equals(person.getOwnerUsername(), request.user.getUsername())) {
                        if (dbManager.removeById(person.getId(), request.user.getUsername())) {
                            treeSet.remove(person);
                        }
                        message = "Персонажи удалены";
                    } else {
                        message = "Проверьте указанный рост или у вас нет прав на удаление";
                    }
                }
            } else {
                message = "Рост не может быть меньше нуля";
            }
        } catch (NumberFormatException e) {
            System.out.println("Рост введен некорректно");
        } catch (SQLException exception) {
            exception.printStackTrace();
            message = "SQL ошибка на сервере";
        } catch (Exception exception) {
            message = "Аргумент другого типа";
        }

        return new CommandResult(true, message);
    }

    /**
     * filter of persons whose coordinate is greater
     */
    public CommandResult filterGreater(Request<?> request) {
        try {
            double x = Double.parseDouble((String) request.type);

            for (Person person : treeSet) {
                if (person.getLocation().getX() > x) {
                    return new CommandResult(true, person.getName() + " : " + person.getLocation().getX());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Вы неправильно ввели данные");
        }
        return new CommandResult(true, "Выведен персонаж с большей координатой локации");
    }

    private static final ArrayList<Double> uniq = new ArrayList<>();

    /**
     * print a not repeated location
     */
    public CommandResult printUniqueLocation(Request<?> request) {
        for (Person person : treeSet) {
            double X = person.getLocation().getX();
            if (!uniq.contains(X)) {
                uniq.add(X);
            } else {
                uniq.remove(X);
            }
        }
        System.out.println("Выведены все уникальные значения");
        return new CommandResult(true, String.valueOf(uniq));
    }

    /**
     * print info about collection : name, creation date, count of persons
     */
    public CommandResult info(Request<?> request) {
        String inf = treeSet.getClass().getName() + " " + PersonCollection.creationDate + " " + treeSet.size();
        return new CommandResult(true, inf);
    }

    /**
     * print information about available commands
     */
    public CommandResult help(Request<?> request) {
        StringBuilder result = new StringBuilder();
        CommandManager commandManager = new CommandManager(new RequestManager());
        commandManager.getCommandMap().forEach((description, command) -> result.append(command.getDescription()).append("\n"));
        return new CommandResult(true, result.toString());
    }

    /**
     * set collection
     *
     * @param treeSet - our person collection
     */
    public void setCollection(ConcurrentSkipListSet<Person> treeSet) {

        for (Person person : treeSet) {
            person.setName(person.getName());
            person.setNationality(person.getNationality());
            person.setCoordinates(person.getCoordinates());
            person.setEyeColor(person.getEyeColor());
            person.setHairColor(person.getHairColor());
            person.setLocation(person.getLocation());
            person.setHeight(person.getHeight());
        }

        this.treeSet = treeSet;
    }

    /**
     * change the parameters of the character with the id entered by the client,
     * check if such a character exists
     */
    public CommandResult update(Request<?> request) {
        String message = null;
        try {
            Person person = (Person) request.type;
            if (getById(person.getId()) == null) {
                return new CommandResult(false, "Персонажа с таким ID не существует");
            }
            boolean update = dbManager.updatePerson(person.getId(), person, request.user.getUsername());
            if (update) {
                treeSet.stream()
                        .filter(person1 -> person1.getId() == person.getId())
                        .forEach(person1 -> person1.update(person));
                return new CommandResult(true, "Персонаж успешно обновлен");
            } else {
                return new CommandResult(false, "Вы не можете изменить данного персонажа");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID введен неверно");
        } catch (AccessDeniedException | SQLException e) {
            System.out.println("Ошибка с бд");
        }
        return new CommandResult(true, message);
    }

    /**
     * method for update find the character with the entered id
     *
     * @param id - id that the client entered
     * @return person
     */
    public Person getById(Integer id) {
        return treeSet.stream()
                .filter(person -> person.getId() == id)
                .findFirst()
                .orElse(null);
    }


    /**
     * counter of persons whose color code is greater
     * check for color correctness and display a message to the server and the user
     */
    public CommandResult countEyeColor(Request<?> request) {
        try {
            int count = 0;
            Integer code = Integer.parseInt((String) request.type);
            for (Person person : treeSet) {
                if (person.getEyeColor().getCode() > code) {
                    count += 1;
                }
            }
            String countColor = String.valueOf(count);
            return new CommandResult(true, countColor);
        } catch (NumberFormatException e) {
            System.out.println("Цвет введен неверно");
            return new CommandResult(false, "Цвет введен неверно");
        }
    }

    /**
     * Save collection
     */
    public void save(String filename) {
        saveCollection(filename);
    }

    /**
     * Parsing a collection in xml
     *
     * @param filename - the name of the file in which we save the collection, "s" by default
     */
    public void saveCollection(String filename) {
        parser.convertToXML(this, filename);
        System.out.println("Коллекция сохранена");
    }

    /**
     * Finish the app
     *
     * @param request - command
     */
    public CommandResult exit(Request<?> request) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        atomicBoolean.set(true);
        return new CommandResult(true, "Удачи");
    }
}

