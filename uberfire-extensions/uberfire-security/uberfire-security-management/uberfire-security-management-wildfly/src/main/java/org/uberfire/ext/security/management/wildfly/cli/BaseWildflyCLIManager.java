/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.wildfly.cli;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;

import javax.security.auth.callback.*;
import javax.security.sasl.RealmCallback;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * <p>Base class for JBoss Wildfly security management that uses the administration Java API for managing the command line interface.</p>
 * <p>Based on JBoss Wildfly administration API & Util classes.</p>
 * 
 * @since 0.8.0
 */
public abstract class BaseWildflyCLIManager {

    private static final Logger LOG = LoggerFactory.getLogger(BaseWildflyCLIManager.class);
    protected static final String DEFAULT_HOST = "localhost";
    protected static final int DEFAULT_PORT = 9990;
    protected static final String DEFAULT_ADMIN_USER = null;
    protected static final String DEFAULT_ADMIN_PASSWORD = null;
    protected static final String DEFAULT_REALM = "ApplicationRealm";
    
    protected String host;
    protected int port;
    protected String adminUser;
    protected String adminPassword;
    protected String realm;

    protected void loadConfig( final ConfigProperties config ) {
        final ConfigProperties.ConfigProperty host = config.get("org.uberfire.ext.security.management.wildfly.cli.host", DEFAULT_HOST);
        final ConfigProperties.ConfigProperty port = config.get("org.uberfire.ext.security.management.wildfly.cli.port", Integer.toString(DEFAULT_PORT));
        final ConfigProperties.ConfigProperty user = config.get("org.uberfire.ext.security.management.wildfly.cli.user", DEFAULT_ADMIN_USER);
        final ConfigProperties.ConfigProperty password = config.get("org.uberfire.ext.security.management.wildfly.cli.password", DEFAULT_ADMIN_PASSWORD);
        final ConfigProperties.ConfigProperty realm = config.get("org.uberfire.ext.security.management.wildfly.cli.realm", DEFAULT_REALM);
        
        this.host = host.getValue();
        this.port = Integer.decode(port.getValue());
        this.adminUser = user.getValue();
        this.adminPassword = password.getValue();
        this.realm = realm.getValue();
    }
    
    public ModelControllerClient getClient() throws Exception {
        return ModelControllerClient.Factory.create(
                InetAddress.getByName(host), port,
                new CallbackHandler() {
                    public void handle(Callback[] callbacks)
                            throws IOException, UnsupportedCallbackException {
                        for (Callback current : callbacks) {
                            if (current instanceof NameCallback) {
                                NameCallback ncb = (NameCallback) current;
                                ncb.setName(adminUser);
                            } else if (current instanceof PasswordCallback) {
                                PasswordCallback pcb = (PasswordCallback) current;
                                pcb.setPassword(adminPassword.toCharArray());
                            } else if (current instanceof RealmCallback) {
                                RealmCallback rcb = (RealmCallback) current;
                                rcb.setText(rcb.getDefaultText());
                            } else {
                                throw new UnsupportedCallbackException(current);
                            }
                        }
                    }
                });
    }

    protected String getPropertiesFilePath(final String context) throws Exception {
        String result = null;
        final ModelControllerClient client = getClient();
        if (client != null) {
            ModelNode operation = new ModelNode();
            operation.get("operation").set("read-resource");
            ModelNode address = operation.get("address");
            address.add("core-service", "management");
            address.add("security-realm", realm);
            address.add(context, "properties");
            try {
                ModelNode returnVal = client.execute(operation);
                if ("success".equalsIgnoreCase(returnVal.get("outcome").asString())) {
                    ModelNode resultNode = returnVal.get("result");
                    if (resultNode != null) {
                        String path = resultNode.get("path").asString();
                        String relativeTo = resultNode.get("relative-to").asString();
                        String relativeToPath = System.getProperty(relativeTo);
                        return new File(relativeToPath, path).getAbsolutePath();
                    }
                }
            } catch (Exception e) {
                LOG.error("Error reading realm using CLI commands.", e);
            } finally {
                client.close();
            }
        }

        return result;
    }

    protected static boolean isConfigPropertySet(ConfigProperties.ConfigProperty property) {
        if (property == null) return false;
        String value = property.getValue();
        return !isEmpty(value);
    }

    protected static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
