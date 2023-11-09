package com.projekatjavav2.controllers;

import com.projekatjavav2.classes.vehicles.Vehicle;
import com.projekatjavav2.classes.WaitingQueue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    @FXML
    private GridPane gp;

    private StackPane[][] sp = new StackPane[9][5];
    private int count = 0;

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

    }

    public void createTerminals() {
        sp[2][0].setBorder(Border.stroke(Color.BLACK));
        sp[2][0].setBackground(Background.fill(Color.BLUEVIOLET));
        sp[2][2].setBorder(Border.stroke(Color.BLACK));
        sp[2][2].setBackground(Background.fill(Color.BLUEVIOLET));
        sp[2][4].setBorder(Border.stroke(Color.BLACK));
        sp[2][4].setBackground(Background.fill(Color.BLUEVIOLET));

        sp[0][0].setBorder(Border.stroke(Color.BLACK));
        sp[0][0].setBackground(Background.fill(Color.ROYALBLUE));
        sp[0][4].setBorder(Border.stroke(Color.BLACK));
        sp[0][4].setBackground(Background.fill(Color.ROYALBLUE));
    }

    public void createVehicle(int posY, Vehicle v) {
        /*String name="";
        if(v instanceof PassengerTransport) name="A"+v.getID();
        else if(v instanceof CargoTransport) name="K"+v.getID();*/

        Rectangle r1 = new Rectangle(50, 30);
        r1.setFill(v.getColor());
        Text text = new Text(String.valueOf(v.getVehicleName()));
        text.setFill(Color.WHITE);
        text.setFont(Font.font(14)); // Set the font size
        if (posY < 9) {
            sp[posY][2].getChildren().addAll(r1, text);
        }

    }

    public synchronized void removeVehicle() {
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

    public synchronized void moveVehiclesUpInQueue(WaitingQueue queue) {

        removeVehicle();
        setUpVehicles(queue.getVehicleList());
    }

    public void setUpVehiclesInitial(WaitingQueue queue) {
        int i = 4;
        Queue<Vehicle> waitingVehicles = queue.getVehicleList();
        for (Vehicle vehicle : waitingVehicles) {
            createVehicle(i, vehicle);
            i++;
        }
    }

    public synchronized void setUpVehicles(Queue<Vehicle> waitingVehicles) {
        int i = 4;
        Queue<Vehicle> tmp = new LinkedList<>(waitingVehicles);
        for (Vehicle vehicle : tmp) {
            createVehicle(i, vehicle);
            i++;
        }
    }

    public void moveIntoTerminal(String s, Vehicle v) {
        /*String name="";
        if(v instanceof PassengerTransport) name="A"+v.getID();
        else if(v instanceof CargoTransport) name="K"+v.getID();*/

        int num = 0;
        if ("t2".equals(s)) num = 2;
        else if ("t3".equals(s)) num=4;
        Rectangle r1 = new Rectangle(50, 30);
        r1.setFill(v.getColor());
        Text text = new Text(String.valueOf(v.getVehicleName()));
        text.setFill(Color.WHITE);
        text.setFont(Font.font(14)); // Set the font size
        //policijski
        sp[2][num].getChildren().addAll(r1, text);
        //carinski
        if ("k1".equals(s)) sp[0][0].getChildren().addAll(r1, text);
        if("k2".equals(s)) sp[0][4].getChildren().addAll(r1,text);
    }
    public void removeFromTerminal(String s) {
        //TODO treba primiti terminal i sam pozvati metodu da nadje ime a ne primati string
        int num = 0;
        List<Node> children = sp[2][num].getChildren();
        if ("t1".equals(s) || "t2".equals(s) || "t3".equals(s)) {
            if ("t2".equals(s)) num = 2;
            else if ("t3".equals(s)) num = 4;
            children = sp[2][num].getChildren();
        } else if ("k1".equals(s) || "k2".equals(s)) {
            if ("k2".equals(s)) num = 4;
            children = sp[0][num].getChildren();
        }
        int lastIndex = children.size() - 2;
        if (lastIndex >= 0) {
            try {
                children.remove(lastIndex);
                children.remove(lastIndex);
            } catch (Exception ex) {
                System.out.println("greska sa indeksom ");
            }
        }
    }

    public String returnTerminalName(Vehicle v)  {
        try {
            Thread.sleep(100);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        //sp[2][4].getChildren();
       // String vehicleID = Integer.toString(v.getID());
        for (int i = 0; i < 5; i++) {
            List<Node> children = sp[2][i].getChildren();
            if (!children.isEmpty()) {
                Node firstChild = children.get(1);
                if (firstChild instanceof Text) {
                    String text = ((Text) firstChild).getText();
                    if (text.equals(v.getVehicleName())) {
                        if (i == 0) return "t1";
                        else if (i == 2) return "t2";
                        else if (i==4) return "t3";
                     //   else i++;
                    }
                }
            }
        }
        return "Not Found";
    }




}