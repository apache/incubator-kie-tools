/*
 * Copyright ${year} Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.openshift.backend.handler;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.model.ProviderConfiguration;

import static org.guvnor.ala.config.ProviderConfig.PROVIDER_NAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_AUTH_BASIC_PASSWORD;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_AUTH_BASIC_USERNAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_MASTER;
import static org.guvnor.ala.ui.backend.service.util.ServiceUtil.getStringValue;

@ApplicationScoped
public class OpenShiftProviderConfigConverter
        implements ProviderConfigConverter<ProviderConfiguration, OpenShiftProviderConfig> {

    public OpenShiftProviderConfigConverter() {
        //Empty constructor for Weld proxying
    }

    @Override
    public Class<ProviderConfiguration> getModelType() {
        return ProviderConfiguration.class;
    }

    @Override
    public Class<OpenShiftProviderConfig> getDomainType() {
        return OpenShiftProviderConfig.class;
    }

    @Override
    public OpenShiftProviderConfig toDomain(ProviderConfiguration modelValue) {
        if (modelValue == null) {
            return null;
        }
        OpenShiftProviderConfigImpl openShiftProviderConfig = new OpenShiftProviderConfigImpl();
        openShiftProviderConfig.clear();

        openShiftProviderConfig.setName(modelValue.getId());
        openShiftProviderConfig.setKubernetesMaster(getStringValue(modelValue.getValues(),
                                                                   KUBERNETES_MASTER.inputKey()));
        openShiftProviderConfig.setKubernetesAuthBasicUsername(getStringValue(modelValue.getValues(),
                                                                              KUBERNETES_AUTH_BASIC_USERNAME.inputKey()));
        openShiftProviderConfig.setKubernetesAuthBasicPassword(getStringValue(modelValue.getValues(),
                                                                              KUBERNETES_AUTH_BASIC_PASSWORD.inputKey()));
        return openShiftProviderConfig;
    }

    @Override
    public ProviderConfiguration toModel(OpenShiftProviderConfig domainValue) {
        if (domainValue == null) {
            return null;
        }

        final Map<String, Object> values = new HashMap<>();
        values.put(PROVIDER_NAME,
                   domainValue.getName());

        values.put(KUBERNETES_MASTER.inputKey(),
                   domainValue.getKubernetesMaster());
        values.put(KUBERNETES_AUTH_BASIC_USERNAME.inputKey(),
                   domainValue.getKubernetesAuthBasicUsername());
        values.put(KUBERNETES_AUTH_BASIC_PASSWORD.inputKey(),
                   domainValue.getKubernetesAuthBasicPassword());

        return new ProviderConfiguration(domainValue.getName(),
                                         values);
    }
}
