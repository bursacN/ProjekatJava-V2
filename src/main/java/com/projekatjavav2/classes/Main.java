package com.projekatjavav2.classes;

import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.vehicles.PersonalCar;
import com.projekatjavav2.classes.vehicles.Truck;
import com.projekatjavav2.classes.vehicles.Vehicle;
import com.projekatjavav2.controllers.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/hello-view.fxml"));
        Parent root=fxmlLoader.load();
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        Queue<Vehicle> vehicles=new LinkedList<>();

        ArrayList<PoliceTerminal> policeTerminals = new ArrayList<>();
        ArrayList<CustomsTerminal> customsTerminals = new ArrayList<>();
        //TODO naci bolje rjesenje bez stringova
        customsTerminals.add(new CustomsTerminal("k1"));
        customsTerminals.add(new CustomsTerminal("k2"));

        policeTerminals.add(new PoliceTerminal("t1"));
        policeTerminals.add(new PoliceTerminal("t2"));
        policeTerminals.add(new PoliceTerminal("t3"));

        HelloController controller=fxmlLoader.getController();

        int numberOfPersonalCars=5;
        int numberOfTrucks=5;

       /* for(int i=0;i<numberOfPersonalCars;i++){
            vehicles.add(new PersonalCar());
        }
        controller.setUpVehicles(vehicles);
        controller.moveIntoTerminal( terminals.get(1).getName(),vehicles.poll());
        controller.moveVehiclesUpInQueue(vehicles);*/

        WaitingQueue vehicless = new WaitingQueue();
        for(int i=0;i<numberOfPersonalCars;i++){
            vehicless.enqueue(new PersonalCar(controller,policeTerminals,vehicless,customsTerminals));
        }
        for(int i=0;i<numberOfTrucks;i++){
            vehicless.enqueue(new Truck(controller,policeTerminals,vehicless,customsTerminals));
        }
        controller.setUpVehiclesInitial(vehicless);
        vehicless.shuffleVehicles();
        vehicless.startVehicles();
   //    controller.moveIntoTerminal("k2",vehicless.dequeue());
      // controller.moveIntoTerminal("t3",vehicless.peek());
       // controller.removeFromTerminal("t3");
      // System.out.println(controller.returnTerminalName(vehicless.peek()));




    }
    public static void main(String[] args) {
        launch();
    }
}