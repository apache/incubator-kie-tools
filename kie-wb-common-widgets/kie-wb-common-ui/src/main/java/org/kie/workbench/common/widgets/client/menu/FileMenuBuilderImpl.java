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

package org.kie.workbench.common.widgets.client.menu;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.CopyPopup;
import org.uberfire.ext.editor.commons.client.file.DeletePopup;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.RenamePopup;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

/**
 *
 */
@Dependent
public class
        FileMenuBuilderImpl
        extends BasicFileMenuBuilderImpl<FileMenuBuilder>
        implements FileMenuBuilder {

    @Inject
    private Caller<DeleteService> deleteService;

    @Inject
    private Caller<RenameService> renameService;

    @Inject
    private Caller<CopyService> copyService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Override
    public FileMenuBuilderImpl addDelete( final Path path ) {
        addDelete( new Command() {
            @Override
            public void execute() {
                final DeletePopup popup = new DeletePopup( new ParameterizedCommand<String>() {
                    @Override
                    public void execute( final String comment ) {
                        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Deleting() );
                        deleteService.call( getDeleteSuccessCallback( path ),
                                            new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).delete( path,
                                                                                                                    comment );
                    }
                } );

                popup.show();
            }
        } );

        return this;
    }

    private RemoteCallback<Void> getDeleteSuccessCallback( final Path path ) {
        return new RemoteCallback<Void>() {

            @Override
            public void callback( final Void response ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemDeletedSuccessfully() ) );
            }
        };
    }

    @Override
    public FileMenuBuilderImpl addRename( final Path path ) {
        addRename( new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( path,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameService.call( getRenameSuccessCallback(),
                                                                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                               details.getNewFileName(),
                                                                                                                                                               details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        } );

        return this;
    }

    @Override
    public FileMenuBuilderImpl addRename( final Path path,
                                          final Validator validator ) {
        addRename( new Command() {
            @Override
            public void execute() {
                final RenamePopup popup = new RenamePopup( path,
                                                           validator,
                                                           new CommandWithFileNameAndCommitMessage() {
                                                               @Override
                                                               public void execute( final FileNameAndCommitMessage details ) {
                                                                   busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Renaming() );
                                                                   renameService.call( getRenameSuccessCallback(),
                                                                                       new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).rename( path,
                                                                                                                                                               details.getNewFileName(),
                                                                                                                                                               details.getCommitMessage() );
                                                               }
                                                           } );

                popup.show();
            }
        } );

        return this;
    }

    private RemoteCallback<Path> getRenameSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRenamedSuccessfully() ) );
            }
        };
    }

    @Override
    public FileMenuBuilderImpl addCopy( final Path path ) {
        addCopy( new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( path,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyService.call( getCopySuccessCallback(),
                                                                                 new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                       details.getNewFileName(),
                                                                                                                                                       details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        } );

        return this;
    }

    @Override
    public FileMenuBuilderImpl addCopy( final Path path,
                                        final Validator validator ) {
        addCopy( new Command() {
            @Override
            public void execute() {
                final CopyPopup popup = new CopyPopup( path,
                                                       validator,
                                                       new CommandWithFileNameAndCommitMessage() {
                                                           @Override
                                                           public void execute( final FileNameAndCommitMessage details ) {
                                                               busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Copying() );
                                                               copyService.call( getCopySuccessCallback(),
                                                                                 new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).copy( path,
                                                                                                                                                       details.getNewFileName(),
                                                                                                                                                       details.getCommitMessage() );
                                                           }
                                                       } );
                popup.show();
            }
        } );

        return this;
    }

    private RemoteCallback<Path> getCopySuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemCopiedSuccessfully() ) );
            }
        };
    }
}
