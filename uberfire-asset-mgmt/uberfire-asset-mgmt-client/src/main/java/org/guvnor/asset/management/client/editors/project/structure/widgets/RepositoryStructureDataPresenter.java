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

package org.guvnor.asset.management.client.editors.project.structure.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;

@Dependent
public class RepositoryStructureDataPresenter
        implements IsWidget {

    private final RepositoryStructureDataView view;

    @Inject
    public RepositoryStructureDataPresenter(final RepositoryStructureDataView view) {
        this.view = view;

        view.clear();
        setMode(RepositoryStructureDataView.ViewMode.CREATE_STRUCTURE);
    }

    public void setMode(final RepositoryStructureDataView.ViewMode mode) {

        if (mode == RepositoryStructureDataView.ViewMode.CREATE_STRUCTURE) {
            view.setCreateStructureText();
        } else if (mode == RepositoryStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT) {
            view.setEditSingleModuleProjectText();

            view.setEditModuleVisibility(true);
        } else if (mode == RepositoryStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT) {
            view.setEditMultiModuleProjectText();

            view.setEditModuleVisibility(true);
        } else if (mode == RepositoryStructureDataView.ViewMode.EDIT_UNMANAGED_REPOSITORY) {
            view.setEditUnmanagedRepositoryText();

            view.setEditModuleVisibility(false);
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void clear() {
        view.clear();
    }

    public GAV getGav() {
        return new GAV(view.getGroupId(),
                       view.getArtifactId(),
                       view.getVersion());
    }

    public void setGav(final GAV gav) {
        view.setGroupId(gav.getGroupId());
        view.setArtifactId(gav.getArtifactId());
        view.setVersion(gav.getVersion());
    }
}
