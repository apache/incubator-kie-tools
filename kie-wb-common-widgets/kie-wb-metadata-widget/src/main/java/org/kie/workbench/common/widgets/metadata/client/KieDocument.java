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

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mvp.PlaceRequest;

/**
 * Definition of a document that can hosted in KIE's Multiple Document Editor
 */
public interface KieDocument {

    /**
     * Returns the current version of the document. Can be null if it is the "latest" version.
     * The version is identical to that used by {@link VersionRecordManager}.
     * @return
     */
    String getVersion();

    /**
     * Sets the current version of the document. This is called automatically by
     * {@link KieMultipleDocumentEditor} in response to changes to the document
     * version from the {@link VersionRecordManager} .
     * @param version
     */
    void setVersion( final String version );

    /**
     * Returns the "latest" path for the document. Latest is the tip/head version.
     * @return Can be null.
     */
    ObservablePath getLatestPath();

    /**
     * Sets the "latest" path for the document. This is called automatically by
     * {@link KieMultipleDocumentEditor} when an older version of a document is
     * restored and hence the latest version changes.
     * @param latestPath Can be null.
     */
    void setLatestPath( final ObservablePath latestPath );

    /**
     * Returns the "current" path for the document reflecting
     * the version selected in {@link VersionRecordManager}
     * @return Cannot be null.
     */
    ObservablePath getCurrentPath();

    /**
     * Sets the "current" path for the document. This is called automatically by
     * {@link VersionRecordManager} in response to a different version of the document
     * being selected; and in response to restoration of an older version of the document.
     * @param currentPath Cannot be null.
     */
    void setCurrentPath( final ObservablePath currentPath );

    /**
     * Returns the original {@link PlaceRequest} associated with the {@link KieMultipleDocumentEditor}
     * when first initialised. The {@link PlaceRequest} is used to support changes to the Editor title.
     * Subclasses may also need to use this to support different {@link LockManager} configurations.
     * @return
     */
    PlaceRequest getPlaceRequest();

    /**
     * Returns whether the document is read-only; normally when {@link KieDocument#getCurrentPath()}
     * points to version that is not the "latest" version however can also be set by subclasses for
     * example should attempts to lock the document for editing fail.
     * @return
     */
    boolean isReadOnly();

    /**
     * Sets whether the document is read-only. This is called automatically by {@link KieMultipleDocumentEditor}
     * in response to Users selecting a version of the document that is not the lastest.
     * @param isReadOnly
     */
    void setReadOnly( final boolean isReadOnly );

    /**
     * Returns the original hashCode of the model represented by the document. This is used by the hashCode-based
     * "is dirty" mechanism; where by a document is considered dirty should the hashCode when the document was
     * loaded differ to the document's current hashCode. This method should be used in conjunction with
     * {@link KieMultipleDocumentEditor#mayClose(Integer, Integer)}
     * @return
     */
    Integer getOriginalHashCode();

    /**
     * Sets the "original" hashCode. This is called automatically by {@link KieMultipleDocumentEditor#getSaveSuccessCallback(KieDocument, int)}
     * when a document has been successfully saved; effectively resetting the "is dirty" mechansism. Subclasses may also call this to set
     * the documents original hashCode after the document has been loaded. However by default this will be null and operate identically
     * to if it had been set by subclasses.
     * @param originalHashCode
     */
    void setOriginalHashCode( final Integer originalHashCode );

    /**
     * Returns the concurrent modification meta-data associated with the document. This is called automatically by the
     * concurrent modification handlers configured by {@link KieMultipleDocumentEditor#registerDocument(KieDocument)}.
     * to check for and handle concurrent modifications. It should not need to be called by subclasses.
     * @return
     */
    ObservablePath.OnConcurrentUpdateEvent getConcurrentUpdateSessionInfo();

    /**
     * Sets the concurrent modification meta-data. This is called automatically by the concurrent modification handlers
     * configured by {@link KieMultipleDocumentEditor#registerDocument(KieDocument)}. It should not need to be called
     * by subclasses directly.
     * @param concurrentUpdateSessionInfo
     */
    void setConcurrentUpdateSessionInfo( final ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo );

}
