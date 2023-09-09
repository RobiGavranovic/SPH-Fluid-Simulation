package sph.sphfluidsimulation;


public enum SimulationMode {
    SEQUENTIAL("Sequential"),
<<<<<<< HEAD
    PARALLEL("Parallel");
=======
    PARALLEL("Parallel"),
    DISTRIBUTED("Distributed");
>>>>>>> b90b9e376493bce5cd9d3d59236455ba705d2efe

    private final String text;

    SimulationMode(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static SimulationMode fromString(String text) {
        for (SimulationMode mode : SimulationMode.values()) {
            if (mode.text.equalsIgnoreCase(text)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("No enum value found for text: " + text);
    }
}

