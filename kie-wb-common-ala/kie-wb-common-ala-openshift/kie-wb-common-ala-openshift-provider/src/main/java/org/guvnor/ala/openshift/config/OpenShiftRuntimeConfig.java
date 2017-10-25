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
package org.guvnor.ala.openshift.config;

import org.guvnor.ala.config.ProvisioningConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.openshift.access.OpenShiftRuntimeId;

/**
 * OpenShift provisioning/runtime configuration.
 * @see ProvisioningConfig
 * @see RuntimeConfig
 */
public interface OpenShiftRuntimeConfig extends ProvisioningConfig, RuntimeConfig {

    default String getApplicationName() {
        return OpenShiftProperty.APPLICATION_NAME.inputExpression();
    }

    default String getKieServerContainerDeployment() {
        return OpenShiftProperty.KIE_SERVER_CONTAINER_DEPLOYMENT.inputExpression();
    }

    default String getProjectName() {
        return OpenShiftProperty.PROJECT_NAME.inputExpression();
    }

    default String getResourceSecretsUri() {
        return OpenShiftProperty.RESOURCE_SECRETS_URI.inputExpression();
    }

    default String getResourceStreamsUri() {
        return OpenShiftProperty.RESOURCE_STREAMS_URI.inputExpression();
    }

    default String getResourceTemplateName() {
        return OpenShiftProperty.RESOURCE_TEMPLATE_NAME.inputExpression();
    }

    default String getResourceTemplateParamDelimiter() {
        return OpenShiftProperty.RESOURCE_TEMPLATE_PARAM_DELIMITER.inputExpression();
    }

    default String getResourceTemplateParamAssigner() {
        return OpenShiftProperty.RESOURCE_TEMPLATE_PARAM_ASSIGNER.inputExpression();
    }

    default String getResourceTemplateParamValues() {
        return OpenShiftProperty.RESOURCE_TEMPLATE_PARAM_VALUES.inputExpression();
    }

    default String getResourceTemplateUri() {
        return OpenShiftProperty.RESOURCE_TEMPLATE_URI.inputExpression();
    }

    default String getServiceName() {
        return OpenShiftProperty.SERVICE_NAME.inputExpression();
    }

    default OpenShiftRuntimeId getRuntimeId() {
        return new OpenShiftRuntimeId(getProjectName(), getServiceName(), getApplicationName());
    }

}
