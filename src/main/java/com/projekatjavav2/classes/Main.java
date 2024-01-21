package com.projekatjavav2.classes;

import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.terminals.Terminal;
import com.projekatjavav2.classes.vehicles.Bus;
import com.projekatjavav2.classes.vehicles.PersonalCar;
import com.projekatjavav2.classes.vehicles.Truck;
import com.projekatjavav2.classes.vehicles.Vehicle;
import com.projekatjavav2.controllers.TerminalController;
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
import java.util.concurrent.atomic.AtomicLong;
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
        stage.setTitle("Granicni prelaz");
        stage.setScene(scene);
        stage.show();

        TerminalController controller=fxmlLoader.getController();

        setUpSimulation(controller);


    }
    public void setUpSimulation(TerminalController controller){

        int numberOfPersonalCars=35;
        int numberOfTrucks=10;
        int numberOfBuses=5;

        WaitingQueue vehicless = new WaitingQueue();
        ArrayList<PoliceTerminal> policeTerminals = new ArrayList<>();
        ArrayList<CustomsTerminal> customsTerminals = new ArrayList<>();
        //Carinski terminali
        customsTerminals.add(new CustomsTerminal(CustomsTerminal.name.k1));
        customsTerminals.add(new CustomsTerminal(CustomsTerminal.name.k2));

        //Policijski terminali
        policeTerminals.add(new PoliceTerminal(PoliceTerminal.name.t1));
        policeTerminals.add(new PoliceTerminal(PoliceTerminal.name.t2));
        policeTerminals.add(new PoliceTerminal(PoliceTerminal.name.t3));


        for(int i=0;i<numberOfPersonalCars;i++){
            vehicless.enqueue(new PersonalCar(controller,policeTerminals,vehicless,customsTerminals));
        }
        for(int i=0;i<numberOfTrucks;i++){
            vehicless.enqueue(new Truck(controller,policeTerminals,vehicless,customsTerminals));
        }
        for(int i=0;i<numberOfBuses;i++){
            vehicless.enqueue(new Bus(controller,policeTerminals,vehicless,customsTerminals));
        }

        vehicless.shuffleVehicles();

        controller.setUpVehiclesInitial(vehicless);

        //System.out.println("ukupan broj putnika "+ getTotalPassengers()+" sa invalid docs "+getPassengersWithInvalidDocs()+" od kojih su vozaci "+getDrivers());



        controller.getButtonStart().setOnAction(event -> {
            AtomicLong elapsedTime;
            controller.startTimer();
            vehicless.startVehicles();
            controller.getButtonStart().setDisable(true);
            controller.getButtonPause().setDisable(false);
        });

        ArrayList<Terminal> terminalss= new ArrayList<>();
        terminalss.addAll(policeTerminals);
        terminalss.addAll(customsTerminals);

        String directoryPath=System.getProperty("user.dir");
        String fileName="TerminalsData.txt";

        setTerminalsData(terminalss, readTxtFile(directoryPath + File.separator +fileName));

        for(Terminal t:terminalss){
            System.out.println(t.getName()+" je ukljucen "+ t.isTurnedOn());
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                watchDirectory(directoryPath,fileName,terminalss);
            } catch (IOException e) {
                e.printStackTrace();
                Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            }
        });
    }
    public static void main(String[] args) {
        launch();
    }

}