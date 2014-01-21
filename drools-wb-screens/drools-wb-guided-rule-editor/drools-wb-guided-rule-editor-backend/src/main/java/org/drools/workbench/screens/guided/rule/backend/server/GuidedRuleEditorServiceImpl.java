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

package org.drools.workbench.screens.guided.rule.backend.server;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.model.GuidedEditorContent;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.file.CopyService;
import org.guvnor.common.services.shared.file.DeleteService;
import org.guvnor.common.services.shared.file.RenameService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.file.DslFileFilter;
import org.kie.workbench.common.services.backend.file.GlobalsFileFilter;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class GuidedRuleEditorServiceImpl implements GuidedRuleEditorService {

    private static final JavaFileFilter FILTER_JAVA = new JavaFileFilter();

    private static final DslFileFilter FILTER_DSLS = new DslFileFilter();

    private static final GlobalsFileFilter FILTER_GLOBALS = new GlobalsFileFilter();

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
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    private ProjectService projectService;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private GenericValidator genericValidator;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final RuleModel model,
                        final String comment ) {
        try {
            final Package pkg = projectService.resolvePackage( context );
            final String packageName = ( pkg == null ? null : pkg.getPackageName() );
            model.setPackageName( packageName );

            // Temporal fix for https://bugzilla.redhat.com/show_bug.cgi?id=998922
            model.getImports().addImport( new Import( "java.lang.Number" ) );

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
            final Path newPath = Paths.convert( nioPath );

            ioService.createFile( nioPath );
            ioService.write( nioPath,
                             toSource( newPath,
                                       model ),
                             makeCommentedOption( comment ) );

            return newPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public RuleModel load( final Path path ) {
        try {
            final String drl = ioService.readAllString( Paths.convert( path ) );
            final String[] dsls = loadDslsForPackage( path );
            final List<String> globals = loadGlobalsForPackage( path );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path,
                                                               sessionInfo ) );

            return RuleModelDRLPersistenceImpl.getInstance().unmarshalUsingDSL( drl,
                                                                                globals,
                                                                                dataModelService.getDataModel( path ),
                                                                                dsls );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public GuidedEditorContent loadContent( final Path path ) {
        try {
            final RuleModel model = load( path );

            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
            final GuidedRuleModelVisitor visitor = new GuidedRuleModelVisitor( model );
            DataModelOracleUtilities.populateDataModel( oracle,
                                                        dataModel,
                                                        visitor.getConsumedModelClasses() );

            return new GuidedEditorContent( model,
                                            dataModel );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private String[] loadDslsForPackage( final Path path ) {
        final List<String> dsls = new ArrayList<String>();
        final Path packagePath = projectService.resolvePackage( path ).getPackageMainResourcesPath();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( packagePath );
        final Collection<org.uberfire.java.nio.file.Path> dslPaths = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                         FILTER_DSLS );
        for ( final org.uberfire.java.nio.file.Path dslPath : dslPaths ) {
            final String dslDefinition = ioService.readAllString( dslPath );
            dsls.add( dslDefinition );
        }
        final String[] result = new String[ dsls.size() ];
        return dsls.toArray( result );
    }

    private List<String> loadGlobalsForPackage( final Path path ) {
        final List<String> globals = new ArrayList<String>();
        final Path packagePath = projectService.resolvePackage( path ).getPackageMainResourcesPath();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert( packagePath );
        final Collection<org.uberfire.java.nio.file.Path> globalPaths = fileDiscoveryService.discoverFiles( nioPackagePath,
                                                                                                            FILTER_GLOBALS );
        for ( final org.uberfire.java.nio.file.Path globalPath : globalPaths ) {
            final String globalDefinition = ioService.readAllString( globalPath );
            globals.add( globalDefinition );
        }
        return globals;
    }

    @Override
    public Path save( final Path resource,
                      final RuleModel model,
                      final Metadata metadata,
                      final String comment ) {
        try {
            final Package pkg = projectService.resolvePackage( resource );
            final String packageName = ( pkg == null ? null : pkg.getPackageName() );
            model.setPackageName( packageName );

            ioService.write( Paths.convert( resource ),
                             RuleModelDRLPersistenceImpl.getInstance().marshal( model ),
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

    @Override
    public String toSource( final Path path,
                            final RuleModel model ) {
        try {
            return sourceServices.getServiceFor( Paths.convert( path ) ).getSource( Paths.convert( path ), model );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public List<ValidationMessage> validate( final Path path,
                                             final RuleModel content ) {
        try {
            final String source = RuleModelDRLPersistenceImpl.getInstance().marshal( content );
            return genericValidator.validate( path,
                                              new ByteArrayInputStream( source.getBytes() ),
                                              FILTER_JAVA,
                                              FILTER_GLOBALS,
                                              FILTER_DSLS );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private CommentedOption makeCommentedOption( final String commitMessage ) {
        final String name = identity.getName();
        final Date when = new Date();
        final CommentedOption co = new CommentedOption( sessionInfo.getId(),
                                                        name,
                                                        null,
                                                        commitMessage,
                                                        when );
        return co;
    }

}
