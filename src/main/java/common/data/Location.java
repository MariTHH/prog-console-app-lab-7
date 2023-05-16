package common.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serial;
import java.io.Serializable;

/**
 * A class with person location
 */
@XmlRootElement(name = "Location")
@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Serializable {
    private Double x;
    private float y;
    private String name;

    public Location(Double x, float y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public Location() {
    }

    @Serial
    private static final long serialVersionUID = 0xFEFA;

    public Double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getLocationName() {
        return name;
    }

    public void setLocationName(String name) {
        this.name = name;
    }

}