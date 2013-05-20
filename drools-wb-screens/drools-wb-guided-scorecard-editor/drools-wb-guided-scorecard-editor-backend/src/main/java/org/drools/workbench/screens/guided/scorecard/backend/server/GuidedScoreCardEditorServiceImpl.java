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

package org.drools.workbench.screens.guided.scorecard.backend.server;

import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.plexus.util.StringUtils;
import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardXMLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.Attribute;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.model.ScoreCardModelContent;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.kie.workbench.common.services.backend.SourceServices;
import org.kie.workbench.common.services.shared.validation.model.BuilderResult;
import org.kie.workbench.common.services.shared.validation.model.BuilderResultLine;
import org.kie.workbench.common.services.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.kie.workbench.common.services.project.service.ProjectService;
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
public class GuidedScoreCardEditorServiceImpl implements GuidedScoreCardEditorService {

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
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private SourceServices sourceServices;

    @Inject
    private ProjectService projectService;

    private static final String RESOURCE_EXTENSION = "scgd";

    @Override
    public Path create( final Path context,
                        final String fileName,
                        final ScoreCardModel content,
                        final String comment ) {
        content.setPackageName( projectService.resolvePackageName( context ) );

        final org.kie.commons.java.nio.file.Path nioPath = paths.convert( context ).resolve( fileName );
        final Path newPath = paths.convert( nioPath,
                                            false );

        ioService.createFile( nioPath );
        ioService.write( nioPath,
                         GuidedScoreCardXMLPersistence.getInstance().marshal( content ),
                         makeCommentedOption( comment ) );

        //Signal creation to interested parties
        resourceAddedEvent.fire( new ResourceAddedEvent( newPath ) );

        return newPath;
    }

    @Override
    public ScoreCardModel load( final Path path ) {
        final String content = ioService.readAllString( paths.convert( path ) );

        //Signal opening to interested parties
        resourceOpenedEvent.fire( new ResourceOpenedEvent( path ) );

        return GuidedScoreCardXMLPersistence.getInstance().unmarshall( content );
    }

    @Override
    public ScoreCardModelContent loadContent( final Path path ) {
        final ScoreCardModel model = load( path );
        final PackageDataModelOracle oracle = dataModelService.getDataModel( path );
        return new ScoreCardModelContent( model,
                                          oracle );
    }

    @Override
    public Path save( final Path resource,
                      final ScoreCardModel model,
                      final Metadata metadata,
                      final String comment ) {
        model.setPackageName( projectService.resolvePackageName( resource ) );

        ioService.write( paths.convert( resource ),
                         GuidedScoreCardXMLPersistence.getInstance().marshal( model ),
                         metadataService.setUpAttributes( resource, metadata ),
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
    public String toSource( final Path path,
                            final ScoreCardModel model ) {
        final BuilderResult result = validateScoreCard( model );
        if ( !result.hasLines() ) {
            return toDRL( path, model );
        }
        return toDRL( result );
    }

    @Override
    public BuilderResult validate( final Path path,
                                   final ScoreCardModel model ) {
        final BuilderResult result = validateScoreCard( model );
        return result;
    }

    @Override
    public boolean isValid( final Path path,
                            final ScoreCardModel model ) {
        return !validate( path,
                          model ).hasLines();
    }

    private String toDRL( Path path,
                          final ScoreCardModel model ) {
        return sourceServices.getServiceFor( paths.convert( path ) ).getSource( paths.convert( path ), model );
    }

    private String toDRL( final BuilderResult result ) {
        final StringBuilder drl = new StringBuilder();
        for ( final BuilderResultLine msg : result.getLines() ) {
            drl.append( "//" ).append( msg.getMessage() ).append( "\n" );
        }
        return drl.toString();
    }

    private BuilderResult validateScoreCard( final ScoreCardModel model ) {
        final BuilderResult builderResult = new BuilderResult();
        if ( StringUtils.isBlank( model.getFactName() ) ) {
            builderResult.addLine( createBuilderResultLine( "Fact Name is empty.",
                                                            "Setup Parameters" ) );
        }
        if ( StringUtils.isBlank( model.getFieldName() ) ) {
            builderResult.addLine( createBuilderResultLine( "Resultant Score Field is empty.",
                                                            "Setup Parameters" ) );
        }
        if ( model.getCharacteristics().size() == 0 ) {
            builderResult.addLine( createBuilderResultLine( "No Characteristics Found.",
                                                            "Characteristics" ) );
        }
        int ctr = 1;
        for ( final Characteristic c : model.getCharacteristics() ) {
            String characteristicName = "Characteristic ('#" + ctr + "')";
            if ( StringUtils.isBlank( c.getName() ) ) {
                builderResult.addLine( createBuilderResultLine( "Name is empty.",
                                                                characteristicName ) );
            } else {
                characteristicName = "Characteristic ('" + c.getName() + "')";
            }
            if ( StringUtils.isBlank( c.getFact() ) ) {
                builderResult.addLine( createBuilderResultLine( "Fact is empty.",
                                                                characteristicName ) );
            }
            if ( StringUtils.isBlank( c.getField() ) ) {
                builderResult.addLine( createBuilderResultLine( "Characteristic Field is empty.",
                                                                characteristicName ) );
            } else if ( StringUtils.isBlank( c.getDataType() ) ) {
                builderResult.addLine( createBuilderResultLine( "Internal Error (missing datatype).",
                                                                characteristicName ) );
            }
            if ( c.getAttributes().size() == 0 ) {
                builderResult.addLine( createBuilderResultLine( "No Attributes Found.",
                                                                characteristicName ) );
            }
            if ( model.isUseReasonCodes() ) {
                if ( StringUtils.isBlank( model.getReasonCodeField() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Resultant Reason Codes Field is empty.",
                                                                    characteristicName ) );
                }
                if ( !"none".equalsIgnoreCase( model.getReasonCodesAlgorithm() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Baseline Score is not specified.",
                                                                    characteristicName ) );
                }
            }
            int attrCtr = 1;
            for ( final Attribute attribute : c.getAttributes() ) {
                final String attributeName = "Attribute ('#" + attrCtr + "')";
                if ( StringUtils.isBlank( attribute.getOperator() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Attribute Operator is empty.",
                                                                    attributeName ) );
                }
                if ( StringUtils.isBlank( attribute.getValue() ) ) {
                    builderResult.addLine( createBuilderResultLine( "Attribute Value is empty.",
                                                                    attributeName ) );
                }
                if ( model.isUseReasonCodes() ) {
                    if ( StringUtils.isBlank( c.getReasonCode() ) ) {
                        if ( StringUtils.isBlank( attribute.getReasonCode() ) ) {
                            builderResult.addLine( createBuilderResultLine( "Reason Code must be set at either attribute or characteristic.",
                                                                            attributeName ) );
                        }
                    }
                }
                attrCtr++;
            }
            ctr++;
        }
        return builderResult;
    }

    private BuilderResultLine createBuilderResultLine( final String msg,
                                                       final String name ) {
        return new BuilderResultLine().setMessage( msg ).setResourceFormat( RESOURCE_EXTENSION ).setResourceName( name );
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
