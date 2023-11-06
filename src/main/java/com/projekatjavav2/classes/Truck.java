package com.projekatjavav2.classes;

import com.projekatjavav2.controllers.HelloController;
import com.projekatjavav2.interfaces.CargoTransport;

import java.util.ArrayList;

public class Truck extends Vehicle implements CargoTransport {

    @Override
    protected boolean proccessVehicleOnPoliceTerminal() throws InterruptedException {
        return false;
    }

    @Override
    protected boolean proccessVehicleOnCustomsTerminal() {
        return false;
    }

    public Truck(HelloController controller, ArrayList<PoliceTerminal> terminals, WaitingQueue waitingQueue, CustomsTerminal customsTerminal) {
        super( controller, terminals,waitingQueue, customsTerminal);
    }
}
