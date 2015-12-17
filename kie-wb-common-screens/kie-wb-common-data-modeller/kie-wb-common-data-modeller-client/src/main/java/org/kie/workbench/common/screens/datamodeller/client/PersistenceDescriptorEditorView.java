/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.PersistenceUnitPropertyGrid;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassList;
import org.kie.workbench.common.screens.datamodeller.client.pdescriptor.ProjectClassListView;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface PersistenceDescriptorEditorView
        extends KieEditorView {


    String getPersistenceUnitName();

    void setPersistenceUnitName( String persistenceUnitName );

    String getPersistenceProvider();

    void setPersistenceProvider( String persistenceProvider );

    String getJTADataSource();

    void setJTADataSource( String jtaDataSource );

    boolean getJTATransactions();

    void setJTATransactions( boolean jtaTransactions );

    boolean getResourceLocalTransactions();

    void setResourceLocalTransactions( boolean resourceLocalTransactions );

    void setResourceLocalTransactionsVisible( boolean visible );

    void setTransactionTypeHelpMessage( String message );

    void setSource( String source );

    void setPresenter( Presenter presenter );

    void clear();

    void redraw();

    void setReadOnly( boolean readOnly );

    Widget getSourceEditor();

    PersistenceUnitPropertyGrid getPersistenceUnitProperties();

    ProjectClassList getPersistenceUnitClasses();

    interface Presenter extends ProjectClassListView.LoadClassesHandler {

        void onPersistenceUnitNameChange();

        void onPersistenceProviderChange();

        void onJTADataSourceChange();

        void onJTATransactionsChange();

        void onResourceLocalTransactionsChange();
    }
}