package com.projekatjavav2.classes;

import com.projekatjavav2.controllers.HelloController;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

public class Vehicle extends Thread {

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
    Semaphore customsSemaphore = new Semaphore(1, true);

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
                        if (terminal.terminalState == Terminal.state.FREE) {
                            terminal.terminalState = Terminal.state.BUSY;
                            try {
                                System.out.println("Terminal " + terminal.getName() + " is entered by vehicle id" + this.ID);
                                Platform.runLater(() -> controller.moveIntoTerminal(terminal.getName(), this));
                                Platform.runLater(() -> controller.moveVehiclesUpInQueueTest(waitingQueue));
                                customsQueue.enqueue(waitingQueue.dequeue());
                                // System.out.println("LISTA VOZILA KOJI SU U CUSTOM QUEUE iz police "+"\n"+ customsQueue.getVehicleList());
                                // Platform.runLater(() -> controller.moveVehiclesUpInQueueTest(waitingQueue));

                                sleep(2000);

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Terminal " + terminal.getName() + " is finished with vehicle id but waiting to remove" + this.ID);
                            // this.vehicleState = state.FINISHED;
                            //  Platform.runLater(()->controller.removeFromTerminal(terminal.getName()));
                            this.vehicleState = state.WAITINGCUSTOMS;
                        /*    if (customsQueue.isFirst(this)) {
                                vehicleState = state.FIRSTCUSTOMS;
                            }*/
                            // terminal.terminalState = Terminal.state.FREE;
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
                        //      System.out.println("LISTA VOZILA KOJI SU U CUSTOM QUEUE " + "\n" + customsQueue.getVehicleList());
                        try {
                            String currentTerminal = controller.returnTerminalName(this);
                            Platform.runLater(() -> controller.removeFromTerminal(currentTerminal));
                               System.out.println("Terminal " + currentTerminal + " is removing vehicle id" + this.ID);
                            if (terminals.get(0).getName().equals(currentTerminal)) {
                                terminals.get(0).setTerminalState(Terminal.state.FREE);
                            }
                            else if (terminals.get(1).getName().equals(currentTerminal)) {
                                terminals.get(1).setTerminalState(Terminal.state.FREE);
                            }
                            // terminalSemaphore.release();
                            System.out.println("Terminal " + customsTerminal.getName() + " is occupied with vehicle id" + this.ID);
                            Platform.runLater(() -> controller.moveIntoTerminal(customsTerminal.getName(), this));
                            customsQueue.dequeue();
                            // Platform.runLater(() -> controller.moveVehiclesUpInQueueTest(waitingQueue));

                            sleep(2000);

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Terminal " + customsTerminal.getName() + " is finished with vehicle id" + this.ID);
                        // this.vehicleState = state.FINISHED;
                        this.vehicleState = state.FINISHED;
                        customsTerminal.terminalState = Terminal.state.FREE;
                        break;
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

            // }
        }
    }


}
