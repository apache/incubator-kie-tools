package org.kie.workbench.common.widgets.client.datamodel;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;

public class MockHasImports implements HasImports {

    private Imports imports = new Imports();

    @Override
    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports( final Imports imports ) {
        this.imports = imports;
    }
}
