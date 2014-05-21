/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
package org.drools.workbench.screens.testscenario.backend.server.indexing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.type.TestScenarioResourceTypeDefinition;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.metadata.engine.Indexer;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;

@ApplicationScoped
public class TestScenarioFileIndexer implements Indexer {

    private static final Logger logger = LoggerFactory.getLogger( TestScenarioFileIndexer.class );

    @Inject
    @Named("ioStrategy")
    protected IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    protected TestScenarioResourceTypeDefinition type;

    @Override
    public boolean supportsPath( final Path path ) {
        return type.accept( Paths.convert( path ) );
    }

    @Override
    public KObject toKObject( final Path path ) {
        KObject index = null;

        try {
            final String content = ioService.readAllString( path );
            final Scenario model = ScenarioXMLPersistence.getInstance().unmarshal( content );

            final ProjectDataModelOracle dmo = getProjectDataModelOracle( path );
            final DefaultIndexBuilder builder = new DefaultIndexBuilder( );
            final TestScenarioIndexVisitor visitor = new TestScenarioIndexVisitor( dmo,
                                                                                   builder,
                                                                                   model );
            visitor.visit();

            index = KObjectUtil.toKObject( path,
                                           builder.build() );

            return index;

        } catch ( Exception e ) {
            logger.error( "Unable to index '" + path.toUri().toString() + "'.",
                          e.getMessage() );
        }
        return index;
    }

    @Override
    public KObjectKey toKObjectKey( final Path path ) {
        return KObjectUtil.toKObjectKey( path );
    }

    //Delegate resolution of package name to method to assist testing
    protected String getPackageName( final Path path ) {
        return projectService.resolvePackage( Paths.convert( path ) ).getPackageName();
    }

    //Delegate resolution of DMO to method to assist testing
    protected ProjectDataModelOracle getProjectDataModelOracle( final Path path ) {
        return dataModelService.getProjectDataModel( Paths.convert( path ) );
    }

}
