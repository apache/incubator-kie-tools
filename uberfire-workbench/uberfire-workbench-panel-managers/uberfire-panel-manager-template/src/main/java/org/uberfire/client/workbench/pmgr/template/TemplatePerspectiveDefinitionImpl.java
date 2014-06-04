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
package org.uberfire.client.workbench.pmgr.template;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.TemplatePerspectiveActivity;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import static org.uberfire.workbench.model.ContextDisplayMode.*;

/**
 * Implementation of TemplatePerspectiveDefinition
 */
@Portable
public class TemplatePerspectiveDefinitionImpl
        implements
        PerspectiveDefinition {

    private transient PerspectiveActivity perspective;
    private String name;

    private boolean isTransient = false;

    private PanelDefinition root;
    private ContextDefinition contextDefinition;
    private ContextDisplayMode contextDisplayMode = SHOW;

    public TemplatePerspectiveDefinitionImpl() {
        this.root = new PanelDefinitionImpl( PanelType.TEMPLATE );
    }

    public TemplatePerspectiveDefinitionImpl( final TemplatePerspectiveActivity perspective,
                                              String fieldName,
                                              String name ) {
        this.perspective = perspective;
        this.root = new TemplatePanelDefinitionImpl( perspective, PanelType.TEMPLATE, fieldName );
        setName( name );
    }

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public void setTransient( boolean isTransient ) {
        this.isTransient = isTransient;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( final String name ) {
        this.name = name;
    }

    @Override
    public PanelDefinition getRoot() {
        return root;
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
    public String toString() {
        return "TemplatePerspectiveDefinitionImpl [name=" + name + ", contextDefinition=" + contextDefinition
                + ", contextDisplayMode=" + contextDisplayMode + "]";
    }

}
