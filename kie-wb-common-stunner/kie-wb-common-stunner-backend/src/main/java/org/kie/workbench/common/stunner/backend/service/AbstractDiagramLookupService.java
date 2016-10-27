/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.service;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.core.backend.lookup.impl.AbstractVFSLookupManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentationImpl;
import org.kie.workbench.common.stunner.core.service.BaseDiagramService;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import java.util.List;
import java.util.Map;

public abstract class AbstractDiagramLookupService<D extends Diagram>
        extends AbstractVFSLookupManager<D, DiagramRepresentation, DiagramLookupRequest>
        implements DiagramLookupManager, DiagramLookupService {

    private static final Logger LOG =
            LoggerFactory.getLogger( AbstractDiagramLookupService.class.getName() );

    private final BaseDiagramService<D> diagramService;

    public AbstractDiagramLookupService( IOService ioService,
                                         BaseDiagramService<D> diagramService ) {
        super( ioService );
        this.diagramService = diagramService;
    }

    @Override
    protected boolean acceptsPath( Path path ) {
        return diagramService.accepts( path );
    }

    @Override
    protected D getItemByPath( Path path ) {
        return diagramService.getDiagramByPath( path );
    }

    @Override
    protected List<D> getItems( DiagramLookupRequest request ) {
        org.uberfire.java.nio.file.Path root = parseCriteriaPath( request );
        return getItemsByPath( root );
    }

    @Override
    protected boolean matches( String criteria, Diagram item ) {
        return true;
    }

    @Override
    protected DiagramRepresentation buildResult( Diagram item ) {
        return new DiagramRepresentationImpl.DiagramRepresentationBuilder( item ).build();
    }

    protected org.uberfire.java.nio.file.Path parseCriteriaPath( DiagramLookupRequest request ) {
        String criteria = request.getCriteria();
        if ( StringUtils.isEmpty( criteria ) ) {
            LOG.error( "Empty criteria not supported." );
            throw new UnsupportedOperationException( "Empty criteria not supported." );
        } else {
            Map<String, String> criteriaMap = AbstractCriteriaLookupManager.parseCriteria( criteria );
            String pathRaw = criteriaMap.get( "path" );
            if ( !StringUtils.isEmpty( pathRaw ) ) {
                // TODO: Still not need, here should parse the path criteria value and create and return an instance
                // of org.uberfire.java.nio.file.Path for it.
                return null;
            } else {
                LOG.error( "No path criteria found.." );
                throw new UnsupportedOperationException( "No path criteria found." );
            }
        }
    }

    protected BaseDiagramService<D> getDiagramService() {
        return diagramService;
    }

}
