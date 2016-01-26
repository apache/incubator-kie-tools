/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class ScenarioTestEditorServiceImpl
        extends KieService<TestScenarioModelContent>
        implements ScenarioTestEditorService {

    @Inject
    @Named( "ioStrategy" )
    private IOService ioService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private ScenarioRunnerService scenarioRunner;

    @Inject
    private CommentedOptionFactory commentedOptionFactory;
    private SafeSessionInfo        safeSessionInfo;

    public ScenarioTestEditorServiceImpl() {
    }

    @Inject
    public ScenarioTestEditorServiceImpl( final SessionInfo sessionInfo ) {
        safeSessionInfo = new SafeSessionInfo( sessionInfo );
    }

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final Scenario content,
                        final String comment ) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
            final Path newPath = Paths.convert( nioPath );

            if ( ioService.exists( nioPath ) ) {
                throw new FileAlreadyExistsException( nioPath.toString() );
            }

            ioService.write( nioPath,
                             ScenarioXMLPersistence.getInstance().marshal( content ),
                             commentedOptionFactory.makeCommentedOption( comment ) );

            return newPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Scenario load( final Path path ) {
        try {
            final String content = ioService.readAllString( Paths.convert( path ) );

            Scenario scenario = ScenarioXMLPersistence.getInstance().unmarshal( content );
            scenario.setName( path.getFileName() );

            return scenario;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path save( final Path resource,
                      final Scenario content,
                      final Metadata metadata,
                      final String comment ) {
        try {
            Metadata currentMetadata = metadataService.getMetadata( resource );
            ioService.write( Paths.convert( resource ),
                             ScenarioXMLPersistence.getInstance().marshal( content ),
                             metadataService.setUpAttributes( resource,
                                                              metadata ),
                             commentedOptionFactory.makeCommentedOption( comment ) );

            fireMetadataSocialEvents( resource, currentMetadata, metadata );
            return resource;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        try {
            deleteService.delete( path,
                                  comment );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        try {
            return renameService.rename( path,
                                         newName,
                                        comment);

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    comment);

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public TestScenarioModelContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    protected TestScenarioModelContent constructContent(Path path, Overview overview) {
        final Scenario scenario = load(path);
        final String packageName = projectService.resolvePackage(path).getPackageName();
        final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();

        //Get FQCN's used by model
        final TestScenarioModelVisitor visitor = new TestScenarioModelVisitor(dataModel, scenario);
        final Set<String> consumedFQCNs = visitor.visit();

        //Get FQCN's used by Globals
        consumedFQCNs.addAll(oracle.getPackageGlobals().values());

        DataModelOracleUtilities.populateDataModel(oracle,
                                                   dataModel,
                                                   consumedFQCNs);

        //Signal opening to interested parties
        resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                         safeSessionInfo));

        return new TestScenarioModelContent(scenario,
                                            overview,
                                            packageName,
                                            dataModel);
    }

    @Override
    public TestScenarioResult runScenario(final Path path,
                                          final Scenario scenario) {
        try {
            return scenarioRunner.run(
                    scenario,
                    projectService.resolveProject(path));

        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

}
