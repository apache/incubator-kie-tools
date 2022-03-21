/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.popover;

import org.uberfire.mvp.Command;

public interface ErrorReportPopover extends PopoverView {

    /**
     * Makes the <code>ErrorReportPopover</code> visible with Keep/Apply buttons.
     * @param errorTitleText
     * @param errorContentText
     * @param keepText
     * @param applyText
     * @param applyCommand
     * @param mx x position of the popover
     * @param my y position of the popover
     * @param position position where the popover is put (LEFT or RIGHT)
     */
    void setup(final String errorTitleText,
               final String errorContentText,
               final String keepText,
               final String applyText,
               final Command applyCommand,
               final int mx,
               final int my,
               final Position position);

    /**
     * Makes the <code>ErrorReportPopover</code> visible with keep button only.
     * @param errorTitleText
     * @param errorContentText
     * @param keepText
     * @param mx x position of the popover
     * @param my y position of the popover
     * @param position position where the popover is put (LEFT or RIGHT)
     */
    void setup(final String errorTitleText,
               final String errorContentText,
               final String keepText,
               final int mx,
               final int my,
               final Position position);

    /**
     * Makes this popover container(and the main content along with it) invisible. Has no effect if the popover is not
     * already showing.
     */
    void hide();

    /**
     * Returns the status of the popver (open or closed)
     * @return true if shown, false otherwise
     */
    boolean isShown();

    interface Presenter {

        /**
         * Method to set/update status of the <code>ErrorReportPopover</code> <b>before</b> actually showing the view.
         * Implemented to decouple this setup from the actual <b>show</b>, to be able to eventually add other modifications
         * (e.g. change vertical position based on the actual height, that is available only <b>after</b> this method has been invoked)
         * @param errorTitleText
         * @param errorContentText
         * @param keepText
         * @param applyText
         * @param applyCommand
         * @param mx x position of the replace
         * @param my y position of the popover
         * @param position position where the popover is put (LEFT or RIGHT)
         */
        void setup(final String errorTitleText,
                   final String errorContentText,
                   final String keepText,
                   final String applyText,
                   final Command applyCommand,
                   final int mx,
                   final int my,
                   final Position position);

        /**
         * Method to set/update status of the <code>ErrorReportPopover</code> <b>before</b> actually showing the view.
         * Implemented to decouple this setup from the actual <b>show</b>, to be able to eventually add other modifications
         * (e.g. change vertical position based on the actual height, that is available only <b>after</b> this method has been invoked)
         * @param errorTitleText
         * @param errorContentText
         * @param keepText
         * @param mx x position of the replace
         * @param my y position of the popover
         * @param position position where the popover is put (LEFT or RIGHT)
         */
        void setup(final String errorTitleText,
                   final String errorContentText,
                   final String keepText,
                   final int mx,
                   final int my,
                   final Position position);

        /**
         * Makes this popover container(and the main content along with it) invisible. Has no effect if the popover is not
         * already showing.
         */
        void hide();

        /**
         * Returns the status of the <code>ErrorReportPopover</code> (open or closed)
         * @return true if shown, false otherwise
         */
        boolean isShown();

        /**
         * Method that actually <b>show</b> the view
         */
        void show();

        /**
         * Retrieve the actual height of the <code>ErrorReportPopover</code>
         *
         * @return
         */
        int getActualHeight();
    }
}
