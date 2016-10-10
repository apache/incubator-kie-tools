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

package org.kie.workbench.common.stunner.backend.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.api.DiagramManager;
import org.kie.workbench.common.stunner.core.backend.annotation.Tenant;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.remote.DiagramService;

import javax.inject.Inject;
import java.util.Collection;

@Service
public class TenantDiagramService implements DiagramService {

    DiagramManager<Diagram> tenantDiagramManager;

    @Inject
    public TenantDiagramService( @Tenant DiagramManager<Diagram> tenantDiagramManager ) {
        this.tenantDiagramManager = tenantDiagramManager;
    }

    @Override
    public void saveOrUpdate( Diagram diagram ) {
        if ( contains( diagram ) ) {
            update( diagram );

        } else {
            register( diagram );

        }

    }

    @Override
    public void update( Diagram diagram ) {
        tenantDiagramManager.update( diagram );
    }

    @Override
    public void register( Diagram diagram ) {
        tenantDiagramManager.register( diagram );
    }

    @Override
    public boolean contains( Diagram diagram ) {
        return tenantDiagramManager.contains( diagram );
    }

    @Override
    public boolean remove( Diagram diagram ) {
        return tenantDiagramManager.remove( diagram );
    }

    @Override
    public Diagram getDiagramByUUID( String criteria ) {
        return tenantDiagramManager.getDiagramByUUID( criteria );
    }

    @Override
    public Collection<Diagram> getItems() {
        return tenantDiagramManager.getItems();
    }

    @Override
    public void clear() {
        tenantDiagramManager.clear();
    }

}
