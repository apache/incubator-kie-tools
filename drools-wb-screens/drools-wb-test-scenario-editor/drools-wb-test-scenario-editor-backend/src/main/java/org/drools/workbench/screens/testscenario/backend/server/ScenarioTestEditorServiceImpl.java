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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestResultMessage;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.FileExtensionFilter;
import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.backend.file.LinkedFilter;
import org.guvnor.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.session.SessionService;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class ScenarioTestEditorServiceImpl
        implements ScenarioTestEditorService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private SessionService sessionService;

    @Inject
    private ProjectService projectService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<TestResultMessage> testResultMessageEvent;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private ConfigurationService configurationService;

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
                             makeCommentedOption( comment ) );

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
            ioService.write( Paths.convert( resource ),
                             ScenarioXMLPersistence.getInstance().marshal( content ),
                             metadataService.setUpAttributes( resource,
                                                              metadata ),
                             makeCommentedOption( comment ) );

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
                                         comment );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        try {
            return copyService.copy( path,
                                     newName,
                                     comment );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        return new CommentedOption( sessionInfo.getId(),
                                    name,
                                    null,
                                    commitMessage,
                                    when );
    }

    @Override
    public TestScenarioModelContent loadContent( Path path ) {
        try {
            final Scenario scenario = load( path );
            final String packageName = projectService.resolvePackage( path ).getPackageName();
            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();

            //Get FQCN's used by model
            final TestScenarioModelVisitor visitor = new TestScenarioModelVisitor( dataModel, scenario );
            final Set<String> consumedFQCNs = visitor.visit();

            //Get FQCN's used by Globals
            consumedFQCNs.addAll( oracle.getPackageGlobals().values() );

            DataModelOracleUtilities.populateDataModel( oracle,
                                                        dataModel,
                                                        consumedFQCNs );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path,
                                                               sessionInfo ) );

            return new TestScenarioModelContent( scenario,
                                                 packageName,
                                                 dataModel );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public void runScenario( final Path path,
                             final Scenario scenario ) {
        try {

            final Project project = projectService.resolveProject( path );
            final KieSession session = sessionService.newKieSession( project );
            final ScenarioRunnerWrapper runner = new ScenarioRunnerWrapper( testResultMessageEvent,
                                                                            getMaxRuleFirings() );

            runner.run( scenario,
                        session );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private int getMaxRuleFirings() {
        for ( ConfigGroup editorConfigGroup : configurationService.getConfiguration( ConfigType.EDITOR ) ) {
            if ( ScenarioTestEditorService.TEST_SCENARIO_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                for ( ConfigItem item : editorConfigGroup.getItems() ) {
                    String itemName = item.getName();
                    if ( itemName.equals( ScenarioTestEditorService.TEST_SCENARIO_EDITOR_MAX_RULE_FIRINGS ) ) {
                        return (Integer) item.getValue();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public void runAllScenarios( final Path testResourcePath ) {
        runAllScenarios( testResourcePath,
                         testResultMessageEvent );
    }

    //@Override
    public void runAllScenarios( final Path testResourcePath,
                                 Event<TestResultMessage> customTestResultEvent ) {
        try {
            final Project project = projectService.resolveProject( testResourcePath );
            List<Path> scenarioPaths = loadScenarioPaths( testResourcePath );
            List<Scenario> scenarios = new ArrayList<Scenario>();
            for ( Path path : scenarioPaths ) {
                Scenario s = load( path );
                scenarios.add( s );
            }

            new ScenarioRunnerWrapper( testResultMessageEvent, getMaxRuleFirings() ).run(
                    scenarios,
                    sessionService.newKieSession( project )
                                                                                        );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    public List<Path> loadScenarioPaths( final Path path ) {
        try {
            // Check Path exists
            final List<Path> items = new ArrayList<Path>();
            if ( !Files.exists( Paths.convert( path ) ) ) {
                return items;
            }

            // Ensure Path represents a Folder
            org.uberfire.java.nio.file.Path pPath = Paths.convert( path );
            if ( !Files.isDirectory( pPath ) ) {
                pPath = pPath.getParent();
            }

            LinkedFilter filter = new LinkedDotFileFilter();
            LinkedFilter metaInfFolderFilter = new LinkedMetaInfFolderFilter();
            filter.setNextFilter( metaInfFolderFilter );
            FileExtensionFilter fileExtensionFilter = new FileExtensionFilter( ".scenario" );

            // Get list of immediate children
            final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream = ioService.newDirectoryStream( pPath );
            for ( final org.uberfire.java.nio.file.Path p : directoryStream ) {
                if ( filter.accept( p ) && fileExtensionFilter.accept( p ) ) {
                    if ( Files.isRegularFile( p ) ) {
                        items.add( Paths.convert( p ) );
                    } else if ( Files.isDirectory( p ) ) {
                        items.add( Paths.convert( p ) );
                    }
                }
            }

            // Add ability to move up one level in the hierarchy
            //items.add(new ParentPackageItem(Paths.convert(pPath.getParent()), ".."));

            return items;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
