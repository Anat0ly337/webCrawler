package com.web.crawler.dto;

import java.util.Objects;

public class Link {
    private String url;
    private String depth;

    public Link(String url, String depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        Link link = (Link) o;
        return Objects.equals(getUrl(), link.getUrl()) &&
                Objects.equals(getDepth(), link.getDepth());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getDepth());
    }
}
