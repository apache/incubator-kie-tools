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
import org.drools.workbench.screens.workitems.type.WorkItemsTypeDefinition;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.backend.file.FileExtensionsFilter;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.builder.BuildMessage;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

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

    @Inject
    private WorkItemsTypeDefinition resourceTypeDefinition;

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
        try {
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

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public String load( final Path path ) {
        try {
            final String content = ioService.readAllString( paths.convert( path ) );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

            return content;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public WorkItemsModelContent loadContent( final Path path ) {
        try {
            final String definition = load( path );
            final List<String> workItemImages = loadWorkItemImages( path );
            return new WorkItemsModelContent( definition,
                                              workItemImages );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private List<String> loadWorkItemImages( final Path resourcePath ) {
        final Path projectRoot = projectService.resolveProject( resourcePath ).getRootPath();
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
        try {
            ioService.write( paths.convert( resource ),
                             content,
                             metadataService.setUpAttributes( resource,
                                                              metadata ),
                             makeCommentedOption( comment ) );

            //Signal update to interested parties
            resourceUpdatedEvent.fire( new ResourceUpdatedEvent( resource ) );

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

    @Override
    public boolean accepts( final Path path ) {
        return resourceTypeDefinition.accept( path );
    }

    @Override
    public List<BuildMessage> validate( final Path path ) {
        try {
            final String content = ioService.readAllString( paths.convert( path ) );
            final List<BuildMessage> messages = doValidation( content );
            for ( BuildMessage msg : messages ) {
                msg.setPath( path );
            }
            return messages;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public List<BuildMessage> validate( final String content ) {
        return doValidation( content );
    }

    private List<BuildMessage> doValidation( final String content ) {
        final List<BuildMessage> messages = new ArrayList<BuildMessage>();
        try {
            MVEL.eval( content,
                       new HashMap() );
        } catch ( Exception e ) {
            final BuildMessage msg = new BuildMessage();
            msg.setLevel( BuildMessage.Level.ERROR );
            msg.setText( e.getMessage() );
            messages.add( msg );
        }
        return messages;
    }

    @Override
    public boolean isValid( final String content ) {
        return validate( content ).isEmpty();
    }

    @Override
    public Set<PortableWorkDefinition> loadWorkItemDefinitions( final Path path ) {
        final Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();

        try {
            //Load WorkItemDefinitions from VFS
            final Path projectRoot = projectService.resolveProject( path ).getRootPath();
            workDefinitions.putAll( resourceWorkDefinitionsLoader.loadWorkDefinitions( projectRoot ) );

            //Load WorkItemDefinitions from ConfigurationService
            workDefinitions.putAll( configWorkDefinitionsLoader.loadWorkDefinitions() );

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

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
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
