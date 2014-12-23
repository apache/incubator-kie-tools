package org.uberfire.ext.plugin.client.perspective.editor.util;

public enum DragType {


    GRID("GRID"), SCREEN("Screen Component"), EXTERNAL("External Component"), HTML("HTML Component");

    private String label;

    DragType( String label ){
        this.label = label;
    }


    public String label() {
        return label;
    }
}
