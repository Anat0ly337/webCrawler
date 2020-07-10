package com.web.crawler.dto;

import java.io.Serializable;

public class WordHits {
    private String word;
    private int hit;

    public WordHits(String word, int hit) {
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

    public void setHit(int hit) {
        this.hit = hit;
    }

}
