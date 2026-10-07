package app.customermannager;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {
    private static final String NAME_CHARS = "[\\p{L} '\\-]*";
    private static final String CONTACT_CHARS = "\\+?\\d{0,12}";
    private static final String CONTACT_FINAL = "\\+?\\d{9,12}";

    @FXML private TextField nameField, contactField, searchField;
    @FXML private ComboBox<String> provinceBox;
    @FXML private Label errorLabel, totalLabel;
    @FXML private TableView<Customer> table;
    @FXML private TableColumn<Customer, Void> snCol;
    @FXML private TableColumn<Customer, String> nameCol, contactCol, provCol;

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private FilteredList<Customer> filtered;

    @FXML
    private void initialize() {
        provinceBox.setItems(FXCollections.observableArrayList(
                "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
                "Muchinga", "Northern", "North-Western", "Southern", "Western"));

        // Input filters: reject bad keystrokes and pastes
        nameField.setTextFormatter(new TextFormatter<String>(change -> {
            String next = change.getControlNewText();
            if (next.length() <= 50 && next.matches(NAME_CHARS)) {
                errorLabel.setText("");
                return change;
            }
            errorLabel.setText("Name can only contain letters, spaces, hyphens and apostrophes.");
            return null;
        }));
        contactField.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getControlNewText().matches(CONTACT_CHARS)) {
                errorLabel.setText("");
                return change;
            }
            errorLabel.setText("Contact can only contain digits (and a leading +).");
            return null;
        }));

        // SN column = row position
        snCol.setSortable(false);
        snCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        provCol.setCellValueFactory(new PropertyValueFactory<>("province"));

        // Search: filter first, then sort, then show
        filtered = new FilteredList<>(customers, c -> true);
        SortedList<Customer> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        Label placeholder = new Label("No customers yet");
        table.setPlaceholder(placeholder);
        searchField.textProperty().addListener((obs, old, q) -> {
            String s = q == null ? "" : q.trim().toLowerCase();
            filtered.setPredicate(c -> s.isEmpty()
                    || c.getName().toLowerCase().contains(s)
                    || c.getContact().toLowerCase().contains(s)
                    || c.getProvince().toLowerCase().contains(s));
            placeholder.setText(s.isEmpty() ? "No customers yet" : "No matching customers");
        });

        totalLabel.textProperty().bind(Bindings.format("Showing %d of %d customers",
                Bindings.size(filtered), Bindings.size(customers)));
    }

    @FXML
    private void onAdd() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String prov = provinceBox.getValue();

        if (name.isEmpty()) { fail("Name is required.", nameField); return; }
        if (!name.matches("[\\p{L}][\\p{L} '\\-]*")) {
            fail("Name must start with a letter.", nameField); return;
        }
        if (!contact.matches(CONTACT_FINAL)) {
            fail("Contact must be 9 to 12 digits (optional leading +).", contactField); return;
        }
        if (prov == null) { fail("Please select a province.", provinceBox); return; }

        customers.add(new Customer(name, contact, prov));
        errorLabel.setText("");
        nameField.clear();
        contactField.clear();
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

    private void fail(String msg, Control focusTarget) {
        errorLabel.setText(msg);
        focusTarget.requestFocus();   // keeps entered text, as slide 30 asks
    }
}