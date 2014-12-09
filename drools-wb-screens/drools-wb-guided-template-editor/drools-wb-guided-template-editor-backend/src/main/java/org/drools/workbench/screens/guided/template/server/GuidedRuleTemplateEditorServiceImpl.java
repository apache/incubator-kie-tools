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

package org.drools.workbench.screens.guided.template.server;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.guided.template.backend.RuleTemplateModelXMLPersistenceImpl;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.rule.backend.server.GuidedRuleModelVisitor;
import org.drools.workbench.screens.guided.template.model.GuidedTemplateEditorContent;
import org.drools.workbench.screens.guided.template.service.GuidedRuleTemplateEditorService;
import org.drools.workbench.screens.guided.template.type.GuidedRuleTemplateResourceTypeDefinition;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.JavaFileFilter;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
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
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class GuidedRuleTemplateEditorServiceImpl
        extends KieService
        implements GuidedRuleTemplateEditorService {

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
    private GenericValidator genericValidator;

    @Inject
    private GuidedRuleTemplateResourceTypeDefinition resourceTypeDefinition;

    public Path create( final Path context,
                        final String fileName,
                        final TemplateModel content,
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
                             RuleTemplateModelXMLPersistenceImpl.getInstance().marshal( content ),
                             makeCommentedOption( comment ) );

            return newPath;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public TemplateModel load( final Path path ) {
        try {
            final String content = ioService.readAllString( Paths.convert( path ) );

            return (TemplateModel) RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( content );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public GuidedTemplateEditorContent loadContent( final Path path ) {
        try {
            final TemplateModel model = load( path );
            final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
            final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();

            //Get FQCN's used by model
            final GuidedRuleModelVisitor visitor = new GuidedRuleModelVisitor( model );
            final Set<String> consumedFQCNs = visitor.getConsumedModelClasses();

            //Get FQCN's used by Globals
            consumedFQCNs.addAll( oracle.getPackageGlobals().values() );

            DataModelOracleUtilities.populateDataModel( oracle,
                                                        dataModel,
                                                        consumedFQCNs );

            //Signal opening to interested parties
            resourceOpenedEvent.fire( new ResourceOpenedEvent( path,
                                                               sessionInfo ) );

            return new GuidedTemplateEditorContent( model,
                                                    loadOverview( path ),
                                                    dataModel );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Path save( final Path resource,
                      final TemplateModel model,
                      final Metadata metadata,
                      final String comment ) {
        try {
            final Package pkg = projectService.resolvePackage( resource );
            final String packageName = ( pkg == null ? null : pkg.getPackageName() );
            model.setPackageName( packageName );

            ioService.write( Paths.convert( resource ),
                             RuleTemplateModelXMLPersistenceImpl.getInstance().marshal( model ),
                             metadataService.setUpAttributes( resource, metadata ),
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
                           final TemplateModel model) {
        return sourceServices.getServiceFor(Paths.convert(path)).getSource(Paths.convert(path), model);
    }

    @Override
    public boolean accepts( final Path path ) {
        return resourceTypeDefinition.accept( path );
    }

    @Override
    public List<ValidationMessage> validate( final Path path ) {
        try {
            final String content = ioService.readAllString( Paths.convert( path ) );
            final TemplateModel model = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( content );
            return validateTemplateVariables( path,
                                              model );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public List<ValidationMessage> validate( final Path path,
                                             final TemplateModel model ) {
        try {
            final List<ValidationMessage> messages = validateTemplateVariables( path,
                                                                                model );
            messages.addAll( genericValidator.validate( path,
                                                        new ByteArrayInputStream(
                                                                RuleTemplateModelXMLPersistenceImpl.getInstance()
                                                                        .marshal( model ).getBytes( Charsets.UTF_8 )
                                                        ),
                                                        FILTER_JAVA,
                                                        FILTER_DRL,
                                                        FILTER_DSLR,
                                                        FILTER_DSL,
                                                        FILTER_RDRL,
                                                        FILTER_RDSLR,
                                                        FILTER_GLOBAL ) );
            return messages;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    private List<ValidationMessage> validateTemplateVariables( final Path path,
                                                               final TemplateModel model ) {
        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        if ( model.getInterpolationVariablesList().length > 0 && model.getRowsCount() == 0 ) {
            messages.add( makeValidationMessages( path,
                                                  "One or more Template Variables defined but no data has been entered." ) );
        }
        return messages;
    }

    private ValidationMessage makeValidationMessages( final Path path,
                                                      final String message ) {
        final ValidationMessage msg = new ValidationMessage();
        msg.setPath( path );
        msg.setLevel( ValidationMessage.Level.WARNING );
        msg.setText( message );
        return msg;
    }

}
