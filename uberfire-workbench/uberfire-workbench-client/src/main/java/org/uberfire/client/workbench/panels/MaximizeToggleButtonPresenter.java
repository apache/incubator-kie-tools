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

package org.uberfire.client.workbench.panels;

import org.uberfire.client.mvp.UberView;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

/**
 * Logic for a widget that allows toggling between a "maximized" and normal (unmaximized) state. This class keeps track
 * of the current state on its own and changes its decoration depending on whether it's in the maximized or unmaximized
 * state. The actions taken on each transition are supplied by the caller.
 */
public class MaximizeToggleButtonPresenter {

    private final View view;
    private boolean maximized;
    private Command maximizeCommand;
    private Command unmaximizeCommand;

    public interface View extends UberView<MaximizeToggleButtonPresenter> {

        /**
         * Changes the maximized appearance of the button. When maximized is true, the button should show an
         * "unmaximize" icon; when maximized is false, the button should show a "maximize" icon.
         */
        void setMaximized( boolean maximized );

        /**
         * Shows or hides this view's widget.
         */
        void setVisible( boolean b );

    }

    public MaximizeToggleButtonPresenter( View view ) {
        this.view = PortablePreconditions.checkNotNull( "view", view );
        view.init( this );
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
     * to call this method from the maximizeCommand and unmaximizeCommand.
     *
     * @param maximized the new maximized state to set.
     */
    public void setMaximized( boolean maximized ) {
        this.maximized = maximized;
        view.setMaximized( maximized );
    }

    /**
     * Handles a click event from the view.
     */
    public void handleClick() {
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
     * Returns the view for this presenter.
     * @return
     */
    public View getView() {
        return view;
    }

    public void setVisible( boolean b ) {
        view.setVisible( b );
    }
}