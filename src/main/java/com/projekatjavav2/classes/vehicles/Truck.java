package com.projekatjavav2.classes.vehicles;

import com.projekatjavav2.classes.terminals.CustomsTerminal;
import com.projekatjavav2.classes.terminals.PoliceTerminal;
import com.projekatjavav2.classes.WaitingQueue;
import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.CargoTransport;

import java.util.ArrayList;

public class Truck extends Vehicle implements CargoTransport {

    private String name;

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
    }
}
