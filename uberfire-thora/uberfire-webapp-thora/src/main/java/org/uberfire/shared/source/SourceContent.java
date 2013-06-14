package org.uberfire.shared.source;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SourceContent {

    private String content;
    private List<String> breadcrumb;

    public SourceContent() {
    }

    public SourceContent( String content,
                          List<String> breadcrumb ) {
        this.content = content;
        this.breadcrumb = breadcrumb;
    }

    public String getContent() {
        return content;
    }

    public List<String> getBreadcrumb() {
        return breadcrumb;
    }
}
