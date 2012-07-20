package org.jboss.bpm.console.client.model;

import java.util.List;

/**
 * User: Jeff Yu
 * Date: 13/04/11
 */
public class StringRefWrapper {

    private List<StringRef> values;


    public StringRefWrapper() {

    }


    public StringRefWrapper(List<StringRef> values) {
        this.values = values;
    }

    public List<StringRef> getValues() {
        return values;
    }

    public void setValues(List<StringRef> values) {
        this.values = values;
    }
}
