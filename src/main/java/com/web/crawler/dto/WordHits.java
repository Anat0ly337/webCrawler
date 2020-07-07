package com.web.crawler.dto;

public class WordHits {
    String word;
    Integer hit;

    public WordHits(String word, Integer hit) {
        this.word = word;
        this.hit = hit;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getHit() {
        return hit;
    }

    public void setHit(Integer hit) {
        this.hit = hit;
    }
}
