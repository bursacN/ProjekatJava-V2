package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.Passenger;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.CargoTransport;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Truck extends Vehicle implements CargoTransport {

    private String name;
    private Color color;

    Random r=new Random();
    private ArrayList<Passenger> passengersList=new ArrayList<>();

    private double declaredMass=0;
    private double realMass=0;
    private boolean hasDocumentation=false;
    private int maxPassengers=3;
    private int minPassengers=1;
    DecimalFormat df = new DecimalFormat("#.##"); // Pattern for two decimal places

    private static int trucksWithIncreasedRealMass = 0;
    private static int totalTrucksToCreate = 10;

    @Override
    protected boolean proccessVehicleOnPoliceTerminal() throws InterruptedException {
        try {
            Thread.sleep(2000);
            return true;
        }
        catch (Exception ex){

        }
        return false;
    }

    @Override
    public String getVehicleName() {
        return name;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    protected boolean proccessVehicleOnCustomsTerminal() {
        try {
            Thread.sleep(2000);
            return true;
        }
        catch (Exception ex){

        }
        return false;
    }

    public Truck(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {
        super( controller, terminals,waitingQueue, customsTerminals);
        name="K"+this.getID();
        color=Color.DARKBLUE;

        int numPassengers = r.nextInt(maxPassengers - minPassengers + 1) + minPassengers;
        // System.out.println(numPassengers);
        for (int i = 1; i < numPassengers; i++) {
            if(i==1){
                passengersList.add(new Passenger(i,true));
            }
            else passengersList.add(new Passenger(i,false));
        }
        this.hasDocumentation=r.nextBoolean();

        String formatted = df.format(r.nextDouble(7,36)*1000);
        declaredMass = Double.parseDouble(formatted);
        realMass=getRealMassPercentage();


       // System.out.println(" stvarna masa "+ realMass+" deklarisana "+ declaredMass);

    }
    private double getRealMassPercentage(){

        if (trucksWithIncreasedRealMass < (totalTrucksToCreate * 0.2)) {
            double percentageIncrease = r.nextDouble() * 0.3; // Up to 30% increase
            realMass = declaredMass * (1 + percentageIncrease);

            trucksWithIncreasedRealMass++;
        } else {
            realMass = declaredMass; // Real mass is the same as declared mass
        }
        String formatted = df.format(realMass);
        realMass = Double.parseDouble(formatted);
        return realMass;


    }
}
