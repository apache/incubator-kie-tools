/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.popups;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

/**
 * View definition for popup that allows Users to select a document from a list of additional documents available.
 */
public interface SelectDocumentPopupView extends UberView<SelectDocumentPopupPresenter> {

    /**
     * View definition of a single document that can be selected.
     */
    interface SelectableDocumentView extends IsElement {

        /**
         * Gets the Path of the document.
         * @return
         */
        Path getPath();

        /**
         * Sets the Path of the document. Cannot be null.
         * @param path
         */
        void setPath( final Path path );

        /**
         * Sets the command to exeute when a document is selected in the View. Cannot be null.
         * @param activateDocumentCommand
         */
        void setSelectDocumentCommand( final Command activateDocumentCommand );

        /**
         * Sets whether a document is selected in the View. Selected documents can be rendered differently.
         * @param isSelected true if the document has been selected by the User.
         */
        void setSelected( final boolean isSelected );

    }

    /**
     * Clears the View.
     */
    void clear();

    /**
     * Adds a document to the View. Cannot be null.
     * @param document
     */
    void addDocument( final SelectableDocumentView document );

    /**
     * Enables the "OK" button.
     * @param enabled true if the button is enabled.
     */
    void enableOKButton( final boolean enabled );

    /**
     * Shows the popup.
     */
    void show();

    /**
     * Hides the popup.
     */
    void hide();

}
