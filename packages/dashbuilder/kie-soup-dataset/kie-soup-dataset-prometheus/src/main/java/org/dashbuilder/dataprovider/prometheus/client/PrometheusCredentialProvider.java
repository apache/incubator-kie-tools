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
package org.dashbuilder.dataprovider.prometheus.client;

public class PrometheusCredentialProvider {

    public static final String PROMETHEUS_USER_PROP = "dashbuilder.prometheus.user";
    public static final String PROMETHEUS_PASSWORD_PROP = "dashbuilder.prometheus.password";

    private static PrometheusCredentialProvider instance;

    private PrometheusCredentialProvider() {
        // empty
    }

    public static PrometheusCredentialProvider get() {
        if (instance == null) {
            instance = new PrometheusCredentialProvider();
        }
        return instance;

    }

    public String getUser() {
        return System.getProperty(PROMETHEUS_USER_PROP);
    }

    public String getPassword() {
        return System.getProperty(PROMETHEUS_PASSWORD_PROP);
    }

}