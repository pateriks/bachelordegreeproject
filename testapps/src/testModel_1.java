import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import java.util.ArrayList;
import java.util.List;

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

        /*
            model : 10:0d4dc265-b7aa-4951-aa60-cfca40471a20
         */

        EntitiesOptions entities = new EntitiesOptions.Builder().model("10:0d4dc265-b7aa-4951-aa60-cfca40471a20").emotion(false).sentiment(true).limit(100).build();
        ConceptsOptions co = new ConceptsOptions.Builder().limit(100).build();
        CategoriesOptions cao = new CategoriesOptions();
        List<String> ls = new ArrayList<>();
        ls.add("coffee");
        EmotionOptions eo = new EmotionOptions.Builder().document(false).targets(ls).build();
        Features features = new Features.Builder().entities(entities).concepts(co).categories(cao).emotion(eo).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().url("https://people.kth.se/~pateriks/718.txt").features(features).build();

        AnalysisResults results = service.analyze(parameters).execute();
        System.out.println(results);
    }
}
