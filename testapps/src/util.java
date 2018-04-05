import com.ibm.watson.developer_cloud.cognitive_client.*;

public class util {
    public static void dos(){
        //maxItems limits number of entities, keywords or concepts
        NaturalLanguageUnderstandingClient client = new NaturalLanguageUnderstandingClient(
                "4737dd5e-2d4b-4e6d-8387-bafb7330df29",
                "Lc2CgDxQbqYK",
                50
        );

        //AggregateData ad = client.analyzeData("https://people.kth.se/~pateriks/Final%20Project%20Report.txt", Util.DataType.URL, "IBM Wikipedia entry");
        //AggregateData ad = DataManager.analyzeWebSearchResults ("IBM", 50, Search.SearchType.GOOGLE_REGULAR, "IBM Google search", false, false, null, client);
        AggregateData ad = DataManager.analyzeDirectory("pdf", "IBM search results", true, true, "dir3-analysis", Util.DataType.HTML, client);
        System.out.println(ad);
    }

    public static void tres(){
        //maxItems limits number of entities, keywords or concepts
        NaturalLanguageUnderstandingClient client = new NaturalLanguageUnderstandingClient(
                "4737dd5e-2d4b-4e6d-8387-bafb7330df29",
                "Lc2CgDxQbqYK",
                50
        );

        //AggregateData ad = client.analyzeData("https://people.kth.se/~pateriks/Final%20Project%20Report.txt", Util.DataType.URL, "IBM Wikipedia entry");
        AggregateData ad = DataManager.analyzeWebSearchResults ("Zlatan move to MLS", 3, Search.SearchType.GOOGLE_REGULAR, "IBM Google search", false, false, null, client);
        //AggregateData ad = DataManager.analyzeDirectory("pdf", "IBM search results", true, true, "dir3-analysis", Util.DataType.HTML, client);
        System.out.println(ad);
    }

    public static void cuatro(){
        //maxItems limits number of entities, keywords or concepts
        NaturalLanguageUnderstandingClient client = new NaturalLanguageUnderstandingClient(
                "4737dd5e-2d4b-4e6d-8387-bafb7330df29",
                "Lc2CgDxQbqYK",
                50
        );

        AggregateData ad = client.analyzeData("https://people.kth.se/~pateriks/Final%20Project%20Report.txt", Util.DataType.URL, "IBM Wikipedia entry");
        //AggregateData ad = DataManager.analyzeWebSearchResults ("IBM", 50, Search.SearchType.GOOGLE_REGULAR, "IBM Google search", false, false, null, client);
        //AggregateData ad = DataManager.analyzeDirectory("pdf", "IBM search results", true, true, "dir3-analysis", Util.DataType.HTML, client);
        System.out.println(ad);
    }
}
