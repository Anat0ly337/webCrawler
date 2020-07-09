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


    public ParserUrl() {
        links = new HashSet<>();
        isBasicUrl = true;
    }


    public void startMethod(String url) {
        setupBasicDomain(url);
        Elements linksOnPage = null;
        try {
            linksOnPage = getElements(url);
        } catch (IOException e) {
            System.out.println("ошибка");
        }
        if (linksOnPage.size() == 0) {
            System.out.println("Пусто");
        }
        getPageLinksOnDepth(url, 1, linksOnPage, 1);
    }


    ////////////////////////////////
    private void getPageLinksOnDepth(String url, int depth, Elements linksOnStartPage, int numberLinkOnStartPage) {
        if (links.size() > Integer.parseInt(this.limit)) {
            System.out.println("всё");
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (depth > Integer.parseInt(this.maxDepth)) {
            depth = 1;
            String u = getLinkWithoutAnchor(linksOnStartPage.get(numberLinkOnStartPage + 1).attr("abs:href"));
            getPageLinksOnDepth(u, depth, linksOnStartPage, numberLinkOnStartPage + 1);
        }
        if (!links.contains(url)&& url!=null) {
            System.out.println(">> Depth: " + depth + " [" + url + "]" + " " + links.size());
            System.out.println(links.size());
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
                getPageLinksOnDepth(getLinkWithoutAnchor(page.attr("abs:href")), depth, linksOnStartPage, numberLinkOnStartPage);
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
