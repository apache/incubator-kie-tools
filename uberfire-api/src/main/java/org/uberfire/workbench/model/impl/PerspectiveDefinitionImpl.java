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

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.workbench.model.ContextDisplayMode.SHOW;
import static org.uberfire.workbench.model.impl.PanelTypeHelper.isRoot;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;

/**
 * Default implementation of PerspectiveDefinition
 */
@Portable
public class PerspectiveDefinitionImpl
        implements
        PerspectiveDefinition {

    private String name;

    private boolean isTransient = false;

    private PanelDefinition root;
    private ContextDefinition contextDefinition;
    private ContextDisplayMode contextDisplayMode = SHOW;

    public PerspectiveDefinitionImpl() {
        this.root = new PanelDefinitionImpl( PanelType.ROOT_TAB );
    }

    public PerspectiveDefinitionImpl( final PanelType type ) {
        checkNotNull( "type", type );
        if ( !isRoot( type ) ) {
            throw new IllegalArgumentException( "Perspective requires a 'ROOT' panel type, but got '" + type.name() + "' instead!" );
        }

        this.root = new PanelDefinitionImpl( type );
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
      return "PerspectiveDefinitionImpl [name=" + name + ", contextDefinition=" + contextDefinition
              + ", contextDisplayMode=" + contextDisplayMode + "]";
    }

}
