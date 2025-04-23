module com.projekatjavav2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    opens com.projekatjavav2 to javafx.fxml;
    exports com.projekatjavav2.controllers;
    opens com.projekatjavav2.controllers to javafx.fxml;
    exports com.projekatjavav2.classes;
    opens com.projekatjavav2.classes to javafx.fxml;
    exports com.projekatjavav2.classes.vehicles;
    opens com.projekatjavav2.classes.vehicles to javafx.fxml;
    exports com.projekatjavav2.classes.terminals;
    opens com.projekatjavav2.classes.terminals to javafx.fxml;
}