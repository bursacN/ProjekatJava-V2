package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.Passenger;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static com.projekatjavav2.classes.FileUtil.serializeObject;
import static com.projekatjavav2.classes.FileUtil.writeReport;

public class Bus extends Vehicle implements PassengerTransport, Serializable {
    private String name;
    private transient Color color;
    Random r = new Random();
    private ArrayList<Passenger> passengersList = new ArrayList<>();
    private int maxPassengers = 52;
    private int minPassengers = 1;

    private static final Object obj=new Object();
    private ArrayList<Passenger> removedPassengersList = new ArrayList<>();

    public Bus(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {
        super(controller, terminals, waitingQueue, customsTerminals);
        name = "B" + this.getID();
        color = Color.LIGHTSKYBLUE;


        int numPassengers = r.nextInt(maxPassengers - minPassengers + 1) + minPassengers;
        int numberOfSuitcases=0;
        int isDriver=r.nextInt(numPassengers);

        for (int i = 1; i < numPassengers; i++) {

            // Determine if the passenger has a suitcase
            boolean hasSuitcase = r.nextDouble() <= 0.7;
            if(hasSuitcase) numberOfSuitcases++;
            // Determine if the passenger with a suitcase has forbidden items
            if (i == isDriver) {
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
            Iterator<Passenger> iterator = passengersList.iterator();
            while (iterator.hasNext()) {
                Passenger p = iterator.next();
                Thread.sleep(100);

                if (!p.isHasValidDocument()) {
                    if (p.getIsDriver()) {
                        System.out.println("Vozac sa id "+p.getID()+" nema validne dokumente i izbacuje se iz auta");
                        removedPassengersList.add(p);
                        serializeObject(this);
                        return false;
                    } else {
                        System.out.println("Putnik sa id "+p.getID()+" nema validne dokumente i izbacuje se iz auta");
                        removedPassengersList.add(p);
                        iterator.remove(); // Use iterator to safely remove the element
                    }
                }
            }
            // Thread.sleep(2000);
            if(!removedPassengersList.isEmpty()) serializeObject(this);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    @Override
    protected boolean proccessVehicleOnCustomsTerminal() {
        try {
            Iterator<Passenger> iterator=passengersList.iterator();
            while (iterator.hasNext()){
                Passenger p=iterator.next();
                Thread.sleep(100);
                if(p.isHaveForbiddenItems()==true){ //TODO pitanje da li vozac uopste moze imati forbiddden items
                    if(p.getIsDriver()){
                        writeReport(forbbidenItemsReport(p));
                        return false;
                    }
                    else{
                        writeReport(forbbidenItemsReport(p));
                        iterator.remove();
                    }
                }
            }
            // Thread.sleep(2000);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
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
    public String forbbidenItemsReport(Passenger p){
        return this.name+" autobus mora izbaciti putnika "+ p.getID()+" jer ima nedozvoljene iteme"+ File.separator;
    }

}
