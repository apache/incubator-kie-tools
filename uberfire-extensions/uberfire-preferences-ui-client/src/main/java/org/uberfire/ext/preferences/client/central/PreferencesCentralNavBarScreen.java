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

package org.uberfire.ext.preferences.client.central;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructurePresenter;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructureView;
import org.uberfire.ext.preferences.client.central.tree.TreeView;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.workbench.model.CompassPosition;

@WorkbenchScreen(identifier = PreferencesCentralNavBarScreen.IDENTIFIER)
public class PreferencesCentralNavBarScreen {

    public static final String IDENTIFIER = "PreferencesCentralNavBarScreen";

    private final HierarchyStructurePresenter hierarchyStructurePresenter;

    @Inject
    public PreferencesCentralNavBarScreen(@TreeView final HierarchyStructurePresenter treePresenter) {
        this.hierarchyStructurePresenter = treePresenter;
    }

    public void init(@Observes final PreferencesCentralInitializationEvent initEvent) {
        final String preferenceIdentifier = initEvent.getPreferenceIdentifier();
        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo = initEvent.getCustomScopeResolutionStrategy();
        final PreferenceScope scope = initEvent.getPreferenceScope();

        hierarchyStructurePresenter.init(preferenceIdentifier,
                                         scopeResolutionStrategyInfo,
                                         scope);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Preferences Central";
    }

    @WorkbenchPartView
    public HierarchyStructureView getView() {
        return hierarchyStructurePresenter.getView();
    }

    @DefaultPosition
    public CompassPosition getDefaultPosition() {
        return CompassPosition.WEST;
    }
}
