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

package org.kie.workbench.common.screens.library.client.settings.sections.persistence.properties;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.library.client.settings.sections.persistence.PersistencePresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Dependent
public class PropertiesItemPresenter extends ListItemPresenter<Property, PersistencePresenter, PropertiesItemPresenter.View> {

    public interface View extends ListItemView<PropertiesItemPresenter> {

        void setName(String name);

        void setValue(String value);
    }

    PersistencePresenter parentPresenter;
    private Property property;

    @Inject
    public PropertiesItemPresenter(final View view) {
        super(view);
    }

    @Override
    public PropertiesItemPresenter setup(final Property property,
                                         final PersistencePresenter parentPresenter) {

        this.property = property;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setName(property.getName());
        view.setValue(property.getValue());

        return this;
    }

    @Override
    public Property getObject() {
        return property;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }
}
