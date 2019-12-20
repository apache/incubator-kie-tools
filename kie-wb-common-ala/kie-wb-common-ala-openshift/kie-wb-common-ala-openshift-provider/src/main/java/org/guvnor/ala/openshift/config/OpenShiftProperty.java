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

import io.fabric8.kubernetes.client.Config;
import io.fabric8.openshift.client.OpenShiftConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.kie.server.api.KieServerConstants;

/**
 * All OpenShift provider and runtime configuration parameters.
 */
public enum OpenShiftProperty {

    /* ---------- Provider properties: Kubernetes Client ---------- */
    ALL_PROXY(Config.KUBERNETES_ALL_PROXY),
    HTTP_PROXY(Config.KUBERNETES_HTTP_PROXY),
    HTTPS_PROXY(Config.KUBERNETES_HTTPS_PROXY),
    KUBERNETES_API_VERSION(Config.KUBERNETES_API_VERSION_SYSTEM_PROPERTY),
    KUBERNETES_AUTH_BASIC_PASSWORD(Config.KUBERNETES_AUTH_BASIC_PASSWORD_SYSTEM_PROPERTY),
    KUBERNETES_AUTH_BASIC_USERNAME(Config.KUBERNETES_AUTH_BASIC_USERNAME_SYSTEM_PROPERTY),
    KUBERNETES_AUTH_TOKEN(Config.KUBERNETES_OAUTH_TOKEN_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CA_DATA(Config.KUBERNETES_CA_CERTIFICATE_DATA_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CA_FILE(Config.KUBERNETES_CA_CERTIFICATE_FILE_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CLIENT_DATA(Config.KUBERNETES_CLIENT_CERTIFICATE_DATA_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CLIENT_FILE(Config.KUBERNETES_CLIENT_CERTIFICATE_FILE_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CLIENT_KEY_ALGO(Config.KUBERNETES_CLIENT_KEY_ALGO_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CLIENT_KEY_DATA(Config.KUBERNETES_CLIENT_KEY_DATA_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CLIENT_KEY_FILE(Config.KUBERNETES_CLIENT_KEY_FILE_SYSTEM_PROPERTY),
    KUBERNETES_CERTS_CLIENT_KEY_PASSPHRASE(Config.KUBERNETES_CLIENT_KEY_PASSPHRASE_SYSTEM_PROPERTY),
    KUBERNETES_CONNECTION_TIMEOUT(Config.KUBERNETES_CONNECTION_TIMEOUT_SYSTEM_PROPERTY),
    KUBERNETES_KEYSTORE_FILE(Config.KUBERNETES_KEYSTORE_FILE_PROPERTY),
    KUBERNETES_KEYSTORE_PASSPHRASE(Config.KUBERNETES_KEYSTORE_PASSPHRASE_PROPERTY),
    KUBERNETES_LOGGING_INTERVAL(Config.KUBERNETES_LOGGING_INTERVAL_SYSTEM_PROPERTY),
    KUBERNETES_MASTER(Config.KUBERNETES_MASTER_SYSTEM_PROPERTY),
    KUBERNETES_NAMESPACE(Config.KUBERNETES_NAMESPACE_SYSTEM_PROPERTY),
    KUBERNETES_REQUEST_TIMEOUT(Config.KUBERNETES_REQUEST_TIMEOUT_SYSTEM_PROPERTY),
    KUBERNETES_ROLLING_TIMEOUT(Config.KUBERNETES_ROLLING_TIMEOUT_SYSTEM_PROPERTY),
    KUBERNETES_SCALE_TIMEOUT(Config.KUBERNETES_SCALE_TIMEOUT_SYSTEM_PROPERTY),
    KUBERNETES_TLS_VERSIONS(Config.KUBERNETES_TLS_VERSIONS),
    KUBERNETES_TRUST_CERTIFICATES(Config.KUBERNETES_TRUST_CERT_SYSTEM_PROPERTY),
    KUBERNETES_TRUSTSTORE_FILE(Config.KUBERNETES_TRUSTSTORE_FILE_PROPERTY),
    KUBERNETES_TRUSTSTORE_PASSPHRASE(Config.KUBERNETES_TRUSTSTORE_PASSPHRASE_PROPERTY),
    KUBERNETES_USER_AGENT(Config.KUBERNETES_USER_AGENT),
    KUBERNETES_WATCH_RECONNECT_INTERVAL(Config.KUBERNETES_WATCH_RECONNECT_INTERVAL_SYSTEM_PROPERTY),
    KUBERNETES_WATCH_RECONNECT_LIMIT(Config.KUBERNETES_WATCH_RECONNECT_LIMIT_SYSTEM_PROPERTY),
    KUBERNETES_WEBSOCKET_PING_INTERVAL(Config.KUBERNETES_WEBSOCKET_PING_INTERVAL_SYSTEM_PROPERTY),
    KUBERNETES_WEBSOCKET_TIMEOUT(Config.KUBERNETES_WEBSOCKET_TIMEOUT_SYSTEM_PROPERTY),
    NO_PROXY(Config.KUBERNETES_NO_PROXY),
    PROXY_PASSWORD(Config.KUBERNETES_PROXY_PASSWORD),
    PROXY_USERNAME(Config.KUBERNETES_PROXY_USERNAME),

    /* ---------- Provider properties: OpenShift Client ---------- */
    KUBERNETES_OAPI_VERSION(OpenShiftConfig.KUBERNETES_OAPI_VERSION_SYSTEM_PROPERTY),
    OPENSHIFT_BUILD_TIMEOUT(OpenShiftConfig.OPENSHIFT_BUILD_TIMEOUT_SYSTEM_PROPERTY),
    OPENSHIFT_URL(OpenShiftConfig.OPENSHIFT_URL_SYSTEM_PROPERTY),

    /* ---------- Provider properties: Guvnor ALA API ---------- */
    PROVIDER_NAME(ProviderConfig.PROVIDER_NAME.replaceAll("-", ".")),

    /* ---------- Runtime properties: Guvnor ALA OpenShift Impl ---------- */
    APPLICATION_NAME("application.name"),
    KIE_SERVER_CONTAINER_DEPLOYMENT(KieServerConstants.KIE_SERVER_CONTAINER_DEPLOYMENT),
    PROJECT_NAME("project.name"),
    RUNTIME_NAME(RuntimeConfig.RUNTIME_NAME.replaceAll("-", ".")),
    RESOURCE_SECRETS_URI("resource.secrets.uri"),
    RESOURCE_STREAMS_URI("resource.streams.uri"),
    RESOURCE_TEMPLATE_NAME("resource.template.name"),
    RESOURCE_TEMPLATE_PARAM_DELIMITER("resource.template.param.delimiter"),
    RESOURCE_TEMPLATE_PARAM_ASSIGNER("resource.template.param.assigner"),
    RESOURCE_TEMPLATE_PARAM_VALUES("resource.template.param.values"),
    RESOURCE_TEMPLATE_URI("resource.template.uri"),
    SERVICE_NAME("service.name");

    private final String propertyKey;
    private final String inputKey;
    private final String inputExpression;

    OpenShiftProperty(String propertyKey) {
        this.propertyKey = propertyKey;
        this.inputKey = propertyKey.replaceAll("[^A-Za-z0-9]", "-");
        this.inputExpression = "${input." + this.inputKey + "}";
    }

    public final String envKey() {
        return name();
    }

    public final String propertyKey() {
        return propertyKey;
    }

    public final String inputKey() {
        return inputKey;
    }

    public final String inputExpression() {
        return inputExpression;
    }

    public static final void main(String... args) {
        for (OpenShiftProperty p : values()) {
            System.out.println(String.format("%s | %s | %s | %s", p.envKey(), p.propertyKey(), p.inputKey(), p.inputExpression()));
        }
    }

}
