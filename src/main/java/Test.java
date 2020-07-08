import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void startMethod(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements linksOnPage = document.select("a[href]");

        List<Element> pages = linksOnPage.stream().collect(Collectors.toList());

        for (int i = 0; i < 8; i++) {
            Element element = pages.get(i);
        }
    }

    public static void main(String[] args) {
        startMethod("https://en.wikipedia.org/wiki/Elon_Musk");
    }


}