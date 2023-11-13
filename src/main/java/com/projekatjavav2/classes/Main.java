package com.projekatjavav2.classes;

import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.terminals.Terminal;
import com.projekatjavav2.classes.vehicles.Bus;
import com.projekatjavav2.classes.vehicles.PersonalCar;
import com.projekatjavav2.classes.vehicles.Truck;
import com.projekatjavav2.classes.vehicles.Vehicle;
import com.projekatjavav2.controllers.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.projekatjavav2.classes.FileUtil.*;
import static com.projekatjavav2.classes.Passenger.*;
import static com.projekatjavav2.classes.vehicles.Vehicle.togglePause;

public class Main extends Application {
    static FileHandler handler;
    public static Logger logger;
    static {
        try {
            handler = new FileHandler("Exceptions.log");
            logger = Logger.getLogger(Main.class.getName());
            logger.addHandler(handler);
        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/terminal.fxml"));
        Parent root=fxmlLoader.load();
        Scene scene = new Scene(root, 800, 600);
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

        int numberOfPersonalCars=35;
        int numberOfTrucks=10;
        int numberOfBuses=5;

       /* for(int i=0;i<numberOfPersonalCars;i++){
            vehicles.add(new PersonalCar());
        }
        controller.setUpVehicles(vehicles);
        controller.moveIntoTerminal( terminals.get(1).getName(),vehicles.poll());
        controller.moveVehiclesUpInQueue(vehicles);*/

        WaitingQueue vehicless = new WaitingQueue();
        for(int i=0;i<numberOfPersonalCars;i++){
            vehicless.enqueue(new PersonalCar(controller,policeTerminals,vehicless,customsTerminals));
          //  serializeObject(vehicless.peek());
        }
        for(int i=0;i<numberOfTrucks;i++){
            vehicless.enqueue(new Truck(controller,policeTerminals,vehicless,customsTerminals));
        }
        for(int i=0;i<numberOfBuses;i++){
            vehicless.enqueue(new Bus(controller,policeTerminals,vehicless,customsTerminals));
        }
        controller.setUpVehiclesInitial(vehicless);
       // System.out.println("total passengers "+ getTotalPassengers()+ " sa invalid "+ getPassengersWithInvalidDocs());
        System.out.println("ukupan broj putnika "+ getTotalPassengers()+" sa invalid docs "+getPassengersWithInvalidDocs()+" od kojih su vozaci "+getDrivers());
        vehicless.shuffleVehicles();

        controller.getButtonStart().setOnAction(event -> {
            // Put the code you want to execute when the button is clicked here
            controller.startTimer( System.currentTimeMillis());
            vehicless.startVehicles();
        });
        //TODO prebaciti u hellocontroller
        controller.getButtonPause().setOnAction(event -> {
            if ( controller.getButtonPause().isSelected()) {
                controller.getButtonPause().setText("RESUME");
            } else {
                controller.getButtonPause().setText("PAUSE");
            }
            // Put the code you want to execute when the button is clicked here
            togglePause();
        });
        controller.getButtonShowAll().setOnAction(event -> {
          controller.showAll();
        });
        ArrayList<Terminal> terminalss= new ArrayList<>();
        terminalss.addAll(policeTerminals);
        terminalss.addAll(customsTerminals);
        setTerminalsData(terminalss, readTxtFile("C:\\Users\\bursa\\Desktop\\etf\\java\\projekat pokusaji\\ProjekatJava V2\\TerminalsData.txt"));
        for(Terminal t:terminalss){
            System.out.println(t.getName()+" je ukljucen "+ t.isTurnedOn());
        }
        String directoryPath="C:\\Users\\bursa\\Desktop\\etf\\java\\projekat pokusaji\\ProjekatJava V2";
        String fileName="TerminalsData.txt";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
          watchDirectory(directoryPath,fileName,terminalss);
            } catch (IOException e) {
                e.printStackTrace();
                Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            }
        });


       // Callable<String> watchTask = () -> watchDirectory(directoryPath, fileName);









        //TEST
    //  controller.moveIntoTerminal("k1",vehicless.dequeue());
      // controller.moveIntoTerminal("t3",vehicless.peek());
      //  controller.removeFromTerminal("k1");
      // System.out.println(controller.returnTerminalName(vehicless.peek()));


    }
    public static void main(String[] args) {
        launch();
    }

}