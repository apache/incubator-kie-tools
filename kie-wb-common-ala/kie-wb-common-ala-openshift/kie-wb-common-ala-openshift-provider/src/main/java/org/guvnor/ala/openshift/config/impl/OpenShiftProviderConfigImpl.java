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
package org.guvnor.ala.openshift.config.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;

/**
 * Cloneable implementation of OpenShiftProviderConfig.
 */
public class OpenShiftProviderConfigImpl implements OpenShiftProviderConfig, CloneableConfig<OpenShiftProviderConfig> {

    // openshift provider properties
    private String name;
    // openshift client properties
    private String allProxy;
    private String httpProxy;
    private String httpsProxy;
    private String kubernetesApiVersion;
    private String kubernetesAuthBasicPassword;
    private String kubernetesAuthBasicUsername;
    private String kubernetesAuthToken;
    private String kubernetesCertsCaData;
    private String kubernetesCertsCaFile;
    private String kubernetesCertsClientData;
    private String kubernetesCertsClientFile;
    private String kubernetesCertsClientKeyAlgo;
    private String kubernetesCertsClientKeyData;
    private String kubernetesCertsClientKeyFile;
    private String kubernetesCertsClientKeyPassphrase;
    private String kubernetesConnectionTimeout;
    private String kubernetesKeystoreFile;
    private String kubernetesKeystorePassphrase;
    private String kubernetesLoggingInterval;
    private String kubernetesMaster;
    private String kubernetesNamespace;
    private String kubernetesOapiVersion;
    private String kubernetesRequestTimeout;
    private String kubernetesRollingTimeout;
    private String kubernetesScaleTimeout;
    private String kubernetesTlsVersions;
    private String kubernetesTrustCertificates;
    private String kubernetesTruststoreFile;
    private String kubernetesTruststorePassphrase;
    private String kubernetesUserAgent;
    private String kubernetesWatchReconnectInterval;
    private String kubernetesWatchReconnectLimit;
    private String kubernetesWebsocketPingInterval;
    private String kubernetesWebsocketTimeout;
    private String noProxy;
    private String openshiftBuildTimeout;
    private String openshiftUrl;
    private String proxyPassword;
    private String proxyUsername;

    public OpenShiftProviderConfigImpl() {
        // openshift provider properties
        setName(OpenShiftProviderConfig.super.getName());
        // openshift client properties
        setAllProxy(OpenShiftProviderConfig.super.getAllProxy());
        setHttpProxy(OpenShiftProviderConfig.super.getHttpProxy());
        setHttpsProxy(OpenShiftProviderConfig.super.getHttpsProxy());
        setKubernetesApiVersion(OpenShiftProviderConfig.super.getKubernetesApiVersion());
        setKubernetesAuthBasicPassword(OpenShiftProviderConfig.super.getKubernetesAuthBasicPassword());
        setKubernetesAuthBasicUsername(OpenShiftProviderConfig.super.getKubernetesAuthBasicUsername());
        setKubernetesAuthToken(OpenShiftProviderConfig.super.getKubernetesAuthToken());
        setKubernetesCertsCaData(OpenShiftProviderConfig.super.getKubernetesCertsCaData());
        setKubernetesCertsCaFile(OpenShiftProviderConfig.super.getKubernetesCertsCaFile());
        setKubernetesCertsClientData(OpenShiftProviderConfig.super.getKubernetesCertsClientData());
        setKubernetesCertsClientFile(OpenShiftProviderConfig.super.getKubernetesCertsClientFile());
        setKubernetesCertsClientKeyAlgo(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyAlgo());
        setKubernetesCertsClientKeyData(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyData());
        setKubernetesCertsClientKeyFile(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyFile());
        setKubernetesCertsClientKeyPassphrase(OpenShiftProviderConfig.super.getKubernetesCertsClientKeyPassphrase());
        setKubernetesConnectionTimeout(OpenShiftProviderConfig.super.getKubernetesConnectionTimeout());
        setKubernetesKeystoreFile(OpenShiftProviderConfig.super.getKubernetesKeystoreFile());
        setKubernetesKeystorePassphrase(OpenShiftProviderConfig.super.getKubernetesKeystorePassphrase());
        setKubernetesLoggingInterval(OpenShiftProviderConfig.super.getKubernetesLoggingInterval());
        setKubernetesMaster(OpenShiftProviderConfig.super.getKubernetesMaster());
        setKubernetesNamespace(OpenShiftProviderConfig.super.getKubernetesNamespace());
        setKubernetesOapiVersion(OpenShiftProviderConfig.super.getKubernetesOapiVersion());
        setKubernetesRequestTimeout(OpenShiftProviderConfig.super.getKubernetesRequestTimeout());
        setKubernetesRollingTimeout(OpenShiftProviderConfig.super.getKubernetesRollingTimeout());
        setKubernetesScaleTimeout(OpenShiftProviderConfig.super.getKubernetesScaleTimeout());
        setKubernetesTlsVersions(OpenShiftProviderConfig.super.getKubernetesTlsVersions());
        setKubernetesTrustCertificates(OpenShiftProviderConfig.super.getKubernetesTrustCertificates());
        setKubernetesTruststoreFile(OpenShiftProviderConfig.super.getKubernetesTruststoreFile());
        setKubernetesTruststorePassphrase(OpenShiftProviderConfig.super.getKubernetesTruststorePassphrase());
        setKubernetesUserAgent(OpenShiftProviderConfig.super.getKubernetesUserAgent());
        setKubernetesWatchReconnectInterval(OpenShiftProviderConfig.super.getKubernetesWatchReconnectInterval());
        setKubernetesWatchReconnectLimit(OpenShiftProviderConfig.super.getKubernetesWatchReconnectLimit());
        setKubernetesWebsocketPingInterval(OpenShiftProviderConfig.super.getKubernetesWebsocketPingInterval());
        setKubernetesWebsocketTimeout(OpenShiftProviderConfig.super.getKubernetesWebsocketTimeout());
        setNoProxy(OpenShiftProviderConfig.super.getNoProxy());
        setOpenshiftBuildTimeout(OpenShiftProviderConfig.super.getOpenshiftBuildTimeout());
        setOpenshiftUrl(OpenShiftProviderConfig.super.getOpenshiftUrl());
        setProxyPassword(OpenShiftProviderConfig.super.getProxyPassword());
        setProxyUsername(OpenShiftProviderConfig.super.getProxyUsername());
    }

    public OpenShiftProviderConfigImpl(OpenShiftProviderConfig origin) {
        if (origin != null) {
            // openshift provider properties
            setName(origin.getName());
            // openshift client properties
            setAllProxy(origin.getAllProxy());
            setHttpProxy(origin.getHttpProxy());
            setHttpsProxy(origin.getHttpsProxy());
            setKubernetesApiVersion(origin.getKubernetesApiVersion());
            setKubernetesAuthBasicPassword(origin.getKubernetesAuthBasicPassword());
            setKubernetesAuthBasicUsername(origin.getKubernetesAuthBasicUsername());
            setKubernetesAuthToken(origin.getKubernetesAuthToken());
            setKubernetesCertsCaData(origin.getKubernetesCertsCaData());
            setKubernetesCertsCaFile(origin.getKubernetesCertsCaFile());
            setKubernetesCertsClientData(origin.getKubernetesCertsClientData());
            setKubernetesCertsClientFile(origin.getKubernetesCertsClientFile());
            setKubernetesCertsClientKeyAlgo(origin.getKubernetesCertsClientKeyAlgo());
            setKubernetesCertsClientKeyData(origin.getKubernetesCertsClientKeyData());
            setKubernetesCertsClientKeyFile(origin.getKubernetesCertsClientKeyFile());
            setKubernetesCertsClientKeyPassphrase(origin.getKubernetesCertsClientKeyPassphrase());
            setKubernetesConnectionTimeout(origin.getKubernetesConnectionTimeout());
            setKubernetesKeystoreFile(origin.getKubernetesKeystoreFile());
            setKubernetesKeystorePassphrase(origin.getKubernetesKeystorePassphrase());
            setKubernetesLoggingInterval(origin.getKubernetesLoggingInterval());
            setKubernetesMaster(origin.getKubernetesMaster());
            setKubernetesNamespace(origin.getKubernetesNamespace());
            setKubernetesOapiVersion(origin.getKubernetesOapiVersion());
            setKubernetesRequestTimeout(origin.getKubernetesRequestTimeout());
            setKubernetesRollingTimeout(origin.getKubernetesRollingTimeout());
            setKubernetesScaleTimeout(origin.getKubernetesScaleTimeout());
            setKubernetesTlsVersions(origin.getKubernetesTlsVersions());
            setKubernetesTrustCertificates(origin.getKubernetesTrustCertificates());
            setKubernetesTruststoreFile(origin.getKubernetesTruststoreFile());
            setKubernetesTruststorePassphrase(origin.getKubernetesTruststorePassphrase());
            setKubernetesUserAgent(origin.getKubernetesUserAgent());
            setKubernetesWatchReconnectInterval(origin.getKubernetesWatchReconnectInterval());
            setKubernetesWatchReconnectLimit(origin.getKubernetesWatchReconnectLimit());
            setKubernetesWebsocketPingInterval(origin.getKubernetesWebsocketPingInterval());
            setKubernetesWebsocketTimeout(origin.getKubernetesWebsocketTimeout());
            setNoProxy(origin.getNoProxy());
            setOpenshiftBuildTimeout(origin.getOpenshiftBuildTimeout());
            setOpenshiftUrl(origin.getOpenshiftUrl());
            setProxyPassword(origin.getProxyPassword());
            setProxyUsername(origin.getProxyUsername());
        }
    }

    @JsonIgnore
    public OpenShiftProviderConfigImpl clear() {
        // openshift provider properties
        setName(null);
        // openshift client properties
        setAllProxy(null);
        setHttpProxy(null);
        setHttpsProxy(null);
        setKubernetesApiVersion(null);
        setKubernetesAuthBasicPassword(null);
        setKubernetesAuthBasicUsername(null);
        setKubernetesAuthToken(null);
        setKubernetesCertsCaData(null);
        setKubernetesCertsCaFile(null);
        setKubernetesCertsClientData(null);
        setKubernetesCertsClientFile(null);
        setKubernetesCertsClientKeyAlgo(null);
        setKubernetesCertsClientKeyData(null);
        setKubernetesCertsClientKeyFile(null);
        setKubernetesCertsClientKeyPassphrase(null);
        setKubernetesConnectionTimeout(null);
        setKubernetesKeystoreFile(null);
        setKubernetesKeystorePassphrase(null);
        setKubernetesLoggingInterval(null);
        setKubernetesMaster(null);
        setKubernetesNamespace(null);
        setKubernetesOapiVersion(null);
        setKubernetesRequestTimeout(null);
        setKubernetesRollingTimeout(null);
        setKubernetesScaleTimeout(null);
        setKubernetesTlsVersions(null);
        setKubernetesTrustCertificates(null);
        setKubernetesTruststoreFile(null);
        setKubernetesTruststorePassphrase(null);
        setKubernetesUserAgent(null);
        setKubernetesWatchReconnectInterval(null);
        setKubernetesWatchReconnectLimit(null);
        setKubernetesWebsocketPingInterval(null);
        setKubernetesWebsocketTimeout(null);
        setNoProxy(null);
        setOpenshiftBuildTimeout(null);
        setOpenshiftUrl(null);
        setProxyPassword(null);
        setProxyUsername(null);
        return this;
    }

    // openshift provider properties

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // openshift client properties

    @Override
    public String getAllProxy() {
        return allProxy;
    }

    public void setAllProxy(String allProxy) {
        this.allProxy = allProxy;
    }

    @Override
    public String getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(String httpProxy) {
        this.httpProxy = httpProxy;
    }

    @Override
    public String getHttpsProxy() {
        return httpsProxy;
    }

    public void setHttpsProxy(String httpsProxy) {
        this.httpsProxy = httpsProxy;
    }

    @Override
    public String getKubernetesApiVersion() {
        return kubernetesApiVersion;
    }

    public void setKubernetesApiVersion(String kubernetesApiVersion) {
        this.kubernetesApiVersion = kubernetesApiVersion;
    }

    @Override
    public String getKubernetesAuthBasicPassword() {
        return kubernetesAuthBasicPassword;
    }

    public void setKubernetesAuthBasicPassword(String kubernetesAuthBasicPassword) {
        this.kubernetesAuthBasicPassword = kubernetesAuthBasicPassword;
    }

    @Override
    public String getKubernetesAuthBasicUsername() {
        return kubernetesAuthBasicUsername;
    }

    public void setKubernetesAuthBasicUsername(String kubernetesAuthBasicUsername) {
        this.kubernetesAuthBasicUsername = kubernetesAuthBasicUsername;
    }

    @Override
    public String getKubernetesAuthToken() {
        return kubernetesAuthToken;
    }

    public void setKubernetesAuthToken(String kubernetesAuthToken) {
        this.kubernetesAuthToken = kubernetesAuthToken;
    }

    @Override
    public String getKubernetesCertsCaData() {
        return kubernetesCertsCaData;
    }

    public void setKubernetesCertsCaData(String kubernetesCertsCaData) {
        this.kubernetesCertsCaData = kubernetesCertsCaData;
    }

    @Override
    public String getKubernetesCertsCaFile() {
        return kubernetesCertsCaFile;
    }

    public void setKubernetesCertsCaFile(String kubernetesCertsCaFile) {
        this.kubernetesCertsCaFile = kubernetesCertsCaFile;
    }

    @Override
    public String getKubernetesCertsClientData() {
        return kubernetesCertsClientData;
    }

    public void setKubernetesCertsClientData(String kubernetesCertsClientData) {
        this.kubernetesCertsClientData = kubernetesCertsClientData;
    }

    @Override
    public String getKubernetesCertsClientFile() {
        return kubernetesCertsClientFile;
    }

    public void setKubernetesCertsClientFile(String kubernetesCertsClientFile) {
        this.kubernetesCertsClientFile = kubernetesCertsClientFile;
    }

    @Override
    public String getKubernetesCertsClientKeyAlgo() {
        return kubernetesCertsClientKeyAlgo;
    }

    public void setKubernetesCertsClientKeyAlgo(String kubernetesCertsClientKeyAlgo) {
        this.kubernetesCertsClientKeyAlgo = kubernetesCertsClientKeyAlgo;
    }

    @Override
    public String getKubernetesCertsClientKeyData() {
        return kubernetesCertsClientKeyData;
    }

    public void setKubernetesCertsClientKeyData(String kubernetesCertsClientKeyData) {
        this.kubernetesCertsClientKeyData = kubernetesCertsClientKeyData;
    }

    @Override
    public String getKubernetesCertsClientKeyFile() {
        return kubernetesCertsClientKeyFile;
    }

    public void setKubernetesCertsClientKeyFile(String kubernetesCertsClientKeyFile) {
        this.kubernetesCertsClientKeyFile = kubernetesCertsClientKeyFile;
    }

    @Override
    public String getKubernetesCertsClientKeyPassphrase() {
        return kubernetesCertsClientKeyPassphrase;
    }

    public void setKubernetesCertsClientKeyPassphrase(String kubernetesCertsClientKeyPassphrase) {
        this.kubernetesCertsClientKeyPassphrase = kubernetesCertsClientKeyPassphrase;
    }

    @Override
    public String getKubernetesConnectionTimeout() {
        return kubernetesConnectionTimeout;
    }

    public void setKubernetesConnectionTimeout(String kubernetesConnectionTimeout) {
        this.kubernetesConnectionTimeout = kubernetesConnectionTimeout;
    }

    @Override
    public String getKubernetesKeystoreFile() {
        return kubernetesKeystoreFile;
    }

    public void setKubernetesKeystoreFile(String kubernetesKeystoreFile) {
        this.kubernetesKeystoreFile = kubernetesKeystoreFile;
    }

    @Override
    public String getKubernetesKeystorePassphrase() {
        return kubernetesKeystorePassphrase;
    }

    public void setKubernetesKeystorePassphrase(String kubernetesKeystorePassphrase) {
        this.kubernetesKeystorePassphrase = kubernetesKeystorePassphrase;
    }

    @Override
    public String getKubernetesLoggingInterval() {
        return kubernetesLoggingInterval;
    }

    public void setKubernetesLoggingInterval(String kubernetesLoggingInterval) {
        this.kubernetesLoggingInterval = kubernetesLoggingInterval;
    }

    @Override
    public String getKubernetesMaster() {
        return kubernetesMaster;
    }

    public void setKubernetesMaster(String kubernetesMaster) {
        this.kubernetesMaster = kubernetesMaster;
    }

    @Override
    public String getKubernetesNamespace() {
        return kubernetesNamespace;
    }

    public void setKubernetesNamespace(String kubernetesNamespace) {
        this.kubernetesNamespace = kubernetesNamespace;
    }

    @Override
    public String getKubernetesOapiVersion() {
        return kubernetesOapiVersion;
    }

    public void setKubernetesOapiVersion(String kubernetesOapiVersion) {
        this.kubernetesOapiVersion = kubernetesOapiVersion;
    }

    @Override
    public String getKubernetesRequestTimeout() {
        return kubernetesRequestTimeout;
    }

    public void setKubernetesRequestTimeout(String kubernetesRequestTimeout) {
        this.kubernetesRequestTimeout = kubernetesRequestTimeout;
    }

    @Override
    public String getKubernetesRollingTimeout() {
        return kubernetesRollingTimeout;
    }

    public void setKubernetesRollingTimeout(String kubernetesRollingTimeout) {
        this.kubernetesRollingTimeout = kubernetesRollingTimeout;
    }

    @Override
    public String getKubernetesScaleTimeout() {
        return kubernetesScaleTimeout;
    }

    public void setKubernetesScaleTimeout(String kubernetesScaleTimeout) {
        this.kubernetesScaleTimeout = kubernetesScaleTimeout;
    }

    @Override
    public String getKubernetesTlsVersions() {
        return kubernetesTlsVersions;
    }

    public void setKubernetesTlsVersions(String kubernetesTlsVersions) {
        this.kubernetesTlsVersions = kubernetesTlsVersions;
    }

    @Override
    public String getKubernetesTrustCertificates() {
        return kubernetesTrustCertificates;
    }

    public void setKubernetesTrustCertificates(String kubernetesTrustCertificates) {
        this.kubernetesTrustCertificates = kubernetesTrustCertificates;
    }

    @Override
    public String getKubernetesTruststoreFile() {
        return kubernetesTruststoreFile;
    }

    public void setKubernetesTruststoreFile(String kubernetesTruststoreFile) {
        this.kubernetesTruststoreFile = kubernetesTruststoreFile;
    }

    @Override
    public String getKubernetesTruststorePassphrase() {
        return kubernetesTruststorePassphrase;
    }

    public void setKubernetesTruststorePassphrase(String kubernetesTruststorePassphrase) {
        this.kubernetesTruststorePassphrase = kubernetesTruststorePassphrase;
    }

    @Override
    public String getKubernetesUserAgent() {
        return kubernetesUserAgent;
    }

    public void setKubernetesUserAgent(String kubernetesUserAgent) {
        this.kubernetesUserAgent = kubernetesUserAgent;
    }

    @Override
    public String getKubernetesWatchReconnectInterval() {
        return kubernetesWatchReconnectInterval;
    }

    public void setKubernetesWatchReconnectInterval(String kubernetesWatchReconnectInterval) {
        this.kubernetesWatchReconnectInterval = kubernetesWatchReconnectInterval;
    }

    @Override
    public String getKubernetesWatchReconnectLimit() {
        return kubernetesWatchReconnectLimit;
    }

    public void setKubernetesWatchReconnectLimit(String kubernetesWatchReconnectLimit) {
        this.kubernetesWatchReconnectLimit = kubernetesWatchReconnectLimit;
    }

    @Override
    public String getKubernetesWebsocketPingInterval() {
        return kubernetesWebsocketPingInterval;
    }

    public void setKubernetesWebsocketPingInterval(String kubernetesWebsocketPingInterval) {
        this.kubernetesWebsocketPingInterval = kubernetesWebsocketPingInterval;
    }

    @Override
    public String getKubernetesWebsocketTimeout() {
        return kubernetesWebsocketTimeout;
    }

    public void setKubernetesWebsocketTimeout(String kubernetesWebsocketTimeout) {
        this.kubernetesWebsocketTimeout = kubernetesWebsocketTimeout;
    }

    @Override
    public String getNoProxy() {
        return noProxy;
    }

    public void setNoProxy(String noProxy) {
        this.noProxy = noProxy;
    }

    @Override
    public String getOpenshiftBuildTimeout() {
        return openshiftBuildTimeout;
    }

    public void setOpenshiftBuildTimeout(String openshiftBuildTimeout) {
        this.openshiftBuildTimeout = openshiftBuildTimeout;
    }

    @Override
    public String getOpenshiftUrl() {
        return openshiftUrl;
    }

    public void setOpenshiftUrl(String openshiftUrl) {
        this.openshiftUrl = openshiftUrl;
    }

    @Override
    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    @Override
    public OpenShiftProviderConfig asNewClone(OpenShiftProviderConfig origin) {
        return new OpenShiftProviderConfigImpl(origin);
    }

    @Override
    public String toString() {
        return "OpenShiftClientConfigImpl{name=" + name +
                ", httpProxy=" + httpProxy +
                ", httpsProxy=" + httpsProxy +
                ", kubernetesApiVersion=" + kubernetesApiVersion +
                ", kubernetesAuthBasicPassword=" + kubernetesAuthBasicPassword +
                ", kubernetesAuthBasicUsername=" + kubernetesAuthBasicUsername +
                ", kubernetesAuthToken=" + kubernetesAuthToken +
                ", kubernetesCertsCaData=" + kubernetesCertsCaData +
                ", kubernetesCertsCaFile=" + kubernetesCertsCaFile +
                ", kubernetesCertsClientData=" + kubernetesCertsClientData +
                ", kubernetesCertsClientFile=" + kubernetesCertsClientFile +
                ", kubernetesCertsClientKeyAlgo=" + kubernetesCertsClientKeyAlgo +
                ", kubernetesCertsClientKeyData=" + kubernetesCertsClientKeyData +
                ", kubernetesCertsClientKeyFile=" + kubernetesCertsClientKeyFile +
                ", kubernetesCertsClientKeyPassphrase=" + kubernetesCertsClientKeyPassphrase +
                ", kubernetesConnectionTimeout=" + kubernetesConnectionTimeout +
                ", kubernetesKeystoreFile=" + kubernetesKeystoreFile +
                ", kubernetesKeystorePassphrase=" + kubernetesKeystorePassphrase +
                ", kubernetesLoggingInterval=" + kubernetesLoggingInterval +
                ", kubernetesMaster=" + kubernetesMaster +
                ", kubernetesNamespace=" + kubernetesNamespace +
                ", kubernetesOapiVersion=" + kubernetesOapiVersion +
                ", kubernetesRequestTimeout=" + kubernetesRequestTimeout +
                ", kubernetesRollingTimeout=" + kubernetesRollingTimeout +
                ", kubernetesScaleTimeout=" + kubernetesScaleTimeout +
                ", kubernetesTlsVersions=" + kubernetesTlsVersions +
                ", kubernetesTrustCertificates=" + kubernetesTrustCertificates +
                ", kubernetesTruststoreFile=" + kubernetesTruststoreFile +
                ", kubernetesTruststorePassphrase=" + kubernetesTruststorePassphrase +
                ", kubernetesUserAgent=" + kubernetesUserAgent +
                ", kubernetesWatchReconnectInterval=" + kubernetesWatchReconnectInterval +
                ", kubernetesWatchReconnectLimit=" + kubernetesWatchReconnectLimit +
                ", kubernetesWebsocketPingInterval=" + kubernetesWebsocketPingInterval +
                ", kubernetesWebsocketTimeout=" + kubernetesWebsocketTimeout +
                ", noProxy=" + noProxy +
                ", openshiftBuildTimeout=" + openshiftBuildTimeout +
                ", openshiftUrl=" + openshiftUrl +
                ", proxyPassword=" + proxyPassword +
                ", proxyUsername=" + proxyUsername +
                "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allProxy == null) ? 0 : allProxy.hashCode());
        result = prime * result + ((httpProxy == null) ? 0 : httpProxy.hashCode());
        result = prime * result + ((httpsProxy == null) ? 0 : httpsProxy.hashCode());
        result = prime * result + ((kubernetesApiVersion == null) ? 0 : kubernetesApiVersion.hashCode());
        result = prime * result + ((kubernetesAuthBasicPassword == null) ? 0 : kubernetesAuthBasicPassword.hashCode());
        result = prime * result + ((kubernetesAuthBasicUsername == null) ? 0 : kubernetesAuthBasicUsername.hashCode());
        result = prime * result + ((kubernetesAuthToken == null) ? 0 : kubernetesAuthToken.hashCode());
        result = prime * result + ((kubernetesCertsCaData == null) ? 0 : kubernetesCertsCaData.hashCode());
        result = prime * result + ((kubernetesCertsCaFile == null) ? 0 : kubernetesCertsCaFile.hashCode());
        result = prime * result + ((kubernetesCertsClientData == null) ? 0 : kubernetesCertsClientData.hashCode());
        result = prime * result + ((kubernetesCertsClientFile == null) ? 0 : kubernetesCertsClientFile.hashCode());
        result = prime * result + ((kubernetesCertsClientKeyAlgo == null) ? 0 : kubernetesCertsClientKeyAlgo.hashCode());
        result = prime * result + ((kubernetesCertsClientKeyData == null) ? 0 : kubernetesCertsClientKeyData.hashCode());
        result = prime * result + ((kubernetesCertsClientKeyFile == null) ? 0 : kubernetesCertsClientKeyFile.hashCode());
        result = prime * result + ((kubernetesCertsClientKeyPassphrase == null) ? 0 : kubernetesCertsClientKeyPassphrase.hashCode());
        result = prime * result + ((kubernetesConnectionTimeout == null) ? 0 : kubernetesConnectionTimeout.hashCode());
        result = prime * result + ((kubernetesKeystoreFile == null) ? 0 : kubernetesKeystoreFile.hashCode());
        result = prime * result + ((kubernetesKeystorePassphrase == null) ? 0 : kubernetesKeystorePassphrase.hashCode());
        result = prime * result + ((kubernetesLoggingInterval == null) ? 0 : kubernetesLoggingInterval.hashCode());
        result = prime * result + ((kubernetesMaster == null) ? 0 : kubernetesMaster.hashCode());
        result = prime * result + ((kubernetesNamespace == null) ? 0 : kubernetesNamespace.hashCode());
        result = prime * result + ((kubernetesOapiVersion == null) ? 0 : kubernetesOapiVersion.hashCode());
        result = prime * result + ((kubernetesRequestTimeout == null) ? 0 : kubernetesRequestTimeout.hashCode());
        result = prime * result + ((kubernetesRollingTimeout == null) ? 0 : kubernetesRollingTimeout.hashCode());
        result = prime * result + ((kubernetesScaleTimeout == null) ? 0 : kubernetesScaleTimeout.hashCode());
        result = prime * result + ((kubernetesTlsVersions == null) ? 0 : kubernetesTlsVersions.hashCode());
        result = prime * result + ((kubernetesTrustCertificates == null) ? 0 : kubernetesTrustCertificates.hashCode());
        result = prime * result + ((kubernetesTruststoreFile == null) ? 0 : kubernetesTruststoreFile.hashCode());
        result = prime * result + ((kubernetesTruststorePassphrase == null) ? 0 : kubernetesTruststorePassphrase.hashCode());
        result = prime * result + ((kubernetesUserAgent == null) ? 0 : kubernetesUserAgent.hashCode());
        result = prime * result + ((kubernetesWatchReconnectInterval == null) ? 0 : kubernetesWatchReconnectInterval.hashCode());
        result = prime * result + ((kubernetesWatchReconnectLimit == null) ? 0 : kubernetesWatchReconnectLimit.hashCode());
        result = prime * result + ((kubernetesWebsocketPingInterval == null) ? 0 : kubernetesWebsocketPingInterval.hashCode());
        result = prime * result + ((kubernetesWebsocketTimeout == null) ? 0 : kubernetesWebsocketTimeout.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((noProxy == null) ? 0 : noProxy.hashCode());
        result = prime * result + ((openshiftBuildTimeout == null) ? 0 : openshiftBuildTimeout.hashCode());
        result = prime * result + ((openshiftUrl == null) ? 0 : openshiftUrl.hashCode());
        result = prime * result + ((proxyPassword == null) ? 0 : proxyPassword.hashCode());
        result = prime * result + ((proxyUsername == null) ? 0 : proxyUsername.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OpenShiftProviderConfigImpl)) {
            return false;
        }
        OpenShiftProviderConfigImpl other = (OpenShiftProviderConfigImpl) obj;
        if (allProxy == null) {
            if (other.allProxy != null) {
                return false;
            }
        } else if (!allProxy.equals(other.allProxy)) {
            return false;
        }
        if (httpProxy == null) {
            if (other.httpProxy != null) {
                return false;
            }
        } else if (!httpProxy.equals(other.httpProxy)) {
            return false;
        }
        if (httpsProxy == null) {
            if (other.httpsProxy != null) {
                return false;
            }
        } else if (!httpsProxy.equals(other.httpsProxy)) {
            return false;
        }
        if (kubernetesApiVersion == null) {
            if (other.kubernetesApiVersion != null) {
                return false;
            }
        } else if (!kubernetesApiVersion.equals(other.kubernetesApiVersion)) {
            return false;
        }
        if (kubernetesAuthBasicPassword == null) {
            if (other.kubernetesAuthBasicPassword != null) {
                return false;
            }
        } else if (!kubernetesAuthBasicPassword.equals(other.kubernetesAuthBasicPassword)) {
            return false;
        }
        if (kubernetesAuthBasicUsername == null) {
            if (other.kubernetesAuthBasicUsername != null) {
                return false;
            }
        } else if (!kubernetesAuthBasicUsername.equals(other.kubernetesAuthBasicUsername)) {
            return false;
        }
        if (kubernetesAuthToken == null) {
            if (other.kubernetesAuthToken != null) {
                return false;
            }
        } else if (!kubernetesAuthToken.equals(other.kubernetesAuthToken)) {
            return false;
        }
        if (kubernetesCertsCaData == null) {
            if (other.kubernetesCertsCaData != null) {
                return false;
            }
        } else if (!kubernetesCertsCaData.equals(other.kubernetesCertsCaData)) {
            return false;
        }
        if (kubernetesCertsCaFile == null) {
            if (other.kubernetesCertsCaFile != null) {
                return false;
            }
        } else if (!kubernetesCertsCaFile.equals(other.kubernetesCertsCaFile)) {
            return false;
        }
        if (kubernetesCertsClientData == null) {
            if (other.kubernetesCertsClientData != null) {
                return false;
            }
        } else if (!kubernetesCertsClientData.equals(other.kubernetesCertsClientData)) {
            return false;
        }
        if (kubernetesCertsClientFile == null) {
            if (other.kubernetesCertsClientFile != null) {
                return false;
            }
        } else if (!kubernetesCertsClientFile.equals(other.kubernetesCertsClientFile)) {
            return false;
        }
        if (kubernetesCertsClientKeyAlgo == null) {
            if (other.kubernetesCertsClientKeyAlgo != null) {
                return false;
            }
        } else if (!kubernetesCertsClientKeyAlgo.equals(other.kubernetesCertsClientKeyAlgo)) {
            return false;
        }
        if (kubernetesCertsClientKeyData == null) {
            if (other.kubernetesCertsClientKeyData != null) {
                return false;
            }
        } else if (!kubernetesCertsClientKeyData.equals(other.kubernetesCertsClientKeyData)) {
            return false;
        }
        if (kubernetesCertsClientKeyFile == null) {
            if (other.kubernetesCertsClientKeyFile != null) {
                return false;
            }
        } else if (!kubernetesCertsClientKeyFile.equals(other.kubernetesCertsClientKeyFile)) {
            return false;
        }
        if (kubernetesCertsClientKeyPassphrase == null) {
            if (other.kubernetesCertsClientKeyPassphrase != null) {
                return false;
            }
        } else if (!kubernetesCertsClientKeyPassphrase.equals(other.kubernetesCertsClientKeyPassphrase)) {
            return false;
        }
        if (kubernetesConnectionTimeout == null) {
            if (other.kubernetesConnectionTimeout != null) {
                return false;
            }
        } else if (!kubernetesConnectionTimeout.equals(other.kubernetesConnectionTimeout)) {
            return false;
        }
        if (kubernetesKeystoreFile == null) {
            if (other.kubernetesKeystoreFile != null) {
                return false;
            }
        } else if (!kubernetesKeystoreFile.equals(other.kubernetesKeystoreFile)) {
            return false;
        }
        if (kubernetesKeystorePassphrase == null) {
            if (other.kubernetesKeystorePassphrase != null) {
                return false;
            }
        } else if (!kubernetesKeystorePassphrase.equals(other.kubernetesKeystorePassphrase)) {
            return false;
        }
        if (kubernetesLoggingInterval == null) {
            if (other.kubernetesLoggingInterval != null) {
                return false;
            }
        } else if (!kubernetesLoggingInterval.equals(other.kubernetesLoggingInterval)) {
            return false;
        }
        if (kubernetesMaster == null) {
            if (other.kubernetesMaster != null) {
                return false;
            }
        } else if (!kubernetesMaster.equals(other.kubernetesMaster)) {
            return false;
        }
        if (kubernetesNamespace == null) {
            if (other.kubernetesNamespace != null) {
                return false;
            }
        } else if (!kubernetesNamespace.equals(other.kubernetesNamespace)) {
            return false;
        }
        if (kubernetesOapiVersion == null) {
            if (other.kubernetesOapiVersion != null) {
                return false;
            }
        } else if (!kubernetesOapiVersion.equals(other.kubernetesOapiVersion)) {
            return false;
        }
        if (kubernetesRequestTimeout == null) {
            if (other.kubernetesRequestTimeout != null) {
                return false;
            }
        } else if (!kubernetesRequestTimeout.equals(other.kubernetesRequestTimeout)) {
            return false;
        }
        if (kubernetesRollingTimeout == null) {
            if (other.kubernetesRollingTimeout != null) {
                return false;
            }
        } else if (!kubernetesRollingTimeout.equals(other.kubernetesRollingTimeout)) {
            return false;
        }
        if (kubernetesScaleTimeout == null) {
            if (other.kubernetesScaleTimeout != null) {
                return false;
            }
        } else if (!kubernetesScaleTimeout.equals(other.kubernetesScaleTimeout)) {
            return false;
        }
        if (kubernetesTlsVersions == null) {
            if (other.kubernetesTlsVersions != null) {
                return false;
            }
        } else if (!kubernetesTlsVersions.equals(other.kubernetesTlsVersions)) {
            return false;
        }
        if (kubernetesTrustCertificates == null) {
            if (other.kubernetesTrustCertificates != null) {
                return false;
            }
        } else if (!kubernetesTrustCertificates.equals(other.kubernetesTrustCertificates)) {
            return false;
        }
        if (kubernetesTruststoreFile == null) {
            if (other.kubernetesTruststoreFile != null) {
                return false;
            }
        } else if (!kubernetesTruststoreFile.equals(other.kubernetesTruststoreFile)) {
            return false;
        }
        if (kubernetesTruststorePassphrase == null) {
            if (other.kubernetesTruststorePassphrase != null) {
                return false;
            }
        } else if (!kubernetesTruststorePassphrase.equals(other.kubernetesTruststorePassphrase)) {
            return false;
        }
        if (kubernetesUserAgent == null) {
            if (other.kubernetesUserAgent != null) {
                return false;
            }
        } else if (!kubernetesUserAgent.equals(other.kubernetesUserAgent)) {
            return false;
        }
        if (kubernetesWatchReconnectInterval == null) {
            if (other.kubernetesWatchReconnectInterval != null) {
                return false;
            }
        } else if (!kubernetesWatchReconnectInterval.equals(other.kubernetesWatchReconnectInterval)) {
            return false;
        }
        if (kubernetesWatchReconnectLimit == null) {
            if (other.kubernetesWatchReconnectLimit != null) {
                return false;
            }
        } else if (!kubernetesWatchReconnectLimit.equals(other.kubernetesWatchReconnectLimit)) {
            return false;
        }
        if (kubernetesWebsocketPingInterval == null) {
            if (other.kubernetesWebsocketPingInterval != null) {
                return false;
            }
        } else if (!kubernetesWebsocketPingInterval.equals(other.kubernetesWebsocketPingInterval)) {
            return false;
        }
        if (kubernetesWebsocketTimeout == null) {
            if (other.kubernetesWebsocketTimeout != null) {
                return false;
            }
        } else if (!kubernetesWebsocketTimeout.equals(other.kubernetesWebsocketTimeout)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (noProxy == null) {
            if (other.noProxy != null) {
                return false;
            }
        } else if (!noProxy.equals(other.noProxy)) {
            return false;
        }
        if (openshiftBuildTimeout == null) {
            if (other.openshiftBuildTimeout != null) {
                return false;
            }
        } else if (!openshiftBuildTimeout.equals(other.openshiftBuildTimeout)) {
            return false;
        }
        if (openshiftUrl == null) {
            if (other.openshiftUrl != null) {
                return false;
            }
        } else if (!openshiftUrl.equals(other.openshiftUrl)) {
            return false;
        }
        if (proxyPassword == null) {
            if (other.proxyPassword != null) {
                return false;
            }
        } else if (!proxyPassword.equals(other.proxyPassword)) {
            return false;
        }
        if (proxyUsername == null) {
            if (other.proxyUsername != null) {
                return false;
            }
        } else if (!proxyUsername.equals(other.proxyUsername)) {
            return false;
        }
        return true;
    }

}
