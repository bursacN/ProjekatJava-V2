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
        return vehicles.poll();
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
        List<Vehicle> vehicleList = new LinkedList<>(vehicles);
        Collections.shuffle(vehicleList);
        vehicles.clear();


        vehicles.addAll(vehicleList);
    }

    public Queue<Vehicle> getVehicleList() {
        return vehicles;
    }
}
