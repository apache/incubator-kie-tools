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

package org.uberfire.ext.security.management.wildfly.properties;

import org.jboss.sasl.util.UsernamePasswordHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;

import java.security.NoSuchAlgorithmException;

/**
 * <p>Base class for JBoss Wildfly security management when using realms based on properties files.</p>
 * <p>Based on JBoss Wildfly controller client API & Util classes.</p>
 * 
 * @since 0.8.0
 */
public abstract class BaseWildflyPropertiesManager {

    private static final Logger LOG = LoggerFactory.getLogger(BaseWildflyPropertiesManager.class);
    public static final String DEFAULT_REALM = "ApplicationRealm";
    
    protected String realm = DEFAULT_REALM;

    protected void loadConfig( final ConfigProperties config ) {
        final ConfigProperties.ConfigProperty realm = config.get("org.uberfire.ext.security.management.wildfly.properties.realm", DEFAULT_REALM);
        this.realm = realm.getValue();
    }
    
    protected static String generateHashPassword(final String username, final String realm, final String password) {
        String result = null;
        try {
            result = new UsernamePasswordHashUtil().generateHashedHexURP(
                    username,
                    realm,
                    password.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
