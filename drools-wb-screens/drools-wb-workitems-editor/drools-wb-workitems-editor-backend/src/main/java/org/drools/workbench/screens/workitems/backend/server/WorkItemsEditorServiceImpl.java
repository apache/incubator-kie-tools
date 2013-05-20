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

package org.drools.workbench.screens.workitems.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.core.process.core.ParameterDefinition;
import org.drools.core.process.core.WorkDefinition;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.process.core.datatype.impl.type.BooleanDataType;
import org.drools.core.process.core.datatype.impl.type.FloatDataType;
import org.drools.core.process.core.datatype.impl.type.IntegerDataType;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.process.core.datatype.impl.type.StringDataType;
import org.drools.workbench.models.commons.shared.workitems.PortableBooleanParameterDefinition;
import org.drools.workbench.models.commons.shared.workitems.PortableFloatParameterDefinition;
import org.drools.workbench.models.commons.shared.workitems.PortableIntegerParameterDefinition;
import org.drools.workbench.models.commons.shared.workitems.PortableObjectParameterDefinition;
import org.drools.workbench.models.commons.shared.workitems.PortableParameterDefinition;
import org.drools.workbench.models.commons.shared.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.commons.shared.workitems.PortableWorkDefinition;
import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;
import org.drools.workbench.screens.workitems.model.WorkItemsModelContent;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.workbench.common.services.shared.validation.model.BuilderResult;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.backend.file.FileDiscoveryService;
import org.kie.workbench.common.services.backend.file.FileExtensionsFilter;
import org.kie.workbench.common.services.shared.exceptions.GenericPortableException;
import org.kie.workbench.common.services.shared.file.CopyService;
import org.kie.workbench.common.services.shared.file.DeleteService;
import org.kie.workbench.common.services.shared.file.RenameService;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.ResourceAddedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceOpenedEvent;
import org.uberfire.client.workbench.widgets.events.ResourceUpdatedEvent;
import org.uberfire.security.Identity;

@Service
@ApplicationScoped
public class WorkItemsEditorServiceImpl implements WorkItemsEditorService {

    private static final Logger log = LoggerFactory.getLogger( WorkItemsEditorServiceImpl.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private CopyService copyService;

    @Inject
    private DeleteService deleteService;

    @Inject
    private RenameService renameService;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    private ResourceWorkDefinitionsLoader resourceWorkDefinitionsLoader;

    @Inject
    private ConfigWorkDefinitionsLoader configWorkDefinitionsLoader;

    @Inject
    private ProjectService projectService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    private WorkItemDefinitionElements workItemDefinitionElements;
    private FileExtensionsFilter imageFilter = new FileExtensionsFilter( new String[]{ "png", "gif", "jpg" } );

    @PostConstruct
    public void setupWorkItemDefinitionElements() {
        workItemDefinitionElements = new WorkItemDefinitionElements( loadWorkItemDefinitionElements() );
    }

    private Map<String, String> loadWorkItemDefinitionElements() {
        final Map<String, String> workItemDefinitionElements = new HashMap<String, String>();
        final List<ConfigGroup> editorConfigGroups = configurationService.getConfiguration( ConfigType.EDITOR );
        for ( ConfigGroup editorConfigGroup : editorConfigGroups ) {
            if ( WORK_ITEMS_EDITOR_SETTINGS.equals( editorConfigGroup.getName() ) ) {
                for ( ConfigItem item : editorConfigGroup.getItems() ) {
                    final String itemName = item.getName();
                    final String itemValue = editorConfigGroup.getConfigItemValue( itemName );
                    workItemDefinitionElements.put( itemName,
                                                    itemValue );
                }
            }
        }
        return workItemDefinitionElements;
    }

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final String content,
                        final String comment ) {
        //Get the template for new Work Item Definitions, stored as a configuration item
        String defaultDefinition = workItemDefinitionElements.getDefinitionElements().get( WORK_ITEMS_EDITOR_SETTINGS_DEFINITION );
        if ( defaultDefinition == null ) {
            defaultDefinition = "";
        }
        defaultDefinition.replaceAll( "\\|",
                                      "" );

        //Write file to VFS
        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( context ).resolve( fileName );
        final Path newPath = paths.convert( nioPath,
                                            false );

        ioService.createFile( nioPath );
        ioService.write( nioPath,
                         defaultDefinition,
                         makeCommentedOption( comment ) );

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

        return newPath;
    }

    @Override
    public String load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return content;
    }

    @Override
    public WorkItemsModelContent loadContent( final Path path ) {
        final String definition = load( path );
        final List<String> workItemImages = loadWorkItemImages( path );
        return new WorkItemsModelContent( definition,
                                          workItemImages );
    }

    private List<String> loadWorkItemImages( final Path resourcePath ) {
        final Path projectRoot = projectService.resolveProject( resourcePath );
        final org.kie.commons.java.nio.file.Path nioProjectPath = paths.convert( projectRoot );
        final org.kie.commons.java.nio.file.Path nioResourceParent = paths.convert( resourcePath ).getParent();

        final Collection<org.kie.commons.java.nio.file.Path> imagePaths = fileDiscoveryService.discoverFiles( nioProjectPath,
                                                                                                              imageFilter,
                                                                                                              true );
        final List<String> images = new ArrayList<String>();
        for ( org.kie.commons.java.nio.file.Path imagePath : imagePaths ) {
            final org.kie.commons.java.nio.file.Path relativePath = nioResourceParent.relativize( imagePath );
            images.add( relativePath.toString() );
        }
        return images;
    }

    @Override
    public WorkItemDefinitionElements loadDefinitionElements() {
        return workItemDefinitionElements;
    }

    @Override
    public Path save( final Path resource,
                      final String content,
                      final Metadata metadata,
                      final String comment ) {
        ioService.write( paths.convert( resource ),
                         content,
                         metadataService.setUpAttributes( resource,
                                                          metadata ),
                         makeCommentedOption( comment ) );

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

    @Override
    public BuilderResult validate( final Path path,
                                   final String content ) {
        //TODO {porcelli} validate
        return new BuilderResult();
    }

    @Override
    public boolean isValid( final Path path,
                            final String content ) {
        return !validate( path, content ).hasLines();
    }

    @Override
    public Set<PortableWorkDefinition> loadWorkItemDefinitions( final Path path ) {
        final Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();

        //Load WorkItemDefinitions from VFS
        try {
            final Path projectRoot = projectService.resolveProject( path );
            workDefinitions.putAll( resourceWorkDefinitionsLoader.loadWorkDefinitions( projectRoot ) );
        } catch ( Exception e ) {
            log.error( e.getMessage(),
                       e );
            throw new GenericPortableException( e.getMessage() );
        }

        //Load WorkItemDefinitions from ConfigurationService
        try {
            workDefinitions.putAll( configWorkDefinitionsLoader.loadWorkDefinitions() );
        } catch ( Exception e ) {
            log.error( e.getMessage(),
                       e );
            throw new GenericPortableException( e.getMessage() );
        }

        //Copy the Work Items into Structures suitable for GWT
        final Set<PortableWorkDefinition> workItems = new HashSet<PortableWorkDefinition>();
        for ( Map.Entry<String, WorkDefinition> entry : workDefinitions.entrySet() ) {
            final PortableWorkDefinition wid = new PortableWorkDefinition();
            final WorkDefinitionImpl wd = (WorkDefinitionImpl) entry.getValue();
            wid.setName( wd.getName() );
            wid.setDisplayName( wd.getDisplayName() );
            wid.setParameters( convertWorkItemParameters( entry.getValue().getParameters() ) );
            wid.setResults( convertWorkItemParameters( entry.getValue().getResults() ) );
            workItems.add( wid );
        }
        return workItems;
    }

    private Set<PortableParameterDefinition> convertWorkItemParameters( final Set<ParameterDefinition> parameters ) {
        final Set<PortableParameterDefinition> pps = new HashSet<PortableParameterDefinition>();
        for ( ParameterDefinition pd : parameters ) {
            final DataType pdt = pd.getType();
            PortableParameterDefinition ppd = null;
            if ( pdt instanceof BooleanDataType ) {
                ppd = new PortableBooleanParameterDefinition();
            } else if ( pdt instanceof FloatDataType ) {
                ppd = new PortableFloatParameterDefinition();
            } else if ( pdt instanceof IntegerDataType ) {
                ppd = new PortableIntegerParameterDefinition();
            } else if ( pdt instanceof ObjectDataType ) {
                ppd = new PortableObjectParameterDefinition();
                final PortableObjectParameterDefinition oppd = (PortableObjectParameterDefinition) ppd;
                final ObjectDataType odt = (ObjectDataType) pdt;
                oppd.setClassName( odt.getClassName() );
            } else if ( pd.getType() instanceof StringDataType ) {
                ppd = new PortableStringParameterDefinition();
            }
            if ( ppd != null ) {
                ppd.setName( pd.getName() );
                pps.add( ppd );
            }
        }
        return pps;
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }
}
