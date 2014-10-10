package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

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
