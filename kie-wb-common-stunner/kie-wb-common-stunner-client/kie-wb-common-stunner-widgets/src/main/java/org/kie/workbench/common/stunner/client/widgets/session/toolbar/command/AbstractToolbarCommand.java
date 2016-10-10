/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.session.toolbar.command;

import org.kie.workbench.common.stunner.client.widgets.session.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.core.client.session.CanvasSession;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

public abstract class AbstractToolbarCommand<S extends CanvasSession> implements ToolbarCommand<S> {

    String uuid;

    protected Toolbar<S> toolbar;
    protected S session;

    public AbstractToolbarCommand() {
        this.uuid = UUID.uuid();
    }

    protected abstract boolean getState();

    @Override
    public ToolbarCommand<S> initialize( final Toolbar<S> toolbar,
                                         final S session ) {
        this.toolbar = toolbar;
        this.session = session;
        checkState();
        return this;
    }

    protected void checkState() {
        if ( getState() ) {
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
        final YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup( "Are you sure?",
                null, yesCommand, noCommand, noCommand );
        popup.show();

    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractToolbarCommand ) ) {
            return false;
        }
        AbstractToolbarCommand that = ( AbstractToolbarCommand ) o;
        return uuid.equals( that.uuid );

    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public void destroy() {
        doDestroy();
        this.session = null;
        this.uuid = null;
    }

    protected void doDestroy() {
    }

    protected void enable() {
        toolbar.enable( this );
    }

    protected void disable() {
        toolbar.disable( this );
    }

}
