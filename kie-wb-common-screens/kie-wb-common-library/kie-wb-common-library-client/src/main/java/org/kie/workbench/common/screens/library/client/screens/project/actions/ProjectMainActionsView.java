/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.actions;

import org.uberfire.client.mvp.UberElemental;

public interface ProjectMainActionsView extends UberElemental<ProjectMainActionsView.Presenter> {

    void setBuildDropDownEnabled(boolean enabled);

    void setBuildAndDeployDropDownEnabled(boolean enabled);

    void setRedeployEnabled(boolean redeploy);

    void setViewDeploymentDetailsEnabled(boolean enabled);

    void showBusyIndicator(String message);

    void hideBusyIndicator();

    interface Presenter {

        void triggerBuild();

        void triggerBuildAndInstall();

        void triggerBuildAndDeploy();

        void triggerRedeploy();

        void onRunTest();
    }
}
