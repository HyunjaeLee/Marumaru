import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) throws Exception {

        search("도쿄구울").keySet().forEach(System.out::println);

        list("http://marumaru.in/b/manga/64026").keySet().forEach(System.out::println);
        /*
        Collection<String> images = images("http://www.shencomics.com/archives/264690");
        int num = 1;
        for(String image : images) {
            save(image, "/Users/Hyunjae/Downloads/" + num + ".jpg");
            num++;
        }
        */
    }

    public static Map<String, String> search(String keyword) throws IOException {

        Document doc = Jsoup.connect("http://marumaru.in/?r=home&mod=search&keyword=" + keyword)
                .userAgent("Mozilla")
                .timeout(10000)
                .get();
        Element div = doc.select("div.postbox").first();
        Elements elements = div.select("a[href]");
        Map<String, String> map = new LinkedHashMap<>();
        elements.forEach(element -> map.put(element.text(), elements.attr("abs:href")));
        return map;

    }

    public static Map<String, String> list(String url) throws IOException {

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(10000)
                .get();
        Elements elements = doc.select("a[href*=archives]");
        Map<String, String> map = new LinkedHashMap<>();
        elements.forEach(element -> map.put(element.text(), elements.attr("abs:href")));
        return map;

    }

    static {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); // Silent
    }

    public static Collection<String> images(String url) throws IOException {

        final WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        final HtmlPage page = webClient.getPage(url);
        String html = page.getWebResponse().getContentAsString();

        Collection<String> images = new LinkedHashSet<>();

        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("img[src*=data]");
        elements.forEach(element -> images.add(element.attr("data-src")));

        return images;

    }

    public static void save(String url, String file) throws IOException {

        URLConnection connection = new URL(url).openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla");
        ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(new File(file));
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

    }

}
