package sph.sphfluidsimulation;

public class SimulationContext extends DialogConfig {
    public int threadCount = 0;
    public double width;
    public double height;

    public SimulationContext(DialogConfig dialogConfig) {
        super(dialogConfig.particleCount, dialogConfig.simulationMode, dialogConfig.rainEnabled);
        //check mode(enum) settings
        switch (simulationMode) {
            case SEQUENTIAL:
                this.threadCount = 1;
                break;
            case PARALLEL:
                this.threadCount = Runtime.getRuntime().availableProcessors();
                break;
        }

        //inicializacija simulationcontexta v SphApplication nima še scene size defined
        if(SphApplication.scene != null){
            width = SphApplication.scene.getWidth();
            height = SphApplication.scene.getHeight();
        }
    }
}
