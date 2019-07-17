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
package org.uberfire.commons.cluster;


import java.util.Properties;

import javax.naming.Context;

public class ClusterParameters {

    public static final String APPFORMER_JMS_CONNECTION_MODE = "appformer-jms-connection-mode";

    public static final String APPFORMER_PROVIDER_URL = "appformer-jms-url";

    public static final String APPFORMER_INITIAL_CONTEXT_FACTORY = "appformer-initial-context-factory";
    public static final String APPFORMER_JMS_CONNECTION_FACTORY = "appformer-jms-connection-factory";

    public static final String APPFORMER_JMS_USERNAME = "appformer-jms-username";
    public static final String APPFORMER_JMS_PASSWORD = "appformer-jms-password";

    //The specified value must be a positive long corresponding to the time the message must be delivered (in milliseconds)
    public static final String APPFORMER_JMS_THROTTLE = "appformer-jms-throttle";

    private final Properties initialContextFactory = new Properties();
    private final ConnectionMode connectionMode;
    private final String providerUrl;
    private final String jmsConnectionFactoryJndiName;
    private final String jmsUserName;
    private final String jmsPassword;
    private long jmsThrottle;

    public ClusterParameters() {
        ConnectionMode connectionMode;
        try {
            connectionMode = ConnectionMode.valueOf(System.getProperty(APPFORMER_JMS_CONNECTION_MODE,
                                                                       ConnectionMode.NONE.toString()));
        } catch (final Throwable ignore) {
            connectionMode = ConnectionMode.NONE;
        }
        this.connectionMode = connectionMode;
        this.initialContextFactory.put(Context.INITIAL_CONTEXT_FACTORY,
                                       System.getProperty(APPFORMER_INITIAL_CONTEXT_FACTORY,
                                                          "org.wildfly.naming.client.WildFlyInitialContextFactory"));
        this.jmsConnectionFactoryJndiName = System.getProperty(APPFORMER_JMS_CONNECTION_FACTORY,
                                                               "java:/ConnectionFactory");
        this.providerUrl = System.getProperty(APPFORMER_PROVIDER_URL, "tcp://localhost:61616");
        this.jmsUserName = System.getProperty(APPFORMER_JMS_USERNAME);
        this.jmsPassword = System.getProperty(APPFORMER_JMS_PASSWORD);

        String throttleParameter = System.getProperty(APPFORMER_JMS_THROTTLE, "1000");
        try {
            this.jmsThrottle = Long.valueOf(throttleParameter);
        } catch (NumberFormatException e) {
            this.jmsThrottle = -1;
        }
    }

    public boolean isAppFormerClustered() {
        return this.connectionMode != ConnectionMode.NONE;
    }

    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public Properties getInitialContextFactory() {
        return initialContextFactory;
    }

    public String getJmsConnectionFactoryJndiName() {
        return jmsConnectionFactoryJndiName;
    }

    public String getJmsUserName() {
        return jmsUserName;
    }

    public String getJmsPassword() {
        return jmsPassword;
    }

    public long getJmsThrottle() {
        return jmsThrottle;
    }

}
