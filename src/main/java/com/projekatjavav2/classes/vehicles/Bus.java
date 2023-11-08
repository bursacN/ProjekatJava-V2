package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.Passenger;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class Bus extends Vehicle implements PassengerTransport {
    private String name;
    private Color color;
    Random r = new Random();
    private ArrayList<Passenger> passengersList = new ArrayList<>();
    private int maxPassengers = 52;
    private int minPassengers = 1;

    public Bus(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {
        super(controller, terminals, waitingQueue, customsTerminals);
        name = "B" + this.getID();
        color = Color.LIGHTSKYBLUE;


        int numPassengers = r.nextInt(maxPassengers - minPassengers + 1) + minPassengers;
        int numberOfSuitcases=0;

        for (int i = 1; i < numPassengers; i++) {

            // Determine if the passenger has a suitcase
            boolean hasSuitcase = r.nextDouble() <= 0.7;
            if(hasSuitcase) numberOfSuitcases++;
            // Determine if the passenger with a suitcase has forbidden items
            if (i == 1) {
                passengersList.add(new Passenger(i, true,hasSuitcase));
            }
            else passengersList.add(new Passenger(i, false,hasSuitcase));
        }
        int numberOfForbiddenItems=(int)(0.1*numberOfSuitcases);


        while(numberOfForbiddenItems!=0){
            int tmp=r.nextInt(passengersList.size());
            if(passengersList.get(tmp).isHasSuitcase()){
                passengersList.get(tmp).setHaveForbiddenItems(true);
                numberOfForbiddenItems--;
            }
        }

    }

    @Override
    protected boolean proccessVehicleOnPoliceTerminal() throws InterruptedException {
        try {
            Thread.sleep(2000);
            return true;
        } catch (Exception ex) {

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
        } catch (Exception ex) {

        }
        return false;
    }
}
