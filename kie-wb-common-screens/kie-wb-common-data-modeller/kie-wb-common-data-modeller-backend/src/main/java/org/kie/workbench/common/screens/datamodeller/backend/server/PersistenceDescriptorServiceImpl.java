/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.io.BufferedInputStream;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.screens.datamodeller.util.PersistenceDescriptorXMLMarshaller;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class PersistenceDescriptorServiceImpl implements PersistenceDescriptorService {

    private static final String PERSISTENCE_DESCRIPTOR_PATH = "src/main/resources/META-INF/persistence.xml";

    private IOService ioService;

    private MetadataService metadataService;

    private CommentedOptionFactory optionsFactory;

    private KieProjectService projectService;

    public PersistenceDescriptorServiceImpl() {
    }

    @Inject
    public PersistenceDescriptorServiceImpl(
            final @Named( "ioStrategy" ) IOService ioService,
            final KieProjectService projectService,
            final MetadataService metadataService,
            final CommentedOptionFactory optionsFactory ) {

        this.ioService = ioService;
        this.projectService = projectService;
        this.metadataService = metadataService;
        this.optionsFactory = optionsFactory;
    }

    @Override
    public PersistenceDescriptorModel load( Path path ) {
        try {

            BufferedInputStream inputStream = new BufferedInputStream( ioService.newInputStream( Paths.convert( path ) ) );
            return PersistenceDescriptorXMLMarshaller.fromXML( inputStream, false );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public PersistenceDescriptorModel load( Project project ) {
        Path descriptorPath = calculatePersistenceDescriptorPath( project );
        if ( descriptorPath != null ) {
            return load( descriptorPath );
        } else {
            return null;
        }
    }

    @Override
    public Path calculatePersistenceDescriptorPath( Project project ) {
        Path rootPath;
        if ( project == null || ( rootPath = project.getRootPath() ) == null ) {
            return null;
        }

        final org.uberfire.java.nio.file.Path nioRootPath = Paths.convert( rootPath );
        final Path descriptorPath = Paths.convert( nioRootPath.resolve( PERSISTENCE_DESCRIPTOR_PATH ) );
        return descriptorPath;
    }

    @Override
    public Path save( Path path, PersistenceDescriptorModel model, Metadata metadata, String comment ) {

        try {
            org.uberfire.java.nio.file.Path result = null;
            String content = PersistenceDescriptorXMLMarshaller.toXML( model );
            if ( metadata != null ) {
                result = ioService.write( Paths.convert( path ),
                        content,
                        metadataService.setUpAttributes( path, metadata ),
                        optionsFactory.makeCommentedOption( comment ) );
            } else if ( comment != null ) {
                result = ioService.write( Paths.convert( path ),
                        content,
                        optionsFactory.makeCommentedOption( comment ) );
            } else {
                result = ioService.write( Paths.convert( path ), content );
            }
            return Paths.convert( result );
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public List<ValidationMessage> validate( Path path, PersistenceDescriptorModel model ) {
        //TODO implement validation
        return null;
    }

    @Override
    public String toSource( Path path, PersistenceDescriptorModel model ) {
        String content;
        try {
            content = PersistenceDescriptorXMLMarshaller.toXML( model );
        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
        return content;
    }

    @Override
    public PersistenceDescriptorModel createProjectDefaultDescriptor( final Path path ) {
        KieProject project = projectService.resolveProject( path );
        if ( project == null ) return null;

        //TODO read default descriptor parameters from a configuration/template.
        //We can basically copy the configuration from WEB-INF/classes/META-INF/persistence.xml file
        //since this file will always exist for a kie-wb installation.
        //This default values are taken from "org.jbpm.domain" persistence unit shipped by default with kie-wb-distributions
        PersistenceDescriptorModel descriptorModel = new PersistenceDescriptorModel();
        descriptorModel.setVersion( "2.0" );

        PersistenceUnitModel unitModel = new PersistenceUnitModel();
        descriptorModel.setPersistenceUnit( unitModel );

        unitModel.setName( project.getPom().getGav().toString() );
        unitModel.setTransactionType( TransactionType.JTA );
        unitModel.setProvider( "org.hibernate.ejb.HibernatePersistence" );
        unitModel.setJtaDataSource( "java:jboss/datasources/ExampleDS" );

        unitModel.addProperty( new Property( "hibernate.dialect", "org.hibernate.dialect.H2Dialect" ) );
        unitModel.addProperty( new Property( "hibernate.max_fetch_depth", "3" ) );
        unitModel.addProperty( new Property( "hibernate.hbm2ddl.auto", "update" ) );
        unitModel.addProperty( new Property( "hibernate.show_sql", "false" ) );


        // <!-- BZ 841786: AS7/EAP 6/Hib 4 uses new (sequence) generators which seem to cause problems -->
        unitModel.addProperty( new Property( "hibernate.id.new_generator_mappings", "false" ) );
        unitModel.addProperty( new Property( "hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform" ) );

        //use only entities configured by the user, avoiding in this way that classes in the project classpath (e.g, from dependencies)
        //to be included in the EntityManager
        unitModel.setExcludeUnlistedClasses( true );

        return descriptorModel;
    }

}
