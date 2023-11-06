package com.projekatjavav2.classes.vehicles;
import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;

import java.util.ArrayList;
import java.util.Random;

public class PersonalCar extends Vehicle implements PassengerTransport {

    private int capacity=5;
    Random r=new Random();
    private String name;


    @Override
    protected boolean proccessVehicleOnPoliceTerminal() {
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
    }


}
