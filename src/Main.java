public class Main {

    public static void main(String[] args) {

        //Marumaru.search("슬라임").forEach(data -> System.out.println(data.getName() + " | " + data.getUrl()));

        //Marumaru.list("http://marumaru.in/b/manga/64026").forEach(data -> System.out.println(data.getName() + " | " + data.getUrl()));

        //Download.builder(Marumaru.images("http://www.shencomics.com/archives/264690"));

        Marumaru.all().forEach(data1 -> {
            System.out.println(data1.getName());
            Marumaru.list(data1.getUrl()).forEach(data2 -> {
                System.out.println(data2.getName());
                Marumaru.images(data2.getUrl()).forEach(data3 -> System.out.println(data3.getName() + " | " + data3.getUrl()));
            });
        });

    }

}
