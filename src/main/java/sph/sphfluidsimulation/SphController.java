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

    SimulationContext simulationContext;
    Physics physics;

    private static final int particleSize = 1;
    public static List<Particle> particles;
    public static Grid[][] grid;
    public int initialized = 0;
    public boolean resizePending = false;

    public static Random random = new Random();

    public SphController(SimulationContext simulationContext) {
        this.simulationContext = simulationContext;
        physics = new Physics(simulationContext);
        particles = initializeParticles(Integer.parseInt(simulationContext.particleCount)); //assume correctness
    }

    /*
   Particle initializer: This method is responsible for initializing and setting the position of particles at the start of simulation.
   Parameter: n (int): Starting number of particles within simulation.
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
            particles.add(new Particle(startPositionX, startPositionY, particleSize, simulationContext));
        }
        return particles;
    }

    // Grid Updater: Updates the size of the grid and initializes it.
    public Grid[][] updateGridSize(double width, double height, double gridSize) {
        int gridLengthX = (int) Math.floor(width / gridSize) + 1;
        Grid[][] grid = new Grid[(int) Math.floor(height / gridSize) + 1][gridLengthX];

        for (Grid[] gridArray : grid) {
            for (int i = 0; i < gridLengthX; i++) {
                gridArray[i] = new Grid();
            }
        }
        return grid;
    }

    // Mouse emitter: Creates new particles based on the mouse dialogConfig.
    // When the mouse button is pressed or dragged, particles are added at the current mouse position.
    EventHandler<MouseEvent> handler = event -> {
        double startX;
        double startY;
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            startX = event.getSceneX();
            startY = event.getSceneY();
            SphController.particles.add(new Particle(startX, SphApplication.scene.getHeight() - startY, particleSize, simulationContext));
        }
    };


    private void rainEmitter(boolean spawnState, double width, double height){
        if (spawnState) return;
        Particle newEmitterParticle1 = new Particle((int) width * 0.33, height, particleSize, simulationContext);
        Particle newEmitterParticle2 = new Particle((int) width * 0.66, height, particleSize, simulationContext);

        //random number: max = 10, min = 10 -> random.nextInt(max - min) + min;
        newEmitterParticle1.velocityX += random.nextInt(10 + 10) - 10;
        newEmitterParticle2.velocityX += random.nextInt(10 + 10) - 10;

        SphController.particles.add(newEmitterParticle1);
        SphController.particles.add(newEmitterParticle2);
    }

    //This method initializes the simulation by setting up the necessary components and runs the simulation loop.
    public void initialize() {
        //for some reason it starts 2x, first time just ignore it, return
        if (++initialized < 2)
            return;

        grid = updateGridSize(simulationContext.width, simulationContext.height, Physics.gridSize);


        System.out.println("Started simulation");
        pane.setStyle("-fx-background-color: black;");

        //set window size
        simulationContext.width = pane.getScene().getWidth();
        simulationContext.height = pane.getHeight();

        //mouse listeners
        pane.setOnMousePressed(event -> handler.handle(event));
        pane.setOnMouseReleased(event -> handler.handle(event));

        //window listeners
        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            simulationContext.width = newValue.doubleValue();
            resizePending = true;
        });

        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            simulationContext.height = newValue.doubleValue();
            resizePending = true;
        });

        final boolean[] rainEmitterLock = {false}; // false - ready to enter

        //Loop of the simulation
        //issue of frames - simulation runs based on frames not on time - limiting frames is only a hotfix at the moment for faking of realistic movement (so It's not moving too fast)
        int maxFPS = 1000;
        long frameDuration = 1000 / maxFPS;

        //simulation loop runs on animation timer
        AnimationTimer timer = new AnimationTimer() {

            private long previousTime = 0;

            @Override
            public void handle(long currentTime) {
                //check if window changed before the grid is read
                if (resizePending) {
                    grid = updateGridSize(simulationContext.width, simulationContext.height, Physics.gridSize);
                    resizePending = false;
                }

                //emitter - new particle every 2nd tick
                if(simulationContext.rainEnabled) {
                    rainEmitterLock[0] = !rainEmitterLock[0];
                    rainEmitter(rainEmitterLock[0], simulationContext.width, simulationContext.height);
                }

                try {
                    physics.moveParticles(particles, simulationContext);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                physics.drawParticles(pane, particles);
            }
        };
        timer.start();
    }
}