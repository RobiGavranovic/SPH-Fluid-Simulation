package sph.sphfluidsimulation;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.concurrent.Callable;

public class AddParticleToPaneTask implements Callable<Void> {
    Pane pane;
    Particle particle;

    public AddParticleToPaneTask(Pane pane, Particle particle) {
        this.pane = pane;
        this.particle = particle;
    }

    @Override
    public Void call() {
        Platform.runLater(() -> {
            pane.getChildren().add(particle);
        });
        return null;
    }
}
