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

package org.uberfire.ext.editor.commons.client.menu;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.version.VersionService;
import org.uberfire.ext.editor.commons.version.events.RestoreEvent;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public class RestoreVersionCommandProvider {

    @Inject
    private Caller<VersionService> versionService;

    @Inject
    private Event<RestoreEvent> restoreEvent;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    public Command getCommand( final Path path ) {
        return new Command() {
            @Override
            public void execute() {
                new SaveOperationService().save( path,
                                                 new ParameterizedCommand<String>() {
                                                     @Override
                                                     public void execute( final String comment ) {
                                                         busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Restoring() );
                                                         versionService.call( getRestorationSuccessCallback(),
                                                                              new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).restore( path, comment );
                                                     }
                                                 } );
            }
        };
    }

    private RemoteCallback<Path> getRestorationSuccessCallback() {
        return new RemoteCallback<Path>() {
            @Override
            public void callback( final Path restored ) {
                //TODO {porcelli} close current?
//                busyIndicatorView.hideBusyIndicator();
//                restoreEvent.fire( new RestoreEvent( restored ) );
            }
        };
    }

}
