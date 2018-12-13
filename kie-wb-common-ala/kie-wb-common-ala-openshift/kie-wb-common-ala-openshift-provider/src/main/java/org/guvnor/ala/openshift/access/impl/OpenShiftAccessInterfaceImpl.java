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
package org.guvnor.ala.openshift.access.impl;

import static org.kie.soup.commons.validation.Preconditions.checkInstanceOf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.utils.URLUtils;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import okhttp3.TlsVersion;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.openshift.access.OpenShiftAccessInterface;
import org.guvnor.ala.openshift.access.OpenShiftClient;
import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.openshift.model.OpenShiftProvider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.lifecycle.Disposable;

/**
 * Configures, builds, and caches the openshift client, per provider id.
 */
public class OpenShiftAccessInterfaceImpl implements OpenShiftAccessInterface,
                                                     Disposable {

    protected static final Logger LOG = LoggerFactory.getLogger(OpenShiftAccessInterfaceImpl.class);
    private final Map<String, OpenShiftClient> clientMap = new ConcurrentHashMap<>();

    @Override
    public OpenShiftClient getOpenShiftClient(final ProviderId providerId) {
        if (!clientMap.containsKey(providerId.getId())) {
            checkInstanceOf("providerId", providerId, OpenShiftProvider.class);
            ProviderConfig providerConfig = ((OpenShiftProvider) providerId).getConfig();
            OpenShiftClient client = newOpenShiftClient(providerConfig);
            clientMap.put(providerId.getId(), client);
        }
        return clientMap.get(providerId.getId());
    }

    @Override
    public OpenShiftClient newOpenShiftClient(final ProviderConfig providerConfig) {
        checkInstanceOf("providerConfig", providerConfig, OpenShiftProviderConfig.class);
        OpenShiftConfig clientConfig = buildOpenShiftConfig((OpenShiftProviderConfig) providerConfig);
        return new OpenShiftClient(new DefaultOpenShiftClient(clientConfig));
    }

    // package-protected for junit testing purposes (from inside the package)
    static OpenShiftConfig buildOpenShiftConfig(OpenShiftProviderConfig config) {
        OpenShiftConfigBuilder builder = new OpenShiftConfigBuilder(OpenShiftConfig.wrap(Config.autoConfigure(null)));
        /*
         * Kubernetes configuration properties; see io.fabric8.kubernetes.client.Config
         */
        String allProxy = trimToNull(config.getAllProxy());
        if (allProxy != null) {
            builder = builder.withHttpProxy(allProxy);
            builder = builder.withHttpsProxy(allProxy);
        }
        String httpProxy = trimToNull(config.getHttpProxy());
        if (httpProxy != null) {
            // NOTE: overrides allProxy above
            builder = builder.withHttpProxy(httpProxy);
        }
        String httpsProxy = trimToNull(config.getHttpsProxy());
        if (httpsProxy != null) {
            // NOTE: overrides allProxy above
            builder = builder.withHttpsProxy(httpsProxy);
        }
        String kubernetesApiVersion = trimToNull(config.getKubernetesApiVersion());
        if (kubernetesApiVersion != null) {
            builder = builder.withApiVersion(kubernetesApiVersion);
        }
        String kubernetesAuthBasicPassword = trimToNull(config.getKubernetesAuthBasicPassword());
        if (kubernetesAuthBasicPassword != null) {
            builder = builder.withPassword(kubernetesAuthBasicPassword);
        }
        String kubernetesAuthBasicUsername = trimToNull(config.getKubernetesAuthBasicUsername());
        if (kubernetesAuthBasicUsername != null) {
            builder = builder.withUsername(kubernetesAuthBasicUsername);
        }
        String kubernetesAuthToken = trimToNull(config.getKubernetesAuthToken());
        if (kubernetesAuthToken != null) {
            builder = builder.withOauthToken(kubernetesAuthToken);
        }
        String kubernetesCertsCaData = trimToNull(config.getKubernetesCertsCaData());
        if (kubernetesCertsCaData != null) {
            builder = builder.withCaCertData(kubernetesCertsCaData);
        }
        String kubernetesCertsCaFile = trimToNull(config.getKubernetesCertsCaFile());
        if (kubernetesCertsCaFile != null) {
            builder = builder.withCaCertFile(kubernetesCertsCaFile);
        }
        String kubernetesCertsClientData = trimToNull(config.getKubernetesCertsClientData());
        if (kubernetesCertsClientData != null) {
            builder = builder.withClientCertData(kubernetesCertsClientData);
        }
        String kubernetesCertsClientFile = trimToNull(config.getKubernetesCertsClientFile());
        if (kubernetesCertsClientFile != null) {
            builder = builder.withClientCertFile(kubernetesCertsClientFile);
        }
        String kubernetesCertsClientKeyAlgo = trimToNull(config.getKubernetesCertsClientKeyAlgo());
        if (kubernetesCertsClientKeyAlgo != null) {
            builder = builder.withClientKeyAlgo(kubernetesCertsClientKeyAlgo);
        }
        String kubernetesCertsClientKeyData = trimToNull(config.getKubernetesCertsClientKeyData());
        if (kubernetesCertsClientKeyData != null) {
            builder = builder.withClientKeyData(kubernetesCertsClientKeyData);
        }
        String kubernetesCertsClientKeyFile = trimToNull(config.getKubernetesCertsClientKeyFile());
        if (kubernetesCertsClientKeyFile != null) {
            builder = builder.withClientKeyFile(kubernetesCertsClientKeyFile);
        }
        String kubernetesCertsClientKeyPassphrase = trimToNull(config.getKubernetesCertsClientKeyPassphrase());
        if (kubernetesCertsClientKeyPassphrase != null) {
            builder = builder.withClientKeyPassphrase(kubernetesCertsClientKeyPassphrase);
        }
        String kubernetesConnectionTimeout = trimToNull(config.getKubernetesConnectionTimeout());
        if (kubernetesConnectionTimeout != null) {
            builder = builder.withConnectionTimeout(Integer.parseInt(kubernetesConnectionTimeout));
        }
        String kubernetesKeystoreFile = trimToNull(config.getKubernetesKeystoreFile());
        if (kubernetesKeystoreFile != null) {
            builder = builder.withKeyStoreFile(kubernetesKeystoreFile);
        }
        String kubernetesKeystorePassphrase = trimToNull(config.getKubernetesKeystorePassphrase());
        if (kubernetesKeystorePassphrase != null) {
            builder = builder.withKeyStorePassphrase(kubernetesKeystorePassphrase);
        }
        String kubernetesLoggingInterval = trimToNull(config.getKubernetesLoggingInterval());
        if (kubernetesLoggingInterval != null) {
            builder = builder.withLoggingInterval(Integer.parseInt(kubernetesLoggingInterval));
        }
        String kubernetesMaster = trimToNull(config.getKubernetesMaster());
        if (kubernetesMaster != null) {
            builder = builder.withMasterUrl(kubernetesMaster);
        }
        String kubernetesNamespace = trimToNull(config.getKubernetesNamespace());
        if (kubernetesNamespace != null) {
            builder = builder.withNamespace(kubernetesNamespace);
        }
        String kubernetesRequestTimeout = trimToNull(config.getKubernetesRequestTimeout());
        if (kubernetesRequestTimeout != null) {
            builder = builder.withRequestTimeout(Integer.parseInt(kubernetesRequestTimeout));
        }
        String kubernetesRollingTimeout = trimToNull(config.getKubernetesRollingTimeout());
        if (kubernetesRollingTimeout != null) {
            builder = builder.withRollingTimeout(Long.parseLong(kubernetesRollingTimeout));
        }
        String kubernetesScaleTimeout = trimToNull(config.getKubernetesScaleTimeout());
        if (kubernetesScaleTimeout != null) {
            builder = builder.withScaleTimeout(Long.parseLong(kubernetesScaleTimeout));
        }
        String kubernetesTlsVersions = trimToNull(config.getKubernetesTlsVersions());
        if (kubernetesTlsVersions != null) {
            String[] split = kubernetesTlsVersions.split(",");
            TlsVersion[] vers = new TlsVersion[split.length];
            for (int i = 0; i < split.length; i++) {
                vers[i] = TlsVersion.forJavaName(split[i]);
            }
            builder.withTlsVersions(vers);
        }
        String kubernetesTrustCertificates = trimToNull(config.getKubernetesTrustCertificates());
        if (kubernetesTrustCertificates != null) {
            builder = builder.withTrustCerts(Boolean.parseBoolean(kubernetesTrustCertificates));
        }
        String kubernetesTruststoreFile = trimToNull(config.getKubernetesTruststoreFile());
        if (kubernetesTruststoreFile != null) {
            builder = builder.withTrustStoreFile(kubernetesTruststoreFile);
        }
        String kubernetesTruststorePassphrase = trimToNull(config.getKubernetesTruststorePassphrase());
        if (kubernetesTruststorePassphrase != null) {
            builder = builder.withTrustStorePassphrase(kubernetesTruststorePassphrase);
        }
        String kubernetesUserAgent = trimToNull(config.getKubernetesUserAgent());
        if (kubernetesUserAgent != null) {
            builder = builder.withUserAgent(kubernetesUserAgent);
        }
        String kubernetesWatchReconnectInterval = trimToNull(config.getKubernetesWatchReconnectInterval());
        if (kubernetesWatchReconnectInterval != null) {
            builder = builder.withWatchReconnectInterval(Integer.parseInt(kubernetesWatchReconnectInterval));
        }
        String kubernetesWatchReconnectLimit = trimToNull(config.getKubernetesWatchReconnectLimit());
        if (kubernetesWatchReconnectLimit != null) {
            builder = builder.withWatchReconnectLimit(Integer.parseInt(kubernetesWatchReconnectLimit));
        }
        String kubernetesWebsocketPingInterval = trimToNull(config.getKubernetesWebsocketPingInterval());
        if (kubernetesWebsocketPingInterval != null) {
            builder = builder.withWebsocketPingInterval(Long.parseLong(kubernetesWebsocketPingInterval));
        }
        String kubernetesWebsocketTimeout = trimToNull(config.getKubernetesWebsocketTimeout());
        if (kubernetesWebsocketTimeout != null) {
            builder = builder.withWebsocketTimeout(Long.parseLong(kubernetesWebsocketTimeout));
        }
        String noProxy = trimToNull(config.getNoProxy());
        if (noProxy != null) {
            builder = builder.withNoProxy(noProxy.split(","));
        }
        String proxyPassword = trimToNull(config.getProxyPassword());
        if (proxyPassword != null) {
            builder = builder.withProxyPassword(proxyPassword);
        }
        String proxyUsername = trimToNull(config.getProxyUsername());
        if (proxyUsername != null) {
            builder = builder.withProxyUsername(proxyUsername);
        }
        /*
         * OpenShift configuration properties; see io.fabric8.openshift.client.OpenShiftConfig
         */
        String kubernetesOapiVersion = trimToNull(config.getKubernetesOapiVersion());
        if (kubernetesOapiVersion != null) {
            builder = builder.withOapiVersion(kubernetesOapiVersion);
        }
        String openshiftBuildTimeout = trimToNull(config.getOpenshiftBuildTimeout());
        if (openshiftBuildTimeout != null) {
            builder = builder.withBuildTimeout(Long.parseLong(openshiftBuildTimeout));
        }
        String openshiftUrl = trimToNull(config.getOpenshiftUrl());
        if (openshiftUrl != null) {
            // The OPENSHIFT_URL environment variable may be set to the root url (i.e. without the '/oapi/version' path) in some configurations
            if (isRootUrl(openshiftUrl)) {
                openshiftUrl = URLUtils.join(openshiftUrl, "oapi", builder.getOapiVersion());
            }
            builder = builder.withOpenShiftUrl(openshiftUrl);
        } else {
            builder.withOpenShiftUrl(URLUtils.join(builder.getMasterUrl(), "oapi", builder.getOapiVersion()));
        }
        return builder.build();
    }

    private static String trimToNull(String s) {
        if (s != null) {
            s = s.trim();
            if (s.isEmpty()) {
                s = null;
            }
        }
        return s;
    }

    private static boolean isRootUrl(String url) {
        try {
            String path = new URL(url).getPath();
            return "".equals(path) || "/".equals(path);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public void dispose() {
        clientMap.values().forEach(OpenShiftClient::dispose);
    }
}
