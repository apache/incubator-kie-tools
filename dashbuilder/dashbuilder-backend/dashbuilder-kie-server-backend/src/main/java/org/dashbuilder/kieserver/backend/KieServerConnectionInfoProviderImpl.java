/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver.backend;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.kieserver.KieServerConnectionInfo;
import org.dashbuilder.kieserver.KieServerConnectionInfoProvider;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.jboss.errai.bus.server.annotations.Service;

/**
 * Provide access to user configured connection provider
 *
 */
@Service
@ApplicationScoped
public class KieServerConnectionInfoProviderImpl implements KieServerConnectionInfoProvider {

    static final String CONFIGURATION_NOT_FOUND_MESSAGE = "Configuration for dataset %s / server template %s not found";
    static final String MISSING_URL_MESSAGE = "URL configuration for dataset %s / server template %s is missing";
    static final String MISSING_AUTH_MESSAGE = "Auth configuration for dataset %s / server template %s is missing." +
                                               "You should provide user/password or token authentication";

    private static final String SERVER_TEMPLATE_SEPARATOR = ",";
    static final String SERVER_TEMPLATE_LIST_PROPERTY = "dashbuilder.kieserver.serverTemplates";
    static final String SERVER_TEMPLATE_PROP_PREFFIX = "dashbuilder.kieserver.serverTemplate";
    static final String DATASET_PROP_PREFFIX = "dashbuilder.kieserver.dataset";

    private static final String SERVER_TEMPLATE_PROPERTY_TEMPLATE = SERVER_TEMPLATE_PROP_PREFFIX + ".%s.%s";
    private static final String REMOTE_DATASET_PROPERTY_TEMPLATE = DATASET_PROP_PREFFIX + ".%s.%s";

    public static enum KieServerConfigurationKey {

        LOCATION("location"),
        USER("user"),
        PASSWORD("password"),
        REPLACE_QUERY("replace_query"),
        TOKEN("token");

        private KieServerConfigurationKey(String value) {
            this.value = value;
        }

        private String value;

        public String getValue() {
            return this.value;
        }

    }

    @Override
    public List<String> serverTemplates() {
        return Optional.ofNullable(System.getProperty(SERVER_TEMPLATE_LIST_PROPERTY))
                       .map(templates -> templates.split(SERVER_TEMPLATE_SEPARATOR))
                       .map(template -> Arrays.stream(template)
                                              .map(String::trim)
                                              .filter(s -> !s.isEmpty())
                                              .collect(Collectors.toList()))
                       .orElse(Collections.emptyList());
    }

    @Override
    public KieServerConnectionInfo verifiedConnectionInfo(RemoteDataSetDef def) {
        String name = def.getName();
        String serverTemplateId = def.getServerTemplateId();
        String missingConfigError = String.format(CONFIGURATION_NOT_FOUND_MESSAGE, name, serverTemplateId);
        String missingUrlError = String.format(MISSING_URL_MESSAGE, name, serverTemplateId);
        String missingAuthError = String.format(MISSING_AUTH_MESSAGE, name, serverTemplateId);
        KieServerConnectionInfo connectionInfo = get(name, serverTemplateId).orElseThrow(() -> new RuntimeException(missingConfigError));

        if (!connectionInfo.getLocation().isPresent()) {
            throw new RuntimeException(missingUrlError);
        }

        if (!connectionInfo.getUser().isPresent() &&
            !connectionInfo.getToken().isPresent()) {
            throw new RuntimeException(missingAuthError);
        }
        return connectionInfo;
    }

    @Override
    public Optional<KieServerConnectionInfo> get(String name,
                                                 String serverTemplate) {

        Optional<KieServerConnectionInfo> optional = get(name, this::remoteDatasetProperty);
        if (!optional.isPresent()) {
            optional = get(serverTemplate, this::serverTemplateProperty);
        }
        return optional;
    }

    public Optional<String> serverTemplateProperty(String serverTemplate,
                                                   KieServerConfigurationKey configurationKey) {
        String property = String.format(SERVER_TEMPLATE_PROPERTY_TEMPLATE,
                                        serverTemplate,
                                        configurationKey.value);
        return filteredProperty(property);
    }

    public Optional<String> remoteDatasetProperty(String datasetUUID,
                                                  KieServerConfigurationKey configurationKey) {
        String property = String.format(REMOTE_DATASET_PROPERTY_TEMPLATE,
                                        datasetUUID,
                                        configurationKey.value);
        return filteredProperty(property);
    }

    private Optional<String> filteredProperty(String property) {
        return Optional.ofNullable(System.getProperty(property)).filter(v -> !v.trim().isEmpty());
    }

    private Optional<KieServerConnectionInfo> get(String confType,
                                                  BiFunction<String, KieServerConfigurationKey, Optional<String>> propertyProvider) {

        Optional<String> url = propertyProvider.apply(confType, KieServerConfigurationKey.LOCATION);
        Optional<String> user = propertyProvider.apply(confType, KieServerConfigurationKey.USER);
        Optional<String> password = propertyProvider.apply(confType, KieServerConfigurationKey.PASSWORD);
        Optional<String> token = propertyProvider.apply(confType, KieServerConfigurationKey.TOKEN);
        Optional<String> replaceQueryOp = propertyProvider.apply(confType, KieServerConfigurationKey.REPLACE_QUERY);

        boolean noPropertyFound = Stream.of(url, user, password, token, replaceQueryOp)
                                        .noneMatch(Optional::isPresent);

        if (noPropertyFound) {
            return Optional.empty();
        }

        boolean replaceQuery = replaceQueryOp.isPresent() && Boolean.TRUE.toString().equalsIgnoreCase(replaceQueryOp.get());

        return Optional.of(new KieServerConnectionInfo(url, user, password, token, replaceQuery));
    }

}