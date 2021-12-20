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
package org.drools.workbench.screens.scenariosimulation.client.collectioneditor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

public abstract class ElementPresenter<E extends ElementView> implements ElementView.Presenter<E> {

    protected CollectionView.Presenter collectionEditorPresenter;

    @Inject
    protected PropertyView.Presenter propertyPresenter;

    @Inject
    protected ViewsProvider viewsProvider;

    /**
     * <code>List</code> of currently present <code>ElementView</code>s
     */
    protected List<E> elementViewList = new ArrayList<>();

    @Override
    public void setCollectionEditorPresenter(CollectionView.Presenter collectionEditorPresenter) {
        this.collectionEditorPresenter = collectionEditorPresenter;
    }

    @Override
    public void onToggleRowExpansion(boolean isShown) {
        elementViewList.forEach(elementView -> onToggleRowExpansion(elementView, isShown));
    }

    @Override
    public void updateCommonToggleStatus(boolean isShown) {
        if (elementViewList.stream()
                .allMatch(elementView ->  !isShown == elementView.isShown())) {
            collectionEditorPresenter.updateRowExpansionStatus(isShown);
        }
    }

    @Override
    public void remove() {
        List<E> newList = new ArrayList<>(elementViewList);
        newList.forEach(this::onDeleteItem);
    }

    @Override
    public void toggleEditingStatus(boolean toDisable) {
        elementViewList.forEach(element -> {
            element.getEditItemButton().setDisabled(toDisable);
            element.getDeleteItemButton().setDisabled(toDisable);
        });
    }


}
