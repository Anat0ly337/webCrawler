package com.web.crawler.service.impl;

import com.web.crawler.CrawlerApplication;
import com.web.crawler.service.CSVProvider;
import com.web.crawler.service.ParserURL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
class CSVProviderImplTest {
    Set<String> urls = new TreeSet<>();
    private static final String FILE = "E:\\results.csv";

    @Before
    public void setup(){
    }

    @Test
    void emptyFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(FILE);
        writer.print("");
        writer.close();
        File file = new File(FILE);
        assertTrue(file.length() == 0);
    }

    @Test
    void writeToCSV() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(FILE);
        writer.print("");
        writer.close();
        urls.add("https://en.wikipedia.org/wiki/Wikipedia:Did_you_know/Learning_DYK");
        urls.add("https://en.wikipedia.org/wiki/Wikipedia:Protection_policy");
        CSVProvider csvProvider = new CSVProviderImpl();
        csvProvider.writeToCSV(urls);
        File file = new File(FILE);
        assertTrue(file.length()<=0);
    }

    @Test
    void readCSVNotNull() {
        urls.add("https://en.wikipedia.org/wiki/Wikipedia:Did_you_know/Learning_DYK");
        urls.add("https://en.wikipedia.org/wiki/Wikipedia:Protection_policy");
        new CSVProviderImpl().writeToCSV(urls);
        List<String> list =  new CSVProviderImpl().readCSV();
        assertNotNull(list);
        assertTrue(list.size()!=0);
    }

    //is Contains real link
    @Test
    void readCSVContaianLink() {
       List<String> list =  new CSVProviderImpl().readCSV();
       boolean isContainHttp = list.stream().findAny().get().contains("http");
       assertTrue(isContainHttp);
    }

    @Test
    void readCSVbadLinks() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(FILE);
        writer.print("");
        writer.close();
        urls.add("dsadas");
        urls.add("n.wikipedia.org/wiki/Wikipedia:Protection_policy");
        try {
            new CSVProviderImpl().writeToCSV(urls);
            new CSVProviderImpl().readCSV();
        }catch (Exception e){
            Assert.assertNotEquals("",e.getMessage());
        }
    }


}