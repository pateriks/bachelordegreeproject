package utility;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Paths {

    public static String getDir(){
        return System.getProperty("user.dir");
    }
    public static File getFile(String fileName) {
        String s = System.getProperty("user.dir");
        System.err.println(s);
        File ret = new File(s.concat("/pdf/" + fileName + ".txt"));
        return ret;
    }

    public static void convertJson2Cvs(JSONObject output) {
        //String jsonString = "{\"infile\": [{\"field1\": 11,\"field2\": 12,\"field3\": 13},{\"field1\": 21,\"field2\": 22,\"field3\": 23},{\"field1\": 31,\"field2\": 32,\"field3\": 33}]}";
        //output = new JSONObject(jsonString);
        try {
            System.err.println(output.names());
            JSONArray docs = output.getJSONArray("entities");
            int i = 0;
            while(Files.exists(java.nio.file.Paths.get(getDir() + "/cvs/JSONObj" + i + ".csv"))){i++;}

            File file = new File(getDir() + "/cvs/JSONObj" + i + ".csv");
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
}
