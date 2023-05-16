package common.data;

import com.sun.istack.NotNull;
import server.GenerationId;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A class with a character constructor
 */
@XmlRootElement
public class Person implements Comparable<Person>, Serializable {
    @Serial
    private static final long serialVersionUID = 0xDADA;
    private int id;
    @XmlElement(
            name = "name",
            required = true
    )
    @NotNull
    private String name;
    @XmlElement(
            name = "coordinates",
            required = true
    )
    @NotNull
    private Coordinates coordinates;
    @XmlTransient
    private ZonedDateTime creationDate;
    @XmlElement(
            name = "height",
            required = true
    )
    private int height;
    @XmlElement(
            name = "eyeColor",
            required = true
    )
    private Color eyeColor;
    @XmlElement(
            name = "hairColor",
            required = true
    )
    private Color hairColor;
    @XmlElement(
            name = "nationality",
            required = true
    )
    private Country nationality;
    @XmlElement(
            name = "location",
            required = true
    )
    private Location location;

    public Person() {
        this.id = GenerationId.generateID();
        this.creationDate = ZonedDateTime.now();
    }

    public Person(String name, Coordinates coordinates, int height, Color eyeColor, Color hairColor, Country nationality, Location location) {
        this.id = GenerationId.generateID();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = ZonedDateTime.now();
        this.height = height;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

    @XmlTransient
    public int getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @XmlTransient
    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @XmlTransient
    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @XmlTransient
    public Color getEyeColor() {
        return this.eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    @XmlTransient
    public Color getHairColor() {
        return this.hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    @XmlTransient
    public Country getNationality() {
        return this.nationality;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    @XmlTransient
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * sorting the collection by height
     *
     * @param personName the object to be compared.
     * @return compare collection
     */
    public int compareTo(Person personName) {
        return name.compareTo(personName.name);
    }

    public void update(Person person) {
        this.name = person.name;
        this.coordinates = person.coordinates;
        this.creationDate = person.creationDate;
        this.hairColor = person.hairColor;
        this.eyeColor = person.eyeColor;
        this.location = person.location;
        this.nationality = person.nationality;
        this.height = person.height;
    }
}


