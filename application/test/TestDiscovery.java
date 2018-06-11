import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.*;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/*
Test av Discovery version 1.0

Användning:

Kör TestDiscovery i en JVM
Skriv in en vad för test du vill göra.
Exempel:
(*) "Upload <FileName.ext> to Discovery"
(*) "Query document"

Välj om input sker från fil (1) eller skrivs in i commandotolken (2)
(1) input filer ska finnas i en mapp "root/temp" och vara på formatet (Java String username, Java String password, Java String modelId)
om särskild modelId inte används lämnas fältet som en tom String OBS det måste finnas tre Java Objekt.
(2) skriv in respektive fält som blir begärt i commandotolken. (bara ett dokument kan skrivas in)
om särskilt fält inte används lämas fältet som en tom String

För query test: skriv in en söksträng som ska användas på Discovery-tjänsten (för beriknings extraktion). Använd några av test texterna.
Exempel: "Extract all from <FileName.ext>"
OBS all är entities, keyword och semantic roles

Svar från NLU tjänsten printas nu ut i kommando tolken om allting lyckats.

Exempel på en exekvering:
    "Query document" + enter + "y" + enter + "Temp_3" + enter + "Temp_4" + enter + enter + "Extract all from VendorLockin.pdf" + enter

*/

/* Kommentar av utvecklare

Interpreter
    "url": "https://gateway.watsonplatform.net/natural-language-understanding/api",
    "username": "75099d61-45f6-43f6-9f77-27977c5db72e",
    "password": "0Q0vQSTjBR0M"
    "modelId" : "10:7a7fc498-8251-492d-bc5d-4140df1b2cd5"

Skapa laddningsfil (exempel):
    save.put("doc1", "9973020c8f56e75c368214ec60cb0f97");
    save.put("doc2", "cd5776681625a0b16dfca707ba5385fa");
    save.put("doc3", "296c0131-90d7-4863-89ac-e35f7c7f4aa0");
    utility.Paths.saveObj(save);

Sparade laddningsfiler:
    Temp_3: Dicovery-xo credentials
    Temp_4: API Collection; environment, collection och configuration (anpassad modell)
    Temp_5: List of uploaded documentIds (deleted 2018-05-12).
    Temp_6: Discovery environment, collection och configuration standard modell
    Temp_7: Discovery teststandardLIMITED credentials
    Temp_8: Discovery testPerson environment
    Temp_9: Discovery teststandardLIMITED API Collection ids
    
*/
public class TestDiscovery {

    private static String VERSION = "2018-05-03";
    private static String USERNAME = "{username}";
    private static String PASSWORD =  "{password}";

    static String API_URL = "https://gateway.watsonplatform.net/discovery/api";
    static String MODEL_ID;
    static HashMap<String, String> DOCUMENT_IDS;

    public static final HashMap<String, String> save = new HashMap<>();

    public static void main (String[] args) {

        String fileName = "";
        String documentId = "";
        String environmentId = "";
        String collectionId = "";
        String configurationId = "";
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
                "2018-05-03",
                "75099d61-45f6-43f6-9f77-27977c5db72e",
                "0Q0vQSTjBR0M"
        );
        /* slut instansiering interpreter */

        /* User input & tolkning av input */
        System.out.println("What test? Available tests (upload, delete, query Dicovery service)\n" +
                "for example: " + "\"Upload VendorLockin.pdf to Discovery\" or \"Query document\"");
        String test = in.nextLine();
        while(true) {
            if (test.equalsIgnoreCase("break")) {
                break;
            }
            List<EntitiesResult> er = analyseQuery(service2, test).getEntities();
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
                            System.out.println("Try again or type \"break\"");
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
            System.out.println("Credentials file?");
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
                System.out.println("Problem reading file" + setupData);
                System.exit(1);
            }
            System.out.println("Collection API file?");
            setupData = in.nextLine();
            try {
                fileInputStream = utility.Paths.getWorkingFileInputStream(setupData);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                environmentId = (String) objectInputStream.readObject();
                System.out.println(USERNAME);
                collectionId = (String) objectInputStream.readObject();
                System.out.println(PASSWORD);
                configurationId = (String) objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                System.out.println("Problem reading file" + setupData);
                System.exit(1);
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
                        System.out.println("Problem reading file" + testData);
                        System.exit(1);
                    }
                    ObjectInputStream objectInputStream2 = new ObjectInputStream(fileInputStream2);
                    DOCUMENT_IDS = (HashMap<String, String>) objectInputStream2.readObject();
                    objectInputStream2.close();
                } catch (Exception e) {
                    System.out.println("Error parsing input data");
                    System.exit(1);
                }
            }else{
                System.out.println("Session? (if not used leave blank)");
                String name = in.nextLine();
                DOCUMENT_IDS = new HashMap<>();
                while (!name.equalsIgnoreCase("")) {
                    try {
                        DOCUMENT_IDS.putAll((Map) new ObjectInputStream(utility.Paths.getWorkingFileInputStream(name)).readObject());
                    } catch (Exception e) {
                        System.out.println("Error parsing input data");
                    }
                    System.out.println("More files?");
                    name = in.nextLine();
                }
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
            System.out.println("Environment Id (if not used leave blank): ");
            environmentId = in.nextLine();
            System.out.println("Collection Id (if not used leave blank): ");
            collectionId = in.nextLine();
        }
        String doc = ""; String met = ""; String tar = "";
        String query = "";
        if(test.contains("QUERY")) {
            System.out.println("Input query: ");
            query = in.nextLine();
            AnalysisResults as = analyseQuery(service2, query);
            Iterator<EntitiesResult> it = as.getEntities().iterator();
            while (it.hasNext()) {
                EntitiesResult er = it.next();
                System.err.println(er.getType() + " " + er.getText());
                if (er.getType().equalsIgnoreCase("document")) {
                    System.err.println("doc: " + er.getText());
                    doc = er.getText();
                } else if (er.getType().equalsIgnoreCase("method_element")) {
                    System.err.println("met: " + er.getText());
                    met = met.concat(er.getText());
                } else if (er.getType().equalsIgnoreCase("emotion_entity")) {
                    System.err.println("tar: " + er.getText());
                    tar = er.getText();
                }
            }
            System.out.println("\"arguments\":[{\n" + "\"doc\":\"" + doc + "\"\n\"method\":\"" + met + "\"\n\"target\":\"" + tar + "\"\n}]");
        }
        /* slut user input och slut tolkning av query */

        /* Skapa en service instans */
        Discovery service = new Discovery(VERSION);
        service.setEndPoint(API_URL);
        service.setUsernameAndPassword(
                USERNAME,
                PASSWORD
        );
        /* slut av service instansiering */

        /* Radera dokument */
        if (test.contains("DELETE")) {
            testDelete(service, documentId, environmentId, collectionId);
        }
        /* slut radera dokument */

        /* Test ladda upp dokument */
        if(test.contains("UPLOAD")) {
            testUpload(service, fileName, environmentId, collectionId);
        }
        /* slut ladda upp dokument */

        /* Test sök bland dokument */
        if(test.contains("QUERY")) {
            testQuery(service, doc, met, environmentId, collectionId);
        }
        /* slut sök bland dokument */

        /* Ladda upp konfigurering
        addConfiguration(service, configuration, environmentId);
        /* slut konfigurering */

        /* Hämta konfigurering
        System.out.println(getConfiguration(service, environmentId, configurationId));
        /* slut hämta konfigurering */

        /* Test konfigurering
        System.out.println(testConfiguration(service, environmentId, configuration, "FULLTEXT50.pdf"));
        */
    }

    public static void testUpload(Discovery service, String fileName, String environmentId, String collectionId){
        boolean success = false;
        DocumentAccepted dA = null;
        if (!fileName.equalsIgnoreCase("")) {
            dA = addDoc(service, fileName, environmentId, collectionId);
            if(!dA.getStatus().equalsIgnoreCase("processing")){
                success = true;
            }
                /*try {
                    utility.Paths.saveObj(DOCUMENT_IDS, "session");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
        }
        if(success) {
            QueryResponse eR = query(service, fileName, "", null, environmentId, collectionId);
            System.out.println("Start of output ****************************");
            System.out.println(dA);
            try {
                System.out.println(getHTML(eR));
            } catch (ParseException e) {
                System.out.println("HTML parse exception");
            }
            System.out.println("End of output ******************************");
        }
    }

    public static void testQuery(Discovery service, String doc, String met, String environmentId, String collectionId){
        QueryResponse queryResponse = query(service, doc, met, null, environmentId, collectionId);
        System.out.println("Start of output ****************************");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            System.out.println(gson.toJson(textResultsParse(queryResponse.toString())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("End of output ******************************");
    }

    public static void testDelete(Discovery service, String documentId, String environmentId, String collectionId){
        {
            String output = "";
            boolean empty;
            try{
                empty = DOCUMENT_IDS.isEmpty();
            }catch (NullPointerException e){
                empty = true;
            }
            if (empty) { //Radera ett dokument
                output = deleteDoc(service, documentId, environmentId, collectionId)? "{Deleted}" : "{ErrorDeleting}";
            } else { //Radera alla dokument
                String[] documentIds = new String[3];
                DOCUMENT_IDS.values().toArray(documentIds);
                try {
                    for (String document2del : documentIds) {
                        output = output.concat(deleteDoc(service, document2del, environmentId, collectionId) ? "{Deleted}" : "{ErrorDeleting}");
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Illegal format");
                }
            }
            System.out.println("Start of output ****************************");
            System.out.println(output);
            System.out.println("End of output ******************************");
        }
    }

    public static QueryResponse query(Discovery discovery, String fileName, String method, String[] queries, String environmentId, String collectionId) {
        QueryOptions.Builder queryBuilder = new QueryOptions.Builder(environmentId, collectionId);

        //Example queries
        if(method.equalsIgnoreCase("")) {
            queries = new String[9];
            queries[0] = "enriched_text.entities.text::\"time\"";
            queries[1] = "enriched_text.entities.text,count:2";
            queries[2] = "enriched_text.categories.label::\"/technology and computing\"";
            queries[3] = "enriched_text.categories.label::\"/technology and computing/consumer electronics/telephones/mobile phones/smart phones\"";
            queries[4] = "enriched_text.entities.type::\"TECHNICAL\"";
            queries[5] = "enriched_text.entities.count>=1.0";
            queries[6] = "extracted_metadata.file_type::\"pdf\"";
            queries[7] = "enriched_html_elements.elements.attributes.type::\"Location\"";
            queries[8] = "enriched_text.entities.relevance>=0.25389";
        }
        //queryBuilder.query("enriched_text.entities.type::\"SCIENTIFIC\"");//.passages(true).highlight(true);
        if (method.toLowerCase().contains("")) {
            queryBuilder.query(queries[5]);
        }else{
            queryBuilder.naturalLanguageQuery("In future implementation");
        }
        queryBuilder.filter("extracted_metadata.filename::"+ fileName);
        QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();

        return queryResponse;
    }

    public static String getHtml(Discovery discovery,String fileName, String environmentId, String collectionId) throws UnsupportedEncodingException{
        QueryResponse eR = query(discovery, fileName, "", null, environmentId, collectionId);
        try {
            return getHTML(eR);
        } catch (ParseException e) {
            throw new UnsupportedEncodingException();
        }
    }

    public static DocumentAccepted addDoc(Discovery discovery, String fileName, String environmentId, String collectionId){
        AddDocumentOptions.Builder builder = new AddDocumentOptions.Builder(environmentId, collectionId);
        try {
            builder.file(utility.Paths.getFile(fileName)).filename(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Can not find file: " + fileName);
        }
        DocumentAccepted documentAccepted = discovery.addDocument(builder.build()).execute();
        System.out.println(documentAccepted.toString());
        DOCUMENT_IDS.put(fileName, documentAccepted.getDocumentId());
        DocumentStatus documentStatus = docStatus(discovery, fileName, environmentId, collectionId);
        int timeLimit = 100;
        int i = 0;
        System.out.print("[");
        while (documentStatus.getStatus().equalsIgnoreCase("processing") & i < timeLimit) {
            System.out.print(">");
            documentStatus = docStatus(discovery, fileName, environmentId, collectionId);
            System.out.print("< ");
            System.err.println(documentStatus);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Unexpected internal error");
            }
            i++;
        }
        System.out.println("]");
        System.out.println(documentStatus);
        if(i < 99){
            QueryResponse eR = query(discovery, fileName, "", null, environmentId, collectionId);
            System.out.println();
            try {
                System.out.println(getHTML(eR));
            } catch (ParseException e) {
                System.out.println("HTML parse exception");
            }
            System.out.println();
        }
        return documentAccepted;
    }

    public static DocumentStatus docStatus (Discovery discovery, String fileName, String environmentId, String collectionId){
        GetDocumentStatusOptions getDocumentStatusOptions = new GetDocumentStatusOptions.Builder().environmentId(environmentId).collectionId(collectionId).documentId(DOCUMENT_IDS.get(fileName)).build();
        return discovery.getDocumentStatus(getDocumentStatusOptions).execute();
    }

    public static boolean deleteDoc (Discovery discovery, String documentId, String environmentId, String collectionId){
        DeleteDocumentOptions deleteDocumentOptions = new DeleteDocumentOptions.Builder(environmentId, collectionId, documentId).build();
        try {
            discovery.deleteDocument(deleteDocumentOptions).execute();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static AnalysisResults analyseQuery(NaturalLanguageUnderstanding service, String toAnalyse){
        EntitiesOptions entities = new EntitiesOptions.Builder().model("10:314cdc8b-aea3-4dc0-b6c2-e4ec72e61f89").build();
        Features features = new Features.Builder().entities(entities).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(toAnalyse).features(features).build();
        AnalysisResults results = service.analyze(parameters).execute();
        return results;
    }

    public static String getHTML (QueryResponse toParse) throws ParseException{
        JSONParser parser = new JSONParser();
        //Tier 1
        JSONObject jsonObject = (JSONObject) parser.parse(toParse.toString());
        System.err.println(jsonObject);
        //Tier 2
        JSONObject jsonObject2 = (((JSONObject)((JSONArray) parser.parse(jsonObject.get("results").toString())).get(0)));
        System.err.println(jsonObject2);
        return jsonObject2.get("html").toString();
    }

    public static JSONObject textResultsParse(String json) throws ParseException{
        JSONParser parser = new JSONParser();
        //PrintWriter writer = null;
        //HTMLDocument htmlDocument;
        //OutputStream stream;
        //new File(utility.Paths.getDir() + "/output/html.html");
        //writer = new PrintWriter(fileName, "UTF-8");
        //JEditorPane p = new JEditorPane();
        //p.setContentType("text/html");
        //Tier 1
        JSONObject jsonObject = (JSONObject) parser.parse(json);
        //Tier 2
        JSONObject jsonObject2 = (((JSONObject)((JSONArray) parser.parse(jsonObject.get("results").toString())).get(0)));
        //Tier 3.0
        JSONObject jsonObject30 = (JSONObject) parser.parse(jsonObject2.get("enriched_text").toString());
        return jsonObject30;
        //entities, relations, extracted_metadata, result_metadata, html
        //Tier 3.1
        //JSONObject jsonObject31 = (JSONObject) parser.parse(jsonObject2.get("extracted_metadata").toString());
        //Tier 3.2
        //JSONObject jsonObject32 = (JSONObject) parser.parse(jsonObject2.get("result_metadata").toString());
        //Tier 3.3
        //String html = jsonObject2.get("html").toString();
        //try {
        //    FileUtils.write(new File(utility.Paths.getDir() + "/output/resp.html"), html);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        //p.setText(html);
        //htmlDocument = (HTMLDocument) p.getDocument();
        //HTMLWriter htmlWriter = new HTMLWriter(writer, htmlDocument);
        //try {
        //    htmlWriter.write();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //System.out.println(htmlDocument);
        //Tier 4
        //return ((JSONObject)((JSONArray) parser.parse(jsonObject30.get("entities").toString())).get(0)).toString();
        //Tier 5
        //String element = "score"; //score, label, count, disambiguation, text, type
        //String count = jsonObject4.get("count").toString();
        //String text = jsonObject4.get("text").toString();
        //String type = jsonObject4.get("type").toString();
        //JSONObject jsonObject52 = (JSONObject) parser.parse(jsonObject4.get("disambiguation").toString());
        //JSONObject jsonObject51 = (JSONObject) parser.parse(jsonObject4.get("sentiment").toString());
        //System.err.println(jsonObject51);
        //System.err.println(count);
        //System.err.println(jsonObject52);
        //System.err.println(text);
        //System.err.println(type);
        //writer.close();
    }

    public static Configuration addConfiguration(Discovery discovery, Configuration configuration, String environmentId){
        CreateConfigurationOptions.Builder createBuilder = new CreateConfigurationOptions.Builder(environmentId);
        createBuilder.configuration(configuration);
        configuration = discovery.createConfiguration(createBuilder.build()).execute();
        return configuration;
    }

    public static Configuration getConfiguration(Discovery discovery, String environmentId, String configurationId){
        GetConfigurationOptions getOptions = new GetConfigurationOptions.Builder(environmentId, configurationId).build();
        Configuration response = discovery.getConfiguration(getOptions).execute();
        return response;
    }

    public static TestDocument testConfiguration(Discovery discovery, String environmentId, Configuration configuration, String fileName){
        TestConfigurationInEnvironmentOptions getOptions = null;
        try {
            getOptions = new TestConfigurationInEnvironmentOptions.Builder()
                    .configuration(configuration.toString())
                    .environmentId(environmentId)
                    .file(utility.Paths.getFile(fileName))
                    .build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return discovery.testConfigurationInEnvironment(getOptions).execute();
    }

    public static Configuration newConfiguraion (){
        Configuration configuration = new Configuration();
        HtmlSettings htmlSettings = new HtmlSettings();
        PdfSettings pdfSettings = new PdfSettings();
        WordSettings wordSettings = new WordSettings();
        XPathPatterns excludeContents = new XPathPatterns();
        XPathPatterns keepContents = new XPathPatterns();
        List<String> excludeStrings = new LinkedList<>();
        excludeContents.setXpaths(excludeStrings);
        List<String> excludeTagsCompletly = new LinkedList<>();
        excludeTagsCompletly.add("script");
        excludeTagsCompletly.add("sup");
        List<String> excludeTagsKeepContent = new LinkedList<>();
        excludeTagsKeepContent.add("font");
        excludeTagsKeepContent.add("em");
        excludeTagsKeepContent.add("span");
        List<String> excludeTagAttributes = new LinkedList<>();
        excludeTagAttributes.add("EVENT_ACTIONS");
        htmlSettings.setExcludeTagsCompletely(excludeTagsCompletly);
        htmlSettings.setExcludeContent(excludeContents);
        htmlSettings.setExcludeTagsKeepContent(excludeTagsKeepContent);
        htmlSettings.setKeepContent(keepContents);
        htmlSettings.setExcludeTagAttributes(excludeTagAttributes);
        PdfHeadingDetection pdfHeadingDetection = new PdfHeadingDetection();
        List<FontSetting> fontSettings = new LinkedList<>();
        FontSetting fontSetting = new FontSetting();
        fontSetting.setLevel(1);
        fontSetting.setMinSize(24);
        fontSetting.setMaxSize(80);
        fontSettings.add(fontSetting);
        FontSetting fontSetting1 = new FontSetting();
        fontSetting1.setLevel(2);
        fontSetting1.setMinSize(18);
        fontSetting1.setMaxSize(24);
        fontSetting1.setItalic(false);
        fontSetting1.setBold(false);
        fontSettings.add(fontSetting1);
        FontSetting fontSetting2 = new FontSetting();
        fontSetting2.setLevel(2);
        fontSetting2.setMinSize(18);
        fontSetting2.setMaxSize(24);
        fontSetting2.setBold(true);
        fontSettings.add(fontSetting2);
        FontSetting fontSetting3 = new FontSetting();
        fontSetting3.setLevel(3);
        fontSetting3.setMinSize(13);
        fontSetting3.setMaxSize(18);
        fontSetting3.setBold(false);
        fontSetting3.setItalic(false);
        fontSettings.add(fontSetting3);
        FontSetting fontSetting4 = new FontSetting();
        fontSetting4.setLevel(3);
        fontSetting4.setMinSize(13);
        fontSetting4.setMaxSize(18);
        fontSetting4.setBold(true);
        fontSettings.add(fontSetting4);
        FontSetting fontSetting5 = new FontSetting();
        fontSetting5.setLevel(4);
        fontSetting5.setMinSize(11);
        fontSetting5.setMaxSize(13);
        fontSetting5.setBold(false);
        fontSetting5.setItalic(false);
        fontSettings.add(fontSetting5);
        pdfHeadingDetection.setFonts(fontSettings);
        pdfSettings.setHeading(pdfHeadingDetection);
        SegmentSettings segmentSettings = new SegmentSettings();
        WordHeadingDetection wordHeadingDetection = new WordHeadingDetection();
        List<FontSetting> fontSettings1 = new LinkedList<>();
        FontSetting fontSetting6 = new FontSetting();
        fontSetting6.setLevel(1);
        fontSetting6.setMinSize(24);
        fontSetting6.setBold(false);
        fontSetting6.setItalic(false);
        fontSettings1.add(fontSetting6);
        FontSetting fontSetting7 = new FontSetting();
        fontSetting7.setLevel(2);
        fontSetting7.setMinSize(18);
        fontSetting7.setMinSize(23);
        fontSetting7.setBold(true);
        fontSetting7.setItalic(false);
        fontSettings1.add(fontSetting7);
        FontSetting fontSetting8 = new FontSetting();
        fontSetting8.setLevel(3);
        fontSetting8.setMinSize(14);
        fontSetting8.setMinSize(17);
        fontSetting8.setBold(false);
        fontSetting8.setItalic(false);
        fontSettings1.add(fontSetting8);
        FontSetting fontSetting9 = new FontSetting();
        fontSetting9.setLevel(4);
        fontSetting9.setMinSize(13);
        fontSetting9.setMinSize(13);
        fontSetting9.setBold(true);
        fontSetting9.setItalic(false);
        fontSettings1.add(fontSetting9);
        wordHeadingDetection.setFonts(fontSettings1);
        List<WordStyle> wordStyles = new LinkedList<>();
        WordStyle wordStyle = new WordStyle();
        wordStyle.setLevel(1);
        List<String> strings = new LinkedList<>();
        strings.add("pullout heading");
        strings.add("pulloutheading");
        strings.add("header");
        wordStyle.setNames(strings);
        List<String> strings1 = new LinkedList<>();
        WordStyle wordStyle1 = new WordStyle();
        wordStyle1.setLevel(2);
        strings1.add("subtitle");
        wordStyle1.setNames(strings1);
        wordStyles.add(wordStyle);
        wordStyles.add(wordStyle1);
        wordHeadingDetection.setStyles(wordStyles);
        wordHeadingDetection.setStyles(wordStyles);
        wordSettings.setHeading(wordHeadingDetection);
        List<NormalizationOperation> normalizationOperations = new LinkedList<>();
        NormalizationOperation normalizationOperation = new NormalizationOperation();
        normalizationOperations.add(normalizationOperation);
        Conversions conversions = new Conversions();
        conversions.setWord(wordSettings);
        conversions.setSegment(segmentSettings);
        conversions.setHtml(htmlSettings);
        conversions.setPdf(pdfSettings);
        conversions.setJsonNormalizations(normalizationOperations);
        configuration.setConversions(conversions);
        configuration.setDescription("This is mostly used for testing entity extraction");
        List<Enrichment> enrichments = new LinkedList<>();
        Enrichment enrichment = new Enrichment();
        enrichment.setDescription("Entities enrichment");
        enrichment.setSourceField("text");
        enrichment.setDestinationField("enriched_text");
        NluEnrichmentFeatures nluEnrichmentFeatures = new NluEnrichmentFeatures.Builder().entities(new NluEnrichmentEntities.Builder().limit(10).build()).build();
        EnrichmentOptions enrichmentOptions = new EnrichmentOptions.Builder().features(nluEnrichmentFeatures).build();
        enrichment.setOptions(enrichmentOptions);
        enrichments.add(enrichment);
        configuration.setEnrichments(enrichments);
        configuration.setName("testconfig");
        List<NormalizationOperation> normalizationOperations2 = new LinkedList<>();
        configuration.setNormalizations(normalizationOperations2);
        System.err.println(configuration);
        return configuration;
    }
}
