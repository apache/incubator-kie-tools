package org.drools.workbench.screens.globals.model;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.packages.HasPackageName;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * The model for Globals
 */
@Portable
public class GlobalsModel implements HasPackageName {

    private String packageName;

    private List<Global> globals = new ArrayList<Global>();

    public List<Global> getGlobals() {
        return globals;
    }

    public void setGlobals( List<Global> globals ) {
        this.globals = globals;
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public void setPackageName( final String packageName ) {
        this.packageName = packageName;
    }
}
