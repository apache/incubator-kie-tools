package org.uberfire.ext.perspective.editor.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenParameter {

    private String key;
    private String value;

    public ScreenParameter() {
    }

    public ScreenParameter( String key,
                            String value ) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }
}
