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
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:application.properties")
public class ParserUrl {

    @Value("${SEED.URL}")
    private String URL;
    @Value("${SEED.DEPTH}")
    private String maxDepth;
    @Value("${SEED.LIMIT}")
    private String limit;
    private Set<String> links;
    private String basicDomain;
    private boolean isBasicUrl;

    public ParserUrl() {
        links = new HashSet<>();
        isBasicUrl = true;
    }

    public void startMethod(String url){
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements linksOnPage = document.select("a[href]");

        List<Element> pages = linksOnPage.stream().collect(Collectors.toList());

        for (int i = 0; i < Integer.parseInt(maxDepth) ; i++) {
            Element element = pages.get(i);

            String link = getLinkWithoutAnchor(element.attr("abs:href"));
            if (link == null) {
                continue;
            }
            getPageLinks(link, depth);
        }
    }


    public void getPageLinks(String url, int depth,int countStartUrl) {
        if (isBasicUrl) {
            setupBasicDomain(URL);
            isBasicUrl = false;
        }

        if (depth>=Integer.parseInt(maxDepth)){
            getPageLinks(URL,0,countStartUrl+1);
        }

        if (url == null) {
            if (!(this.links.size() >= Integer.parseInt(this.limit))) {
                if (!links.contains(url)) {
                    try {
                        links.add(url);
                        System.out.println(links.size());
                        Document document = Jsoup.connect(url).get();
                        Elements linksOnPage = document.select("a[href]");
                        depth++;
                        System.out.println(">> Depth: " + depth + " [" + url + "]");

                        /*for (Element page : linksOnPage) {
                            //# - anchor JavaScript
                            String link = getLinkWithoutAnchor(page.attr("abs:href"));
                            if (link == null) {
                                continue;
                            }
                            getPageLinks(link, depth);
                        }*/

                        List<Element> pages = linksOnPage.stream().collect(Collectors.toList());

                        for (int i = 0; i < Integer.parseInt(maxDepth) ; i++) {
                            Element element = pages.get(i);
                        }


                    } catch (IOException e) {
                        System.err.println("For '" + url + "': " + e.getMessage());
                    }
                }
            }
        }
    }




    private String getLinkWithoutAnchor(String url) {
        String result = url;
        if (url.contains(this.basicDomain)) {
            if (url.contains("#")) {
                int index = url.indexOf("#");
                url = url.substring(0, index);
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
