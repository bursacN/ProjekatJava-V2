package com.projekatjavav2.classes;
import java.util.Random;

public class PersonalCar extends Vehicle{

    private int capacity=5;
    Random r=new Random();


    public PersonalCar(){

        for(int i=0;i<r.nextInt(5);i++){
          //  passangerList.add(new Passenger(i,"Putnik br "+i));
        }

    }


}
