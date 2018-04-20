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

package org.kie.workbench.common.screens.library.client.settings.sections.persistence.persistabledataobjects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemView;

@Dependent
public class PersistableDataObjectsItemPresenter extends ListItemPresenter<String, PersistencePresenter, PersistableDataObjectsItemPresenter.View> {

    public interface View extends ListItemView<PersistableDataObjectsItemPresenter>,
                                  IsElement {

        void setClassName(String className);
    }

    PersistencePresenter parentPresenter;
    private String className;

    @Inject
    public PersistableDataObjectsItemPresenter(final View view) {
        super(view);
    }

    public PersistableDataObjectsItemPresenter setup(final String className,
                                                     final PersistencePresenter parentPresenter) {

        this.parentPresenter = parentPresenter;
        this.className = className;

        view.init(this);
        view.setClassName(className);

        return this;
    }

    @Override
    public String getObject() {
        return className;
    }

    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }

    public View getView() {
        return view;
    }
}
