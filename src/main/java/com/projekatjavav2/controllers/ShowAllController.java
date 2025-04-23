package com.projekatjavav2.controllers;


import com.projekatjavav2.classes.Main;
import com.projekatjavav2.classes.vehicles.Vehicle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.util.logging.Level;

import static com.projekatjavav2.controllers.TerminalController.mouseClicked;


public class ShowAllController implements Initializable {
    @FXML
    private GridPane gp;
    private StackPane[][] sp = new StackPane[9][5];


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 5; col++) {
                StackPane stackPane = new StackPane();
                sp[row][col] = stackPane;
                gp.add(stackPane, col, row);
            }
        }
    }
    public void createVehicle(int posY, int posX,Vehicle v) {
        /*String name="";
        if(v instanceof PassengerTransport) name="A"+v.getID();
        else if(v instanceof CargoTransport) name="K"+v.getID();*/
        if(v!=null) {
            Rectangle r1 = new Rectangle(50, 30);
            r1.setFill(v.getColor());
            Text text = new Text(String.valueOf(v.getVehicleName()));
            text.setFill(Color.WHITE);
            text.setFont(Font.font(14)); // Set the font size
            if (posY < 9) {
                mouseClicked(r1,text,v);
                sp[posX][posY].getChildren().addAll(r1, text);
            }
        }
    }
    public synchronized void removeVehicle() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 5; col++) {
                List<Node> children = sp[row][col].getChildren();
                int lastIndex = children.size() - 2;
                if (lastIndex >= 0) {
                    try {
                        children.remove(lastIndex);
                        children.remove(lastIndex);
                    } catch (Exception ex) {
                        Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
                        System.out.println("greska sa indeksom " + row);
                    }
                }
            }
        }
    }
    public synchronized void setUpVehicles(Queue<Vehicle> waitingVehicles) {
        int i = 4;
        Queue<Vehicle> tmp = new LinkedList<>(waitingVehicles);
        for(int x=0;x<5;x++)
            for(int y=0;y<9;y++){
                if(waitingVehicles.peek()!=null) {
                    createVehicle(x, y, waitingVehicles.poll());
                }
            }
    }

}
