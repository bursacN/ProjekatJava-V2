package com.projekatjavav2.classes;

import com.projekatjavav2.classes.terminals.Terminal;
import com.projekatjavav2.classes.vehicles.Vehicle;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class FileUtil {


    private static String binarySerializationPath = getUniqueFilename(".dat");

    private static String textReportPath = getUniqueFilename(".txt");
    public static ObjectOutputStream objectOutputStream;

    static PrintWriter textWriter;

    //static REad

    static {
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(binarySerializationPath));
            textWriter =new PrintWriter(new BufferedWriter(new FileWriter(textReportPath)));
        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
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
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }

    }
    static public void writeReport(String string){
        textWriter.println(string);
        System.out.println("Text saved to: " + textReportPath);
        textWriter.flush();
    }

    public static Object deserializeObject(String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            Object obj = objectInputStream.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
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

    public static ArrayList<Vehicle> deserializeVehicles(String path) {
        ArrayList<Vehicle> vehicles = new ArrayList<>();

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
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }
        return vehicles;
    }
    public static List<String> readTxtFile(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
            e.printStackTrace();
        }

        return lines;
    }
    public static void setTerminalsData(ArrayList<Terminal> terminals,List<String> lines){
        for(String line:lines){
            String[] parts=line.split(" ",2);
            for(Terminal t:terminals){
                if(parts[0].equals(t.getName())){
                    t.setTurnedOn(Boolean.parseBoolean(parts[1]));
                }
            }
        }
    }
    public static String watchDirectory(String directoryPath, String fileName,ArrayList<Terminal> terminalss) throws IOException {
        Path directory = Paths.get(directoryPath);
        WatchService watchService = directory.getFileSystem().newWatchService();
        directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        int i=0;

        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Main.logger.log(Level.WARNING, e.fillInStackTrace().toString());
                return directoryPath;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                Path context = (Path) event.context();
                if (context.toString().equals(fileName) && event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    i++;
                    if(i%2==0) {
                        setTerminalsData(terminalss, readTxtFile(directoryPath + File.separator + fileName));
                        System.out.println("********************Fajl promijenjen********************");
                        for (Terminal t : terminalss) {
                            System.out.println(t.getName() + " je ukljucen " + t.isTurnedOn());
                        }
                    }

                }

            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
        return directoryPath;
    }
    public static String getBinarySerializationPath(){
        return binarySerializationPath ;
    }
    public static String getTextReportPath(){
        return textReportPath ;
    }


}