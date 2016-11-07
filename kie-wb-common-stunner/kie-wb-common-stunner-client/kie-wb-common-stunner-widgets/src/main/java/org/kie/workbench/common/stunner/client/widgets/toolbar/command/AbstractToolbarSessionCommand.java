/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import org.gwtbootstrap3.client.ui.constants.IconRotate;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

public abstract class AbstractToolbarSessionCommand<S extends ClientSession, C extends ClientSessionCommand<S>>
        implements ToolbarCommand<S> {

    private final String uuid;
    private final C command;
    private Toolbar<S> toolbar;

    protected AbstractToolbarSessionCommand( final C command ) {
        this.uuid = UUID.uuid();
        this.command = command;
        this.command.listen( this::checkState );
    }

    protected abstract boolean requiresConfirm();

    @Override
    public ToolbarCommand<S> initialize( final Toolbar<S> toolbar,
                                         final S session ) {
        this.toolbar = toolbar;
        this.command.bind( session );
        checkState();
        return this;
    }

    @Override
    public <T> void execute( final ToolbarCommandCallback<T> callback ) {
        if ( requiresConfirm() ) {
            this.executeWithConfirm( callback );
        } else {
            this.executeWithNoConfirm( callback );
        }
    }

    private  <T> void executeWithNoConfirm( final ToolbarCommandCallback<T> callback ) {
        this.command.execute( new ClientSessionCommand.Callback<T>() {
            @Override
            public void onSuccess( final T result ) {
                if ( null != callback ) {
                    callback.onCommandExecuted( result );
                }
            }

            @Override
            public void onError( final ClientRuntimeError error ) {
                if ( null != callback ) {
                    callback.onError( error );
                }
            }
        } );
    }

    // TODO: I18n.
    protected String getConfirmMessage() {
        return "Are you sure?";
    }

    private  <T> void executeWithConfirm( final ToolbarCommandCallback<T> callback ) {
        final Command yesCommand = () -> {
            this.executeWithNoConfirm( callback );
        };
        final Command noCommand = () -> {
        };
        final YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup( getConfirmMessage(),
                null, yesCommand, noCommand, noCommand );
        popup.show();

    }

    protected void checkState() {
        if ( command.isEnabled() ) {
            enable();
        } else {
            disable();
        }

    }

    public void afterDraw() {
        checkState();
    }

    @Override
    public void execute() {
        this.execute( null );
    }

    protected void executeWithConfirm( final Command command ) {
        final Command yesCommand = () -> {
            command.execute();
        };
        final Command noCommand = () -> {
        };
        // TODO: I18n.
        final YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup( "Are you sure?",
                null, yesCommand, noCommand, noCommand );
        popup.show();

    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractToolbarSessionCommand ) ) {
            return false;
        }
        AbstractToolbarSessionCommand that = ( AbstractToolbarSessionCommand ) o;
        return uuid.equals( that.uuid );

    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public IconRotate getIconRotate() {
        return IconRotate.NONE;
    }

    @Override
    public void destroy() {
        doDestroy();
        this.command.unbind();
    }

    protected void doDestroy() {
        command.unbind();
    }

    protected void enable() {
        toolbar.enable( this );
    }

    protected void disable() {
        toolbar.disable( this );
    }

}
