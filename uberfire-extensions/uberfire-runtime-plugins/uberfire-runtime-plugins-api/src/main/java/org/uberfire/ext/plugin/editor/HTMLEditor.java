package org.uberfire.ext.plugin.editor;

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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof HTMLEditor ) ) {
            return false;
        }

        HTMLEditor that = (HTMLEditor) o;

        if ( htmlCode != null ? !htmlCode.equals( that.htmlCode ) : that.htmlCode != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return htmlCode != null ? htmlCode.hashCode() : 0;
    }
}
