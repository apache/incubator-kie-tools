package org.drools.workbench.screens.enums.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class EnumModelContent {

    private EnumModel model;

    public EnumModelContent() {
    }

    public EnumModelContent(EnumModel model) {
        this.model = model;
    }

    public EnumModel getModel() {
        return model;
    }
}
