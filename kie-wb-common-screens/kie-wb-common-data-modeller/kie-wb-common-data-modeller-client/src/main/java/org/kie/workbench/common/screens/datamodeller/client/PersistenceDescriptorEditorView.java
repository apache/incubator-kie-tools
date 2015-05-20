/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassListView;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface PersistenceDescriptorEditorView
        extends KieEditorView {

    void setContent( PersistenceDescriptorEditorContent content, boolean readonly );

    PersistenceDescriptorEditorContent getContent();

    void setSource( String source );

    void setPresenter( Presenter presenter );

    Widget getSourceEditor();

    void loadClasses( List<String> classes );

    interface Presenter extends ProjectClassListView.LoadClassesHandler {

        void onPersistenceUnitNameChanged( String value );

        void onPersistenceProviderChanged( String value );

        void onJTADataSourceChanged( String value );
    }
}