/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.repository.list;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.ioc.client.container.IOC;

@Dependent
public class RepositoriesViewImpl
        extends Composite
        implements RepositoriesView,
                   RequiresResize {

    private static RepositoriesEditorViewBinder uiBinder = GWT.create(RepositoriesEditorViewBinder.class);

    @UiField
    public FlowPanel panel;

    private RepositoriesPresenter presenter;

    public RepositoriesViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public RepositoryItemPresenter addRepository(final Repository repository,
                                                 final String branch) {
        final RepositoryItemPresenter item = IOC.getBeanManager().lookupBean(RepositoryItemPresenter.class).newInstance();

        item.setRepository(repository,
                           branch);
        panel.add(item);

        return item;
    }

    @Override
    public void removeIfExists(final RepositoryItemPresenter repositoryItem) {
        panel.remove(repositoryItem);
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void setPresenter(final RepositoriesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        panel.setPixelSize(width,
                           height);
    }

    interface RepositoriesEditorViewBinder
            extends
            UiBinder<Widget, RepositoriesViewImpl> {

    }
}