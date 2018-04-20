/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.sections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemView;

@Dependent
public class MenuItem<T> extends ListItemPresenter<Section<T>, SectionManager<T>, MenuItem.View<T>> {

    private final View<T> view;

    private Section<T> section;
    private SectionManager<T> sectionManager;

    @Inject
    public MenuItem(final View<T> view) {
        super(view);
        this.view = view;
    }

    public void showSection() {
        sectionManager.goTo(section);
    }

    public void markAsDirty(final boolean dirty) {
        view.markAsDirty(dirty);
    }

    @Override
    public MenuItem<T> setup(final Section<T> section,
                             final SectionManager<T> settingsPresenter) {

        this.section = section;
        this.sectionManager = settingsPresenter;

        this.view.init(this);
        this.view.setLabel(section.getView().getTitle());

        return this;
    }

    @Override
    public Section<T> getObject() {
        return section;
    }

    public void setActive() {
        getView().setActive();
    }

    public interface View<T> extends ListItemView<MenuItem<T>>,
                                     IsElement {

        void setLabel(final String label);

        void markAsDirty(final boolean dirty);

        void setActive();
    }
}
