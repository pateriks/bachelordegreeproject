import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;

public class testModel_1 {
    public static void main (String[] args) {
        System.err.println("Hej patrik");
        uno();
    }

    public static void uno(){
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

        EntitiesOptions entities = new EntitiesOptions.Builder().sentiment(true).limit(200).build();
        Features features = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().url("https://people.kth.se/~pateriks/718.txt").features(features).build();

        AnalysisResults results = service.analyze(parameters).execute();
        System.out.println(results);
    }
}
