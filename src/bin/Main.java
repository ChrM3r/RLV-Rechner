package bin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("rlvrechner.fxml"));
        Scene scene = new Scene(root, 285, 650);
        scene.getStylesheets().add(getClass().getResource("datepicker.css").toString());

        primaryStage.setTitle("LBS RLV Rechner");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}