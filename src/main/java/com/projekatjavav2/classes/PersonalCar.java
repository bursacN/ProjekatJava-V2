package com.projekatjavav2.classes;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.PassengerTransport;

import java.util.ArrayList;
import java.util.Random;

public class PersonalCar extends Vehicle implements PassengerTransport {

    private int capacity=5;
    Random r=new Random();


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
    public PersonalCar(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, CustomsTerminal customsTerminal) {
        super( controller, terminals,waitingQueue, customsTerminal);
    }


}
