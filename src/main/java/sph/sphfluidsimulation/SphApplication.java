package sph.sphfluidsimulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SphApplication extends Application {
    public static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        SphController controller = new SphController();
        FXMLLoader fxmlLoader = new FXMLLoader(SphApplication.class.getResource("sph-view.fxml"));
        fxmlLoader.setController(controller);

        Parent root = fxmlLoader.load();
        scene = new Scene(root, 1000, 650);

        scene.setOnMousePressed(controller.handler);
        scene.setOnMouseDragged(controller.handler);

        stage.setScene(scene);
        stage.show();

        controller.initialize();
    }

    public static void main(String[] args) {
        launch();
    }
}