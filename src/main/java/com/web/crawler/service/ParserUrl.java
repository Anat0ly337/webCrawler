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
import java.util.stream.Stream;

@Component
@PropertySource("classpath:application.properties")
public class ParserUrl {

    @Value("${SEED.URL}")
    private String URL;

    private static final int MAX_DEPTH = 9;
    private Set<String> links;
    private Map<String, Set<String>> parentChildLinks;
    int i = 0;
    private String basicLanguageForWiki;
    private String basicDomain;
    private boolean isBasicUrl;

    public ParserUrl() {
        links = new HashSet<>();
        parentChildLinks = new HashMap<>();
        isBasicUrl = true;

    }

    public void setBasicDomain(String basicDomain) {
        this.basicDomain = basicDomain;
    }

    public void getPageLinks(String URL, int depth) {
        if (isBasicUrl) {
            if (URL.contains("wikipedia")) {
                setupBasicLanguageForWiki(URL);
            }
            setupBasicDomain(URL);
            isBasicUrl = false;
        }
        //# - anchor JavaScript
        String link = getLinkWithoutAnchor(URL);

        if ((!links.contains(link) && (depth < MAX_DEPTH))) {
            try {
                links.add(link);
                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");
                depth++;

                for (Element page : linksOnPage) {
                    String linka = page.attr("abs:href");

                    if (linka.contains(this.basicDomain)) {
                        this.links.add(page.attr("abs:href"));
                        getPageLinks(linka, depth);
                    }

                }


            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }

    }

    public void asd() {
        Document document = null;
        try {
            document = Jsoup.connect(URL).get();

            String s = document.body().html();
            String x = "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements linksOnPage = document.select("a[href]");
        System.out.println(linksOnPage.size());

        System.out.println("----------------------");
        System.out.println(links.size());

    }

    private void setupBasicLanguageForWiki(String url) {
        if (url.contains("wikipedia")) {
            String language = url;
            language = language.substring(8, 10);
            this.basicLanguageForWiki = language;
        }
    }


    private String getLinkWithoutAnchor(String URL) {
        String result = URL;
        if (URL.contains(this.basicDomain)) {
            if (URL.contains("#")) {
                int index = URL.indexOf("#");
                URL = URL.substring(0, index);
                result = URL;
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


    public Map getStat(String url, Set<String> values) throws IOException {
        Map<Url,List<WordHits>>  map = new HashMap<>();
        Document document = Jsoup.connect(url).get();
        List<WordHits> wordHits = new ArrayList<>();
        String html = document.body().html();

        values.forEach(s -> {
            int element = document.getElementsMatchingOwnText(s).size();
            wordHits.add(new WordHits(s,element));

        });
        map.put(new Url(url),wordHits);
        String x = "";
        return map;
    }


}
