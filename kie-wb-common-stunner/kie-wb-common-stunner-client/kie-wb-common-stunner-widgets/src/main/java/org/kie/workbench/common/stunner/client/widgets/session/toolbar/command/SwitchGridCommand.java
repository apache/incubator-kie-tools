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

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasGrid;
import org.kie.workbench.common.stunner.core.client.canvas.DefaultCanvasGrid;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultCanvasReadOnlySession;

import javax.enterprise.context.Dependent;

@Dependent
public class SwitchGridCommand extends AbstractToolbarCommand<DefaultCanvasReadOnlySession> {

    private CanvasGrid grid;

    @Override
    protected boolean getState() {
        // Always active.
        return true;
    }

    @Override
    public ToolbarCommand<DefaultCanvasReadOnlySession> initialize( final Toolbar<DefaultCanvasReadOnlySession> toolbar,
                                                                    final DefaultCanvasReadOnlySession session ) {
        super.initialize( toolbar, session );
        showGrid();
        return this;
    }

    @Override
    public IconType getIcon() {
        return IconType.TH;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Switch grid";
    }

    @Override
    public <T> void execute( final ToolbarCommandCallback<T> callback ) {
        if ( isGridVisible() ) {
            hideGrid();

        } else {
            showGrid();

        }

    }

    private void showGrid() {
        this.grid = DefaultCanvasGrid.INSTANCE;
        session.getCanvas().setGrid( this.grid );

    }

    private void hideGrid() {
        this.grid = null;
        session.getCanvas().setGrid( this.grid );

    }

    private boolean isGridVisible() {
        return null != grid;

    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        this.grid = null;

    }

}
