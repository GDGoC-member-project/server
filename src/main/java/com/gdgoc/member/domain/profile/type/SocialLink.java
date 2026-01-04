package com.gdgoc.member.domain.profile.type;

public class SocialLink {
    private Icon icon;
    private String url;

    public SocialLink() {}

    public SocialLink(Icon icon, String url) {
        this.icon = icon;
        this.url = url;
    }

    public Icon getIcon() { return icon; }
    public String getUrl() { return url; }

    public void setIcon(Icon icon) { this.icon = icon; }
    public void setUrl(String url) { this.url = url; }
}

