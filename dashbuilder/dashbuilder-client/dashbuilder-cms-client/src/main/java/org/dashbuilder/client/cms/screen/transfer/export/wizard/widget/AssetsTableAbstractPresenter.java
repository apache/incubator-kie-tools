/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.screen.transfer.export.wizard.widget;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;

public abstract class AssetsTableAbstractPresenter<T> implements AssetsTableView.Presenter<T> {

    @Inject
    AssetsTableView view;

    @Inject
    ManagedInstance<AssetsTableView> viewManagedInstance;

    private List<T> data;

    @PostConstruct
    public void init() {
        view = viewManagedInstance.get();
        view.init(this);
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public void setData(List<T> data) {
        this.data = data;
        view.update();
    }

    @Override
    public List<T> getSelectedData() {
        return (List<T>) view.getSelectedAssets();
    }

    public HTMLElement getElement() {
        return view.getElement();
    }
    
    public void refresh() {
        view.clearFilter();
    }

    @PreDestroy
    public void destroyView() {
        viewManagedInstance.destroyAll();
    }

}