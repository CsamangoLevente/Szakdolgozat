package hu.ArcadeFx.Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class User {
    private final StringProperty lastname;
    private final StringProperty firstname;
    private final StringProperty userId;
    private final ObjectProperty<LocalDate> dateCreated;

    public User(String lastname, String firstname, String userId, LocalDate dateCreated) {
        this.lastname = new SimpleStringProperty(lastname);
        this.firstname = new SimpleStringProperty(firstname);
        this.userId = new SimpleStringProperty(userId);
        this.dateCreated = new SimpleObjectProperty<>(dateCreated);
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public StringProperty lastnameProperty() {
        return lastname;
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public ObjectProperty<LocalDate> dateCreatedProperty() {
        return dateCreated;
    }
}