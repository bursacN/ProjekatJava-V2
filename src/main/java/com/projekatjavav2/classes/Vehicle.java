package com.projekatjavav2.classes;

import com.projekatjavav2.controllers.HelloController;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Semaphore;

public class Vehicle extends Thread{

    public enum state{
        TOP5,FIRST,WAITING,FINISHED
    }
    //  public state gui;
    public state vehicleState;
    private static int nextID=1;
    private static Vehicle vehicleWithLowestID = null;
    HelloController controller;
    public static Object obj=new Object();

    WaitingQueue waitingQueue;

    ArrayList<PoliceTerminal> terminals;
    Semaphore terminalSemaphore = new Semaphore(2,true);

    private int ID;
    public Vehicle() {
        //  gui=state.TOP5;
        vehicleState=state.WAITING;
        ID =nextID++;
    }
    public Vehicle(HelloController controller, ArrayList<PoliceTerminal> terminals,WaitingQueue waitingQueue){

        //  gui=state.TOP5;
        this.controller=controller;
        vehicleState=state.WAITING;
        ID=nextID++;
        this.terminals=terminals;
        this.waitingQueue=waitingQueue;
    }
    protected ArrayList<Passenger> passangerList= new ArrayList<>();

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

        while (vehicleState != state.FINISHED) {

            synchronized (obj) {
                if (waitingQueue.isFirst(this)) {
                    vehicleState = state.FIRST;
                }
            }
            if (vehicleState == state.FIRST) {
                for (Terminal terminal : terminals) {
                    try {
                        terminalSemaphore.acquire();
                        if (terminal.terminalState == Terminal.state.FREE) {
                            terminal.terminalState = Terminal.state.BUSY;
                            try {
                                System.out.println("Terminal " + terminal.getName() + " is occupied with vehicle id" + this.ID);
                                Platform.runLater(() -> controller.moveIntoTerminal(terminal.getName(), this));
                                waitingQueue.dequeue();
                                Platform.runLater(() -> controller.moveVehiclesUpInQueueTest(waitingQueue));

                                sleep(2000);

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Terminal " + terminal.getName() + " is finished with vehicle id" + this.ID);
                            this.vehicleState = state.FINISHED;
                            terminal.terminalState = Terminal.state.FREE;
                            break;
                        }
                    } catch (Exception ex) {

                    } finally {
                        terminalSemaphore.release();
                    }
                }
            }
            if (vehicleState == state.WAITING) {

                if (waitingQueue.isFirst(this)) {
                    vehicleState = state.FIRST;
                }

            }
        }
    }





}
