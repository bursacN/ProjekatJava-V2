package com.projekatjavav2.classes;

import com.projekatjavav2.classes.vehicles.PersonalCar;
import com.projekatjavav2.classes.vehicles.Vehicle;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtil {


    private static String binarySerializationPath = getUniqueFilename(".dat");

    private static String textReportPath = getUniqueFilename(".txt");
    public static ObjectOutputStream objectOutputStream;

    static PrintWriter textWriter;

    static {
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(binarySerializationPath));
            textWriter =new PrintWriter(new BufferedWriter(new FileWriter(textReportPath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void serializeObject(Object obj) {
        try {

            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            //  objectOutputStream.close();
            System.out.println("Object serialized and saved to: " + binarySerializationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static public void writeReport(String string){
        textWriter.println(string);
        System.out.println("Text saved to: " + binarySerializationPath);
        textWriter.flush();
    }

    public static Object deserializeObject(String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            Object obj = objectInputStream.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getUniqueFilename(String extension) {
        Date currentDate = new Date();
        // Define a date format for the filename

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        // Format the current date to create a unique filename
        String uniqueFilename = dateFormat.format(currentDate) + extension;

        return System.getProperty("user.dir") + File.separator + uniqueFilename;
    }

    public static List<Vehicle> deserializeVehiclesWithRemovedPassengers(String path) {
        List<Vehicle> vehicles = new ArrayList<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path))) {
            Vehicle v;
            while (true) {
                try {
                    v = (Vehicle) inputStream.readObject();
                 //   System.out.println(v.getVehicleName()+ " vozilo je evidentirano na policijskog terminalu zbog putnika ");
                    vehicles.add(v);
                } catch (EOFException ex) {
                    break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehicles;
    }
}