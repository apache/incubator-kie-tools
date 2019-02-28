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

import java.util.Collection;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.server.api.model.KieServerMode;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

@Dependent
public class DeploymentPopup implements DeploymentPopupView.Presenter {

    private DeploymentPopupView view;
    private Driver driver;

    @Inject
    public DeploymentPopup(DeploymentPopupView view) {
        this.view = view;
        view.init(this);
    }

    public void show(Driver driver) {
        PortablePreconditions.checkNotNull("driver", driver);

        this.driver = driver;

        view.initContainerId(driver.getContainerId(), driver.getMode().isModifyConfig());
        view.initContainerAlias(driver.getContainerAlias(), driver.getMode().isModifyConfig());
        view.initStartContainer(driver.isStartContainer(), driver.getMode().isModifyConfig());

        if (driver.getMode().isSelectTemplate()) {
            view.initServerTemplates(driver.getAllServerTemplates(), driver.getServerTemplate());
        } else {
            view.disableServerTemplates();
        }

        view.show();
    }

    @Override
    public void onOk() {
        boolean valid = true;

        final ProjectEditorConstants constants = ProjectEditorResources.CONSTANTS;

        view.clearValidations();

        if (driver.getMode().isModifyConfig()) {
            if (isEmpty(view.getContainerId())) {
                valid = false;

                view.invalidateContainerId(constants.FieldMandatory0(constants.ContainerId()));
            }

            if (isContainerIdInUse(view.getContainerId())) {
                valid = false;

                view.invalidateContainerId(constants.ContainerIdAlreadyInUse());
            }

            if (isEmpty(view.getContainerAlias())) {
                valid = false;

                view.invalidateContainerAlias(constants.FieldMandatory0(constants.ContainerAlias()));
            }
        }

        if (driver.getMode().isSelectTemplate()) {
            if (isEmpty(view.getServerTemplate())) {
                valid = false;

                view.invalidateContainerServerTemplate(constants.FieldMandatory0(constants.ServerTemplate()));
            }
        }

        if (valid) {
            view.hide();
            driver.finish(view.getContainerId(), view.getContainerAlias(), view.getServerTemplate(), view.isStartContainer());
        }
    }

    private boolean isContainerIdInUse(String containerId) {

        if (driver.getMode().isSelectTemplate()) {
            Optional<ServerTemplate> optional = driver.getAllServerTemplates().stream()
                    .filter(serverTemplate -> serverTemplate.getId().equals(view.getServerTemplate()))
                    .findAny();

            if (optional.isPresent()) {
                ServerTemplate template = optional.get();

                return template.getMode().equals(KieServerMode.PRODUCTION) && template.getContainerSpec(containerId) != null;
            }
        }

        return false;
    }

    private boolean isEmpty(String value) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    public void onCancel() {
        view.hide();
        driver.cancel();
    }

    public interface Driver {

        Mode getMode();

        String getContainerId();

        String getContainerAlias();

        Collection<ServerTemplate> getAllServerTemplates();

        ServerTemplate getServerTemplate();

        boolean isStartContainer();

        void finish(String containerId, String containerAlias, String serverTemplate, boolean startContainer);

        void cancel();
    }

    public enum Mode {
        SINGLE_SERVER(true, false),
        MULTIPLE_SERVER(true, true),
        MULTIPLE_SERVER_FORCED(false, true);

        private boolean modifyConfig;
        private boolean selectTemplate;

        Mode(boolean modifyConfig, boolean selectTemplate) {
            this.modifyConfig = modifyConfig;
            this.selectTemplate = selectTemplate;
        }

        public boolean isModifyConfig() {
            return modifyConfig;
        }

        public boolean isSelectTemplate() {
            return selectTemplate;
        }
    }
}
