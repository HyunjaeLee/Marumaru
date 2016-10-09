import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Marumaru {

    public static Map<String, String> cookies = new HashMap<>();

    private static Document connect(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .cookies(cookies)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }

    public static Collection<Data> search(String keyword) {

        keyword.replaceAll(" ", "+");
        Document doc = connect(Properties.getProperty("url.search") + keyword);
        Element div = doc.select("div.postbox").first();
        Elements elements = div.select("a[href]");
        Collection<Data> dataCollection = new LinkedList<>();
        elements.forEach(element -> {
            Data data = new Data();
            data.setName(element.text());
            data.setUrl(element.attr("abs:href"));
            dataCollection.add(data);
        });
        return dataCollection;

    }

    public static Collection<Data> list(String url) {

        Document doc = connect(url);
        Elements elements = doc.select("a[href*=archives]");
        Collection<Data> dataCollection = new LinkedList<>();
        elements.forEach(element -> {
            Data data = new Data();
            data.setName(element.text());
            data.setUrl(element.attr("abs:href"));
            dataCollection.add(data);
        });
        return dataCollection;

    }

    private static void nashorn(Document doc) {

        String script =
                "var Marumaru = Java.type('Marumaru');" +
                "var document = {};" +
                "var location = {reload: function(){" +
                "var cookie = document.cookie.toString().split('=');" +
                "Marumaru.cookies.put(cookie[0], cookie[1])" +
                "}};" +
                doc.select("script").first().data() + ";";

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            scriptEngine.eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }

    public static Collection<Data> images(String url) {

        Document doc = connect(url);
        if(doc.title().equals("You are being redirected...")) {
            nashorn(doc);
            doc = connect(url);
        }

        Collection<Data> images = new LinkedList<>();
        Elements elements = doc.select("img[src*=data]");
        int count = 1;
        for(Element element : elements){
            Data image = new Data();
            image.setName(count + ".jpg");
            image.setUrl(element.attr("data-src"));
            images.add(image);
            count++;
        }

        return images;

    }

    public static Collection<Data> all() {

        Document doc = connect(Properties.getProperty("url.all"));
        Elements div = doc.select("div.widget_review01");
        Elements ul = div.select("ul");
        Elements elements = ul.select("a[href]");
        Collection<Data> dataCollection = new LinkedList<>();
        elements.forEach(element -> {
            Data data = new Data();
            data.setName(element.text());
            data.setUrl(element.attr("abs:href"));
            dataCollection.add(data);
        });
        return dataCollection;

    }

}
