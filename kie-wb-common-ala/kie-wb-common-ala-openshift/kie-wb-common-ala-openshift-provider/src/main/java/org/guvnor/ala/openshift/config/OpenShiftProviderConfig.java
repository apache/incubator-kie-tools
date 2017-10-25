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

import org.guvnor.ala.config.ProviderConfig;

/**
 * OpenShift provider configuration.
 * @see ProviderConfig
 */
public interface OpenShiftProviderConfig extends ProviderConfig {

    default String getName() {
        return OpenShiftProperty.PROVIDER_NAME.inputExpression();
    }

    default String getAllProxy() {
        return OpenShiftProperty.ALL_PROXY.inputExpression();
    }

    default String getHttpProxy() {
        return OpenShiftProperty.HTTP_PROXY.inputExpression();
    }

    default String getHttpsProxy() {
        return OpenShiftProperty.HTTPS_PROXY.inputExpression();
    }

    default String getKubernetesApiVersion() {
        return OpenShiftProperty.KUBERNETES_API_VERSION.inputExpression();
    }

    default String getKubernetesAuthBasicPassword() {
        return OpenShiftProperty.KUBERNETES_AUTH_BASIC_PASSWORD.inputExpression();
    }

    default String getKubernetesAuthBasicUsername() {
        return OpenShiftProperty.KUBERNETES_AUTH_BASIC_USERNAME.inputExpression();
    }

    default String getKubernetesAuthToken() {
        return OpenShiftProperty.KUBERNETES_AUTH_TOKEN.inputExpression();
    }

    default String getKubernetesCertsCaData() {
        return OpenShiftProperty.KUBERNETES_CERTS_CA_DATA.inputExpression();
    }

    default String getKubernetesCertsCaFile() {
        return OpenShiftProperty.KUBERNETES_CERTS_CA_FILE.inputExpression();
    }

    default String getKubernetesCertsClientData() {
        return OpenShiftProperty.KUBERNETES_CERTS_CLIENT_DATA.inputExpression();
    }

    default String getKubernetesCertsClientFile() {
        return OpenShiftProperty.KUBERNETES_CERTS_CLIENT_FILE.inputExpression();
    }

    default String getKubernetesCertsClientKeyAlgo() {
        return OpenShiftProperty.KUBERNETES_CERTS_CLIENT_KEY_ALGO.inputExpression();
    }

    default String getKubernetesCertsClientKeyData() {
        return OpenShiftProperty.KUBERNETES_CERTS_CLIENT_KEY_DATA.inputExpression();
    }

    default String getKubernetesCertsClientKeyFile() {
        return OpenShiftProperty.KUBERNETES_CERTS_CLIENT_KEY_FILE.inputExpression();
    }

    default String getKubernetesCertsClientKeyPassphrase() {
        return OpenShiftProperty.KUBERNETES_CERTS_CLIENT_KEY_PASSPHRASE.inputExpression();
    }

    default String getKubernetesConnectionTimeout() {
        return OpenShiftProperty.KUBERNETES_CONNECTION_TIMEOUT.inputExpression();
    }

    default String getKubernetesKeystoreFile() {
        return OpenShiftProperty.KUBERNETES_KEYSTORE_FILE.inputExpression();
    }

    default String getKubernetesKeystorePassphrase() {
        return OpenShiftProperty.KUBERNETES_KEYSTORE_PASSPHRASE.inputExpression();
    }

    default String getKubernetesLoggingInterval() {
        return OpenShiftProperty.KUBERNETES_LOGGING_INTERVAL.inputExpression();
    }

    default String getKubernetesMaster() {
        return OpenShiftProperty.KUBERNETES_MASTER.inputExpression();
    }

    default String getKubernetesNamespace() {
        return OpenShiftProperty.KUBERNETES_NAMESPACE.inputExpression();
    }

    default String getKubernetesOapiVersion() {
        return OpenShiftProperty.KUBERNETES_OAPI_VERSION.inputExpression();
    }

    default String getKubernetesRequestTimeout() {
        return OpenShiftProperty.KUBERNETES_REQUEST_TIMEOUT.inputExpression();
    }

    default String getKubernetesRollingTimeout() {
        return OpenShiftProperty.KUBERNETES_ROLLING_TIMEOUT.inputExpression();
    }

    default String getKubernetesScaleTimeout() {
        return OpenShiftProperty.KUBERNETES_SCALE_TIMEOUT.inputExpression();
    }

    default String getKubernetesTlsVersions() {
        return OpenShiftProperty.KUBERNETES_TLS_VERSIONS.inputExpression();
    }

    default String getKubernetesTrustCertificates() {
        return OpenShiftProperty.KUBERNETES_TRUST_CERTIFICATES.inputExpression();
    }

    default String getKubernetesTruststoreFile() {
        return OpenShiftProperty.KUBERNETES_TRUSTSTORE_FILE.inputExpression();
    }

    default String getKubernetesTruststorePassphrase() {
        return OpenShiftProperty.KUBERNETES_TRUSTSTORE_PASSPHRASE.inputExpression();
    }

    default String getKubernetesUserAgent() {
        return OpenShiftProperty.KUBERNETES_USER_AGENT.inputExpression();
    }

    default String getKubernetesWatchReconnectInterval() {
        return OpenShiftProperty.KUBERNETES_WATCH_RECONNECT_INTERVAL.inputExpression();
    }

    default String getKubernetesWatchReconnectLimit() {
        return OpenShiftProperty.KUBERNETES_WATCH_RECONNECT_LIMIT.inputExpression();
    }

    default String getKubernetesWebsocketPingInterval() {
        return OpenShiftProperty.KUBERNETES_WEBSOCKET_PING_INTERVAL.inputExpression();
    }

    default String getKubernetesWebsocketTimeout() {
        return OpenShiftProperty.KUBERNETES_WEBSOCKET_TIMEOUT.inputExpression();
    }

    default String getNoProxy() {
        return OpenShiftProperty.NO_PROXY.inputExpression();
    }

    default String getOpenshiftBuildTimeout() {
        return OpenShiftProperty.OPENSHIFT_BUILD_TIMEOUT.inputExpression();
    }

    default String getOpenshiftUrl() {
        return OpenShiftProperty.OPENSHIFT_URL.inputExpression();
    }

    default String getProxyPassword() {
        return OpenShiftProperty.PROXY_PASSWORD.inputExpression();
    }

    default String getProxyUsername() {
        return OpenShiftProperty.PROXY_USERNAME.inputExpression();
    }

}
