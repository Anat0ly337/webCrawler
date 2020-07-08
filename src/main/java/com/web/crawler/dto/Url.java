package com.web.crawler.dto;

import java.util.Objects;

public class Url {
    private String url;
    private Integer depth;

    public Url(String url, Integer depth) {
        this.url = url;
        this.depth = depth;
    }

    public Url(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Url)) return false;
        Url url1 = (Url) o;
        return Objects.equals(getUrl(), url1.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
