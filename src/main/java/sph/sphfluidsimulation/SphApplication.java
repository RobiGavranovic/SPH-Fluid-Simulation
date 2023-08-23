package sph.sphfluidsimulation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class SphApplication extends Application {
    public static Scene scene;
    public static int numOfNewThreads;
    public static int numOfParticles;
    public static boolean isRainEnabled;

    @Override
    public void start(Stage stage) throws IOException {

        Dialog<List<Object>> dialog = new Dialog<>();
        dialog.setTitle("Particle Simulation Configuration");

        ButtonType buttonOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonOkay, ButtonType.CANCEL);

        TextField particleCountField = new TextField("5000");
        ComboBox<String> modeComboBox = new ComboBox<>(FXCollections.observableArrayList("Sequential", "Parallel"));
        modeComboBox.setValue("Sequential");
        ComboBox<String> rainComboBox = new ComboBox<>(FXCollections.observableArrayList("On", "Off"));
        rainComboBox.setValue("Off");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Number of particles:"), particleCountField);
        grid.addRow(1, new Label("Mode:"), modeComboBox);
        grid.addRow(2, new Label("Rain:"), rainComboBox);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> List.of(particleCountField.getText(), modeComboBox.getValue(), rainComboBox.getValue()));

        List<Object> result = dialog.showAndWait().orElse(null);

        if (result == null) {
            Platform.exit();
            return;
        }

        try {
            numOfParticles = Integer.parseInt((String) result.get(0));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default value of 5000.");
            numOfParticles = 5000;
        }

        numOfNewThreads = "Sequential".equals(result.get(1)) ? 1 : Runtime.getRuntime().availableProcessors();
        isRainEnabled = "On".equals(result.get(2));


        //simulation window
        SphController controller = new SphController();
        FXMLLoader fxmlLoader = new FXMLLoader(SphApplication.class.getResource("sph-view.fxml"));
        fxmlLoader.setController(controller);
        stage.setTitle("Smoothed-particle hydrodynamics fluid simulation");

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