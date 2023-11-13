package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.Main;
import com.projekatjavav2.classes.Passenger;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;

import static com.projekatjavav2.classes.FileUtil.serializeObject;

public class PersonalCar extends Vehicle implements PassengerTransport, Serializable {

    private int capacity = 5;
    Random r = new Random();
    private String name;
    private transient Color color;

    StringBuilder problems = new StringBuilder();

    private ArrayList<Passenger> passengersList = new ArrayList<>();

    private ArrayList<Passenger> removedPassengersList = new ArrayList<>();
    private int maxPassengers = 5;
    private int minPassengers = 1;

    public PersonalCar(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {
        super(controller, terminals, waitingQueue, customsTerminals);
        name = "A" + this.getID();
        color = Color.DARKRED;
        int numPassengers = r.nextInt(maxPassengers - minPassengers + 1) + minPassengers;
        int isDriver=r.nextInt(numPassengers-minPassengers+1)+minPassengers;
       //  System.out.println("Vozac u vozilu "+name+" je "+isDriver);
        for (int i = 1; i <= numPassengers; i++) {
            if (i == isDriver) {
                passengersList.add(new Passenger(i, true));
            } else passengersList.add(new Passenger(i, false));
        }
     //   serializeObject(this);
    }


    @Override
    protected boolean proccessVehicleOnPoliceTerminal() { //vraca false ako treba izbaciti vozilo
        try {
            Iterator<Passenger> iterator = passengersList.iterator();
            while (iterator.hasNext()) {
                Passenger p = iterator.next();
                Thread.sleep(500);
                if (p.isHasValidDocument() == false) {
                    if (p.getIsDriver()) {
                        System.out.println("Vozac sa id "+p.getID()+" nema validne dokumente i izbacuje se iz auta");
                        problems.append("Vozac sa id "+p.getID()+" nema validne dokumente pa kamion ne moze preci policijski terminal"+"\n");
                        removedPassengersList.add(p);
                        serializeObject(this);
                        return false;
                    } else {
                        System.out.println("Putnik sa id "+p.getID()+" nema validne dokumente i izbacuje se iz auta");
                        problems.append("Putnik sa id "+p.getID()+" nema validne dokumente i izbacuje se iz auta"+"\n");
                        removedPassengersList.add(p);
                        iterator.remove();
                    }
                }
            }
            // Thread.sleep(2000);
         //   if(!removedPassengersList.isEmpty()) serializeObject(this); //TODO f
            //serializeObject(this);
            return true;
        } catch (Exception ex) {
            Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected boolean proccessVehicleOnCustomsTerminal() {
        try {
            Thread.sleep(2000);
            return true;
        } catch (Exception ex) {
            Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
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

    @Override
    public String getProblemsString() {
        return problems.toString();
    }

    @Override
    public void setProblemsString(String s) {
        problems.append(s);
    }

    public PersonalCar() {

        for (int i = 0; i < r.nextInt(5); i++) {
            //  passangerList.add(new Passenger(i,"Putnik br "+i));
        }

    }

    public void printPassengers() {
        for (Passenger p : passengersList) {
            System.out.println(p.getIsDriver() + "  id " + p.getID());
        }
    }
    public ArrayList<Passenger> getRemovedPassengersList() {
        return removedPassengersList;
    }

    public void setRemovedPassengersList(ArrayList<Passenger> removedPassengersList) {
        this.removedPassengersList = removedPassengersList;
    }

    public ArrayList<Passenger> getPassengersList() {
        return passengersList;
    }


}
