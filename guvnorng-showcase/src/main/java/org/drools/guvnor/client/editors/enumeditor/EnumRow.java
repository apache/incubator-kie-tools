package org.drools.guvnor.client.editors.enumeditor;

public class EnumRow {

    private String fieldName = "";
    private String factName = "";
    private String context = "";

    public EnumRow(String line) {

        String text = line;
        if (text == "") {
            factName = "";
            fieldName = "";
            context = "";
        } else {
            factName = text.substring(1, text.indexOf("."));

            fieldName = text.substring(text.indexOf(".") + 1, text.indexOf("':"));
            context = text.substring(text.indexOf(":") + 1).trim();
        }
    }


    public String getText() {
        if (factName == "") {
            return "";
        } else {
            return "'" + factName + "." + fieldName + "': " + context;
        }
    }


    public String getFactName() {
        return factName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getContext() {
        return context;
    }

    public void setFactName(String factName) {
        this.factName = factName;

    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
