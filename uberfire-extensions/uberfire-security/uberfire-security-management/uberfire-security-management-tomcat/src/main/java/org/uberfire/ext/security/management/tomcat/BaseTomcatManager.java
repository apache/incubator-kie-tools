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

package org.uberfire.ext.security.management.tomcat;

import org.apache.catalina.users.MemoryUserDatabase;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.impl.UserAttributeImpl;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import java.util.*;

/**
 * <p>Base users and groups management methods for the tomcat provider implementations.</p>
 * 
 * @since 0.8.0
 */
public abstract class BaseTomcatManager {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTomcatManager.class);

    public static final String DEFAULT_CATALINA_BASE = "/opt/tomcat";
    public static final String USERS_FILE = "conf/tomcat-users.xml";
    public static final String DATABASE_NAME = "UserDatabase";
    protected static final String CATALINA_BASE_PROPERTY = "catalina.base";

    public static final String ATTRIBUTE_USER_FULLNAME = "user.fullName";
    protected static final UserManager.UserAttribute USER_FULLNAME = new UserAttributeImpl(ATTRIBUTE_USER_FULLNAME, false, true, "Full name");

    protected static final Collection<UserManager.UserAttribute> USER_ATTRIBUTES = Arrays.asList(USER_FULLNAME);

    protected String defaultCatalinaBase = DEFAULT_CATALINA_BASE;
    protected String usersFile = USERS_FILE;

    protected void loadConfig( final ConfigProperties config ) {
        final ConfigProperties.ConfigProperty catalinaBasePath = config.get("org.uberfire.ext.security.management.tomcat.catalina-base", DEFAULT_CATALINA_BASE);
        final ConfigProperties.ConfigProperty usersName = config.get("org.uberfire.ext.security.management.tomcat.users-file", USERS_FILE);

        // Check mandatory properties.
        if (!isConfigPropertySet(catalinaBasePath)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.tomcat.catalina-base' is mandatory and not set.");
        if (!isConfigPropertySet(usersName)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.tomcat.users-file' is mandatory and not set.");

        this.defaultCatalinaBase = catalinaBasePath.getValue();
        this.usersFile = usersName.getValue();
        initializeTomcatProperties();
    }
    
    protected void initializeTomcatProperties() {
        // If not running in a tomcat server environment, add the necessary catalina.base property to work with Tomcat's API and libraries. 
        if (isEmpty(System.getProperty(CATALINA_BASE_PROPERTY))) {
            System.setProperty(CATALINA_BASE_PROPERTY, defaultCatalinaBase);
        }
    }
    
    protected MemoryUserDatabase getDatabase() throws SecurityManagementException {
        MemoryUserDatabase database = new MemoryUserDatabase(DATABASE_NAME);
        database.setPathname(usersFile);
        database.setReadonly(false);
        try {
            database.open();
            if (!database.getReadonly()) database.save();
        } catch (Exception e) {
            throw new SecurityManagementException(e);
        }
        return (database);
    }

    protected void saveDatabase(MemoryUserDatabase database) throws SecurityManagementException {
        try {
            database.save();
        } catch (Exception e) {
            throw new SecurityManagementException(e);
        }
    }
    
    protected void closeDatabase(MemoryUserDatabase database) throws SecurityManagementException {
        try {
            database.close();
        } catch (Exception e) {
            throw new SecurityManagementException(e);
        }
    }

    protected org.apache.catalina.User getUser(MemoryUserDatabase database, String identifier) {
        org.apache.catalina.User user = database.findUser(identifier);
        if (user == null) throw new UserNotFoundException(identifier);
        return user;
    }
    
    protected org.apache.catalina.Role getRole(MemoryUserDatabase database, String identifier) {
        org.apache.catalina.Role group = database.findRole(identifier);
        if (group == null) throw new GroupNotFoundException(identifier); 
        return group;
    }
    
    protected User createUser(org.apache.catalina.User user, Iterator<org.apache.catalina.Role> groups) {
        if (user == null) return null;
        final Set<Group> _groups = new HashSet<Group>();
        final Set<Role> _roles = new HashSet<Role>();
        final Set<String> registeredRoles = SecurityManagementUtils.getRegisteredRoleNames();
        if (groups != null && groups.hasNext()) {
            while (groups.hasNext()) {
                org.apache.catalina.Role group = groups.next();
                String name = group.getRolename();
                SecurityManagementUtils.populateGroupOrRoles(name, registeredRoles, _groups, _roles);
            }
        }
        return SecurityManagementUtils.createUser(user.getName(), _groups, _roles);
    }

    protected Group createGroup(org.apache.catalina.Role group) {
        if (group == null) return null;
        return SecurityManagementUtils.createGroup(group.getRolename());
    }

    protected Role createRole(org.apache.catalina.Role group) {
        if (group == null) return null;
        return SecurityManagementUtils.createRole(group.getRolename());
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
