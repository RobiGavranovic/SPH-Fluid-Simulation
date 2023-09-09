module sph.sphfluidsimulation {
    requires javafx.controls;
    requires javafx.fxml;

    opens sph.sphfluidsimulation to javafx.fxml;
    exports sph.sphfluidsimulation;
}