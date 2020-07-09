package com.web.crawler.service;

import com.web.crawler.dto.Url;
import com.web.crawler.dto.WordHits;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Component
@PropertySource("classpath:application.properties")
public class ParserUrl {

    @Value("${SEED.URL}")
    private String URL;
    @Value("${SEED.DEPTH}")
    private String maxDepth;
    @Value("${SEED.LIMIT}")
    private String limit;
    private int count = 0;
    private Set<String> links;
    private String basicDomain;
    private boolean isBasicUrl;
    Elements linksOnPage;

    public ParserUrl() {
        links = new HashSet<>();
        isBasicUrl = true;
    }

    public void getStarted(String url) {
        setupBasicDomain(url);
    }

    public void startMethod(String url) {
        setupBasicDomain(url);
        try {
            linksOnPage = getElements(url);
        } catch (IOException e) {
            System.out.println("ошибка");
        }
        if (linksOnPage.size() == 0) {
            System.out.println("Пусто");
        }
        count++;

        while (count < Integer.parseInt(this.maxDepth)) {
            iterateStartPageLinks(url,1);

        }
    }

    private void iterateStartPageLinks(String url, int numberElement) {
        if (linksOnPage.size() != 0) {
            Element element = linksOnPage.get(numberElement);
            int depth = 1;
            getPageLinksOnDepth(element.attr("abs:href"), depth, numberElement);
        }

    }


    ////////////////////////////////
    private void getPageLinksOnDepth(String url, int depth, int numberElement) {
        if (count>=Integer.parseInt(this.limit)){
            System.out.println("всё");
            Thread.interrupted();
        }
        if (depth >= Integer.parseInt(this.maxDepth)) {
            iterateStartPageLinks(url, numberElement + 1);
        }
        String link = getLinkWithoutAnchor(url);
        if (!(links.contains(link) || link == null)) {
            count++;
            System.out.println(">> Depth: " + depth + " [" + url + "]" + " " + count);
            links.add(url);

            Elements linksOnPage = null;
            try {
                linksOnPage = getElements(url);
            } catch (IOException e) {
                System.out.println("ошибка");
                return;
            }
            depth++;
            for (Element page : linksOnPage) {
                getPageLinksOnDepth(page.attr("abs:href"), depth, numberElement);
            }
        }
    }


    private Elements getElements(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.select("a[href]");
    }


    private String getLinkWithoutAnchor(String url) {
        String result = url;
        //in this case we get only wikipedia english links
        if (url.contains(this.basicDomain)) {
            if (url.contains("#")) {
                url = url.substring(0, url.indexOf("#"));
                result = url;
            }
        } else {
            return null;
        }
        return result;
    }


    private void setupBasicDomain(String URL) {
        try {
            URI uri = new URI(URL);
            this.basicDomain = uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public Map getStatisticsFromPage(String url, Set<String> values) throws IOException {
        Map<Url, List<WordHits>> map = new HashMap<>();
        Document document = Jsoup.connect(url).get();
        List<WordHits> wordHits = new ArrayList<>();
        values.forEach(s -> {
            int element = document.getElementsMatchingOwnText(s).size();
            wordHits.add(new WordHits(s, element));
        });
        map.put(new Url(url), wordHits);
        return map;
    }
}
