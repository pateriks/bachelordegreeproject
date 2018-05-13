package utility;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

public class Paths {

    public static String numSuffix(String name){
        int i = 0;
        name = name.concat("_");
        while(Files.exists(java.nio.file.Paths.get(getDir() + name + i))){i++;}
        return (name + i);
    }

    public static String getDir(){
        return System.getProperty("user.dir");
    }

    public static File getFile(String fileName) {
        String s = System.getProperty("user.dir");
        File ret = new File(s.concat("/input/" + fileName));
        return ret;
    }

    public static FileOutputStream getWorkingFileOutputStream() throws IOException {
        return FileUtils.openOutputStream(new File(getDir() + numSuffix("/temp/Temp")));
    }

    public static FileOutputStream getFileOutputStream(String name) throws IOException {
        return FileUtils.openOutputStream(new File(getDir() + numSuffix(name)));
    }

    public static FileInputStream getWorkingFileInputStream(String fileName) throws IOException{
        if (!Files.exists(java.nio.file.Paths.get(getDir() + "/temp/" + fileName))) {
            System.err.println("No such file");
            throw new FileNotFoundException();
        }
        try {
            return FileUtils.openInputStream(new File(getDir() + "/temp/" + fileName));
        } catch (IOException e) {
            System.err.println("IO Exception");
            System.exit(1);
        }
        return null;
    }

    public static void convertJson2Cvs(JSONObject output) {
        //String jsonString = "{\"infile\": [{\"field1\": 11,\"field2\": 12,\"field3\": 13},{\"field1\": 21,\"field2\": 22,\"field3\": 23},{\"field1\": 31,\"field2\": 32,\"field3\": 33}]}";
        try {
            System.err.println(output.names());
            JSONArray docs = output.getJSONArray("entities");
            File file = new File(getDir() + numSuffix("/cvs/JSONObj") + ".csv");
            System.err.println(file.getAbsolutePath());
            System.err.println(docs.toString());
            String csv = CDL.toString(docs);
            System.err.println(csv);
            FileUtils.writeStringToFile(file, csv);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void writeFile(String content){
        try {
            Files.exists(java.nio.file.Paths.get(getDir() + "/output/File_"));
            FileUtils.writeStringToFile(new File(getDir() + numSuffix("/output/File")), content);
        } catch (IOException e) {
            System.err.println("IO");
            System.exit(1);
        }
    }

    public static String readFile(String filename) {
        if (!Files.exists(getFile(filename).toPath())) {
            System.err.println("No such file");
            System.exit(1);
        }
        try {
            return FileUtils.readFileToString(Paths.getFile(filename));
        } catch (IOException e) {
            System.err.println("IO Exception");
            System.exit(1);
        }
        return null;
    }

    public static void saveStringParams(String[] stringParams){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(utility.Paths.getWorkingFileOutputStream());
            System.err.println("Saving [");
            for (String save : stringParams) {
                System.err.print(".");
                objectOutputStream.writeObject(save);
            }
            System.err.println("] Saved to");
            System.err.println(objectOutputStream.toString());
            objectOutputStream.flush();
            objectOutputStream.close();
            System.err.println("output stream closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveObj (Object save){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(utility.Paths.getWorkingFileOutputStream());
            objectOutputStream.writeObject(save);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            System.err.println("IO");
            System.exit(1);
        }
    }

    public static void saveObj (Object save, String name){
        try {
            ObjectOutputStream objectOutputStream = null;
            if(!name.contains("/"))
                name = "/temp/".concat(name);
            objectOutputStream = new ObjectOutputStream(utility.Paths.getFileOutputStream(name));
            objectOutputStream.writeObject(save);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            System.err.println("IO");
            System.exit(1);
        }
    }

}
