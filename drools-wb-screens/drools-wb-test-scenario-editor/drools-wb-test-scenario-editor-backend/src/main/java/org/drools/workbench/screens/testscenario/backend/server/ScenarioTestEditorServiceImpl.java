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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.testscenarios.backend.util.ScenarioXMLPersistence;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.model.TestResultMessage;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.workbench.common.services.backend.session.SessionService;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOPackageCacheEvent;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.workbench.common.services.backend.file.FileExtensionFilter;
import org.kie.workbench.common.services.backend.file.LinkedDotFileFilter;
import org.kie.workbench.common.services.backend.file.LinkedFilter;
import org.kie.workbench.common.services.backend.file.LinkedMetaInfFolderFilter;
import org.kie.workbench.common.services.shared.file.CopyService;
import org.kie.workbench.common.services.shared.file.DeleteService;
import org.kie.workbench.common.services.shared.file.RenameService;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceOpenedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

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
    private Event<InvalidateDMOPackageCacheEvent> invalidatePackageDMOEvent;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Event<TestResultMessage> testResultMessageEvent;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final Scenario content,
                        final String comment ) {
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( context ).resolve( fileName );
        final Path newPath = paths.convert( nioPath,
                                            false );

        ioService.createFile( nioPath );
        ioService.write( nioPath,
                         ScenarioXMLPersistence.getInstance().marshal( content ),
                         makeCommentedOption( comment ) );

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

        return newPath;
    }

    @Override
    public Scenario load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return ScenarioXMLPersistence.getInstance().unmarshal( content );
    }

    @Override
    public Path save( final Path resource,
                      final Scenario content,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         ScenarioXMLPersistence.getInstance().marshal( content ),
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

        //Invalidate Package-level DMO cache as Globals have changed.
        invalidatePackageDMOEvent.fire( new InvalidateDMOPackageCacheEvent( resource ) );

        //Signal update to interested parties
        resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

        return resource;
    }

    @Override
    public void delete( final Path path,
                        final String comment ) {
        deleteService.delete( path,
                              comment );
    }

    @Override
    public Path rename( final Path path,
                        final String newName,
                        final String comment ) {
        return renameService.rename( path,
                                     newName,
                                     comment );
    }

    @Override
    public Path copy( final Path path,
                      final String newName,
                      final String comment ) {
        return copyService.copy( path,
                                 newName,
                                 comment );
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        return new CommentedOption( name,
                                    null,
                                    commitMessage,
                                    when );
    }

    @Override
    public TestScenarioModelContent loadContent( Path path ) {
        return new TestScenarioModelContent(
                load( path ),
                dataModelService.getDataModel( path ),
                projectService.resolvePackageName( path ) );
    }
    
    @Override
    public void runScenario(Path path, Scenario scenario, String sessionName) {

        Path pathToPom = projectService.resolvePathToPom(path);

        new ScenarioRunnerWrapper().run(scenario,
                sessionService.newKieSession(pathToPom, sessionName),
                testResultMessageEvent);
    }
    
    @Override
    public void runAllScenarios(Path testResourcePath, String sessionName) {
        Path pathToPom = projectService.resolvePathToPom(testResourcePath);
        List<Path> scenarioPaths = loadScenarioPaths(testResourcePath);
        List<Scenario> scenarios = new ArrayList<Scenario>();
        for(Path path : scenarioPaths) {
            Scenario s = load(path);
            scenarios.add(s);
        }
        
        new ScenarioRunnerWrapper().run(scenarios,
                sessionService.newKieSession(pathToPom, sessionName),
                testResultMessageEvent);
    }
    
    public List<Path> loadScenarioPaths(final Path path) {      
        // Check Path exists
        final List<Path> items = new ArrayList<Path>();
        if (!Files.exists(paths.convert(path))) {
            return items;
        }

        // Ensure Path represents a Folder
        org.kie.commons.java.nio.file.Path pPath = paths.convert(path);
        if (!Files.isDirectory(pPath)) {
            pPath = pPath.getParent();
        }

        LinkedFilter filter =  new LinkedDotFileFilter();
        LinkedFilter metaInfFolderFilter = new LinkedMetaInfFolderFilter();
        filter.setNextFilter(metaInfFolderFilter);
        FileExtensionFilter fileExtensionFilter = new FileExtensionFilter(".scenario");     

        // Get list of immediate children
        final DirectoryStream<org.kie.commons.java.nio.file.Path> directoryStream = ioService.newDirectoryStream(pPath);
        for (final org.kie.commons.java.nio.file.Path p : directoryStream) {          
            if (filter.accept(p) && fileExtensionFilter.accept(p)) {
                if (Files.isRegularFile(p)) {
                    items.add(paths.convert(p));
                } else if (Files.isDirectory(p)) {
                    items.add(paths.convert(p));
                }
            }
        }

        // Add ability to move up one level in the hierarchy
        //items.add(new ParentPackageItem(paths.convert(pPath.getParent()), ".."));

        return items;
    }

}
