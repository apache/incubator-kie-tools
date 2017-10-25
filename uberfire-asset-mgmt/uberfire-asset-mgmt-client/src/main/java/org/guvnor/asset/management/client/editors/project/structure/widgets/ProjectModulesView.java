/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;

public interface ProjectModulesView extends IsWidget {

    enum ViewMode {
        MODULES_VIEW,
        PROJECTS_VIEW
    }

    interface Presenter {

        void onAddModule();

        void addDataDisplay(final HasData<ProjectModuleRow> display);

        void onDeleteModule(final ProjectModuleRow moduleRow);

        void onEditModule(final ProjectModuleRow moduleRow);
    }

    void setPresenter(final Presenter presenter);

    void setMode(final ViewMode mode);

    void enableActions(final boolean value);
}
