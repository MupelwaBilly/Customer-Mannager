package app.customermannager;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> provinceBox;
    @FXML private Label errorLabel, totalLabel;
    @FXML private TableView<Customer> table;
    @FXML private TableColumn<Customer, Void> snCol;
    @FXML private TableColumn<Customer, String> nameCol, provCol;

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        provinceBox.setItems(FXCollections.observableArrayList(
                "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
                "Muchinga", "Northern", "North-Western", "Southern", "Western"));

        // SN = row position, so it renumbers automatically after delete/sort
        snCol.setSortable(false);
        snCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        provCol.setCellValueFactory(new PropertyValueFactory<>("province"));
        table.setItems(customers);
        table.setPlaceholder(new Label("No customers yet"));

        totalLabel.textProperty().bind(
                Bindings.size(customers).asString("Total customers: %d"));
    }

    @FXML
    private void onAdd() {
        String name = nameField.getText().trim();
        String prov = provinceBox.getValue();

        if (name.isEmpty()) { errorLabel.setText("Name is required."); nameField.requestFocus(); return; }
        if (prov == null)   { errorLabel.setText("Please select a province."); provinceBox.requestFocus(); return; }

        customers.add(new Customer(name, prov));
        errorLabel.setText("");
        nameField.clear();
        provinceBox.setValue(null);
        nameField.requestFocus();
    }

    @FXML
    private void onDelete() {
        Customer sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { errorLabel.setText("Select a customer to delete."); return; }

        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + sel.getName() + "?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Confirm deletion");
        a.showAndWait().filter(b -> b == ButtonType.OK)
                .ifPresent(b -> customers.remove(sel));
    }
}