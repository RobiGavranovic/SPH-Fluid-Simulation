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
import java.util.ArrayList;
import java.util.List;


public class SphApplication extends Application {
    public static Scene scene;
    boolean cancel = false;

    DialogConfig showConfigurationDialog(boolean isWrongInput) {
        //configuration dialog
        Dialog<List<Object>> dialog = new Dialog<>();
        dialog.setTitle("SPH Fluid Simulation Configuration");

        ButtonType buttonOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonOkay, ButtonType.CANCEL);

        TextField particleCountField = new TextField("5000");
        ComboBox<String> modeComboBox = new ComboBox<>(FXCollections.observableArrayList(
                SimulationMode.SEQUENTIAL.toString(),
                SimulationMode.PARALLEL.toString(),
                SimulationMode.DISTRIBUTED.toString())
        );
        modeComboBox.setValue(SimulationMode.PARALLEL.toString());
        CheckBox rainCheckBox = new CheckBox("Rain Emitter");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Number of particles:"), particleCountField);
        grid.addRow(1, new Label("Mode:"), modeComboBox);
        grid.addRow(2, new Label("Rain:"), rainCheckBox);
        if (isWrongInput) grid.addRow(3, new Label("Wrong input, please try again"));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(
                btn -> {
                    if (btn.getButtonData().isCancelButton()) {
                        Platform.exit();
                        cancel = true;
                    }
                    List<Object> result = new ArrayList<>();
                    result.add(new DialogConfig(particleCountField.getText(), SimulationMode.fromString(modeComboBox.getValue()), rainCheckBox.isSelected()));
                    return result;
                });

        List<Object> result = dialog.showAndWait().orElse(null);
        DialogConfig dialogConfig = (DialogConfig) result.get(0);

        return dialogConfig;
    }

    boolean validateParticleCount(DialogConfig dialogConfig) {
        //if cancel button -> fail the validation check
        if (cancel) return false;
        try {
            int particleCount = Integer.parseInt(dialogConfig.particleCount);
            if (particleCount > 0 && particleCount <= 20_000) return true;
            else return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        //show initial window with no wrong input warning
        DialogConfig dialogConfig = showConfigurationDialog(false);
        //check if valid particle count input -> if not show dialog again
        while (!validateParticleCount(dialogConfig)) {
            //if cancel button -> return before showing the simulation
            if (cancel) return;
            dialogConfig = showConfigurationDialog(true);
        }

        SimulationContext simulationContext = new SimulationContext(dialogConfig);
        startSimulation(stage, simulationContext);
    }

    public static void main(String[] args) {
        launch();
    }

    //simulation window
    public void startSimulation(Stage stage, SimulationContext simulationContext) throws IOException {
        SphController controller = new SphController(simulationContext);
        FXMLLoader fxmlLoader = new FXMLLoader(SphApplication.class.getResource("sph-view.fxml"));
        fxmlLoader.setController(controller);
        stage.setTitle("Smoothed-particle hydrodynamics fluid simulation");

        Parent root = fxmlLoader.load();
        scene = new Scene(root, 1000, 650);
        scene.setOnMousePressed(controller.handler);
        scene.setOnMouseDragged(controller.handler);
        stage.setScene(scene);
        stage.show();
        controller.simulationContext.width = scene.getWidth();
        controller.simulationContext.height = scene.getHeight();
        controller.initialize();
    }
}