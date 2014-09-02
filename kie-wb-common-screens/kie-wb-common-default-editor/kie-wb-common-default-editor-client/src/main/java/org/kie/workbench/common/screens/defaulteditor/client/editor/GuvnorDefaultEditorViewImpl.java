/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.inject.Inject;

import org.kie.uberfire.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.ObservablePath;

public class GuvnorDefaultEditorViewImpl
        extends KieEditorViewImpl
        implements GuvnorDefaultEditorView {

    private final DefaultFileEditorPresenter presenter;

    @Inject
    public GuvnorDefaultEditorViewImpl( final DefaultFileEditorPresenter presenter ) {
        this.presenter = presenter;
        initWidget( this.presenter.view );
    }

    @Override
    public void setNotDirty() {
        //Nothing to do
    }

    @Override
    public void onStartup( final ObservablePath path ) {
        presenter.onStartup( path );
    }
}
