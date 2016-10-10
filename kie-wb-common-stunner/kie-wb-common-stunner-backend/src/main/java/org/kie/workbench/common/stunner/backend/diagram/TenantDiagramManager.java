/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.diagram;

import org.kie.workbench.common.stunner.core.api.DiagramManager;
import org.kie.workbench.common.stunner.core.backend.annotation.Application;
import org.kie.workbench.common.stunner.core.backend.annotation.Tenant;
import org.kie.workbench.common.stunner.core.diagram.Diagram;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;

/**
 * The diagram service for each tenant.
 */
@SessionScoped
@Tenant
public class TenantDiagramManager implements DiagramManager<Diagram>, Serializable {

    DiagramManager<Diagram> appDiagramManager;

    protected TenantDiagramManager() {
        this( null );
    }

    @Inject
    public TenantDiagramManager( @Application DiagramManager<Diagram> appDiagramManager ) {
        this.appDiagramManager = appDiagramManager;
    }

    @Override
    public void update( Diagram diagram ) {
        appDiagramManager.update( diagram );
    }

    @Override
    public void register( Diagram diagram ) {
        appDiagramManager.register( diagram );
    }

    @Override
    public boolean contains( Diagram item ) {
        return appDiagramManager.contains( item );
    }

    @Override
    public boolean remove( Diagram diagram ) {
        return appDiagramManager.remove( diagram );
    }

    @Override
    public Diagram getDiagramByUUID( String uuid ) {
        return appDiagramManager.getDiagramByUUID( uuid );
    }

    @Override
    public Collection<Diagram> getItems() {
        return appDiagramManager.getItems();
    }

    @Override
    public void clear() {
        appDiagramManager.clear();
    }

}
