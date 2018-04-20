/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.list;

public abstract class ListItemPresenter<T, ParentPresenter, View extends ListItemView<? extends ListItemPresenter<T, ParentPresenter, View>>> {

    public final View view;

    private ListPresenter<T, ? extends ListItemPresenter<T, ?, ?>> listPresenter;

    public ListItemPresenter(final View view) {
        this.view = view;
    }

    public abstract ListItemPresenter<T, ?, ?> setup(final T object,
                                                     final ParentPresenter parentPresenter);

    public abstract T getObject();

    public View getView() {
        return view;
    }

    public void setListPresenter(final ListPresenter<T, ? extends ListItemPresenter<T, ?, ?>> listPresenter) {
        this.listPresenter = listPresenter;
    }

    public void remove() {
        listPresenter.remove(this);
    }
}
