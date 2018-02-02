/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms.repositories;

import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.uberfire.client.mvp.UberView;

public interface RepositoriesWidgetView
        extends UberView<RepositoriesWidgetView.Presenter> {

    interface Presenter {

        void setContent(final Set<ModuleRepositories.ModuleRepository> repositories,
                        final boolean isReadOnly);

        void setIncludeRepository(final ModuleRepositories.ModuleRepository repository,
                                  final boolean include);

        Widget asWidget();
    }

    void setContent(final Set<ModuleRepositories.ModuleRepository> repositories,
                    final boolean isReadOnly);
}
