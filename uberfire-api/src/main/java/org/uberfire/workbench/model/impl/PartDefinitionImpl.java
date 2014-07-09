/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.workbench.model.impl;

import static org.uberfire.workbench.model.ContextDisplayMode.*;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

/**
 * Default implementation of PartDefinition
 */
@Portable
public class PartDefinitionImpl
implements
PartDefinition {

    private PlaceRequest place;

    private PanelDefinition parentPanel;

    private ContextDefinition contextDefinition;
    private ContextDisplayMode contextDisplayMode = SHOW;

    public PartDefinitionImpl() {
    }

    public PartDefinitionImpl( final PlaceRequest place ) {
        this.place = place;
    }

    /**
     * @return the place
     */
    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    /**
     * @param place the place to set
     */
    @Override
    public void setPlace( final PlaceRequest place ) {
        this.place = place;
    }

    /**
     * @return the parentPanel
     */
    @Override
    public PanelDefinition getParentPanel() {
        return parentPanel;
    }

    /**
     * @param parentPanel the parentPanel to set
     */
    @Override
    public void setParentPanel( final PanelDefinition parentPanel ) {
        this.parentPanel = parentPanel;
    }

    @Override
    public void setContextDefinition( final ContextDefinition contextDefinition ) {
        this.contextDefinition = contextDefinition;
    }

    @Override
    public ContextDefinition getContextDefinition() {
        return contextDefinition;
    }

    @Override
    public ContextDisplayMode getContextDisplayMode() {
        return contextDisplayMode;
    }

    @Override
    public void setContextDisplayMode( final ContextDisplayMode contextDisplayMode ) {
        this.contextDisplayMode = contextDisplayMode;
    }


    @Override
    public int hashCode() {
        return this.place.hashCode();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null ) {
            return false;
        }
        if ( !( o instanceof PartDefinitionImpl ) ) {
            return false;
        }

        PartDefinitionImpl that = (PartDefinitionImpl) o;

        return place.equals( that.place );
    }

    @Override
    public String toString() {
        return "PartDefinitionImpl [place=" + place + "]";
    }

}
