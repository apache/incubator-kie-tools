/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor.editingbox;

import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionView;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.PropertyPresenter;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

public abstract class EditingBoxPresenter implements EditingBox.Presenter {

    @Inject
    protected ViewsProvider viewsProvider;

    @Inject
    protected PropertyPresenter propertyPresenter;

    protected CollectionView.Presenter collectionEditorPresenter;

    @Override
    public void setCollectionEditorPresenter(CollectionView.Presenter collectionEditorPresenter) {
        this.collectionEditorPresenter = collectionEditorPresenter;
    }

    @Override
    public void close(EditingBox toClose) {
        toClose.getEditingBox().removeFromParent();
        collectionEditorPresenter.toggleEditingStatus(false);
    }

}
