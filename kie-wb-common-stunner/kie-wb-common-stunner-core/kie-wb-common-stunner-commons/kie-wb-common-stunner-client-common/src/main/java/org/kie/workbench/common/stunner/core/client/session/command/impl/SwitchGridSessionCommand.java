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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.canvas.DefaultCanvasGrid;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class SwitchGridSessionCommand extends AbstractClientSessionCommand<AbstractClientReadOnlySession> {

    private CanvasGrid grid;

    public SwitchGridSessionCommand() {
        super( true );
    }

    @Override
    public SwitchGridSessionCommand bind( final AbstractClientReadOnlySession session ) {
        super.bind( session );
        hideGrid();
        return this;
    }

    @Override
    public <T> void execute( final Callback<T> callback ) {
        checkNotNull( "callback",
                      callback );
        if ( isGridVisible() ) {
            hideGrid();
        } else {
            showGrid();
        }
        // Run the callback.
        callback.onSuccess( null );
    }

    private void showGrid() {
        this.grid = DefaultCanvasGrid.INSTANCE;
        getSession().getCanvas().setGrid( this.grid );
    }

    private void hideGrid() {
        this.grid = null;
        getSession().getCanvas().setGrid( this.grid );
    }

    private boolean isGridVisible() {
        return null != grid;
    }
}
