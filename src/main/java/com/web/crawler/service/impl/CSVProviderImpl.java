package com.web.crawler.service.impl;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.web.crawler.dto.WordHits;
import com.web.crawler.exception.CustomException;
import com.web.crawler.service.CSVProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@PropertySource("classpath:application.properties")
public class CSVProviderImpl implements CSVProvider {
    @Value("${CSV.FILE}")
    private String fileName;
    @Value("${CSV.LIMIT}")
    private String limitCSVrows;
    private static final String PATH = "E:\\";
    private static Stream<String> stream;
    private Set<String> words;

    public CSVProviderImpl() {
        words = Stream.of("Tesla", "Musk", "Gigafactory", "Elon Mask", "Total").collect(Collectors.toSet());
    }

    @Override
    public void writeToCSV(Set<String> urls) {
        String file = checkDirectory();
        Map<String, List<WordHits>> map = new HashMap<>();
        urls.forEach(s -> {
            try {
                Map<String, List<WordHits>> result = getStatisticsFromPage(s, words);
                map.putAll(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            csvWriter(map, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void csvWriter(Map<String, List<WordHits>> map, String file) {
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(file));
            List<String[]> list = iterateMapToList(map);
            File f = new File(file);
            if (f.canWrite()) {
                writer.writeAll(list);
                writer.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public List<String> readCSV() {
        String file = checkDirectory();
        List<String[]> list;
        try {
            Reader reader = new FileReader(file);
            list = new ArrayList<>();
            CSVReader csvReader = new CSVReader(reader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                list.add(line);
            }
            reader.close();
            csvReader.close();
        } catch (IOException e) {
            throw new CustomException("Невозможно прочитать файл");
        } catch (CsvValidationException e) {
            throw new CustomException("Невозможно прочитать файл");
        }
        return sortByTotalHits(list).stream().limit(10).collect(Collectors.toList());
    }


    private List<String[]> iterateMapToList(Map<String, List<WordHits>> map) {
        Iterator it = map.entrySet().iterator();
        List<String[]> list = new ArrayList<>();

        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            String url = (String) me.getKey();
            List<WordHits> wordHits = (List) me.getValue();
            String[] row = new String[1 + wordHits.size()];
            row[0] = url;
            for (int i = 1; i < row.length; i++) {
                row[i] = wordHits.get(i - 1).getHit().toString();
            }
            list.add(row);
        }
        return list;
    }


    private Map<String, List<WordHits>> getStatisticsFromPage(String url, Set<String> values) throws IOException {
        Map<String, List<WordHits>> map = new HashMap<>();
        Document document = Jsoup.connect(url).get();
        List<WordHits> wordHits = new ArrayList<>();
        values.forEach(s -> {
            int element = document.getElementsMatchingOwnText(s).size();
            wordHits.add(new WordHits(s, element));
        });
        map.put(url, wordHits);
        return map;
    }

    private String checkDirectory() {
        String directory = PATH + File.separator + fileName;
        File file = new File(directory);
        if (!file.exists()) {
            Path filePath = Paths.get(directory);
            try {
                Files.write(filePath, new String().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory;
    }

    private List<String> sortByTotalHits(List<String[]> list) {
        Map<String, Integer> mapForSort = new TreeMap<>();
        Map<String, String> map = new TreeMap<>();
        List<String> result = new ArrayList<>();
        for (String[] strings : list) {
            int count = 0;
            StringBuilder hits = new StringBuilder();
            for (int i = 1; i < strings.length; i++) {
                count = count + Integer.parseInt(strings[i]);
                hits.append(strings[i] + " ");

            }
            map.put(strings[0], hits.toString());
            mapForSort.put(strings[0], count);
        }
        mapForSort = sortMapByValue(mapForSort);
        mapForSort.forEach((k, v) -> {
            result.add(k + " " + map.get(k));
        });
        return result;
    }

    private Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
    }
}

