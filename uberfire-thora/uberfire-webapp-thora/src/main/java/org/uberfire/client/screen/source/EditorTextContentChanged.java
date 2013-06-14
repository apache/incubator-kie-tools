package org.uberfire.client.screen.source;

public class EditorTextContentChanged {

    private final String text;

    public EditorTextContentChanged( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
