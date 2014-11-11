package org.uberfire.ext.perspective.editor.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class HTMLEditor {

    private String htmlCode;

    public HTMLEditor(){}

    public HTMLEditor( String htmlCode ) {

        this.htmlCode = htmlCode;
    }

    public void setHtmlCode( String htmlCode ) {
        this.htmlCode = htmlCode;
    }

    public String getHtmlCode() {
        return htmlCode;
    }
}
