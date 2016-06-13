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

import java.util.List;

import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditorPresenter;
import org.uberfire.backend.vfs.Path;

/**
 * Presenter definition for popup that allows Users to select a document from a list of additional documents available.
 */
public interface SelectDocumentPopupPresenter {

    /**
     * Sets the parent Presenter that is capable of opening documents. Cannot be null.
     * See {@link KieMultipleDocumentEditorPresenter#onOpenDocumentInEditor(Path)}
     * @param presenter
     */
    void setEditorPresenter( final KieMultipleDocumentEditorPresenter presenter );

    /**
     * Sets the documents to show in the View. Cannot be null.
     * @param paths
     */
    void setDocuments( final List<Path> paths );

    /**
     * Shows the View.
     */
    void show();

    /**
     * Handles the User selecting a document to open in the editor.
     */
    void onOK();

    /**
     * Handles the User cancelling the operation.
     */
    void onCancel();

    /**
     * Disposes of the popup, clearing resources.
     */
    void dispose();

}
