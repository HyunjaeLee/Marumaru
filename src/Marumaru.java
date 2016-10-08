import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class Marumaru {

    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF); // Silent
    }

    private static Document connect(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }

    public static Collection<Data> search(String keyword) {

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

    public static Collection<Data> images(String url) {

        final WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        String html = null;
        try {
            final HtmlPage page = webClient.getPage(url);
            html = page.getWebResponse().getContentAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collection<Data> images = new LinkedList<>();
        Document doc = Jsoup.parse(html);
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
