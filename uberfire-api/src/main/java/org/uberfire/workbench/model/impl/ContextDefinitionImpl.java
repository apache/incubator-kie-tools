package org.uberfire.workbench.model.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.ContextDefinition;

@Portable
public class ContextDefinitionImpl implements ContextDefinition {

    private PlaceRequest place;

    public ContextDefinitionImpl() {
    }

    public ContextDefinitionImpl( final PlaceRequest place ) {
        this.place = place;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public void setPlace( final PlaceRequest place ) {
        this.place = place;
    }
}
