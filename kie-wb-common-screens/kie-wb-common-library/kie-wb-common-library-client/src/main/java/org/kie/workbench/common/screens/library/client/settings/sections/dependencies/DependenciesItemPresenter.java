/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.TransitiveEnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.client.mvp.UberElemental;

public class DependenciesItemPresenter {

    public interface View extends UberElemental<DependenciesItemPresenter> {

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void setPackagesWhiteListedState(final WhiteListedPackagesState state);

        void setTransitiveDependency(final boolean disabled);
    }

    private final View view;

    DependenciesPresenter parentPresenter;
    EnhancedDependency enhancedDependency;

    @Inject
    public DependenciesItemPresenter(final View view) {
        this.view = view;
    }

    public DependenciesItemPresenter setup(final EnhancedDependency enhancedDependency,
                                           final WhiteList whiteList,
                                           final DependenciesPresenter dependenciesPresenter) {

        this.enhancedDependency = enhancedDependency;
        this.parentPresenter = dependenciesPresenter;

        final Dependency dependency = enhancedDependency.getDependency();

        view.init(this);
        view.setGroupId(dependency.getGroupId());
        view.setArtifactId(dependency.getArtifactId());
        view.setVersion(dependency.getVersion());
        view.setPackagesWhiteListedState(WhiteListedPackagesState.from(whiteList, enhancedDependency.getPackages()));
        view.setTransitiveDependency(enhancedDependency instanceof TransitiveEnhancedDependency);

        return this;
    }

    public void addAllPackagesToWhiteList() {
        parentPresenter.addAllToWhiteList(enhancedDependency.getPackages());
    }

    public void removeAllPackagesFromWhiteList() {
        parentPresenter.removeAllFromWhiteList(enhancedDependency.getPackages());
    }

    public void remove() {
        parentPresenter.remove(enhancedDependency);
    }

    public View getView() {
        return view;
    }

    public enum WhiteListedPackagesState {
        ALL,
        SOME,
        NONE;

        public static WhiteListedPackagesState from(final Set<String> whiteList,
                                                    final Set<String> packages) {

            if (whiteList.containsAll(packages)) {
                return ALL;
            }

            if (!Collections.disjoint(whiteList, packages)) {
                return SOME;
            }

            return NONE;
        }
    }
}
