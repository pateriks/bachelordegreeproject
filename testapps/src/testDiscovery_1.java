import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.*;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/* Credentials dicovery do:

"url": "https://gateway.watsonplatform.net/discovery/api",
"username": "8b3acc9f-0e36-47d7-a06e-5a61b4c799a3",
"password": "5l7JEcWuowEY"

Temp_3: Dicovery-xo credentials
Temp_4: Dicovery environment, collection och configuration
Temp_5: list of uploaded documentIds (deleted 2018-05-12).
*/
public class testDiscovery_1 {

    private static String VERSION = "2018-05-03";
    private static String USERNAME = "{username}";
    private static String PASSWORD =  "{password}";

    static String API_URL = "https://gateway.watsonplatform.net/discovery/api";
    static String MODEL_ID;
    static HashMap<String, String> DOCUMENT_IDS;
    static String environmentId = "b6f8f4f0-c3f8-415b-85a5-2b81bcff3bd3";
    static String collectionId = "bf2d0643-fc52-4f28-a133-107ae43707cd";
    static String configurationId = "04fa8447-a257-459a-b544-fbd43e874e15";

    //static String documentId = "9973020c8f56e75c368214ec60cb0f97";
    public static final HashMap<String, String> save = new HashMap<>();

    public static void main (String[] args) {
        /* Skapa laddningsfil
        save.put("doc1", "9973020c8f56e75c368214ec60cb0f97");
        save.put("doc2", "cd5776681625a0b16dfca707ba5385fa");
        save.put("doc3", "296c0131-90d7-4863-89ac-e35f7c7f4aa0");
        utility.Paths.saveObj(save);
        System.exit(0);
        /* Slut skapa laddningsfil */
        String fileName = "";
        String documentId = "9973020c8f56e75c368214ec60cb0f97";
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

        /* Interpreter service instansiering */
        NaturalLanguageUnderstanding service2 = new NaturalLanguageUnderstanding(
                "2017-03-16",
                "75099d61-45f6-43f6-9f77-27977c5db72e",
                "0Q0vQSTjBR0M"
        );
        /* slut instansiering interpreter */

        /* User input */
        /* Tolkning av input */
        System.out.println("What test? Available tests (upload, delete, query Dicovery service)");
        String test = in.nextLine();
        while(true) {
            if (test.equalsIgnoreCase("break")) {
                break;
            }
            List<EntitiesResult> er = analyse(service2, test).getEntities();
            if (er.isEmpty()) {
                System.out.println("Try again or enter break");
                test = in.nextLine();
                continue;
            } else {
                Iterator<EntitiesResult> it = er.iterator();
                while (it.hasNext()) {
                    EntitiesResult entity = it.next();
                    if (entity.getType().equalsIgnoreCase("Discovery_test")) {
                        List <String> subTypes = null;
                        if((subTypes = entity.getDisambiguation().getSubtype()).isEmpty()){
                            System.out.println("Try again or enter break");
                            test = in.nextLine();
                            continue;
                        }
                        test = subTypes.get(0);
                        System.out.println("Test: " + test);
                    }else if (entity.getType().equalsIgnoreCase("Document")){
                        fileName = entity.getText();
                    }
                }
            }
            break;
        }
        System.out.println("Read setup from file (y/n)?");
        boolean flow = in.nextLine().toLowerCase().startsWith("y");
        if(flow){
            System.out.println("Setup file?");
            String setupData = in.nextLine();
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = utility.Paths.getWorkingFileInputStream(setupData);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                USERNAME = (String) objectInputStream.readObject();
                System.out.println(USERNAME);
                PASSWORD = (String) objectInputStream.readObject();
                System.out.println(PASSWORD);
                MODEL_ID = (String) objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(test.contains("delete")) {
                System.err.println("delete");
                System.out.println("Documents file?");
                String testData = in.nextLine();
                FileInputStream fileInputStream2 = null;
                try {
                    try {
                        fileInputStream2 = utility.Paths.getWorkingFileInputStream(testData);
                    } catch (IOException e) {

                    }
                    ObjectInputStream objectInputStream2 = new ObjectInputStream(fileInputStream2);
                    DOCUMENT_IDS = (HashMap<String, String>) objectInputStream2.readObject();
                    objectInputStream2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("Session? (if not used leave blank)");
                String name = in.nextLine();
                if(!name.equalsIgnoreCase("")) {
                    try {
                        DOCUMENT_IDS = (HashMap<String, String>) new ObjectInputStream(utility.Paths.getWorkingFileInputStream(name)).readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    DOCUMENT_IDS = new HashMap<>();
                }
            }
            if(test.contains("upload")){
                System.err.println("upload");
                //Get files to upload
            }
            if(test.contains("query")){
                System.err.println("query");
                //Get query
            }
        }else{
            System.out.println("Version: ");
            VERSION = in.nextLine();
            System.out.println("Username: ");
            USERNAME = in.nextLine();
            System.out.println("Password: ");
            PASSWORD = in.nextLine();
            System.out.println("Model Id (if not used leave blank): ");
            MODEL_ID = in.nextLine();
            System.out.println("Document Id (if not used leave blank): ");
            documentId = in.nextLine();
        }
        String query = "";
        if(test.contains("QUERY")) {
            System.out.println("Input query: ");
            query = in.nextLine();
            AnalysisResults as = analyse(service2, query);
            Iterator<EntitiesResult> it = as.getEntities().iterator();
            String doc = ""; String ext = ""; String tar = "";
            while (it.hasNext()) {
                EntitiesResult er = it.next();
                System.out.println(er.getType() + " " + er.getText());
                if (er.getType().equalsIgnoreCase("document")) {
                    System.err.println("doc: " + er.getText());
                    doc = er.getText();
                } else if (er.getType().equalsIgnoreCase("extraction_element")) {
                    System.err.println("ext: " + er.getText());
                    ext = ext.concat(er.getText());
                } else if (er.getType().equalsIgnoreCase("emotion_entity")) {
                    System.err.println("tar: " + er.getText());
                    tar = er.getText();
                }
            }
            System.out.println("Query[\n" + "Doc{" + doc + "}\nExtract{" + ext + "}\nTarget{" + tar + "}]");
        }
        /* slut user input */
        /* slut tolkning av query */

        /* Skapa en service instans */
        Discovery service = new Discovery(VERSION);
        service.setEndPoint(API_URL);
        service.setUsernameAndPassword(
                USERNAME,
                PASSWORD
        );
        /* slut av service instansiering */

        /* Radera dokument */
        if(test.contains("DELETE")) {
            {
                String output = "";
                if (DOCUMENT_IDS.isEmpty()) { //Radera ett dokument
                    output = deleteDoc(service, documentId)? "{Deleted}" : "{ErrorDeleting}";
                } else { //Radera alla dokument
                    String[] documentIds = new String[3];
                    DOCUMENT_IDS.values().toArray(documentIds);
                    for (String document2del : documentIds) {
                        output = output.concat(deleteDoc(service, document2del)? "{Deleted}" : "{ErrorDeleting}");
                    }
                }
                System.out.println("Start of output ****************************");
                System.out.println(output);
                System.out.println("End of output ******************************");
            }
        }
        /* Slut radera dokument */

        /* Ladda upp dokument */
        if(test.contains("UPLOAD")) {
            if(!fileName.equalsIgnoreCase(""))
                addDoc(service, fileName);
        }
        /* Slut ladda upp dokument */

        /* Sök bland dokument */
        if(test.contains("QUERY")) {
            QueryResponse queryResponse = query(service, query);
            List<QueryResult> list = queryResponse.getResults();
        }
        /* slut sök bland dokument */

        utility.Paths.saveObj(DOCUMENT_IDS, "session");
    }

    public static QueryResponse query(Discovery discovery, String query) {
        QueryOptions.Builder queryBuilder = new QueryOptions.Builder(environmentId, collectionId);
        queryBuilder.query(query);
        //queryBuilder.filter("id cd5776681625a0b16dfca707ba5385fa");
        LinkedList<String> e = new LinkedList<String>();
        //e.add("h1");
        //queryBuilder.returnFields(e);
        QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();
        System.out.println (queryResponse.getMatchingResults().toString());
        return queryResponse;
    }

    public static DocumentAccepted addDoc(Discovery discovery, String filename){
        AddDocumentOptions.Builder builder = new AddDocumentOptions.Builder(environmentId, collectionId);
        try {
            builder.file(utility.Paths.getFile(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DocumentAccepted documentAccepted = discovery.addDocument(builder.build()).execute();
        System.out.println(documentAccepted.toString());
        DOCUMENT_IDS.put(filename, documentAccepted.getDocumentId());
        return documentAccepted;
    }

    public static boolean deleteDoc (Discovery discovery, String documentId){
        DeleteDocumentOptions deleteDocumentOptions = new DeleteDocumentOptions.Builder(environmentId, collectionId, documentId).build();
        try {
            discovery.deleteDocument(deleteDocumentOptions).execute();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static AnalysisResults analyse (NaturalLanguageUnderstanding service, String toAnalyse){
        EntitiesOptions entities = new EntitiesOptions.Builder().model("10:79787535-286f-4de9-b547-b1153c2cf3c9").build();
        Features features = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(toAnalyse).features(features).build();
        AnalysisResults results = service.analyze(parameters).execute();
        return results;
    }

}
