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

package org.uberfire.client.views.bs2.maximize;

import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.mvp.Command;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter.View;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A widget that allows toggling between a "maximized" and normal (unmaximized) state. This class keeps track of the
 * current state on its own and changes its decoration depending on whether it's in the maximized or unmaximized
 * state. The actions taken on each transition are supplied by the caller.
 * <p>
 * When used, this button should normally be nested inside a {@link ButtonGroup} with a 10 pixel right margin, a 4
 * pixel top margin, and the {@code pull-right} style class applied.
 */
public class MaximizeToggleButton extends Button implements View {

    private MaximizeToggleButtonPresenter presenter;
    private boolean maximized;
    private Command maximizeCommand;
    private Command unmaximizeCommand;

    public MaximizeToggleButton() {
        setIcon( IconType.CHEVRON_UP );
        setIconSize( IconSize.SMALL );
        setSize( ButtonSize.MINI );
        setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.handleClick();
            }
        } );
    }

    @Override
    public void init( MaximizeToggleButtonPresenter presenter ) {
        this.presenter = checkNotNull( "presenter", presenter );
    }

    /**
     * Normally invoked automatically when this button gets clicked. Exposed for testing purposes.
     */
    public void click() {
        final boolean wasMaximized = maximized;
        setMaximized( !wasMaximized );
        if ( wasMaximized ) {
            if ( unmaximizeCommand != null ) {
                unmaximizeCommand.execute();
                setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
            }
        } else {
            if ( maximizeCommand != null ) {
                maximizeCommand.execute();
                setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
            }
        }
    }

    /**
     * Returns the currently registered maximize command. Can be used to check if there is currently a maximize command registered.
     */
    public Command getMaximizeCommand() {
        return maximizeCommand;
    }

    /**
     * Sets the command to invoke upon each transition from unmaximized to maximized.
     */
    public void setMaximizeCommand( Command maximizeCommand ) {
        this.maximizeCommand = maximizeCommand;
    }

    /**
     * Returns the currently registered unmaximize command. Can be used to check if there is currently an unmaximize command registered.
     */
    public Command getUnmaximizeCommand() {
        return unmaximizeCommand;
    }

    /**
     * Sets the command to invoke upon each transition from maximized to unmaximized.
     */
    public void setUnmaximizeCommand( Command unmaximizeCommand ) {
        this.unmaximizeCommand = unmaximizeCommand;
    }

    /**
     * Reports whether this button is currently in the maximized state. If true, the next click will return to the
     * normal unmaximized state. If false, the next click will transition to the maximized state.
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Changes the maximized state of this button <i>without</i> calling the commands. This can be used to notify the
     * button that some external process has already maximized the thing in question. It is permissible but not necessary
     * to call this method from the maximizeCommadn and unmaximizeCommand.
     *
     * @param maximized the new maximized state to set.
     */
    public void setMaximized( boolean maximized ) {
        this.maximized = maximized;
        if ( maximized ) {
            setIcon( IconType.CHEVRON_DOWN );
            setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
        } else {
            setIcon( IconType.CHEVRON_UP );
            setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        }
    }
}