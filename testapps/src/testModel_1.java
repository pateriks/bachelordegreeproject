import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import java.io.*;
import java.util.*;

/*
Test av nlu modell version 1.0

Användning:

Kör testModel_1 i en JVM
välj om input sker från fil (1) eller skrivs in i commandotolken (2)
(1) input filer ska finnas i en mapp "root/temp" och vara på formatet (String username, String password, String modelId)
om särskild modelId inte används lämnas fältet som en tom String.
(2) skriv in respektive fält som blir frågat efter i commandotolken.

Skriv in en söksträng som ska användas på NLU-tjänsten. Använd några av test texterna.
Exempel: "Extract entities and categories from the text about coffee" (antal söksträngar håller på att byggas ut)

Svar från NLU tjänsten printas nu ut i kommando tolken om allting lyckats.

*/
/* Kommentar av utvecklare

Interpreter
{
  "url": "https://gateway.watsonplatform.net/natural-language-understanding/api",
  "username": "75099d61-45f6-43f6-9f77-27977c5db72e",
  "password": "0Q0vQSTjBR0M"
  "modelId" : "10:7a7fc498-8251-492d-bc5d-4140df1b2cd5"
}
 Temp_0 : NLU standardmodell
 Temp_1 : NLU personmodell
 Temp_2 : NLU forskningsfrågamodell

Använd för test av N/A:

                    "username": "20cf182b-9835-4342-b8d0-5ad14f6818c7",
                    "password": "522jNIAhAjhK"
                    "modelId": "10:0d4dc265-b7aa-4951-aa60-cfca40471a20"

Använd för test av egen model (research question)

                    "username": "4737dd5e-2d4b-4e6d-8387-bafb7330df29",
                    "password": "Lc2CgDxQbqYK"
                    "modelId": "10:0fe98de0-4202-4507-a8b2-5ea33f8a524e"

Använd för test av personer
                     "username": "71de81c4-c61d-4130-94fb-8fd412930587",
                     "password": "ECuJEXK7ridS"
                     "modelId": "10:2722fc5d-c1df-48a7-8a87-082c111767a7"
*/

public class testModel_1 {

    private static String API_URL = "https://gateway.watsonplatform.net/natural-language-understanding/api";
    private static String VERSION = "2018-05-03";
    private static String USERNAME = "{username}";
    private static String PASSWORD =  "{password}";
    private static String MODELID = "";

    public static void main (String[] args) {

        Scanner in = new Scanner(System.in);

        /* Define debug */
        boolean debug = false;
        try{
            debug = args[0].equalsIgnoreCase("debug");
        }catch (ArrayIndexOutOfBoundsException e){
            debug = false;
        }
        if(!debug) {
            System.err.close();
        }
        /* slut define debug */

        /* User input */
        System.out.println("Read setup from file (y/n)?");
        boolean flow = in.nextLine().toLowerCase().startsWith("y");
        if(flow){
            System.out.println("File?");
            String testData = in.nextLine();
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = utility.Paths.getWorkingFileInputStream(testData);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                USERNAME = (String) objectInputStream.readObject();
                PASSWORD = (String) objectInputStream.readObject();
                MODELID = (String) objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Version: ");
            VERSION=in.nextLine();
            System.out.println("Username: ");
            USERNAME=in.nextLine();
            System.out.println("Password: ");
            PASSWORD=in.nextLine();
            System.out.println("Model Id (if not used leave blank): ");
            MODELID=in.nextLine();
        }
        System.out.println("Enter search: ");
        String query = in.nextLine();
        /* slut user input */

        /* Skapande av en service instans (använder internet) */
        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
        VERSION,
        USERNAME,
        PASSWORD
        );
        /* slut av service instansiering */

        /* Hämta query */
        NaturalLanguageUnderstanding service2 = new NaturalLanguageUnderstanding(
                "2017-03-16",
                "75099d61-45f6-43f6-9f77-27977c5db72e",
                "0Q0vQSTjBR0M"
        );

        AnalysisResults as = analyse(service2, query);
        Iterator <EntitiesResult> it = as.getEntities().iterator();
        String doc = ""; String ext = ""; String tar = "";
        while(it.hasNext()){
            EntitiesResult er = it.next();
            System.out.println(er.getType() + " " + er.getText());
            if(er.getType().equalsIgnoreCase("document")){
                System.err.println("doc: " + er.getText());
                doc = er.getText();
            }else if(er.getType().equalsIgnoreCase("extraction_element")){
                System.err.println("ext: " + er.getText());
                ext = ext.concat(er.getText());
            }else if(er.getType().equalsIgnoreCase("emotion_entity")){
                System.err.println("tar: " + er.getText());
                tar = er.getText();
            }
        }
        System.out.println("Query[\n" + "Doc{" + doc + "}\nExtract{" + ext + "}\nTarget{" + tar + "}]");
        /* slut hämtning av query */

        /* Tester att utföra */
        testNLU(service, doc, ext, tar);
        /* slut på testning */
    }

    public static void testNLU(NaturalLanguageUnderstanding service, String test, String extract, String target) {
        /* Hämta input data */
        test = test.toLowerCase();
        extract = extract.toLowerCase();
        target = target.toLowerCase();
        String file;
        if (test.equalsIgnoreCase("coffee")) {
            file = "718.txt";
        } else if (test.equalsIgnoreCase("food")) {
            file = "745.txt";
        } else {
            file = "750.txt";
        }
        String input = utility.Paths.readFile(file);
        System.out.println("Analyserad text:");
        System.out.println("Start of output ****************************");
        System.out.println(input);
        System.out.println("End of output ******************************");
        /* Anropa API REST */
        Features.Builder features = new Features.Builder();
        ConceptsOptions co = null;
        CategoriesOptions cao = null;
        EmotionOptions eo = null;
        EntitiesOptions entities = null;
        if (MODELID.equalsIgnoreCase("")){
            entities = new EntitiesOptions.Builder().emotion(false).sentiment(false).limit(100).build();
        }else{
            entities = new EntitiesOptions.Builder().emotion(false).sentiment(false).limit(100).model(MODELID).build();
        }
        features.entities(entities);
        if(extract.contains("concepts")) {
            co = new ConceptsOptions.Builder().limit(100).build();
            features.concepts(co);
        }
        if(extract.contains("categories")) {
            cao = new CategoriesOptions();
            features.categories(cao);
        }
        List<String> ls = new ArrayList<>();
        if(target.equalsIgnoreCase("")) ls.add(target);
        else ls.add(test);
        if(extract.contains("emotions")) {
            if (target.equalsIgnoreCase("text")) {
                eo = new EmotionOptions.Builder().document(true).targets(ls).build();
                features.emotion(eo);
            } else {
                eo = new EmotionOptions.Builder().document(true).targets(ls).build();
                features.emotion(eo);
            }
        }
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(input).features(features.build()).build();
        AnalysisResults results = service.analyze(parameters).execute();
        /* Printning */
        System.out.println("Inställningar:");
        System.out.println(parameters.features().toString());
        System.out.println("Hämtad data från NLU tjänsten:");
        System.out.println("Start of output ****************************");
        System.out.println(results);
        System.out.println("End of output ******************************");
    }

    public static AnalysisResults analyse (NaturalLanguageUnderstanding service, String toAnalyse){
        EntitiesOptions entities = new EntitiesOptions.Builder().model("10:7a7fc498-8251-492d-bc5d-4140df1b2cd5").build();
        Features features = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(toAnalyse).features(features).build();
        AnalysisResults results = service.analyze(parameters).execute();
        return results;
    }

    public static void models (NaturalLanguageUnderstanding service){
        EntitiesOptions entities = new EntitiesOptions.Builder().build();
    }

    public static void setVERSION(String VERSION) {
        testModel_1.VERSION = VERSION;
    }

    class model{
        String version = "";
        String username = "";
        String password = "";

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }

        public String getVersion() {
            return version;
        }
    }

}