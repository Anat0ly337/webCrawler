package com.web.crawler.service.impl;

import com.web.crawler.exception.CustomException;
import com.web.crawler.service.CSVProvider;
import com.web.crawler.service.ParserURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@PropertySource("classpath:application.properties")
public class ParserURLImpl implements ParserURL {
    @Value("${SEED.URL}")
    private String URL;
    @Value("${SEED.DEPTH}")
    private String maxDepth;
    @Value("${SEED.LIMIT}")
    private String limit;
    private static Set<String> links;
    private String basicDomain;
    @Autowired
    private CSVProvider csvProvider;

    public ParserURLImpl() {
        links = new HashSet<>();
    }

    public static Set<String> getLinks() {
        return links;
    }

    @Override
    public void startMethod() {
        setupBasicDomain(URL);
        Elements linksOnPage = null;
        try {
            linksOnPage = getElements(URL);
        } catch (IOException e) {
            System.out.println("ошибка");
        }
        if (linksOnPage.size() == 0) {
            System.out.println("Пусто");
        }
        getPageLinksOnDepth(URL, 1, linksOnPage, 1);
    }

    ////////////////////////////////
    private void getPageLinksOnDepth(String url, int depth, Elements linksOnStartPage, int numberLinkOnStartPage) {
        if (links.size() > Integer.parseInt(this.limit)) {
            finalResult();
        }
        if (depth > Integer.parseInt(this.maxDepth)) {
            depth = 1;
            String u = getLinkWithoutAnchor(linksOnStartPage.get(numberLinkOnStartPage + 1).attr("abs:href"));
            getPageLinksOnDepth(u, depth, linksOnStartPage, numberLinkOnStartPage + 1);
        }
        if (!links.contains(url) && url != null) {
             System.out.println(">> Depth: " + depth + " [" + url + "]" + " " + links.size());
            System.out.println(links.size());
            links.add(url);

            Elements linksOnPage = null;
            try {
                linksOnPage = getElements(url);
            } catch (IOException e) {
                throw new CustomException("exception");
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


    private void setupBasicDomain(String url) {
        try {
            URI uri = new URI(url);
            this.basicDomain = uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void finalResult() {
        System.out.println("всё");
        csvProvider.writeToCSV(links);
        List<String> result = csvProvider.readCSV();
        result.forEach(strings -> {
            System.out.println("-----");
            System.out.println(strings);
        });
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
