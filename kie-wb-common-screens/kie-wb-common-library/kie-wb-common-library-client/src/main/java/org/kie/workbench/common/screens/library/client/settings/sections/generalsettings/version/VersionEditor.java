/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.sections.generalsettings.version;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class VersionEditor implements VersionEditorView.Presenter,
                                      IsElement {

    public static final String SNAPSHOT = "-SNAPSHOT";

    private final VersionEditorView view;

    private String version;

    private ParameterizedCommand<String> changeVersionCommand;

    @Inject
    public VersionEditor(VersionEditorView view) {
        this.view = view;

        view.init(this);
    }

    public void setUpVersion(String version, ParameterizedCommand<String> changeVersionCommand) {
        PortablePreconditions.checkNotNull("version", version);
        PortablePreconditions.checkNotNull("changeVersionCommand", changeVersionCommand);

        this.changeVersionCommand = changeVersionCommand;

        setupDevMode(version);
    }

    private void setupDevMode(String version) {

        this.version = version;

        // Checking if the new version is an snapshot
        boolean isSnapshot = isSnapshot(version);

        // If it is, we remove the SNAPSHOT text
        if (isSnapshot) {
            version = removeSnapshot(version);
        }

        // Setting the clean version to the view input
        view.setVersion(version);

        // Enabling the dev mode controls on the view (toggle switch and input group addon)
        view.setDevelopmentMode(isSnapshot);
    }

    private boolean isSnapshot(String version) {
        return version.toUpperCase().endsWith(SNAPSHOT);
    }

    private String removeSnapshot(String version) {
        if (isSnapshot(version)) {
            int index = version.toUpperCase().indexOf(SNAPSHOT);
            return version.substring(0, index);
        }
        return version;
    }

    @Override
    public void toggleDevelopmentMode(boolean selected) {
        if (selected != isSnapshot(version)) {
            if (selected) {
                doSetVersion(version + SNAPSHOT);
            } else {
                doSetVersion(removeSnapshot(version));
            }
        }
    }

    @Override
    public void notifyVersionChange(String version) {

        // Try to normalize the version, in case it has wrong values
        version = normalizeVersion(version);

        // Checking if the new version is an snapshot
        boolean snapshot = isSnapshot(version);

        // If new version isn't an snapshot but the gav is snapshot, we change it to snapshot
        if (!snapshot && view.isDevelopmentMode()) {
            version += SNAPSHOT;
        }

        doSetVersion(version);
    }

    private void doSetVersion(final String version) {
        setupDevMode(version);

        if (changeVersionCommand != null) {
            changeVersionCommand.execute(version);
        }
    }

    private String normalizeVersion(String version) {
        if (isSnapshot(version)) {
            return removeSnapshot(version) + SNAPSHOT;
        }

        return version;
    }

    public String getVersion() {
        return view.getVersion();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
