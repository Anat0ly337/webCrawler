package com.web.crawler.controller;

import com.web.crawler.service.ParserUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@PropertySource("classpath:url.properties")
public class MainController {
    @Value("${SEED.URL}")
    private String URL;

    @Autowired
    private ParserUrl parserUrl;

    @GetMapping("/hello")
    public String sendmail(){
        parserUrl.getPageLinks(URL,2);
        return "Hello";
    }

    @GetMapping("/bye")
    public String sendmai() {
        parserUrl.asd();
        return "bye";
    }

    @GetMapping("/a")
    public String a() throws IOException {
        Set<String> power = Stream.of("Musk","Tesla","Mask").collect(Collectors.toSet());
        parserUrl.getStat(URL, power);
        return "a";
    }
}
