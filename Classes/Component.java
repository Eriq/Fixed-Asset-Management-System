package Classes;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Eric on 08/09/2018.
 */
public class Component {
    private SimpleStringProperty name, serial, condition, description;

    public Component(String name, String serial, String condition, String description) {
        this.name = new SimpleStringProperty(name);
        this.serial = new SimpleStringProperty(serial);
        this.condition = new SimpleStringProperty(condition);
        this.description = new SimpleStringProperty(description);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSerial() {
        return serial.get();
    }

    public SimpleStringProperty serialProperty() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial.set(serial);
    }

    public String getCondition() {
        return condition.get();
    }

    public SimpleStringProperty conditionProperty() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition.set(condition);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
