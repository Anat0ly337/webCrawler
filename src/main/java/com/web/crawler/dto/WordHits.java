package com.web.crawler.dto;

import java.io.Serializable;
import java.util.Objects;

public class WordHits implements Serializable {
    private static final long serialVersionUID = -2580064959258637376L;
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
