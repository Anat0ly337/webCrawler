package com.web.crawler.integration;

import com.web.crawler.CrawlerApplication;
import com.web.crawler.service.CSVProvider;
import com.web.crawler.service.ParserURL;
import com.web.crawler.service.impl.ParserURLImpl;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerApplication.class)
@PropertySource("classpath:application.properties")
public class IntegrationTest {
    @Autowired
    private CSVProvider csvProvider;
    @Autowired
    private ParserURLImpl parserURLImpl;
    @Autowired
    private ParserURL parserURL;

    Set<String> urls = new TreeSet<>();
    private static final String FILE = "E:\\results.csv";
    @Value("${SEED.URL}")
    private String URL;

    @Before
    public void setup() {
    }

    @Test
    void mainTest() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(FILE);
        writer.print("");
        writer.close();
        File file = new File(FILE);
        assertTrue(file.length() == 0);
        parserURL.startMethod();
        assertTrue(file.length() > 0);
       // int i = ParserURLImpl.getLinks().size();
        List<String> s = csvProvider.readCSV();
        assertEquals(10,s.size());
    }
}
