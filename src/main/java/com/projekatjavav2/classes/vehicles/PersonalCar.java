package com.projekatjavav2.classes.vehicles;
import com.projekatjavav2.classes.Passenger;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class PersonalCar extends Vehicle implements PassengerTransport {

    private int capacity=5;
    Random r=new Random();
    private String name;
    private Color color;

    private ArrayList<Passenger> passengersList=new ArrayList<>();
    private int maxPassengers=5;
    private int minPassengers=1;


    @Override
    protected boolean proccessVehicleOnPoliceTerminal() { //vraca false ako treba izbaciti vozilo
        try {
            for(Passenger p:passengersList){
                Thread.sleep(500);
                if(p.isHasValidDocument()==false){
                    if(p.getIsDriver()){
                        return false;
                    }
                    else{
                        //TODO binarno se serijilazuju kaznjeni
                        passengersList.remove(p);
                    }
                }
            }
           // Thread.sleep(2000);
            return true;
        }
        catch (Exception ex){
//TODO exception
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

    public PersonalCar(){

        for(int i=0;i<r.nextInt(5);i++){
          //  passangerList.add(new Passenger(i,"Putnik br "+i));
        }

    }
    public PersonalCar(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, ArrayList<CustomsTerminal> customsTerminals) {
        super( controller, terminals,waitingQueue, customsTerminals);
        name="A"+this.getID();
        color= Color.DARKRED;
        int numPassengers = r.nextInt(maxPassengers - minPassengers + 1) + minPassengers;
       // System.out.println(numPassengers);
        for (int i = 1; i < numPassengers; i++) {
            if(i==1){
                passengersList.add(new Passenger(i,true));
            }
            else passengersList.add(new Passenger(i,false));
        }
    }
    public void printPassengers(){
        for(Passenger p:passengersList){
            System.out.println(p.getIsDriver()+"  id "+p.getID());
        }
    }


}
