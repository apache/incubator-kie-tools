/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.file;

import static org.uberfire.backend.vfs.PathSupport.isVersioned;
import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.SaveInProgressEvent;
import org.uberfire.mvp.ParameterizedCommand;

public class SaveOperationService {

    @ApplicationScoped
    public static class SaveOperationNotifier {
       @Inject 
       private Event<SaveInProgressEvent> saveInProgressEvent;
       
       public void notify(Path path) {
           saveInProgressEvent.fire( new SaveInProgressEvent(path) );
       }
    }
    
    public void save( final Path path,
                      final ParameterizedCommand<String> saveCommand ) {
        checkNotNull( "command", saveCommand );

        final SaveOperationNotifier notifier = 
                IOC.getBeanManager().lookupBean( SaveOperationNotifier.class).getInstance();
        
        final ParameterizedCommand<String> wrappedSaveCommand = new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                 saveCommand.execute( parameter );
                 notifier.notify( path );
            }
        };
        
        if ( isVersioned( path ) ) {
            new SavePopUp( wrappedSaveCommand ).show();
        } else {
            wrappedSaveCommand.execute( "" );
        }
    }

}
