/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.wildfly.backend.handler;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.config.impl.WildflyProviderConfigImpl;

import static org.guvnor.ala.config.ProviderConfig.PROVIDER_NAME;
import static org.guvnor.ala.ui.backend.service.util.ServiceUtil.getStringValue;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.HOST;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.MANAGEMENT_PORT;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.PORT;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.WILDFLY_PASSWORD;
import static org.guvnor.ala.wildfly.config.WildflyProviderConfig.WILDFLY_USER;

@ApplicationScoped
public class WildflyProviderConfigConverter
        implements ProviderConfigConverter<ProviderConfiguration, WildflyProviderConfig> {

    public WildflyProviderConfigConverter() {
        //Empty constructor for Weld proxying
    }

    @Override
    public Class<ProviderConfiguration> getModelType() {
        return ProviderConfiguration.class;
    }

    @Override
    public Class<WildflyProviderConfig> getDomainType() {
        return WildflyProviderConfig.class;
    }

    @Override
    public WildflyProviderConfig toDomain(ProviderConfiguration modelValue) {
        if (modelValue == null) {
            return null;
        }

        return new WildflyProviderConfigImpl(modelValue.getId(),
                                             getStringValue(modelValue.getValues(),
                                                            HOST),
                                             getStringValue(modelValue.getValues(),
                                                            PORT),
                                             getStringValue(modelValue.getValues(),
                                                            MANAGEMENT_PORT),
                                             getStringValue(modelValue.getValues(),
                                                            WILDFLY_USER),
                                             getStringValue(modelValue.getValues(),
                                                            WILDFLY_PASSWORD));
    }

    @Override
    public ProviderConfiguration toModel(WildflyProviderConfig domainValue) {
        if (domainValue == null) {
            return null;
        }

        final Map<String, Object> values = new HashMap<>();
        values.put(PROVIDER_NAME,
                   domainValue.getName());
        values.put(HOST,
                   domainValue.getHost());
        values.put(PORT,
                   domainValue.getPort());
        values.put(MANAGEMENT_PORT,
                   domainValue.getManagementPort());
        values.put(WILDFLY_USER,
                   domainValue.getUser());
        values.put(WILDFLY_PASSWORD,
                   domainValue.getPassword());
        return new ProviderConfiguration(domainValue.getName(),
                                         values);
    }
}
