package com.projekatjavav2.classes;

import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

public abstract class Vehicle extends Thread {

    public enum state {
        TOP5, FIRSTPOLICE, WAITINGPOLICE, FINISHED, FIRSTCUSTOMS, WAITINGCUSTOMS
    }

    //  public state gui;
    public state vehicleState;
    private static int nextID = 1;
    private static Vehicle vehicleWithLowestID = null;
    HelloController controller;
    public static Object obj = new Object();

    WaitingQueue waitingQueue;
    WaitingQueue customsQueue = new WaitingQueue();

    ArrayList<PoliceTerminal> terminals;
    CustomsTerminal customsTerminal;
    Semaphore terminalSemaphore = new Semaphore(2, true);

    protected abstract boolean proccessVehicleOnPoliceTerminal() throws InterruptedException;

    protected abstract boolean proccessVehicleOnCustomsTerminal();

    Terminal cargoPoliceTerminal = new Terminal("t3");
    Terminal cargoCustomsTerminal = new Terminal("k2");


    private int sleepTime = 2000;

    private int ID;

    public Vehicle() {
        //  gui=state.TOP5;
        vehicleState = state.WAITINGPOLICE;
        ID = nextID++;
    }

    public Vehicle(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, CustomsTerminal customsTerminal) {

        //  gui=state.TOP5;
        this.controller = controller;
        vehicleState = state.WAITINGPOLICE;
        ID = nextID++;
        this.terminals = terminals;
        this.waitingQueue = waitingQueue;
        this.customsTerminal = customsTerminal;

    }

    protected ArrayList<Passenger> passangerList = new ArrayList<>();
    private boolean trg = false;

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return Integer.toString(ID);
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

        while (vehicleState != state.FINISHED) {

            synchronized (obj) {
                if (waitingQueue.isFirst(this)) {
                    vehicleState = state.FIRSTPOLICE;
                } else if (customsQueue.isFirst(this)) {
                    vehicleState = state.FIRSTCUSTOMS;
                }
            }
            if (vehicleState == state.FIRSTPOLICE) {
                for (Terminal terminal : terminals) {
                    try {
                        terminalSemaphore.acquire();
                        if (terminal.terminalState == Terminal.state.FREE) { // ako je pl terminal free, ulazimo u njega
                            terminal.terminalState = Terminal.state.BUSY;
                            try {
                                System.out.println("Terminal " + terminal.getName() + " is entered by vehicle id" + this.ID);
                                Platform.runLater(() -> controller.moveIntoTerminal(terminal.getName(), this)); //premjestanje vozila u terminal
                                Platform.runLater(() -> controller.moveVehiclesUpInQueueTest(waitingQueue)); // pomjeramo ostala vozila u redu
                                customsQueue.enqueue(waitingQueue.dequeue()); // ulazi u red za carinu
                                //   sleep(sleepTime);
                                proccessVehicleOnPoliceTerminal();

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Terminal " + terminal.getName() + " is finished with vehicle id but its waiting to remove" + this.ID);
                            this.vehicleState = state.WAITINGCUSTOMS; // ceka da se prabaci na customs terminal
                            break;
                        }
                    } catch (Exception ex) {

                    } finally {
                        terminalSemaphore.release();

                    }
                }
            }
            if (vehicleState == state.FIRSTCUSTOMS) {
                try {
                    if (customsTerminal.terminalState == Terminal.state.FREE) {
                        customsTerminal.terminalState = Terminal.state.BUSY;

                        try {
                            String currentTerminal = controller.returnTerminalName(this); // ako je prvi za obradu na customs, trazimo na kom pol terminalu se nalazi
                            Platform.runLater(() -> controller.removeFromTerminal(currentTerminal)); //brisemo ga sa pol terminala
                            System.out.println("Terminal " + currentTerminal + " is removing vehicle id" + this.ID);
                            if (terminals.get(0).getName().equals(currentTerminal)) {   //stavljamo stanje pol terminala na free
                                terminals.get(0).setTerminalState(Terminal.state.FREE);
                            } else if (terminals.get(1).getName().equals(currentTerminal)) {
                                terminals.get(1).setTerminalState(Terminal.state.FREE);
                            }

                            System.out.println("Terminal " + customsTerminal.getName() + " is occupied with vehicle id" + this.ID);
                            Platform.runLater(() -> controller.moveIntoTerminal(customsTerminal.getName(), this)); // premjestamo vozilo na customs terminal
                            customsQueue.dequeue();

                            // sleep(sleepTime);
                            proccessVehicleOnCustomsTerminal();

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Terminal " + customsTerminal.getName() + " is finished with vehicle id" + this.ID);

                        this.vehicleState = state.FINISHED; // vozilo je obradjeno
                        customsTerminal.terminalState = Terminal.state.FREE; //customs terminal je free
                        //  break;
                    }
                } catch (Exception ex) {
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

    }


}
