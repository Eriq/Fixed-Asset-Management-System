package Classes;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

import javax.swing.text.html.ImageView;
import java.time.LocalDate;

/**
 * Created by Eric on 08/02/2018.
 */
public class Asset {

    private SimpleStringProperty name, serial, barcode, model, brand, cost, vendor, condition, description, group, location, department, custodian, life;
    private ReadOnlyObjectWrapper image;

    public Asset(Image picture, String name, String serial, String description, String condition, String group, String location, String life) {
        this.image = new ReadOnlyObjectWrapper<>(picture);
        this.name = new SimpleStringProperty(name);
        this.serial = new SimpleStringProperty(serial);
        this.description = new SimpleStringProperty(description);
        this.group = new SimpleStringProperty(group);
        this.location = new SimpleStringProperty(location);
        this.condition = new SimpleStringProperty(condition);
        this.life = new SimpleStringProperty(life);
    }

    public Asset(Image image, String name, String serial, String barcode, String model, String brand,
                 String cost, String vendor, String condition, String description, String group, String location, String department,
                 String custodian, String life) {
        this.name = new SimpleStringProperty(name);
        this.serial = new SimpleStringProperty(serial);
        this.barcode = new SimpleStringProperty(barcode);
        this.model = new SimpleStringProperty(model);
        this.brand = new SimpleStringProperty(brand);
        this.cost = new SimpleStringProperty(cost);
        this.vendor = new SimpleStringProperty(vendor);
        this.condition = new SimpleStringProperty(condition);
        this.description = new SimpleStringProperty(description);
        this.group = new SimpleStringProperty(group);
        this.location = new SimpleStringProperty(location);
        this.department = new SimpleStringProperty(department);
        this.custodian = new SimpleStringProperty(custodian);
        this.life = new SimpleStringProperty(life);
        this.image = new ReadOnlyObjectWrapper<>(image);
    }

    public String getSerial() {return serial.get();}

    public SimpleStringProperty serialProperty() {return serial;}

    public void setSerial(String serial) {this.serial.set(serial);}

    public String getName() {return name.get();}

    public SimpleStringProperty nameProperty() {return name;}

    public void setName(String name) {this.name.set(name);}

    public String getDescription() {return description.get();}

    public SimpleStringProperty descriptionProperty() {return description;}

    public void setDescription(String description) {this.description.set(description);}

    public String getGroup() {return group.get();}

    public SimpleStringProperty groupProperty() {return group;}

    public void setGroup(String group) {this.group.set(group);}

    public String getLocation() {return location.get();}

    public SimpleStringProperty locationProperty() {return location;}

    public void setLocation(String location) {this.location.set(location);}

    public String getCondition() {return condition.get();}

    public SimpleStringProperty conditionProperty() {return condition;}

    public void setCondition(String condition) {this.condition.set(condition);}

    public String getLife() {return life.get();}

    public SimpleStringProperty lifeProperty() {return life;}

    public void setLife(String life) {this.life.set(life);}

    public Object getImage() {return image.get();}

    public ReadOnlyObjectWrapper imageProperty() {return image;}

    public void setImage(Object image) {this.image.set(image);}

    public String getBarcode() {return barcode.get();}

    public SimpleStringProperty barcodeProperty() {return barcode;}

    public void setBarcode(String barcode) {this.barcode.set(barcode);}

    public String getModel() {return model.get();}

    public SimpleStringProperty modelProperty() {return model;}

    public void setModel(String model) {this.model.set(model);}

    public String getBrand() {return brand.get();}

    public SimpleStringProperty brandProperty() {return brand;}

    public void setBrand(String brand) {this.brand.set(brand);}

    public String getCost() {return cost.get();}

    public SimpleStringProperty costProperty() {return cost;}

    public void setCost(String cost) {this.cost.set(cost);}

    public String getVendor() {return vendor.get();}

    public SimpleStringProperty vendorProperty() {return vendor;}

    public void setVendor(String vendor) {this.vendor.set(vendor);}

    public String getDepartment() {return department.get();}

    public SimpleStringProperty departmentProperty() {return department;}

    public void setDepartment(String department) {this.department.set(department);}

    public String getCustodian() {return custodian.get();}

    public SimpleStringProperty custodianProperty() {return custodian;}

    public void setCustodian(String custodian) {this.custodian.set(custodian);}
}
