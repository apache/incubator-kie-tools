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
import org.uberfire.mvp.PlaceRequest;

public class TestMultipleDocumentEditor extends KieMultipleDocumentEditor<TestDocument> {

    public TestMultipleDocumentEditor( final KieEditorView editorView ) {
        super( editorView );
    }

    @Override
    protected void loadDocument( final ObservablePath path,
                                 final PlaceRequest placeRequest ) {

    }

    @Override
    protected void refreshDocument( final TestDocument document ) {

    }

    @Override
    protected void removeDocument( final TestDocument document ) {

    }

    @Override
    protected String getDocumentTitle( final TestDocument document ) {
        return null;
    }

    @Override
    protected void onSourceTabSelected( final TestDocument document ) {

    }

    @Override
    protected void onValidate( final TestDocument document ) {

    }

    @Override
    protected void onSave( final TestDocument document,
                           String commitMessage ) {

    }
}
