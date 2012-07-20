package org.jboss.bpm.console.client.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Jeff Yu
 * Date: 12/04/11
 */
@XmlRootElement(name = "stringRef")
public class StringRef {

    private String value;

    public StringRef(){}

    public StringRef(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
