package server;

import common.data.*;
import common.network.CommandResult;
import common.network.Request;

import java.sql.*;
import java.time.ZonedDateTime;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private static final String OWNER = "owner";

    private static final String SQL_ADD_USER = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
            TABLE_USER, USERNAME, PASSWORD);

    private static final String SQL_FIND_USERNAME = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?",
            TABLE_USER, USERNAME);
    private static final String SQL_CHECK_USER = String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND %s = ?",
            TABLE_USER, USERNAME, PASSWORD);

    private static final String SQL_ADD_PERSON = String.format("INSERT INTO %s (" +
                    "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING %s",
            TABLE_PERSON,
            NAME, COORDINATE_X, COORDINATE_Y, CREATION_DATE, HEIGHT, EYE_COLOR, HAIR_COLOR, COUNTRY,
            LOCATION_X, LOCATION_Y, LOCATION_NAME, OWNER_USERNAME, PERSON_ID);

    private static final String SQL_GET_MIN_STUDY_GROUP_NAME = String.format("SELECT %s FROM %s ORDER BY %s LIMIT 1", NAME, TABLE_PERSON, NAME);
    /**
     * private static final String SQL_REMOVE_BY_ID = String.format("DELETE FROM %s WHERE %s = ?",
     * TABLE_STUDY_GROUP, STUDY_GROUP_ID);
     * private static final String SQL_GET_GREATER = String.format("SELECT %s, %s FROM %s WHERE %s > ?",
     * STUDY_GROUP_ID, OWNER_USERNAME, TABLE_STUDY_GROUP, NAME);
     * private static final String SQL_GET_LOWER = String.format("SELECT %s, %s FROM %s WHERE %s < ?",
     * STUDY_GROUP_ID, OWNER_USERNAME, TABLE_STUDY_GROUP, NAME);
     * private static final String SQL_GET_ALL_BY_SHOULD_BE_EXPELLED = String.format("SELECT %s, %s FROM %s WHERE %s = ?",
     * STUDY_GROUP_ID, OWNER_USERNAME, TABLE_STUDY_GROUP, SHOULD_BE_EXPELLED);
     * private static final String SQL_UPDATE_BY_ID = String.format("UPDATE %s SET " +
     * "%s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? " +
     * "WHERE %s = ?",
     * TABLE_STUDY_GROUP, NAME, COORDINATE_X, COORDINATE_Y, STUDENTS_COUNT, EXPELLED_STUDENTS, SHOULD_BE_EXPELLED, FORM_OF_EDUCATION,
     * ADMIN_NAME, ADMIN_WEIGHT, ADMIN_PASSPORT, ADMIN_LOCATION_X, ADMIN_LOCATION_Y, ADMIN_LOCATION_NAME, STUDY_GROUP_ID);
     * <p>
     * private static final String SQL_GET_LAST_ID = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1",
     * STUDY_GROUP_ID, TABLE_STUDY_GROUP, STUDY_GROUP_ID);
     * private static final String SQL_ID_EXISTENCE = String.format("SELECT COUNT(*) as count FROM %s WHERE %s = ?",
     * TABLE_STUDY_GROUP, STUDY_GROUP_ID);
     * private static final String SQL_GET_STUDY_GROUPS = String.format("SELECT * FROM %s",
     * TABLE_STUDY_GROUP);
     * private static final String SQL_BELONGS_TO_USER = String.format("SELECT %s FROM %s WHERE %s = ?",
     * OWNER_USERNAME, TABLE_STUDY_GROUP, STUDY_GROUP_ID);
     */
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
            System.out.println("Подключение к базе данных установлено.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Не удалось выполнить подключение к базе данных.");
            System.exit(-1);
        }
    }

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

    public CommandResult login(Request<?> request) {
        try {
            User user = (User) request.type;
            if (checkUser(user)) {
                //System.out.println("Добро пожаловать");
                return new CommandResult(true, "Добро пожаловать");
            }
            return new CommandResult(false, "Неверный логин или пароль");
        } catch (SQLException exception) {
            return new CommandResult(false, "SQL-ошибка на сервере");
        } catch (Exception exception) {
            return new CommandResult(false, "Аргумент другого типа");
        }
    }

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

    public void registerUser(User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_ADD_USER);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.executeUpdate();
        statement.close();
    }

    public ConcurrentSkipListSet<Person> readCollection() {
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
            System.out.println("Ошибка при загрузке коллекции");
            exception.printStackTrace();
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            atomicBoolean.set(true);
        }
        return collection;
    }

    private Person getPersonFromResult(ResultSet resultSet) throws SQLException {
        return new Person(
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

                )
        );
    }

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

    public boolean addPerson(Person person, String owner) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_ADD_PERSON,
                    Statement.RETURN_GENERATED_KEYS);
            int i = prepareStatement(statement, person, true);
            statement.setString(i, owner);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("No rows affected");
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next())
                    person.setId(generatedKeys.getInt(PERSON_ID));
                else
                    throw new SQLException("No ID obtained");
            }
            statement.close();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();

        }

        return false;
    }

}
