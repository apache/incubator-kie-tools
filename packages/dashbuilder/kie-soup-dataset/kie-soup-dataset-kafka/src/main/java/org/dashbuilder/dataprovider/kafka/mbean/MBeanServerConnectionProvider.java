/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.kafka.mbean;

import java.util.Collections;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.dashbuilder.dataprovider.kafka.model.KafkaMetricsRequest;

public class MBeanServerConnectionProvider {

    private static final String RMI_URL_TEMPLATE = "service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi";
    private static final JMXRMIConnectorProvider PROVIDER = new JMXRMIConnectorProvider();

    private MBeanServerConnectionProvider() {
        // do nothing
    }

    public static JMXConnector newConnection(KafkaMetricsRequest request) {
        try {
            String host = request.getHost();
            String port = request.getRmiPort();
            validateParams(host, port);
            String formattedUrl = String.format(RMI_URL_TEMPLATE, host, port);
            JMXServiceURL url = new JMXServiceURL(formattedUrl);
            JMXConnector connector = PROVIDER.newJMXConnector(url, Collections.emptyMap());
            connector.connect();
            return connector;
        } catch (Exception e) {
            throw new IllegalArgumentException("Not able to connect to provided server.", e);
        }
    }

    private static void validateParams(String host, String port) {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid host name.");
        }
        if (isInvalid(port)) {
            throw new IllegalArgumentException("Invalid port.");
        }
    }

    private static boolean isInvalid(String port) {
        try {
            return port == null || Integer.parseInt(port) < 1024;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
