package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.Main;
import com.projekatjavav2.classes.Passenger;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.controllers.TerminalController;
import com.projekatjavav2.interfaces.CargoTransport;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;

import static com.projekatjavav2.classes.FileUtil.serializeObject;
import static com.projekatjavav2.classes.FileUtil.writeReport;

public class Truck extends Vehicle implements CargoTransport, Serializable {

    private String name;
    private transient Color color;

    Random r=new Random();
    private ArrayList<Passenger> passengersList=new ArrayList<>();
    private ArrayList<Passenger> removedPassengersList = new ArrayList<>();

    private double declaredMass=0;
    private double realMass=0;
    private boolean needsDocumentation =false;
    StringBuilder problems = new StringBuilder();

    private boolean hasDocumentation=false;
    private int maxPassengers=3;
    private int minPassengers=1;
    DecimalFormat df = new DecimalFormat("#.##");

    private static int trucksWithIncreasedRealMass = 0;
    private static int totalTrucksToCreate = 10;

    public Truck(TerminalController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {
        super( controller, terminals,waitingQueue, customsTerminals);
        name="K"+this.getID();
        color=Color.DARKBLUE;

        int numPassengers = r.nextInt(maxPassengers - minPassengers + 1) + minPassengers;
        int isDriver=r.nextInt(numPassengers-minPassengers+1)+minPassengers;
        // System.out.println(numPassengers);
        for (int i = 1; i <= numPassengers; i++) {
            if(i==isDriver){
                passengersList.add(new Passenger(i,true));
            }
            else passengersList.add(new Passenger(i,false));
        }
        this.needsDocumentation =r.nextBoolean();

        String formatted = df.format(r.nextDouble(7,36)*1000);
        declaredMass = Double.parseDouble(formatted);
        realMass=getRealMassPercentage();


        // System.out.println(" stvarna masa "+ realMass+" deklarisana "+ declaredMass);
       // serializeObject(this);
    }

    @Override
    protected boolean proccessVehicleOnPoliceTerminal() throws InterruptedException {
        try {
            Iterator<Passenger> iterator=passengersList.iterator();
           while(iterator.hasNext()){
               Passenger p=iterator.next();
                Thread.sleep(500);
                if(p.isHasValidDocument()==false){
                    if(p.getIsDriver()){
                        System.out.println("Vozac sa id "+p.getID()+" nema validne dokumente i izbacuje se iz kamiona");
                        problems.append("Vozac sa id "+p.getID()+" nema validne dokumente pa kamion ne moze preci policijski terminal"+"\n");
                        removedPassengersList.add(p);
                        serializeObject(this);
                        return false;
                    }
                    else{
                        System.out.println("Putnik sa id "+p.getID()+" nema validne dokumente i izbacuje se iz kamiona");
                        problems.append("Putnik sa id "+p.getID()+" nema validne dokumente i izbacuje se iz kamiona"+"\n");
                        removedPassengersList.add(p);
                       iterator.remove();
                    }
                }
            }
            return true;
        }
        catch (Exception ex){
            Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
            ex.printStackTrace();
        }
        return false;
    }
    @Override
    protected boolean proccessVehicleOnCustomsTerminal() {
        try {
            Thread.sleep(500);
            if(this.realMass>this.declaredMass){
                writeReport(overWeightReport());
                return false;
            }
            if(this.needsDocumentation==true) this.hasDocumentation=true;
            return true;
        }
        catch (Exception ex){
            Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
            ex.printStackTrace();
        }
        return false;
    }
    private double getRealMassPercentage(){

        if (trucksWithIncreasedRealMass < (totalTrucksToCreate * 0.2)) {
            double percentageIncrease = r.nextDouble() * 0.3;
            realMass = declaredMass * (1 + percentageIncrease);

            trucksWithIncreasedRealMass++;
        } else {
            realMass = declaredMass; // Real mass is the same as declared mass
        }
        String formatted = df.format(realMass);
        realMass = Double.parseDouble(formatted);
        return realMass;


    }
    public String overWeightReport(){
        problems.append(this.name+" Kamion je preopterecen i ne moze da predje carinski terminal"+"\n");
        return this.name+" Kamion je preopterecen i ne moze da predje carinski terminal";
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

    @Override
    public ArrayList<Passenger> getRemovedPassengersList() {
      return removedPassengersList;
    }

    @Override
    public ArrayList<Passenger> getPassengersList() {
        return passengersList;
    }
    public String getMass(){
        return "Declared mass "+declaredMass+" Real mass "+ realMass+"\n";
    }


}
