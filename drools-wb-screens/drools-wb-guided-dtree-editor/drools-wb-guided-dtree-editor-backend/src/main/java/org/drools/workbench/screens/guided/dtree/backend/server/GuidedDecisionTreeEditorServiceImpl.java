/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.dtree.backend.server;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.guided.dtree.backend.GuidedDecisionTreeDRLPersistence;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.screens.guided.dtree.model.GuidedDecisionTreeEditorContent;
import org.drools.workbench.screens.guided.dtree.service.GuidedDecisionTreeEditorService;
import org.drools.workbench.screens.guided.dtree.type.GuidedDTreeResourceTypeDefinition;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.file.DRLFileFilter;
import org.kie.workbench.common.services.backend.file.DSLFileFilter;
import org.kie.workbench.common.services.backend.file.DSLRFileFilter;
import org.kie.workbench.common.services.backend.file.GlobalsFileFilter;
import org.kie.workbench.common.services.backend.file.RDRLFileFilter;
import org.kie.workbench.common.services.backend.file.RDSLRFileFilter;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.workbench.events.ResourceOpenedEvent;
import org.uberfire.workbench.type.FileNameUtil;

@Service
@ApplicationScoped
public class GuidedDecisionTreeEditorServiceImpl extends KieService implements GuidedDecisionTreeEditorService {

    //Filters to include *all* applicable resources
    private static final JavaFileFilter FILTER_JAVA = new JavaFileFilter();
    private static final DRLFileFilter FILTER_DRL = new DRLFileFilter();
    private static final DSLRFileFilter FILTER_DSLR = new DSLRFileFilter();
    private static final DSLFileFilter FILTER_DSL = new DSLFileFilter();
    private static final RDRLFileFilter FILTER_RDRL = new RDRLFileFilter();
    private static final RDSLRFileFilter FILTER_RDSLR = new RDSLRFileFilter();
    private static final GlobalsFileFilter FILTER_GLOBAL = new GlobalsFileFilter();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private GuidedDTreeResourceTypeDefinition resourceType;

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
    private DataModelService dataModelService;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private KieProjectService projectService;

    @Inject
    private GenericValidator genericValidator;

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final GuidedDecisionTree content,
                        final String comment ) {
        try {
            final Package pkg = projectService.resolvePackage( context );
            final String packageName = ( pkg == null ? null : pkg.getPackageName() );
            content.setPackageName( packageName );

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert( context ).resolve( fileName );
            final Path newPath = Paths.convert( nioPath );

            if ( ioService.exists( nioPath ) ) {
                throw new FileAlreadyExistsException( nioPath.toString() );
            }

            ioService.write( nioPath,
                             GuidedDecisionTreeDRLPersistence.getInstance().marshal( content ),
                             makeCommentedOption( comment ) );

            return newPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public GuidedDecisionTree load( final Path path ) {
        try {
            final String drl = ioService.readAllString( Paths.convert( path ) );
            final String baseFileName = FileNameUtil.removeExtension( path,
                                                                      resourceType );
            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            final GuidedDecisionTree model = GuidedDecisionTreeDRLPersistence.getInstance().unmarshal( drl,
                                                                                                       baseFileName,
                                                                                                       oracle );

            return model;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public GuidedDecisionTreeEditorContent loadContent( final Path path ) {
        try {
            final GuidedDecisionTree model = load( path );
            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();

            //Get FQCN's used by model
            final GuidedDecisionTreeModelVisitor visitor = new GuidedDecisionTreeModelVisitor( model );
            final Set<String> consumedFQCNs = visitor.getConsumedModelClasses();

            //Get FQCN's used by Globals
            consumedFQCNs.addAll( oracle.getPackageGlobals().values() );

            DataModelOracleUtilities.populateDataModel( oracle,
                                                        dataModel,
                                                        consumedFQCNs );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path,
                                                               sessionInfo ) );

            return new GuidedDecisionTreeEditorContent( model,
                                                        loadOverview( path ),
                                                        dataModel );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public PackageDataModelOracleBaselinePayload loadDataModel( final Path path ) {
        try {
            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
            //There are no classes to pre-load into the DMO when requesting a new Data Model only
            DataModelOracleUtilities.populateDataModel( oracle,
                                                        dataModel,
                                                        new HashSet<String>() );

            return dataModel;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path save( final Path resource,
                      final GuidedDecisionTree model,
                      final Metadata metadata,
                      final String comment ) {
        try {
            final Package pkg = projectService.resolvePackage( resource );
            final String packageName = ( pkg == null ? null : pkg.getPackageName() );
            model.setPackageName( packageName );

            ioService.write( Paths.convert( resource ),
                             GuidedDecisionTreeDRLPersistence.getInstance().marshal( model ),
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
    public String toSource(final Path path,
                           final GuidedDecisionTree model) {
        return sourceServices.getServiceFor(Paths.convert(path)).getSource(Paths.convert(path),
                model);
    }

    @Override
    public List<ValidationMessage> validate( final Path path,
                                             final GuidedDecisionTree content ) {
        try {
            return genericValidator.validate( path,
                                              new ByteArrayInputStream(
                                                      GuidedDecisionTreeDRLPersistence.getInstance().marshal( content ).getBytes( Charsets.UTF_8 )
                                              ),
                                              FILTER_JAVA,
                                              FILTER_DRL,
                                              FILTER_DSLR,
                                              FILTER_DSL,
                                              FILTER_RDRL,
                                              FILTER_RDSLR,
                                              FILTER_GLOBAL );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
