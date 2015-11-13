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
package org.uberfire.client.workbench.widgets.common;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;

/**
 * Shows simple text-only error messages in a modal popup dialog that sits above the workbench.
 * This is designed to be used only for reporting error conditions; to make a full-featured popup UI, see {@link WorkbenchPopup}.
 */
@ApplicationScoped
public class ErrorPopupPresenter {

    /**
     * The interface that popup views implement. There should be exactly one implementation of this interface on the
     * classpath at compile time, and it will usually come from a module that provides all such views.
     */
    public interface View {

        /**
         * Displays the given message in a modal dialog that sits above all other workbench components. The dialog
         * should include some sort of user interface controls for dismissing itself.
         *
         * @param msg The message to display as plain text. Not HTML; newlines should be rendered as newlines.
         * @param afterShow The command to invoke once the dialog has been displayed. Never null.
         * @param afterClose The command to invoke once the dialog has been closed. Never null.
         */
        void showMessage( final String msg,
                          final Command afterShow,
                          final Command afterClose );
    }

    private final View view;

    @Inject
    public ErrorPopupPresenter( View view ) {
        this.view = checkNotNull( "view", view );
    }

    /**
     * Shows the given message in a modal popup that appears above all other workbench contents.
     *
     * @param msg The message to display as plain text. HTML tags are not interpreted, and newlines are rendered as newlines.
     * @param afterShow The command to invoke once the dialog has been displayed. Must not be null.
     * @param afterClose The command to invoke once the dialog has been closed. Must not be null.
     */
    public void showMessage( final String msg,
                             final Command afterShow,
                             final Command afterClose ) {
        view.showMessage( msg,
                          checkNotNull( "afterShow", afterShow ),
                          checkNotNull( "afterClose", afterClose ) );
    }

    /**
     * Shows the given message in a modal popup that appears above all other workbench contents.
     *
     * @param msg The message to display as plain text. HTML tags are not interpreted, and newlines are rendered as newlines.
     */
    public void showMessage( final String msg ) {
        view.showMessage( msg,
                          Commands.DO_NOTHING,
                          Commands.DO_NOTHING );
    }

}
