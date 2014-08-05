package org.drools.workbench.screens.enums.model;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class EnumModelContent {

    private EnumModel model;

    private Overview overview;

    public EnumModelContent() {
    }

    public EnumModelContent(EnumModel model, Overview overview) {
        this.model = PortablePreconditions.checkNotNull("model", model);
        this.overview = PortablePreconditions.checkNotNull("overview", overview);
    }

    public EnumModel getModel() {
        return model;
    }

    public Overview getOverview() {
        return overview;
    }
}
