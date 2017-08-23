/*
 * Copyright 2017 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.query.response;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPathPageRow;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;

@ApplicationScoped
public class FileDetailsResponseBuilder
        implements ResponseBuilder {

    private IOService ioService;

    public FileDetailsResponseBuilder() {
        //Make proxyable
    }

    @Inject
    public FileDetailsResponseBuilder(@Named("ioStrategy") final IOService ioService ) {
        this.ioService = PortablePreconditions.checkNotNull( "ioService",
                                                             ioService );
    }

    @Override
    public PageResponse<RefactoringPageRow> buildResponse( final int pageSize,
                                                           final int startRow,
                                                           final List<KObject> kObjects ) {
        final int hits = kObjects.size();
        final PageResponse<RefactoringPageRow> response = new PageResponse<RefactoringPageRow>();
        final List<RefactoringPageRow> result = buildResponse( kObjects );
        response.setTotalRowSize( hits );
        response.setPageRowList( result );
        response.setTotalRowSizeExact( true );
        response.setStartRowIndex( startRow );
        response.setLastPage( ( pageSize * startRow + 2 ) >= hits );

        return response;
    }

    @Override
    public List<RefactoringPageRow> buildResponse( final List<KObject> kObjects ) {
        final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>( kObjects.size() );
        for ( final KObject kObject : kObjects ) {
            final Path path = Paths.convert( ioService.get( URI.create( kObject.getKey() ) ) );
            final RefactoringPathPageRow row = new RefactoringPathPageRow();
            row.setValue( path );
            result.add( row );
        }
        return result;
    }
}
