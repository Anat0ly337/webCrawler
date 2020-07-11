package com.web.crawler.dto;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordHits)) return false;
        WordHits wordHits = (WordHits) o;
        return Objects.equals(getWord(), wordHits.getWord());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord());
    }
}
