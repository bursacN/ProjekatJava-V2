package com.projekatjavav2.classes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class WaitingQueue {

    private Queue<Vehicle> vehicles = new LinkedList<>();
    public static Object obj = new Object();
    public CyclicBarrier cb;


    public synchronized void enqueue(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public synchronized Vehicle dequeue() {
        return vehicles.poll(); // Use poll() to remove and return the head of the queue
    }
    public synchronized Vehicle peek(){
        return  vehicles.peek();
    }

    public boolean isEmpty() {
        return vehicles.isEmpty();
    }
    public synchronized boolean isFirst(Vehicle vehicle)
    {
        if(vehicle==vehicles.peek()){
            return true;
        }
        else return false;
    }
    public int getSize(){
        return vehicles.size();
    }

    public void startVehicles(){
        cb=new CyclicBarrier(vehicles.size());

        for(Vehicle v:vehicles){
            System.out.println(v.getID());
            v.start();
        }

    }

    public Queue<Vehicle> getVehicleList() {
        return vehicles; // Create a new ArrayList from the internal queue
    }
}
