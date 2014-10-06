package org.kie.uberfire.perspective.editor.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenParameter {

    public String key;
    public String value;

    public ScreenParameter(){}

    public ScreenParameter( String key, String value ) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
