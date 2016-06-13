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

package org.kie.workbench.common.widgets.metadata.client;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView.KieEditorWrapperPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

public interface KieMultipleDocumentEditorPresenter<D extends KieDocument> extends KieEditorWrapperPresenter {

    /**
     * The Widget for this Editor. To be returned by subclasses @WorkbenchPartView method.
     * @return
     */
    IsWidget getWidget();

    /**
     * The title decoration for this Editor. To be returned by subclasses @WorkbenchPartTitleDecoration method.
     * @param document The document for which to get the title widget. Cannot be null.
     * @return
     */
    IsWidget getTitleWidget( final D document );

    /**
     * The Menus for this Editor. To be returned by subclasses @WorkbenchMenu method.
     * @return
     */
    Menus getMenus();

    /**
     * Construct the default Menus, consisting of "Save", "Copy", "Rename", "Delete",
     * "Validate" and "VersionRecordManager" drop-down. Subclasses can override this
     * to customize their Menus.
     */
    void makeMenuBar();

    /**
     * Ensure resources are released correctly. To be used by subclasses @OnClose method.
     */
    void onClose();

    /**
     * Register a new document in the MDI container. The document's Path is configured with concurrent lock handlers.
     * @param document The document to register. Cannot be null.
     */
    void registerDocument( final D document );

    /**
     * Deregister an existing document from the MDI container.
     * @param document The document to deregister. Cannot be null.
     */
    void deregisterDocument( final D document );

    /**
     * Activate a document. Activation initialises the VersionRecordManager drop-down and Editor tabs
     * with the content of the document. Subclasses could call this, for example, when a document
     * has been selected.
     * @param document The document to activate. Cannot be null.
     * @param overview The {@link Overview} associated with the document. Cannot be null.
     * @param dmo The {@link AsyncPackageDataModelOracle} associated with the document. Cannot be null.
     * @param imports The {@link Imports} associated with the document. Cannot be null.
     * @param isReadOnly true if the document is read-only.
     */
    void activateDocument( final D document,
                           final Overview overview,
                           final AsyncPackageDataModelOracle dmo,
                           final Imports imports,
                           final boolean isReadOnly );

    /**
     * Get the active document. Can return null.
     * @return
     */
    D getActiveDocument();

    /**
     * Load a document. Once a document has been loaded it should be registered to the MDI container.
     * @param path Provided by Uberfire to the @WorkbenchEditor's @OnStartup method.
     * @param placeRequest Provided by Uberfire to the @WorkbenchEditor's @OnStartup method.
     */
    void loadDocument( final ObservablePath path,
                       final PlaceRequest placeRequest );

    /**
     * Refresh a document due to a change in the selected version.
     * @param document
     */
    void refreshDocument( final D document );

    /**
     * Remove a document from the MDI container.
     * @param document
     */
    void removeDocument( final D document );

    /**
     * Get a title for the document to show in the WorkbenchPart's title widget.
     * @param document
     * @return
     */
    String getDocumentTitle( final D document );

    /**
     * The "View Source" tab has been selected. Subclasses should generate the source for the document
     * and update the "View Source" widget's content with {@link #updateSource(String)} .
     * @param document
     * @return
     */
    void onSourceTabSelected( final D document );

    /**
     * Update the "View Source" widget's content.
     * @param source
     */
    void updateSource( final String source );

    /**
     * The "Validate" MenuItem has been selected. Subclasses should perform validation of the document.
     * @param document
     * @return
     */
    void onValidate( final D document );

    /**
     * The "Save" MenuItem has been selected. Subclasses should save the document. The
     * {@link KieMultipleDocumentEditor#getSaveSuccessCallback(KieDocument, int)} should
     * be used to ensure the "isDirty" mechanism is correctly updated.
     * @param document
     * @param commitMessage
     * @return
     */
    void onSave( final D document,
                 final String commitMessage );

    /**
     * Check whether a document can be closed. The original hashCode can be retrieved from the document.
     * The current hashCode should be retrieved from the document's model. To be used in conjunction with
     * subclasses @OnMayClose method.
     * @param originalHashCode The document's model original hashCode.
     * @param currentHashCode The document's model current hashCode.
     * @return
     */
    boolean mayClose( final Integer originalHashCode,
                      final Integer currentHashCode );

    /**
     * Called in response to the User selecting an additional document to open in the editor.
     * @param path The Path to the document to open.
     */
    void onOpenDocumentInEditor( final Path path );

    /**
     * Returns a list of Paths to *all* documents that can be opened in the editor,
     * including those that may be potentially already open in the editor.
     * @param callback The callback returns the list of additional documents; thus supporting asynchronous retrieval of documents.
     */
    void getAvailableDocumentPaths( final Callback<List<Path>> callback );

}
