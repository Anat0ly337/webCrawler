package com.web.crawler.service;

import java.util.List;
import java.util.Set;

public interface CSVProvider {
    void writeToCSV(Set<String> urls);
    List<String> readCSV();
}
