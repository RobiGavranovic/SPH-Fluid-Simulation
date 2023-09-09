package sph.sphfluidsimulation;

public class DialogConfig {
    //variables gotten from dialog window
    public String particleCount;
    public SimulationMode simulationMode;
    public boolean rainEnabled;

    public DialogConfig(String particleCount, SimulationMode mode, boolean rainEnabled) {
        this.particleCount = particleCount;
        this.rainEnabled = rainEnabled;
        this.simulationMode = mode;
    }
}