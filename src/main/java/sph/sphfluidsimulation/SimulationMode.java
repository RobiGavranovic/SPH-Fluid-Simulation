package sph.sphfluidsimulation;


public enum SimulationMode {
    SEQUENTIAL("Sequential"),
    PARALLEL("Parallel"),
    DISTRIBUTED("Distributed");

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

