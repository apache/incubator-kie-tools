/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.repository.structure;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataView;
import org.guvnor.asset.management.client.editors.repository.structure.configure.ConfigureScreenPopupViewImpl;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface RepositoryStructureView
        extends HasBusyIndicator,
                IsWidget {

    interface Presenter {

        void clearView();

        void setModel(RepositoryStructureModel model);

        void loadModel(final Repository repository,
                       final String branch);
    }

    GAV getDataPresenterGav();

    void setDataPresenterMode(final RepositoryStructureDataView.ViewMode mode);

    void setDataPresenterModel(final GAV gav);

    void clearDataView();

    void setPresenter(final RepositoryStructurePresenter repositoryStructurePresenter);

    ProjectModulesView getModulesView();

    void setModulesViewVisible(final boolean visible);

    void clear();

    ConfigureScreenPopupViewImpl getConfigureScreenPopupView();
}
