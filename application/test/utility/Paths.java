package utility;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;

/*


Titta igon JSON convertering

 */
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
        return FileUtils.openInputStream(new File(getDir() + "/temp/" + fileName));
    }

    public static void convertJson2Cvs(JsonObject output) throws IOException{

    }

    public static void writeOuputFile(String content) throws IOException{
        Files.exists(java.nio.file.Paths.get(getDir() + "/output/File_"));
        FileUtils.writeStringToFile(new File(getDir() + numSuffix("/output/File_")), content);
    }

    public static String readFile(String filename) throws IOException {
        if (!Files.exists(getFile(filename).toPath())) {
            System.err.println("No such file");
            throw new FileNotFoundException();
        }
        return FileUtils.readFileToString(Paths.getFile(filename));
    }

    public static void saveStringParams(String[] stringParams) throws IOException{
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
    }

    public static void saveObj (Object object2save) throws IOException{
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(utility.Paths.getWorkingFileOutputStream());
        objectOutputStream.writeObject(object2save);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    public static void saveObj (Object save, String name) throws IOException{
        ObjectOutputStream objectOutputStream = null;
        if(!name.contains("/"))
            name = "/temp/".concat(name);
        objectOutputStream = new ObjectOutputStream(utility.Paths.getFileOutputStream(name));
        objectOutputStream.writeObject(save);
        objectOutputStream.flush();
        objectOutputStream.close();
    }
}
