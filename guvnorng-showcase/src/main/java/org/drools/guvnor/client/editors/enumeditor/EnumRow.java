package org.drools.guvnor.client.editors.enumeditor;

public class EnumRow {

    String text = "";
    String fieldName = "";
    String factName = "";

    public EnumRow(String line) {
        text = line;
    }

    public String getText() {
        return text;
    }

    public String getFactName() {
        return factName.substring(1);
    }

    public String getFieldName() {
        return fieldName;
    }
}
