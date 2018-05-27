package utility;

import cern.colt.Arrays;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class testConvertion_1 {
    public static void main (String[] args) {

        File inputTxt = Paths.getFile("FULLTEXT01_Converted");
        String iS;

        try {

            iS = Arrays.toString(Files.lines(Paths.getFile("FULLTEXT01_Converted").toPath(), StandardCharsets.ISO_8859_1).toArray());
            String svar = uno(iS);
            System.err.println(svar);

            svar = svar.replace(" ", "");
            svar = svar.replace("\t", "");
            svar = svar.replace("\n", "");

            System.out.println(svar);
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("svar", new JsonParser().parse(svar));
            Paths.convertJson2Cvs(jsonObject);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String uno(String input){
        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                "2017-03-16",
                "71de81c4-c61d-4130-94fb-8fd412930587",
                "ECuJEXK7ridS"
        );
        /*
            {
                    "username": "4737dd5e-2d4b-4e6d-8387-bafb7330df29",
                    "password": "Lc2CgDxQbqYK"
            }
            {
                 "url": "https://gateway.watsonplatform.net/natural-language-understanding/api",
                 "username": "71de81c4-c61d-4130-94fb-8fd412930587",
                 "password": "ECuJEXK7ridS",
            }
        */

        /*
            model : 10:0d4dc265-b7aa-4951-aa60-cfca40471a20
         */

        EntitiesOptions entities = new EntitiesOptions.Builder().model("10:0d4dc265-b7aa-4951-aa60-cfca40471a20").emotion(false).sentiment(true).limit(100).build();
        //ConceptsOptions co = new ConceptsOptions.Builder().limit(20).build();
        //CategoriesOptions cao = new CategoriesOptions();
        //List<String> ls = new ArrayList<>();
        //ls.add("item");
        //EmotionOptions eo = new EmotionOptions.Builder().build();
        Features features = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(input).features(features).build();
        AnalysisResults results = service.analyze(parameters).execute();
        return results.toString();
    }
}
