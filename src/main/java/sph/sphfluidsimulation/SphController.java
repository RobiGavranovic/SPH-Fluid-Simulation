package sph.sphfluidsimulation;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class SphController {
    @FXML
    Pane pane;

    int particleSize = 1;
    public static List<Particle> particles;
    public static Grid[][] grid;
    public int initialized = 0;

    /*
    -----------------MOUSE EMITTER--------------
     */
    EventHandler<MouseEvent> handler = event -> {
        double startX;
        double startY;
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            startX = event.getSceneX();
            startY = event.getSceneY();
            SphController.particles.add(new Particle(startX, SphApplication.scene.getHeight() - startY, 1));
        }
    };

    public List<Particle> initializeParticles(int n) {
        List<Particle> particles = new ArrayList<>();
        int startPositionX = 0;
        int increment = particleSize + 4;

        int startPositionY = 2;
        for (; n > 0; n--) {
            startPositionX += increment;
            if (startPositionX > 600) {
                startPositionX = 5;
                startPositionY += increment;
            }
            particles.add(new Particle(startPositionX, startPositionY, particleSize));
        }
        return particles;
    }

    public static void updateGridSize() {
        double gridSize = Physics.gridSize;
        int gridLengthX = (int) Math.floor(Physics.width / gridSize) + 1;
        grid = new Grid[(int) Math.floor(Physics.height / gridSize) + 1][gridLengthX];

        for (Grid[] grid : grid) {
            for (int i = 0; i < gridLengthX; i++) {
                grid[i] = new Grid();
            }
        }
    }

    public void initialize() {
        if (++initialized < 2)
            return;

        System.out.println("Started simulation");
        pane.setStyle("-fx-background-color: black;");

        Physics.width = pane.getScene().getWidth();
        Physics.height = pane.getHeight();

        updateGridSize();
        particles = initializeParticles(5000);

        pane.setOnMousePressed(event -> handler.handle(event));
        pane.setOnMouseReleased(event -> handler.handle(event));

        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            Physics.width = newValue.doubleValue();
            updateGridSize();
        });

        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            Physics.height = newValue.doubleValue();
            updateGridSize();
        });

    /*
    -----------------LOOP--------------
    */
        int maxFPS = 24;
        long frameDuration = 1000 / maxFPS;
        AnimationTimer timer = new AnimationTimer() {

            private long previousTime = 0;

            @Override
            public void handle(long currentTime) {
                Physics.moveParticles(particles);
                long elapsedTime = (currentTime - previousTime) / 1_000_000;
                if (elapsedTime < frameDuration) try {
                    Thread.sleep((frameDuration - elapsedTime));
                } catch (InterruptedException e) {
                }
                previousTime = currentTime;

                Physics.drawParticles(pane, particles);
            }
        };
        timer.start();
    }
}