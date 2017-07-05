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

package org.kie.workbench.common.screens.datasource.management.client.editor.driver;

import org.uberfire.client.mvp.UberElement;

public interface DriverDefMainPanelView extends UberElement<DriverDefMainPanelView.Presenter> {

    interface Presenter extends Handler {

    }

    interface Handler {

        void onNameChange();

        void onDriverClassChange();

        void onGroupIdChange();

        void onArtifactIdChange();

        void onVersionChange();
    }

    void setName(final String text);

    String getName();

    void setNameErrorMessage(final String message);

    void clearNameErrorMessage();

    void setDriverClass(final String driverClass);

    String getDriverClass();

    void setDriverClassErrorMessage(final String message);

    void clearDriverClassErrorMessage();

    void setGroupId(final String groupId);

    String getGroupId();

    void setGroupIdErrorMessage(final String message);

    void clearGroupIdErrorMessage();

    void setArtifactId(final String artifactId);

    String getArtifactId();

    void setArtifactIdErrorMessage(final String message);

    void clearArtifactIdErrorMessage();

    void setVersion(String version);

    String getVersion();

    void setVersionErrorMessage(final String message);

    void clearVersionErrorMessage();
}
