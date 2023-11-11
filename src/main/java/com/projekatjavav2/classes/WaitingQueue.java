package com.projekatjavav2.classes;

import com.projekatjavav2.classes.vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.CyclicBarrier;

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
        vehicles.size();

        cb=new CyclicBarrier(vehicles.size());

        for(Vehicle v:vehicles){
            System.out.println(v.getID());
            v.start();
        }

    }
    public void shuffleVehicles() {
        List<Vehicle> vehicleList = new LinkedList<>(vehicles); // Convert the queue to a list
        Collections.shuffle(vehicleList); // Shuffle the list
        vehicles.clear(); // Clear the original queue

        // Add the shuffled elements back to the queue
        vehicles.addAll(vehicleList);
    }

    public Queue<Vehicle> getVehicleList() {
        return vehicles; // Create a new ArrayList from the internal queue
    }
}
