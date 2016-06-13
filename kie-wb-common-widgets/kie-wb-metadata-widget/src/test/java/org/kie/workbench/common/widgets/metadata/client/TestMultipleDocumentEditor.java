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

import java.util.Collections;
import java.util.List;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.PlaceRequest;

public class TestMultipleDocumentEditor extends KieMultipleDocumentEditor<TestDocument> {

    public TestMultipleDocumentEditor( final KieEditorView editorView ) {
        super( editorView );
    }

    @Override
    public void loadDocument( final ObservablePath path,
                              final PlaceRequest placeRequest ) {

    }

    @Override
    public void refreshDocument( final TestDocument document ) {

    }

    @Override
    public void removeDocument( final TestDocument document ) {

    }

    @Override
    public String getDocumentTitle( final TestDocument document ) {
        return null;
    }

    @Override
    public void onSourceTabSelected( final TestDocument document ) {

    }

    @Override
    public void onValidate( final TestDocument document ) {

    }

    @Override
    public void onSave( final TestDocument document,
                        final String commitMessage ) {

    }

    @Override
    public void getAvailableDocumentPaths( final Callback<List<Path>> callback ) {
        callback.callback( Collections.<Path>emptyList() );
    }

    @Override
    public void onOpenDocumentInEditor( final Path path ) {

    }

}
