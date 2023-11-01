package com.projekatjavav2.controllers;

import com.projekatjavav2.classes.Vehicle;
import com.projekatjavav2.classes.WaitingQueue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private GridPane gp;

    private StackPane[][] sp=new StackPane[9][5];
    private int count=0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 5; col++) {
                StackPane stackPane = new StackPane();
                sp[row][col] = stackPane;
                gp.add(stackPane, col, row);
            }
        }
        createTerminals();
       /* createVehicle(4,1);
        createVehicle(5,2);
        createVehicle(6,3);
        createVehicle(7,4);
        createVehicle(8,5);*/
      //  removeVehicle(5);

    }
    public void createTerminals()
    {
        sp[2][0].setBorder(Border.stroke(Color.BLACK));
        sp[2][0].setBackground(Background.fill(Color.BLUEVIOLET));
        sp[2][2].setBorder(Border.stroke(Color.BLACK));
        sp[2][2].setBackground(Background.fill(Color.BLUEVIOLET));

        sp[0][0].setBorder(Border.stroke(Color.BLACK));
        sp[0][0].setBackground(Background.fill(Color.ROYALBLUE));
    }
    public void createVehicle(int posY, int id) {

        Rectangle r1 = new Rectangle(50, 30);
        r1.setFill(Color.BLUE);
        Text text = new Text(String.valueOf(id));
        text.setFill(Color.WHITE);
        text.setFont(Font.font(14)); // Set the font size
        if(posY<9) {
            sp[posY][2].getChildren().addAll(r1, text);
        }

    }
    public  void removeVehicle(int posY){
        if(posY<9) {
            try {
                sp[posY][2].getChildren().remove(sp[posY][2].getChildren().size() - 2);
            }
            catch (Exception ex){
                System.out.println("greska sa indeksom "+ posY );
            }
        }
    }
    public synchronized void removeVehicleTest() {
        for (int i = 4; i < 9; i++) {
            List<Node> children = sp[i][2].getChildren();
            int lastIndex = children.size() - 2;
            if (lastIndex >= 0) {
                try {
                    children.remove(lastIndex);
                } catch (Exception ex) {
                    System.out.println("greska sa indeksom " + i);
                }
            }
        }
    }

    public void moveVehiclesUpInQueue( Queue<Vehicle> waitingVehicles){

        for(int i=4;i<= waitingVehicles.size()+4;i++){
            removeVehicle(i);
        }
       setUpVehicles(waitingVehicles);

    }
    public synchronized void moveVehiclesUpInQueueTest( WaitingQueue queue){

 /*       for(int i=4;i<= queue.getSize()+4;i++){
            removeVehicle(i);
        }*/
        removeVehicleTest();
        setUpVehicles(queue.getVehicleList());
    }
    public void setUpVehiclesTest(WaitingQueue queue){
        int i = 4;
        Queue<Vehicle> waitingVehicles= queue.getVehicleList();
        for (Vehicle vehicle : waitingVehicles) {
            createVehicle(i, vehicle.getID());
            i++;
        }
    }
    public synchronized void setUpVehicles(Queue<Vehicle> waitingVehicles){
        int i = 4;
        Queue<Vehicle> tmp=new LinkedList<>(waitingVehicles);
        for (Vehicle vehicle : tmp) {
            createVehicle(i, vehicle.getID());
            i++;
        }
    }
    public void moveIntoTerminal(String s, Vehicle v){

        int num=0;
        if("t2".equals(s)) num=2;
        Rectangle r1 = new Rectangle(50, 30);
        r1.setFill(Color.BLUE);
        Text text = new Text(String.valueOf(v.getID()));
        text.setFill(Color.WHITE);
        text.setFont(Font.font(14)); // Set the font size

        sp[2][num].getChildren().addAll(r1,text);

    }


}