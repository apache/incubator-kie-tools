package org.uberfire.shared;

public class Mood {

    private final String text;

    public Mood( String text ) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
