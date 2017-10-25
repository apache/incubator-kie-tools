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

package org.guvnor.common.services.project.client.repositories;

import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

public interface ConflictingRepositoriesPopupView
        extends UberView<ConflictingRepositoriesPopupView.Presenter> {

    interface Presenter {

        void setContent(final GAV gav,
                        final Set<MavenRepositoryMetadata> repositories,
                        final Command command);

        void show();

        void hide();
    }

    void clear();

    void setContent(final GAV gav,
                    final Set<MavenRepositoryMetadata> repositories);

    void addOKButton();

    void addOverrideButton(final Command command);

    void show();

    void hide();
}
