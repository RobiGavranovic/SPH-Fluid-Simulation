package sph.sphfluidsimulation;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SphController {
    @FXML
    Pane pane;

    private static final int particleSize = 1;
    public static List<Particle> particles;
    public static Grid[][] grid;
    public int initialized = 0;
    public static boolean resizePending = false;

    public static Random random = new Random();

    // Mouse emitter: Creates new particles based on the mouse input.
    // When the mouse button is pressed or dragged, particles are added at the current mouse position.
    EventHandler<MouseEvent> handler = event -> {
        double startX;
        double startY;
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            startX = event.getSceneX();
            startY = event.getSceneY();
            SphController.particles.add(new Particle(startX, SphApplication.scene.getHeight() - startY, particleSize));
        }
    };

    /*
    Particle initializer: This method is responsible for initializing and setting the position of particles at the start of simulation.

    Parameter:
    - n (int): Starting number of particles within simulation.
     */
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

    // Grid Updater: Updates the size of the grid and initializes it.
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

    private static void rainEmitter(boolean spawnState){
        if (spawnState) return;
        Particle newEmitterParticle1 = new Particle((int) Physics.width * 0.33, Physics.height, particleSize);
        Particle newEmitterParticle2 = new Particle((int) Physics.width * 0.66, Physics.height, particleSize);

        //rand number: max = 10, min = 10 -> random.nextInt(max - min) + min;
        newEmitterParticle1.velocityX += random.nextInt(10 + 10) - 10;
        newEmitterParticle2.velocityX += random.nextInt(10 + 10) - 10;

        SphController.particles.add(newEmitterParticle1);
        SphController.particles.add(newEmitterParticle2);
    }

    //This method initializes the simulation by setting up the necessary components and runs the simulation loop.
    public void initialize() {
        if (++initialized < 2)
            return;

        System.out.println("Started simulation");
        pane.setStyle("-fx-background-color: black;");

        Physics.width = pane.getScene().getWidth();
        Physics.height = pane.getHeight();

        updateGridSize();
        particles = initializeParticles(SphApplication.numOfParticles);

        pane.setOnMousePressed(event -> handler.handle(event));
        pane.setOnMouseReleased(event -> handler.handle(event));

        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            Physics.width = newValue.doubleValue();
            resizePending = true;
        });

        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            Physics.height = newValue.doubleValue();
            resizePending = true;
        });

        final boolean[] rainEmitterLock = {false}; // false - ready to enter

        //Loop of the simulation
        //issue of frames - simulation runs based on frames not on time - limiting frames is only a hotfix at the moment
        int maxFPS = 40;
        long frameDuration = 1000 / maxFPS;
        AnimationTimer timer = new AnimationTimer() {

            private long previousTime = 0;

            @Override
            public void handle(long currentTime) {
                //check if window changed before new threads are created and grid is read
                if (resizePending) {
                    updateGridSize();
                    resizePending = false;
                }

                //emitter - new particle every 2nd tick
                if(SphApplication.isRainEnabled) {
                    rainEmitterLock[0] = !rainEmitterLock[0];
                    rainEmitter(rainEmitterLock[0]);
                }

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