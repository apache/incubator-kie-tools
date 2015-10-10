/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
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

    public static final String USERS_FILE_PATH = "/opt/tomcat/conf";
    public static final String USERS_FILE_NAME = "tomcat-users.xml";
    public static final String DATABASE_NAME = "UserDatabase";
    protected static final String CATALINA_BASE_PROPERTY = "catalina.base";

    public static final String ATTRIBUTE_USER_FULLNAME = "user.fullName";
    protected static final UserManager.UserAttribute USER_FULLNAME = new UserAttributeImpl(ATTRIBUTE_USER_FULLNAME, false, true, "Full name");

    protected static final Collection<UserManager.UserAttribute> USER_ATTRIBUTES = Arrays.asList(USER_FULLNAME);
    
    protected String usersFilePath = USERS_FILE_PATH;
    protected String usersFileName = USERS_FILE_NAME;

    protected void loadConfig( final ConfigProperties config ) {
        final ConfigProperties.ConfigProperty usersPath = config.get("org.uberfire.ext.security.management.tomcat.users-file-path", USERS_FILE_PATH);
        final ConfigProperties.ConfigProperty usersName = config.get("org.uberfire.ext.security.management.tomcat.users-file-name", USERS_FILE_NAME);

        // Check mandatory properties.
        if (!isConfigPropertySet(usersPath)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.tomcat.users-file-path' is mandatory and not set.");
        if (!isConfigPropertySet(usersName)) throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.tomcat.users-file-name' is mandatory and not set.");

        this.usersFilePath = usersPath.getValue();
        this.usersFileName = usersName.getValue();
        initializeTomcatProperties();
    }
    
    protected void initializeTomcatProperties() {
        // If not running in a tomcat server environment, add the necessary catalina.base property to work with Tomcat's API and libraries. 
        if (isEmpty(System.getProperty(CATALINA_BASE_PROPERTY))) {
            System.setProperty(CATALINA_BASE_PROPERTY, usersFilePath);
        }
    }
    
    protected MemoryUserDatabase getDatabase() throws SecurityManagementException {
        MemoryUserDatabase database = new MemoryUserDatabase(DATABASE_NAME);
        database.setPathname(usersFileName);
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
    
    protected User createUser(org.apache.catalina.User user) {
        if (user == null) return null;
        return SecurityManagementUtils.createUser(user.getName());
    }
    
    protected User createUser(org.apache.catalina.User user, Iterator<org.apache.catalina.Role> groups) {
        if (user == null) return null;
        Set<Group> gs = new HashSet<Group>();
        if (groups != null && groups.hasNext()) {
            while (groups.hasNext()) {
                org.apache.catalina.Role group = groups.next();
                Group g = createGroup(group);
                gs.add(g);
            }
        }
        return SecurityManagementUtils.createUser(user.getName(), gs);
    }

    protected Group createGroup(org.apache.catalina.Role group) {
        if (group == null) return null;
        return SecurityManagementUtils.createGroup(group.getRolename());
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
