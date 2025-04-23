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
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static com.projekatjavav2.classes.FileUtil.*;
import static com.projekatjavav2.classes.vehicles.Vehicle.togglePause;

public class TerminalController implements Initializable {
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
    Stage showAllVehiclesStage;
    static Stage showVehicleDetailsStage;
    static VehiclesWithProblemsController vwp;

    static Stage showVehiclesWithProblemsStage;


   // long elapsedTime=0;
   AtomicLong elapsedTime = new AtomicLong(0);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 5; col++) {
                StackPane stackPane = new StackPane();
                sp[row][col] = stackPane;
                gp.add(stackPane, col, row);
            }
        }
        //createTimeline(0);
        buttonPause.setDisable(true);
        createTerminals();
        timerLabel.setText("Time: 00:00");
        createTimeline(startTime);

        setupShowAllVehiclesScene();

        buttonFaultyVehicles.setOnAction(event -> handleFaultyVehiclesButtonClick(event));
        buttonPause.setOnAction(event -> handlePausedButtonClick(event) );
        buttonShowAll.setOnAction(event -> { showAll(); });

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
       // Queue<Vehicle> waitingVehicles2 = new LinkedList<>(waitingVehicles);
        for(int x=0;x<5;x++)
            for(int y=0;y<9;y++){
                shw.createVehicle(x,y,waitingVehicles.poll());
            }

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
    public void startTimer(){
        timeline.play();
    }
    public void stopTimer(){
        timeline.stop();
    }
    public void pauseTimer(){
        timeline.pause();
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
                    long seconds = (elapsedTime.getAndIncrement() % 60);
                    long minutes = (elapsedTime.get() / 60) % 60;

                    String formattedTime = String.format("%02d:%02d", minutes, seconds);

                    timerLabel.setText("Time: " + formattedTime);
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    private void setupShowAllVehiclesScene() {
        try {

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/showAll.fxml"));
            Parent root = loader.load();
            shw=loader.getController();

            showAllVehiclesStage = new Stage();
            showAllVehiclesStage.setTitle("Prikaz svih vozila");
            showAllVehiclesStage.setScene(new Scene(root));

        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }
    public void showAll(){
        showAllVehiclesStage.show();
    }
    public static void mouseClicked(Rectangle r1,Text text,Vehicle v){
        text.setOnMouseClicked(event -> {

            //System.out.println("Text clicked: " + v.getVehicleName());
            setupShowVehicleDetailsScene();
            vdc.setText(v);
            showVehicleDetails();

        });
        r1.setOnMouseClicked(event -> {

         // System.out.println("Text clicked: " + v.getVehicleName());
            setupShowVehicleDetailsScene();
            vdc.setText(v);
            showVehicleDetails();

        });
    }

    private static void setupShowVehicleDetailsScene() {
        try {

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/vehicleDetails.fxml"));
            Parent root = loader.load();
            vdc=loader.getController();

            showVehicleDetailsStage = new Stage();
            showVehicleDetailsStage.setTitle("Prikaz detalja o vozilima");
            showVehicleDetailsStage.setScene(new Scene(root));

        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }
    public static void showVehicleDetails(){
        showVehicleDetailsStage.show();
    }
    public Button getButtonFaultyVehicles() {
        return buttonFaultyVehicles;
    }
    private static void setupShowVehiclesWithProblemsScene() {
        try {

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/projekatjavav2/vehiclesWithProblems.fxml"));
            Parent root = loader.load();
            vwp=loader.getController();

            showVehiclesWithProblemsStage = new Stage();
            showVehiclesWithProblemsStage.setTitle("Vozila koja su imala probleme");
            showVehiclesWithProblemsStage.setScene(new Scene(root));

        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
    }
    public static void showVehiclesWithProblems(){
        showVehiclesWithProblemsStage.show();
    }
    private void handleFaultyVehiclesButtonClick(ActionEvent event) {

        setupShowVehiclesWithProblemsScene();
        ArrayList<Vehicle> vehicles =  deserializeVehicles(getBinarySerializationPath());
        List<String> lines=readTxtFile(getTextReportPath());
        Iterator<Vehicle> iterator= vehicles.iterator();
        while(iterator.hasNext()){
            Vehicle v=iterator.next();
            if(v.getProblemsString().isEmpty()){
                iterator.remove();
            }
        }
        Queue<Vehicle> queue = new LinkedList<>(vehicles);
        vwp.setUpVehicles(queue);
        showVehiclesWithProblems();

    }
    private void handlePausedButtonClick(ActionEvent event) {
        if ( buttonPause.isSelected()) {
            buttonPause.setText("RESUME");
            pauseTimer();
        } else {
            buttonPause.setText("PAUSE");
            startTimer();
        }
        togglePause();
    }





}