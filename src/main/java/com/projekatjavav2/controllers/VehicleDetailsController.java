package com.projekatjavav2.controllers;

import com.projekatjavav2.classes.vehicles.Vehicle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.projekatjavav2.classes.FileUtil.deserializeVehicles;
import static com.projekatjavav2.classes.FileUtil.getBinarySerializationPath;

public class VehicleDetailsController implements Initializable {



    @FXML
    private TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setText(Vehicle v){

        textArea.setText(v.toString());

    }

}
