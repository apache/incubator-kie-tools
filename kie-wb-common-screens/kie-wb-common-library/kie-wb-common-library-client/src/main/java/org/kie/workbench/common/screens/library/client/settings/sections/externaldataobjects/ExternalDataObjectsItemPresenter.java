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

package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;
import org.kie.workbench.common.widgets.configresource.client.widget.BuiltInTypeImportHelper;

public class ExternalDataObjectsItemPresenter extends ListItemPresenter<Import, ExternalDataObjectsPresenter, ExternalDataObjectsItemPresenter.View> {

    private Import import_;

    ExternalDataObjectsPresenter parentPresenter;

    public interface View extends ListItemView<ExternalDataObjectsItemPresenter>,
                                  IsElement {

        void setTypeName(final String typeName);

        void hideRemoveButton();

        void showRemoveButton();
    }

    @Inject
    public ExternalDataObjectsItemPresenter(final View view) {
        super(view);
    }

    @Override
    public ExternalDataObjectsItemPresenter setup(final Import import_,
                                                  final ExternalDataObjectsPresenter parentPresenter) {

        this.import_ = import_;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setTypeName(import_.getType());

        final String typeName = import_.getType();
        setRemoveButtonVisibility(typeName);

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }

    public void onTypeNameChange(final String typeName) {
        import_.setType(typeName);
        parentPresenter.fireChangeEvent();
        setRemoveButtonVisibility(typeName);
    }

    @Override
    public Import getObject() {
        return import_;
    }

    public View getView() {
        return view;
    }

    private void setRemoveButtonVisibility(final String typeName) {
        if (BuiltInTypeImportHelper.isBuiltIn(typeName)) {
            view.hideRemoveButton();
        } else {
            view.showRemoveButton();
        }
    }
}
