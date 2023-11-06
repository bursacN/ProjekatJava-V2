package com.projekatjavav2.classes;

import com.projekatjavav2.controllers.HelloController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        CustomsTerminal customsTerminal = new CustomsTerminal("k1");

        policeTerminals.add(new PoliceTerminal("t1"));
        policeTerminals.add(new PoliceTerminal("t2"));

        HelloController controller=fxmlLoader.getController();

        int numberOfPersonalCars=50;

       /* for(int i=0;i<numberOfPersonalCars;i++){
            vehicles.add(new PersonalCar());
        }
        controller.setUpVehicles(vehicles);
        controller.moveIntoTerminal( terminals.get(1).getName(),vehicles.poll());
        controller.moveVehiclesUpInQueue(vehicles);*/

        WaitingQueue vehicless = new WaitingQueue();
        for(int i=0;i<numberOfPersonalCars;i++){
            vehicless.enqueue(new Vehicle(controller,policeTerminals,vehicless,customsTerminal));
        }
        controller.setUpVehiclesTest(vehicless);
        vehicless.startVehicles();
     //  controller.moveIntoTerminal("t1",vehicless.dequeue());
     //   controller.moveIntoTerminal("t2",vehicless.peek());
        //controller.removeFromTerminal("t1");
       // System.out.println(controller.returnTerminalName(vehicless.peek()));




    }
    public static void main(String[] args) {
        launch();
    }
}