package app.customermannager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("customer.fxml"));
        Scene scene = new Scene(root, 600, 760);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Customer Manager");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}