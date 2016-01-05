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

package org.uberfire.client.views.pfly.maximize;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter.View;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class MaximizeToggleButton extends Button implements View {

    private MaximizeToggleButtonPresenter presenter;
    private boolean maximized;
    private Command maximizeCommand;
    private Command unmaximizeCommand;

    public MaximizeToggleButton() {
        setMaximized(maximized);
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.handleClick();
            }
        });
        setSize( ButtonSize.SMALL );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return this.addDomHandler( handler, ClickEvent.getType() );
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
            }
        } else {
            if ( maximizeCommand != null ) {
                maximizeCommand.execute();
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
            setIcon( IconType.COMPRESS );
            setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
        } else {
            setIcon( IconType.EXPAND );
            setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        }
    }
}