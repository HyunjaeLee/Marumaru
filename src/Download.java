import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Download implements Runnable {

    private String file;
    private String url;

    public static void builder(Collection<Data> dataCollection) {

        //ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExecutorService executorService = Executors.newCachedThreadPool();
        dataCollection.forEach(data -> {
            Download download = new Download(Properties.getProperty("path.download") + data.getName(), data.getUrl());
            executorService.execute(download);
        });
        executorService.shutdown();

    }

    public Download(String file, String url) {
        this.file = file;
        this.url = url;
    }

    @Override
    public void run() {

        try {

            URLConnection connection = new URL(url).openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla");
            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(file));
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

}
