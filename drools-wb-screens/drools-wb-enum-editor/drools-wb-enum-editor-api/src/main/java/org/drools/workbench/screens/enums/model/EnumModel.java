package org.drools.workbench.screens.enums.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EnumModel {

    private String drl;

    public EnumModel() {

    }

    public EnumModel(String drl) {
        this.drl = drl;
    }

    public String getDRL() {
        return drl;
    }

    public void setDRL(String drl) {
        this.drl = drl;
    }

}
