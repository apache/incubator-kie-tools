/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.defaulteditor.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class DefaultEditorServiceImpl
        extends KieService
        implements DefaultEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Override
    public Path save( final Path resource,
                      final String content,
                      final Metadata metadata,
                      final String comment ) {
        try {
            ioService.write( Paths.convert( resource ),
                             content,
                             metadataService.setUpAttributes( resource,
                                                              metadata ),
                             makeCommentedOption( comment ) );

            return resource;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Overview loadOverview( final ObservablePath path ) {
        return super.loadOverview( path );
    }
}
