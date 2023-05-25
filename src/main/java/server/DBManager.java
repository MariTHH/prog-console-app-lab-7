package server;

import common.data.*;
import common.network.CommandResult;
import common.network.Request;

import java.nio.file.AccessDeniedException;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Responsible for interacting with databases
 */
public class DBManager {
    private static final String TABLE_USER = "\"table_user\"";
    private static final String TABLE_PERSON = "person";

    private static final String USER_ID = "id";
    private static final String USERNAME = "\"username\"";
    private static final String PASSWORD = "\"password\"";

    private static final String PERSON_ID = "id";
    private static final String NAME = "name";
    private static final String COORDINATE_X = "coordinate_x";
    private static final String COORDINATE_Y = "coordinate_y";
    private static final String CREATION_DATE = "creation_date";
    private static final String COUNTRY = "nationality";
    private static final String HEIGHT = "height";
    private static final String EYE_COLOR = "eye_color";
    ;
    private static final String LOCATION_X = "location_x";
    private static final String LOCATION_Y = "location_y";
    private static final String LOCATION_NAME = "location_name";
    private static final String HAIR_COLOR = "hair_color";
    private static final String OWNER_USERNAME = "owner_username";

    private static final String SQL_CREATE_PERSON = String.format("CREATE TABLE %s ( id SERIAL PRIMARY KEY, name TEXT, coordinate_x TEXT," +
            "coordinate_Y TEXT, creation_date TIMESTAMP, height INTEGER, eye_color TEXT, hair_color TEXT, location_x TEXT," +
            "location_y TEXT, location_name text, nationality TEXT, owner_username TEXT);", TABLE_PERSON);
    private static final String SQL_ADD_USER = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
            TABLE_USER, USERNAME, PASSWORD);
    private static final String SQL_CREATE_USER = String.format("CREATE TABLE %s ( id SERIAL PRIMARY KEY, username TEXT, password TEXT);", TABLE_USER);

    private static final String SQL_FIND_USERNAME = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?",
            TABLE_USER, USERNAME);
    private static final String SQL_CHECK_USER = String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND %s = ?",
            TABLE_USER, USERNAME, PASSWORD);
    private static final String SQL_CHECK_USER1 = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?",
            TABLE_USER, USERNAME);
    private static final String SQL_ADD_PERSON = String.format("INSERT INTO %s (" +
                    "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING %s",
            TABLE_PERSON,
            NAME, COORDINATE_X, COORDINATE_Y, CREATION_DATE, HEIGHT, EYE_COLOR, HAIR_COLOR, COUNTRY,
            LOCATION_X, LOCATION_Y, LOCATION_NAME, OWNER_USERNAME, PERSON_ID);
    private static final String SQL_REMOVE_BY_ID = String.format("DELETE FROM %s WHERE %s = ?",
            TABLE_PERSON, PERSON_ID);
    private static final String SQL_UPDATE_BY_ID = String.format("UPDATE %s SET " +
                    "%s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?" +
                    "WHERE %s = ?",
            TABLE_PERSON,
            NAME, COORDINATE_X, COORDINATE_Y, HEIGHT, EYE_COLOR, HAIR_COLOR, COUNTRY,
            LOCATION_X, LOCATION_Y, LOCATION_NAME, PERSON_ID);
    private static final String SQL_ID_EXIST = String.format("SELECT COUNT(*) as count FROM %s WHERE %s = ?",
            TABLE_PERSON, PERSON_ID);
    private static final String SQL_BELONGS_USER = String.format("SELECT %s FROM %s WHERE %s = ?", OWNER_USERNAME, TABLE_PERSON, PERSON_ID);
    private static final String SQL_GET_PERSON = String.format("SELECT * FROM %s", TABLE_PERSON);

    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public DBManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * connect to databases with driver manager
     */
    public void connectDB() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            MainServer.logger.info("Подключение к базе данных установлено.");
        } catch (SQLException e) {
            MainServer.logger.warn("Не удалось выполнить подключение к базе данных.");
        }
    }

    /**
     * Checks that the user is correct
     *
     * @param user - name and password
     */
    public boolean checkUser(User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_CHECK_USER);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        statement.close();
        return count != 0;
    }

    /**
     * Checks that the username is correct
     *
     * @param login - username
     */
    public boolean checkUsername(String login) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_CHECK_USER1);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            statement.close();
            return count != 0;
        } catch (SQLException e) {
            PreparedStatement statement = connection.prepareStatement(SQL_CREATE_USER);
            statement.executeUpdate();
            statement.close();
            return false;
        }
        return true;
    }

    /**
     * Checks that the login is correct
     *
     * @param request - login
     * @return command result
     */
    public CommandResult checkLogin(Request<?> request) {
        try {
            String login = (String) request.type;
            if (checkUsername(login)) {
                return new CommandResult(true, "Добро пожаловать");
            }
            return new CommandResult(false, "Неверный логин");
        } catch (SQLException exception) {
            return new CommandResult(false, "SQL-ошибка на сервере");
        } catch (IllegalArgumentException exception) {
            return new CommandResult(false, "Аргумент другого типа");
        }
    }

    /**
     * Login user
     *
     * @param request - user - name and password
     * @return command result
     */
    public CommandResult login(Request<?> request) {
        try {
            User user = (User) request.type;
            if (checkUser(user)) {
                return new CommandResult(true, "Добро пожаловать");
            }
            return new CommandResult(false, "Неверный пароль");
        } catch (SQLException exception) {
            return new CommandResult(false, "SQL-ошибка на сервере");
        } catch (Exception exception) {
            return new CommandResult(false, "Аргумент другого типа");
        }
    }

    /**
     * Register user
     *
     * @param request - username and password
     * @return command result
     */
    public CommandResult register(Request<?> request) {
        try {
            User user = (User) request.type;

            if (!userExists(user.getUsername())) {
                registerUser(user);
                return new CommandResult(true,
                        "Добро пожаловать");
            }
            return new CommandResult(false, "Такое имя уже используется");
        } catch (SQLException exception) {
            return new CommandResult(false, "SQL-ошибка на сервере");
        } catch (Exception exception) {
            return new CommandResult(false, "Аргумент другого типа");
        }
    }

    /**
     * check username - free or not, create table user if it's not
     *
     * @param request - username
     * @return command result
     */
    public CommandResult checkRegister(Request<?> request) throws SQLException {
        try {
            String login = (String) request.type;

            if (!userExists(login)) {
                return new CommandResult(true,
                        "Имя пользователя доступно");
            }
            return new CommandResult(false, "Такое имя уже используется");
        } catch (SQLException exception) {
            PreparedStatement statement = connection.prepareStatement(SQL_CREATE_USER);
            statement.executeUpdate();
            statement.close();
            return new CommandResult(true, "Имя пользователя доступно");
        } catch (Exception exception) {
            return new CommandResult(false, "Аргумент другого типа");
        }
    }

    /**
     * check exist user or not with select
     *
     * @param username name of user
     * @return true or false
     */
    public boolean userExists(String username) throws SQLException {
        System.out.println(SQL_FIND_USERNAME);
        PreparedStatement statement = connection.prepareStatement(SQL_FIND_USERNAME);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        statement.close();
        return count != 0;
    }

    /**
     * add user to db
     *
     * @param user - name and password
     */
    public void registerUser(User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_ADD_USER);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.executeUpdate();
        statement.close();
    }

    /**
     * read collection from db, create table person if it's not
     *
     * @return collection
     */
    public ConcurrentSkipListSet<Person> readCollection() throws SQLException {
        ConcurrentSkipListSet<Person> collection = new ConcurrentSkipListSet<>();
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_GET_PERSON);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Person person = getPersonFromResult(resultSet);
                collection.add(person);
            }

            statement.close();
            System.out.println("Коллекция загружена из базы данных");
        } catch (SQLException exception) {
            PreparedStatement statement = connection.prepareStatement(SQL_CREATE_PERSON);
            statement.executeUpdate();
            statement.close();
            return collection;
        }
        return collection;
    }

    /**
     * get person from select
     *
     * @param resultSet - person characteristics
     * @return person
     */
    private Person getPersonFromResult(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getInt(PERSON_ID),
                resultSet.getString(NAME),
                new Coordinates(
                        resultSet.getLong(COORDINATE_X),
                        resultSet.getInt(COORDINATE_Y)
                ),
                resultSet.getInt(HEIGHT),
                Color.valueOf(resultSet.getString(EYE_COLOR)),
                Color.valueOf(resultSet.getString(HAIR_COLOR)),
                Country.valueOf(resultSet.getString(COUNTRY)),
                new Location(
                        resultSet.getDouble(LOCATION_X),
                        resultSet.getFloat(LOCATION_Y),
                        resultSet.getString(LOCATION_NAME)

                ),
                resultSet.getString(OWNER_USERNAME)
        );
    }

    /**
     * change person
     *
     * @param person     - our person
     * @param changeDate - now
     */
    private int prepareStatement(PreparedStatement statement, Person person, boolean changeDate) throws SQLException {
        Coordinates coordinates = person.getCoordinates();
        ZonedDateTime creationDate = person.getCreationDate();
        Country nationality = person.getNationality();
        Color eyeColor = person.getEyeColor();
        Color hairColor = person.getHairColor();
        Location location = person.getLocation();

        int i = 1;
        statement.setString(i++, person.getName());
        statement.setLong(i++, coordinates.getX());
        statement.setInt(i++, coordinates.getY());
        if (changeDate)
            statement.setTimestamp(i++, Timestamp.from(creationDate.toInstant()));
        statement.setInt(i++, person.getHeight());
        statement.setString(i++, eyeColor.name());
        statement.setString(i++, hairColor.name());
        statement.setString(i++, nationality.name());
        statement.setDouble(i++, location.getX());
        statement.setFloat(i++, location.getY());
        statement.setString(i++, location.getLocationName());

        return i;
    }

    /**
     * add person to db
     *
     * @param person - our person
     * @param owner  - owner name
     * @return true or false
     */
    public boolean addPerson(Person person, String owner) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_ADD_PERSON,
                    Statement.RETURN_GENERATED_KEYS);
            int i = prepareStatement(statement, person, true);
            statement.setString(i, owner);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Нет измененных строк");
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next())
                    person.setId(generatedKeys.getInt(PERSON_ID));
                else
                    throw new SQLException("ID не получено");
            }
            statement.close();
            return true;
        } catch (SQLException exception) {
            System.out.println("Ошибка sql");

        }

        return false;
    }

    /**
     * remove person by id and check owner name
     *
     * @param id       - person id
     * @param username - name of user
     * @return true or false
     */
    public boolean removeById(int id, String username) throws SQLException {
        if (!existId(id)) {
            System.out.println("Данного ID не существует");
            return false;
        }
        if (!belongsToUser(id, username)) {
            System.out.println("Вы не можете удалить данного персонажа");
            return false;
        }

        PreparedStatement statement = connection.prepareStatement(SQL_REMOVE_BY_ID);
        statement.setInt(1, id);
        statement.executeUpdate();

        return true;
    }

    /**
     * checks the existence of the id
     *
     * @param id - person id
     * @return true or false
     */
    public boolean existId(int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_ID_EXIST);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) != 0;
    }

    /**
     * Checks if the user can change the person
     *
     * @param id       - person id
     * @param username - name of user
     * @return true or false
     */
    public boolean belongsToUser(int id, String username) throws SQLException {
        if (!existId(id)) {
            return false;
        }

        PreparedStatement statement = connection.prepareStatement(SQL_BELONGS_USER);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        String owner = resultSet.getString(OWNER_USERNAME);

        return username.equals(owner);
    }

    /**
     * delete person with specific owner name
     *
     * @param username - name of user
     * @return true or false
     */
    public boolean deleteAllOwned(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + TABLE_PERSON
                    + " WHERE owner_username =?");
            statement.setString(1, username);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * update person if id exist and user can change this person
     *
     * @param id       - person id
     * @param person   - this person
     * @param username - name of user
     * @return true or false
     */
    public boolean updatePerson(int id, Person person, String username) throws SQLException, AccessDeniedException {
        if (!existId(id)) {
            return false;
        }
        if (!belongsToUser(id, username)) {
            return false;
        }
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BY_ID);
            int i = prepareStatement(statement, person, false);
            statement.setInt(i, id);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException exception) {
            System.out.println("Ошибка sql");
            return false;
        }
    }
}
