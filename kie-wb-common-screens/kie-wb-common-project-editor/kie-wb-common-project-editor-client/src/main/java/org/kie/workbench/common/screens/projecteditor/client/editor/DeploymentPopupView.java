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

package org.kie.workbench.common.screens.projecteditor.client.editor;


import org.kie.server.controller.api.model.spec.ServerTemplate;

import java.util.Collection;

public interface DeploymentPopupView {

    void init(Presenter presenter);

    void show();

    void hide();

    void clearValidations();

    String getContainerId();

    void initContainerId(String containerId, boolean modifyConfig);

    String getContainerAlias();

    void initContainerAlias(String containerAlias, boolean modifyConfig);

    void initStartContainer(boolean container, boolean startContainer);

    String getServerTemplate();

    void disableServerTemplates();

    void initServerTemplates(Collection<ServerTemplate> allServerTemplates, ServerTemplate serverTemplate);

    boolean isStartContainer();

    void invalidateContainerId(String message);

    void invalidateContainerAlias(String message);

    void invalidateContainerServerTemplate(String message);

    interface Presenter {

        void onOk();

        void onCancel();
    }
}
