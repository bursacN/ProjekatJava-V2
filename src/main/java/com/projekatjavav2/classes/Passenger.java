package com.projekatjavav2.classes;

import java.io.Serializable;

public class Passenger implements Serializable {

    private int ID;

    // private String name;
    private static int totalPassengers = 0;
    private static int passengersWithInvalidDocs = 0;
    private boolean isDriver=false;
    private boolean hasSuitcase=false;

    private boolean hasValidDocument =true;

    private boolean haveForbiddenItems=false;

    private static int drivers=0;

    public Passenger(int id,boolean isDriver) {

        this.ID = id;
        this.isDriver=isDriver;
        this.hasValidDocument =generateValidDocuments(isDriver);
    }
    public Passenger(int id,boolean isDriver,boolean hasSuitcase) {

        this.ID = id;
        this.isDriver=isDriver;
        this.hasSuitcase=hasSuitcase;
        this.hasValidDocument =generateValidDocuments(isDriver);
    }
    private static synchronized boolean generateValidDocuments(boolean isDriver) {
        totalPassengers++;
        double percentage = (double) passengersWithInvalidDocs / totalPassengers;

        if (percentage < 0.03) {
            passengersWithInvalidDocs++;
            if(isDriver) drivers++;
            return false;
        }
        return true;
    }

    public Passenger() {
    }
    public boolean getIsDriver(){
        return  isDriver;
    }
    public void setIsDriver(boolean tmp){
        this.isDriver=tmp;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }
    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }

    public boolean isHasSuitcase() {
        return hasSuitcase;
    }

    public void setHasSuitcase(boolean hasSuitcase) {
        this.hasSuitcase = hasSuitcase;
    }
    public boolean isHaveForbiddenItems() {
        return haveForbiddenItems;
    }

    public void setHaveForbiddenItems(boolean hasForbiddenItems) {
        this.haveForbiddenItems = hasForbiddenItems;
    }
    public static int getTotalPassengers() {
        return totalPassengers;
    }

    public static void setTotalPassengers(int totalPassengers) {
        Passenger.totalPassengers = totalPassengers;
    }

    public static int getPassengersWithInvalidDocs() {
        return passengersWithInvalidDocs;
    }
    public static int getDrivers() {
        return drivers;
    }

    public static void setPassengersWithInvalidDocs(int passengersWithInvalidDocs) {
        Passenger.passengersWithInvalidDocs = passengersWithInvalidDocs;
    }

    public boolean isHasValidDocument() {
        return hasValidDocument;
    }

    public void setHasValidDocument(boolean hasValidDocument) {
        this.hasValidDocument = hasValidDocument;
    }


   /* public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/
}

