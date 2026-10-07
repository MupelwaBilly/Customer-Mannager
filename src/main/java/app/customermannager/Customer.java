package app.customermannager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty contact = new SimpleStringProperty();
    private final StringProperty province = new SimpleStringProperty();

    public Customer(String name, String contact, String province) {
        this.name.set(name);
        this.contact.set(contact);
        this.province.set(province);
    }
    public String getName() { return name.get(); }
    public String getContact() { return contact.get(); }
    public String getProvince() { return province.get(); }
    public StringProperty nameProperty() { return name; }
    public StringProperty contactProperty() { return contact; }
    public StringProperty provinceProperty() { return province; }
}