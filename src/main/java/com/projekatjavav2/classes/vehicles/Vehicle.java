package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.*;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.terminals.Terminal;
import com.projekatjavav2.controllers.TerminalController;
import com.projekatjavav2.interfaces.CargoTransport;
import com.projekatjavav2.interfaces.PassengerTransport;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import static com.projekatjavav2.classes.FileUtil.serializeObject;

public abstract class Vehicle extends Thread  {

    public enum state {
        FIRSTPOLICE, WAITINGPOLICE, FINISHED, FIRSTCUSTOMS, WAITINGCUSTOMS
    }

    public state vehicleState;

    private Color color;

    private String name;

    private static int nextID = 1;
    int startingTerminalIndex = 0;
    int endingTerminalIndex=0;
    private static Vehicle vehicleWithLowestID = null;
    TerminalController controller;
    private static final Object obj = new Object();

    WaitingQueue waitingQueue;
    WaitingQueue customsQueue = new WaitingQueue();

    ArrayList<PoliceTerminal> terminals;
    ArrayList<CustomsTerminal> customsTerminals;
    Terminal customsTerminal=new CustomsTerminal();
    Semaphore terminalSemaphore = new Semaphore(3, true);

    private static int activeVehiclesCount = 0;

    private static volatile boolean isPaused = false;

    protected abstract boolean proccessVehicleOnPoliceTerminal() throws InterruptedException;
    public abstract String getVehicleName();
    public abstract Color getColor();

    public abstract String getProblemsString();
    public abstract void setProblemsString(String s);

    public abstract ArrayList<Passenger> getRemovedPassengersList() ;

    public abstract ArrayList<Passenger> getPassengersList() ;


    protected abstract boolean proccessVehicleOnCustomsTerminal();


    private int sleepTime = 2000;

    private int ID;

    public Vehicle() {
        vehicleState = state.WAITINGPOLICE;
        ID = nextID++;
    }

    public Vehicle(TerminalController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {

        this.controller = controller;
        vehicleState = state.WAITINGPOLICE;
        ID = nextID++;
        this.terminals = terminals;
        this.waitingQueue = waitingQueue;
        this.customsTerminals = customsTerminals;

    }

  //  protected ArrayList<Passenger> passangerList = new ArrayList<>();
    private boolean trg = false;

    public int getID() {
        return ID;
    }



    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public void run() {
        try { //waiting for all the threads to start
            waitingQueue.cb.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }

        synchronized (obj) {
                activeVehiclesCount++;
            if (this instanceof PassengerTransport) {
                startingTerminalIndex = 0;
                endingTerminalIndex=1;
                customsTerminal = customsTerminals.get(0);
            } else if (this instanceof CargoTransport) {
                startingTerminalIndex = 2;
                customsTerminal = customsTerminals.get(1);
            }
        }

        while (vehicleState != state.FINISHED) {

            synchronized (obj) {
                if (isPaused) {
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (waitingQueue.isFirst(this)) {
                    vehicleState = state.FIRSTPOLICE;
                } else if (customsQueue.isFirst(this)) {
                    vehicleState = state.FIRSTCUSTOMS;
                }
            }
            if (vehicleState == state.FIRSTPOLICE) {

                for (int i = startingTerminalIndex; i < terminals.size()-endingTerminalIndex; i++) {
                    Terminal terminal = terminals.get(i);
                    try {
                        terminalSemaphore.acquire();
                        if (terminal.getTerminalState() == Terminal.state.FREE && terminal.isTurnedOn()) { // ako je pl terminal free, ulazimo u njega
                            terminal.setTerminalState(Terminal.state.BUSY);
                            try {
                                System.out.println("Terminal " + terminal.getName() + " is entered by vehicle id" + this.ID);
                                Platform.runLater(() -> controller.moveIntoTerminal(terminal.getName(), this)); //premjestanje vozila u terminal
                                Platform.runLater(() -> controller.moveVehiclesUpInQueue(waitingQueue)); // pomjeramo ostala vozila u redu
                                customsQueue.enqueue(waitingQueue.dequeue()); // ulazi u red za carinu
                                //   sleep(sleepTime);
                                if(proccessVehicleOnPoliceTerminal()==false){
                                    System.out.println("Terminal " + terminal.getName() + " found that driver doesnt have valid docs and it is kicking out this vehicle" + this.ID);
                                    this.vehicleState=state.FINISHED;
                                    customsQueue.dequeue();
                                    Platform.runLater(() -> controller.removeFromTerminal(terminal.getName()));
                                    terminal.setTerminalState(Terminal.state.FREE);
                                    break;
                                }
                            } catch (InterruptedException e) {
                                Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
                                throw new RuntimeException(e);
                            }
                            System.out.println("Terminal " + terminal.getName() + " is finished with vehicle id but its waiting to remove" + this.ID);
                            this.vehicleState = state.WAITINGCUSTOMS; // ceka da se prabaci na customs terminal
                            break;
                        }
                    } catch (Exception ex) {
                        Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
                        ex.printStackTrace();

                    } finally {
                        terminalSemaphore.release();
                    }
                }
            }
            if (vehicleState == state.FIRSTCUSTOMS) {

                try {
                    if (customsTerminal.getTerminalState() == Terminal.state.FREE && customsTerminal.isTurnedOn()) {
                        customsTerminal.setTerminalState( Terminal.state.BUSY);

                        try {
                            String currentTerminal = controller.returnTerminalName(this); // ako je prvi za obradu na customs, trazimo na kom pol terminalu se nalazi
                            Platform.runLater(() -> controller.removeFromTerminal(currentTerminal)); //brisemo ga sa pol terminala
                            System.out.println("Terminal " + currentTerminal + " is removing vehicle id" + this.ID);
                            if (terminals.get(0).getName().equals(currentTerminal)) {   //stavljamo stanje pol terminala na free
                                terminals.get(0).setTerminalState(Terminal.state.FREE);
                            } else if (terminals.get(1).getName().equals(currentTerminal)) {
                                terminals.get(1).setTerminalState(Terminal.state.FREE);
                            }
                            else if (terminals.get(2).getName().equals(currentTerminal)) {
                                terminals.get(2).setTerminalState(Terminal.state.FREE);
                            }

                            System.out.println("Terminal " + customsTerminal.getName() + " is occupied with vehicle id" + this.ID);
                            Platform.runLater(() -> controller.moveIntoTerminal(customsTerminal.getName(), this)); // premjestamo vozilo na customs terminal
                            customsQueue.dequeue();

                            // sleep(sleepTime);
                            if(proccessVehicleOnCustomsTerminal()==false){
                                System.out.println("Terminal " + customsTerminal.getName() + " found that something is illegal and it is kicking out this vehicle" + this.ID);
                                this.vehicleState=state.FINISHED;
                                serializeObject(this);
                              //  customsQueue.dequeue();
                                Platform.runLater(() -> controller.removeFromTerminal(customsTerminal.getName()));
                                customsTerminal.setTerminalState(Terminal.state.FREE);
                                break;
                            }

                        } catch (Exception e) {
                            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
                            throw new RuntimeException(e);
                        }
                        System.out.println("Terminal " + customsTerminal.getName() + " is finished with vehicle id" + this.ID);
                        if(!getRemovedPassengersList().isEmpty()) serializeObject(this); //ako je bilo izbacivanja putnika serialize
                        this.vehicleState = state.FINISHED; // vozilo je obradjeno
                        Platform.runLater(() -> controller.removeFromTerminal(customsTerminal.getName()));
                        customsTerminal.setTerminalState(Terminal.state.FREE); //customs terminal je free/
                        //  break;
                    }
                } catch (Exception ex) {
                    Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
                    ex.printStackTrace();
                }
            }
            if (vehicleState == state.WAITINGPOLICE) {
                synchronized (obj) {
                    if (waitingQueue.isFirst(this)) {
                        vehicleState = state.FIRSTPOLICE;
                    }
                }
            }
            if (vehicleState == state.WAITINGCUSTOMS) {
                synchronized (obj) {
                    if (customsQueue.isFirst(this)) {
                        vehicleState = state.FIRSTCUSTOMS;
                    }
                }
            }

        }
        synchronized (obj) {
            activeVehiclesCount--;
            if (activeVehiclesCount == 0) {

                System.out.println("All vehicles are done.");
                controller.stopTimer();
                controller.getButtonPause().setDisable(true);
            }
        }

    }
    public static void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            synchronized (obj) {
                obj.notifyAll();
            }
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vehicle Name: ").append(getVehicleName()).append("\n");

        sb.append("Driver: ");
        for (Passenger passenger : getPassengersList()) {
            if(passenger.getIsDriver())
            sb.append(passenger.getID()).append("\n");
        }
        sb.append("Number of Passengers: ").append(getPassengersList().size()).append("\n");

        sb.append("Passengers List:\n");
        for (Passenger passenger : getPassengersList()) {
            boolean chk=getRemovedPassengersList().stream().anyMatch(e->e.getID()==passenger.getID());
            if(!chk){
                sb.append(passenger.getID()).append("\n");
            }
        }
        sb.append("Removed Passengers List:\n");
        for (Passenger passenger : getRemovedPassengersList()) {
            sb.append(passenger.getID()).append("\n");
        }
        if(this instanceof CargoTransport){
            sb.append(((Truck)this).getMass());
        }
        sb.append("List of problems with Vehicle:\n");
        sb.append(this.getProblemsString());

        return sb.toString();
    }


}
