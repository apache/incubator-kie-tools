/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.drools.workbench.screens.scenariosimulation.client.popup;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.mvp.Command;

public interface YesNoConfirmPopup {

    interface Presenter {

        /**
         * Makes the YesNoConfirmPopupView visible with OK/Cancel buttons.
         * @param title
         * @param okButtonText
         * @param confirmMessage
         * @param okCommand
         */
        void show(final String title,
                  final String okButtonText,
                  final String confirmMessage,
                  final Command okCommand);

        /**
         * Makes the YesNoConfirmPopupView visible with OK/Cancel buttons.
         * @param title
         * @param inlineNotificationMessage
         * @param inlineNotificationType
         * @param okButtonText
         * @param okButtonType
         * @param confirmMessage
         * @param okCommand
         */
        void show(final String title,
                  final String inlineNotificationMessage,
                  final InlineNotification.InlineNotificationType inlineNotificationType,
                  final String okButtonText,
                  final Button.ButtonStyleType okButtonType,
                  final String confirmMessage,
                  final Command okCommand);

        /**
         * Makes the YesNoConfirmPopupView visible with YES/NO/Cancel buttons.
         * @param title
         * @param yesButtonText
         * @param noButtonText
         * @param confirmMessage
         * @param yesCommand
         * @param noCommand
         */
        void show(final String title,
                  final String yesButtonText,
                  final String noButtonText,
                  final String confirmMessage,
                  final Command yesCommand,
                  final Command noCommand);

        /**
         * Makes the YesNoConfirmPopupView visible with YES/NO/Cancel buttons.
         *
         * @param title
         * @param inlineNotificationMessage
         * @param inlineNotificationType
         * @param yesButtonText
         * @param noButtonText
         * @param yesButtonType
         * @param noButtonType
         * @param confirmMessage
         * @param yesCommand
         * @param noCommand
         */
        void show(final String title,
                  final String inlineNotificationMessage,
                  final InlineNotification.InlineNotificationType inlineNotificationType,
                  final String yesButtonText,
                  final String noButtonText,
                  final Button.ButtonStyleType yesButtonType,
                  final Button.ButtonStyleType noButtonType,
                  final String confirmMessage,
                  final Command yesCommand,
                  final Command noCommand);

        /**
         * Makes this popup container(and the main content along with it) invisible. Has no effect if the popup is not
         * already showing.
         */
        void hide();
    }

    /**
     * Makes the YesNoConfirmPopupView visible with OK/Cancel buttons.
     * @param title
     * @param okButtonText
     * @param confirmMessage
     * @param okCommand
     */
    void show(final String title,
              final String okButtonText,
              final String confirmMessage,
              final Command okCommand);

    /**
     * Makes the YesNoConfirmPopupView visible with OK/Cancel buttons.
     * @param title
     * @param inlineNotificationMessage
     * @param inlineNotificationType
     * @param okButtonText
     * @param okButtonType
     * @param confirmMessage
     * @param okCommand
     */
    void show(final String title,
              final String inlineNotificationMessage,
              final InlineNotification.InlineNotificationType inlineNotificationType,
              final String okButtonText,
              final Button.ButtonStyleType okButtonType,
              final String confirmMessage,
              final Command okCommand);

    /**
     * Makes the YesNoConfirmPopupView visible with YES/NO/Cancel buttons.
     * @param title
     * @param yesButtonText
     * @param noButtonText
     * @param confirmMessage
     * @param yesCommand
     * @param noCommand
     */
    void show(final String title,
              final String yesButtonText,
              final String noButtonText,
              final String confirmMessage,
              final Command yesCommand,
              final Command noCommand);

    /**
     * Makes the YesNoConfirmPopupView visible with YES/NO/Cancel buttons.
     *
     * @param title
     * @param inlineNotificationMessage
     * @param inlineNotificationType
     * @param yesButtonText
     * @param noButtonText
     * @param yesButtonType
     * @param noButtonType
     * @param confirmMessage
     * @param yesCommand
     * @param noCommand
     */
    void show(final String title,
              final String inlineNotificationMessage,
              final InlineNotification.InlineNotificationType inlineNotificationType,
              final String yesButtonText,
              final String noButtonText,
              final Button.ButtonStyleType yesButtonType,
              final Button.ButtonStyleType noButtonType,
              final String confirmMessage,
              final Command yesCommand,
              final Command noCommand);

    HTMLElement getElement();

    /**
     * Makes this popup container(and the main content along with it) invisible. Has no effect if the popup is not
     * already showing.
     */
    void hide();

}
