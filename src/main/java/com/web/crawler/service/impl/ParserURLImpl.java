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
import java.util.stream.Collectors;

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


    /*
     * start method
     */
    @Override
    public void startMethod() {
        setupBasicDomain(URL);
        Elements linksOnPage = null;
        try {
            linksOnPage = getElements(URL);
        } catch (IOException e) {
            System.out.println("Error");
        }
        if (linksOnPage.size() == 0) {
            System.out.println("Empty");
        }
        getPageLinksOnDepth(URL, 1, linksOnPage, 1);
    }

    @Override
    public List<String> getLinks() {
        return links.stream().collect(Collectors.toList());
    }

    /**
     * recursive method for visite links
     * @param url  current url in iterate
     * @param depth - current depth as to basic seed link
     * @param linksOnStartPage - all links from basic url links
     * @param numberLinkOnStartPage - number of parseble link regarding start page
     */
    private void getPageLinksOnDepth(String url, int depth, Elements linksOnStartPage, int numberLinkOnStartPage) {
        if (links.size() > Integer.parseInt(this.limit)) {
            finalResult();
        }
        if (depth > Integer.parseInt(this.maxDepth)) {
            depth = 1;
            //# - Anchor JavaScript
            String u = getLinkWithoutAnchor(linksOnStartPage.get(numberLinkOnStartPage + 1).attr("abs:href"));
            getPageLinksOnDepth(u, depth, linksOnStartPage, numberLinkOnStartPage + 1);
        }
        if (!links.contains(url) && url != null) {
            System.out.println(links.size()+">> Depth: " + depth + " [" + url + "]");
            links.add(url);

            Elements linksOnPage = null;
            try {
                linksOnPage = getElements(url);
            } catch (IOException e) {
                throw new CustomException("Exception"+ e.getMessage());
            }
            depth++;
            for (Element page : linksOnPage) {
                getPageLinksOnDepth(getLinkWithoutAnchor(page.attr("abs:href")), depth, linksOnStartPage, numberLinkOnStartPage);
            }
        }
    }

    /**
     * read from CSV file
     * @param url - current url
     * @return Elements - elements(href) of url
     */
    private Elements getElements(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.select("a[href]");
    }

    /**
     * get Link without JavaScript anchor (#)
     * @param url - current url
     * @return String - link without JavaScript anchor (#)
     */
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

    //setupBasicDomain
    private void setupBasicDomain(String url) {
        try {
            URI uri = new URI(url);
            this.basicDomain = uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //final sorted result from CSV file
    private void finalResult() {
        System.out.println("IT ALL");
        csvProvider.writeToCSV(links);
        List<String> result = csvProvider.readCSV();
        result.forEach(strings -> {
            System.out.println(strings);
        });

        System.exit(0);
    }
}
