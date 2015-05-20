/*
 * Copyright 2015 JBoss Inc
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;

import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
public class DataModelerEventObserver {

    private static final Logger logger = LoggerFactory.getLogger( DataModelerEventObserver.class );

    private PersistenceDescriptorService descriptorService;

    private IOService ioService;

    public DataModelerEventObserver() {
    }

    @Inject
    public DataModelerEventObserver( final PersistenceDescriptorService descriptorService,
            final @Named( "ioStrategy" ) IOService ioService ) {
        this.descriptorService = descriptorService;
        this.ioService = ioService;
    }

    private void onDataObjectCreated( @Observes DataObjectCreatedEvent event ) {
        Path descriptorPath;
        PersistenceDescriptorModel persistenceDescriptor;

        if ( isPersistable( event.getCurrentDataObject() ) &&
                isPersistenceAutoUpdateEnabled( event.getCurrentProject()  )  ) {
            descriptorPath = descriptorService.calculatePersistenceDescriptorPath( event.getCurrentProject() );
            persistenceDescriptor = safeLoad( descriptorPath );
            if ( persistenceDescriptor != null ) {

               if ( !persistenceDescriptor.getPersistenceUnit().getClasses().contains( event.getCurrentDataObject().getClassName() ) ) {

                   persistenceDescriptor.getPersistenceUnit().getClasses().add( event.getCurrentDataObject().getClassName() );
                   descriptorService.save( descriptorPath,
                           persistenceDescriptor,
                           null,
                           "Entity added to persistence descriptor" );
               }
            }
        }
    }

    private void onDataObjectDeleted( @Observes DataObjectDeletedEvent event ) {
        Path descriptorPath;
        PersistenceDescriptorModel persistenceDescriptor;

        if ( isPersistenceAutoUpdateEnabled( event.getCurrentProject() ) ) {
            descriptorPath = descriptorService.calculatePersistenceDescriptorPath( event.getCurrentProject() );
            persistenceDescriptor = safeLoad( descriptorPath );
            if ( persistenceDescriptor != null ) {
                if ( persistenceDescriptor.getPersistenceUnit().getClasses().remove( event.getCurrentDataObject().getClassName() ) ) {
                    descriptorService.save( descriptorPath,
                            persistenceDescriptor,
                            null,
                            "Entity removed from persistence descriptor" );

                }
            }
        }
    }

    private void onDataObjectChanged() {
        //TODO the following cases are not yet implemented.
        // 1) An already existing not persistable data object is now marked as persistable
        // 2) A persistable data object is now marked as not persistable
        // 3) A persistable data object is renamed or moved from package
    }

    private PersistenceDescriptorModel safeLoad( final Path path ) {
        try {
            if ( path != null && ioService.exists( Paths.convert( path ) ) ) {
                return descriptorService.load( path );
            }
        } catch ( Exception e ) {
            logger.warn( "It was not possible to read persistence descriptor por project: " + path );
        }
        return null;
    }

    private boolean isPersistable( DataObject dataObject ) {
        return dataObject != null && dataObject.getAnnotation( Entity.class.getName() ) != null;
    }

    private boolean isPersistenceAutoUpdateEnabled( Project project ) {
        //TODO read project configuration to know if:
        // 1) persistence is enabled for current project
        // 2) if persistence xml auto update is enabled for current project
        return true;
    }
}
