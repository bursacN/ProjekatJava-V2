package com.projekatjavav2.controllers;

import com.projekatjavav2.classes.Main;
import com.projekatjavav2.classes.vehicles.Vehicle;
import com.projekatjavav2.classes.WaitingQueue;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

import static com.projekatjavav2.classes.FileUtil.*;

public class HelloController implements Initializable {
    @FXML
    private GridPane gp;
    @FXML
    private Label timerLabel;
    private int seconds = 0;
    private long startTime;

    @FXML
    private Button buttonStart;
    @FXML
    private ToggleButton buttonPause;
    @FXML
    private Button buttonShowAll;
    @FXML
    private Button buttonFaultyVehicles;

    private StackPane[][] sp = new StackPane[9][5];
    private int count = 0;
    private Timeline timeline;
    ShowAllController shw;
    static VehicleDetailsController vdc;
    Stage secondStage;

    static Stage thirdStage;

    static VehiclesWithProblems vwp;

    static Stage fourthStage;


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
        timerLabel.setText("Time: 00:00");
        setupSecondScene();

        //setupFourthScene();

        buttonFaultyVehicles.setOnAction(event -> handleFaultyVehiclesButtonClick(event));


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
        if(v!=null) {
            Rectangle r1 = new Rectangle(50, 30);
            r1.setFill(v.getColor());
            Text text = new Text(String.valueOf(v.getVehicleName()));
            text.setFill(Color.WHITE);
            text.setFont(Font.font(14)); // Set the font size
            if (posY < 9) {
                mouseClicked(r1,text,v);
                sp[posY][2].getChildren().addAll(r1, text);
            }
        }

    }

    public synchronized void removeVehicle() {
        for (int i = 4; i < 9; i++) {
            List<Node> children = sp[i][2].getChildren();
            int lastIndex = children.size() - 2;
            if (lastIndex >= 0) {
                try {
                    children.remove(lastIndex);
                    children.remove(lastIndex);
                } catch (Exception ex) {
                    Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
                    System.out.println("greska sa indeksom " + i);
                }
            }
        }
    }

    public synchronized void moveVehiclesUpInQueue(WaitingQueue queue) {

        removeVehicle();
        shw.removeVehicle();
        setUpVehicles(queue.getVehicleList());


    }

    public  void setUpVehiclesInitial(WaitingQueue queue) {
        int i = 4;
        Queue<Vehicle> waitingVehicles = new LinkedList<>(queue.getVehicleList());
        //Queue<Vehicle> waitingVehicles = queue.getVehicleList();
        for(int j=0;j<5;j++){
            createVehicle(i,waitingVehicles.poll());
            i++;
        }
        Queue<Vehicle> waitingVehicles2 = new LinkedList<>(waitingVehicles);
        for(int x=0;x<5;x++) //TODO ilegal argument exception
            for(int y=0;y<9;y++){
                shw.createVehicle(x,y,waitingVehicles.poll());
            }

     // secondStage.show();

    }

    public synchronized void setUpVehicles(Queue<Vehicle> waitingVehicles) {
        int i = 4;
        Queue<Vehicle> tmp = new LinkedList<>(waitingVehicles);
        for (int j = 0; j < 5; j++) {
            if (tmp.peek() != null) {
                createVehicle(i, tmp.poll());
                i++;
            }
        }
        shw.setUpVehicles(tmp);

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
        mouseClicked(r1,text,v);
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
                Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
                System.out.println("greska sa indeksom ");
            }
        }
    }

    public String returnTerminalName(Vehicle v)  {
        try {
            Thread.sleep(100);
        }
        catch (Exception ex){
            Main.logger.log(Level.WARNING, ex.fillInStackTrace().toString());
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
    public void startTimer(long startTime){
        createTimeline(startTime);
        timeline.play();
    }
    public void stopTimer(){
        timeline.stop();
    }
    public Button getButtonStart() {
        return buttonStart;
    }
    public ToggleButton getButtonPause() {
        return buttonPause;
    }
    public Button getButtonShowAll() {
        return buttonShowAll;
    }
    public Timeline createTimeline(long startTime){

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - startTime;

                    long seconds = (elapsedTime / 1000) % 60;
                    long minutes = (elapsedTime / (1000 * 60)) % 60;

                    String formattedTime = String.format("%02d:%02d", minutes, seconds);

                    timerLabel.setText("Time: " + formattedTime);
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }
    private void setupSecondScene() {
        try {
            // Load the FXML file for the second scene
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/showAll.fxml"));
            Parent root = loader.load();
            shw=loader.getController();

            // Create a new stage for the second scene
            secondStage = new Stage();
            secondStage.setTitle("Second Scene");
            secondStage.setScene(new Scene(root));

            // Show the second stage
          //  secondStage.show();
        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }
    public void showAll(){
        secondStage.show();
    }
    public static void mouseClicked(Rectangle r1,Text text,Vehicle v){
        text.setOnMouseClicked(event -> {
            // Handle the click event here
            System.out.println("Text clicked: " + v.getVehicleName());
            setupThirdScene();
            vdc.setText(v);
            showVehicleDetails();

            // You can perform additional actions when the text is clicked
        });
        r1.setOnMouseClicked(event -> {
            // Handle the click event here
            System.out.println("Text clicked: " + v.getVehicleName());
            setupThirdScene();
            vdc.setText(v);
            showVehicleDetails();
            // You can perform additional actions when the text is clicked
        });
    }

    private static void setupThirdScene() {
        try {
            // Load the FXML file for the second scene
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/vehicleDetails.fxml"));
            Parent root = loader.load();
            vdc=loader.getController();

            // Create a new stage for the second scene
            thirdStage = new Stage();
            thirdStage.setTitle("Third Scene");
            thirdStage.setScene(new Scene(root));

            // Show the second stage
            //  secondStage.show();
        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }
    public static void showVehicleDetails(){
        thirdStage.show();
    }
    public Button getButtonFaultyVehicles() {
        return buttonFaultyVehicles;
    }
    private static void setupFourthScene() {
        try {
            // Load the FXML file for the second scene
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/vehiclesWithProblems.fxml"));
            Parent root = loader.load();
            vwp=loader.getController();

            // Create a new stage for the second scene
            fourthStage = new Stage();
            fourthStage.setTitle("Fourth Scene");
            fourthStage.setScene(new Scene(root));

            // Show the second stage
            //  secondStage.show();
        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }
    public static void showVehiclesWithProblems(){
        fourthStage.show();
    }
    private void handleFaultyVehiclesButtonClick(ActionEvent event) {

        setupFourthScene();
        ArrayList<Vehicle> vehicles =  deserializeVehicles(getBinarySerializationPath());
        List<String> lines=readTxtFile(getTextReportPath());
        Iterator<Vehicle> iterator= vehicles.iterator();
        while(iterator.hasNext()){
            Vehicle v=iterator.next();
       /*     for(String line:lines) {
                String[] parts = line.split(" ");
                if(v.getVehicleName().equals(parts[0])){
                    v.setProblemsString(line);
                }
            }*/
            if(v.getProblemsString().isEmpty()){
                iterator.remove();
            }
        }
        Queue<Vehicle> queue = new LinkedList<>(vehicles); //TODO SREDITI


        vwp.setUpVehicles(queue);
        //TODO nece prikazati vozila koja imaju problem samo na carinskom terminalu
        showVehiclesWithProblems();


    }





}