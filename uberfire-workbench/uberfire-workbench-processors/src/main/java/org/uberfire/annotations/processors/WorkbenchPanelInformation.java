package org.uberfire.annotations.processors;

import java.util.List;

public class WorkbenchPanelInformation {

    private String fieldName;
    private List<PartInformation> wbParts;
    private boolean isDefault;
    private String panelType;

    public void setDefault( boolean isDefault ) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    public List<PartInformation> getWbParts() {
        return wbParts;
    }

    public void setWbParts( List<PartInformation> uFParts ) {
        this.wbParts = uFParts;
    }

    public void setPanelType( String panelType ) {
        this.panelType = panelType;
    }

    public String getPanelType() {
        return panelType;
    }

    public String getFieldName() {
        return fieldName;
    }
}
