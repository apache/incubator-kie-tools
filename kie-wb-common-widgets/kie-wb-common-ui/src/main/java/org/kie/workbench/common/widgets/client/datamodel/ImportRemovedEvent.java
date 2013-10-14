package org.kie.workbench.common.widgets.client.datamodel;

import org.drools.workbench.models.datamodel.imports.Import;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * An event signalling removal of an import
 */
public class ImportRemovedEvent {

    private final Import item;
    private final AsyncPackageDataModelOracle dmo;

    public ImportRemovedEvent( final AsyncPackageDataModelOracle dmo,
                               final Import item ) {
        this.dmo = PortablePreconditions.checkNotNull( "dmo",
                                                       dmo );
        this.item = PortablePreconditions.checkNotNull( "item",
                                                        item );
    }

    public Import getImport() {
        return this.item;
    }

    public AsyncPackageDataModelOracle getDataModelOracle() {
        return this.dmo;
    }

}
