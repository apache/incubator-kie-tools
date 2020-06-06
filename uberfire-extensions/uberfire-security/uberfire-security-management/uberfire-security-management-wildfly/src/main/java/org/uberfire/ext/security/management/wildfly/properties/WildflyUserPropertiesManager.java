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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.as.domain.management.security.PropertiesFileLoader;
import org.jboss.as.domain.management.security.UserPropertiesFileLoader;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.msc.service.StartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.api.UserManagerSettings;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.InvalidEntityIdentifierException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.impl.UserManagerSettingsImpl;
import org.uberfire.ext.security.management.search.IdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.UsersIdentifierRuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * <p>Users manager service provider implementation for JBoss Wildfly, when using default realm based on properties files.</p>
 *
 * @since 0.8.0
 */
public class WildflyUserPropertiesManager extends BaseWildflyPropertiesManager implements UserManager,
                                                                                          ContextualManager {

    public static final String DEFAULT_USERS_FILE = "./standalone/configuration/application-users.properties";
    public static final String DEFAULT_PASSWORD = "";
    public static final String VALID_USERNAME_SYMBOLS = "\",\", \"-\", \".\", \"/\", \"=\", \"@\", \"\\\"";
    private static final Logger LOG = LoggerFactory.getLogger(WildflyUserPropertiesManager.class);

    protected final IdentifierRuntimeSearchEngine<User> usersSearchEngine = new UsersIdentifierRuntimeSearchEngine();
    protected UserSystemManager userSystemManager;
    protected String usersFilePath;
    WildflyUsersPropertiesFileLoader usersFileLoader;

    public WildflyUserPropertiesManager() {
        this(new ConfigProperties(System.getProperties()));
    }

    public WildflyUserPropertiesManager(final Map<String, String> gitPrefs) {
        this(new ConfigProperties(gitPrefs));
    }

    public WildflyUserPropertiesManager(final ConfigProperties gitPrefs) {
        loadConfig(gitPrefs);
    }

    protected void loadConfig(final ConfigProperties config) {
        LOG.debug("Configuring JBoss Wildfly provider from properties.");
        super.loadConfig(config);
        final ConfigProperties.ConfigProperty usersFilePathProperty = config.get("org.uberfire.ext.security.management.wildfly.properties.users-file-path",
                                                                                 DEFAULT_USERS_FILE);
        if (!isConfigPropertySet(usersFilePathProperty)) {
            throw new IllegalArgumentException("Property 'org.uberfire.ext.security.management.wildfly.properties.users-file-path' is mandatory and not set.");
        }
        this.usersFilePath = usersFilePathProperty.getValue();
        LOG.debug("Configuration of JBoss WildFly provider finished.");
    }

    @Override
    public void initialize(UserSystemManager userSystemManager) throws Exception {
        this.userSystemManager = userSystemManager;
        getUsersFileLoader();
    }

    @Override
    public void destroy() throws Exception {
        getUsersFileLoader().stop();
    }

    @Override
    public SearchResponse<User> search(SearchRequest request) throws SecurityManagementException {
        List<String> users = getUserNames();
        return usersSearchEngine.searchByIdentifiers(users,
                                                     request);
    }

    @Override
    public User get(String identifier) throws SecurityManagementException {
        validateUserIdentifier(identifier);
        List<String> userNames = getUserNames();
        if (userNames != null && userNames.contains(identifier)) {
            return getUser(identifier);
        }
        throw new UserNotFoundException(identifier);
    }

    @Override
    public List<User> getAll() throws SecurityManagementException {
        return getUserNames().stream().map(this::getUser).collect(Collectors.toList());
    }

    public String getUsersFilePath() {
        return usersFilePath;
    }

    @Override
    public User create(User entity) throws SecurityManagementException {
        checkNotNull("entity",
                     entity);
        final String username = entity.getIdentifier();
        try {
            if (null == username || 0 == username.trim().length()) {
                throw new IllegalArgumentException("No username specified.");
            }
            validateUserIdentifier(username);
            usersFileLoader.getProperties().put(username,
                                                DEFAULT_PASSWORD);
            usersFileLoader.persistProperties();
        } catch (IOException e) {
            LOG.error("Error creating user " + username,
                      e);
            throw new SecurityManagementException(e);
        }
        return entity;
    }

    @Override
    public User update(User entity) throws SecurityManagementException {
        checkNotNull("entity",
                     entity);
        // Properties realm do not need to be updated, as values are just the passwords,
        // no other attributes present.
        return entity;
    }

    @Override
    public void delete(String... usernames) throws SecurityManagementException {
        checkNotNull("usernames",
                     usernames);
        for (String username : usernames) {
            final User user = get(username);
            if (user == null) {
                throw new UserNotFoundException(username);
            }
            try {
                // Remove the entry on the users properties file.
                usersFileLoader.getProperties().remove(username);
                usersFileLoader.persistProperties();
                // Remove the entry on the groups properties file.
                getGroupsPropertiesManager().removeEntry(username);
            } catch (IOException e) {
                LOG.error("Error deleting user " + username,
                          e);
                throw new SecurityManagementException(e);
            }
        }
    }

    @Override
    public void assignGroups(String username,
                             Collection<String> groups) throws SecurityManagementException {
        if (getGroupsPropertiesManager() != null) {
            Set<String> userRoles = SecurityManagementUtils.rolesToString(SecurityManagementUtils.getRoles(userSystemManager,
                                                                                                           username));
            userRoles.addAll(groups);
            getGroupsPropertiesManager().setGroupsForUser(username,
                                                          userRoles);
        }
    }

    @Override
    public void assignRoles(String username,
                            Collection<String> roles) throws SecurityManagementException {
        if (getGroupsPropertiesManager() != null) {
            Set<String> userGroups = SecurityManagementUtils.groupsToString(SecurityManagementUtils.getGroups(userSystemManager,
                                                                                                              username));
            userGroups.addAll(roles);
            getGroupsPropertiesManager().setGroupsForUser(username,
                                                          userGroups);
        }
    }

    @Override
    public void changePassword(String username,
                               String newPassword) throws SecurityManagementException {
        checkNotNull("username",
                     username);
        checkNotNull("username",
                     username);
        if (0 == username.trim().length()) {
            throw new IllegalArgumentException("No username specified for updating password.");
        }
        try {
            usersFileLoader.getProperties().put(username,
                                                generateHashPassword(username,
                                                                     realm,
                                                                     newPassword));
            usersFileLoader.persistProperties();
        } catch (IOException e) {
            LOG.error("Error changing user's password",
                      e);
            throw new SecurityManagementException(e);
        }
    }

    @Override
    public UserManagerSettings getSettings() {
        final Map<Capability, CapabilityStatus> capabilityStatusMap = new HashMap<Capability, CapabilityStatus>(8);
        for (final Capability capability : SecurityManagementUtils.USERS_CAPABILITIES) {
            capabilityStatusMap.put(capability,
                                    getCapabilityStatus(capability));
        }
        return new UserManagerSettingsImpl(capabilityStatusMap,
                                           null);
    }

    private User getUser(String userName) {
        Set<Group> userGroups = null;
        Set<Role> userRoles = null;
        if (getGroupsPropertiesManager() != null) {
            final Set[] gr = getGroupsPropertiesManager().getGroupsAndRolesForUser(userName);
            if (null != gr) {
                userGroups = gr[0];
                userRoles = gr[1];
            }
        }
        return SecurityManagementUtils.createUser(userName,
                                                     userGroups, userRoles);
    }
    protected CapabilityStatus getCapabilityStatus(Capability capability) {
        if (capability != null) {
            switch (capability) {
                case CAN_SEARCH_USERS:
                case CAN_ADD_USER:
                case CAN_UPDATE_USER:
                case CAN_DELETE_USER:
                case CAN_READ_USER:
                case CAN_ASSIGN_GROUPS:
                    /** As it is using the UberfireRoleManager. **/
                case CAN_ASSIGN_ROLES:
                case CAN_CHANGE_PASSWORD:
                    return CapabilityStatus.ENABLED;
            }
        }
        return CapabilityStatus.UNSUPPORTED;
    }

    protected WildflyUsersPropertiesFileLoader buildFileLoader(String usersFilePath) throws Exception {
        File usersFile = new File(usersFilePath);
        if (!usersFile.exists()) {
            throw new RuntimeException("Properties file for users not found at '" + usersFilePath + "'.");
        }
        this.usersFileLoader = new WildflyUsersPropertiesFileLoader(usersFile.getAbsolutePath());
        try {
            this.usersFileLoader.start();
        } catch (Exception e) {
            throw new IOException("Failed to start UserPropertiesFileLoader.",
                                  e);
        }

        return this.usersFileLoader;
    }

    /**
     * NOTE: To obtain the user names from the UsersFileLoader class, do not use the <code>getEnabledUserNames</code> method that comes in the jboss domain-management artifcat from Wildfly,
     * as this method is not present when using the jboss domain-management artifact from EAP modules, as it's version
     * for 6.4.0.GA is quite older. So in order to be compatible with both wildfly and eap, do not use the <code>getEnabledUserNames</code> method.
     */
    protected List<String> getUserNames() {
        try {
            final Properties properties = usersFileLoader.getProperties();
            return toList(properties);
        } catch (Exception e) {
            LOG.error("Error obtaining JBoss users from properties file.",
                      e);
            throw new SecurityManagementException(e);
        }
    }

    private List<String> toList(Properties p) {
        if (null != p && !p.isEmpty()) {
            final ArrayList<String> result = new ArrayList<String>(p.size());
            final Enumeration<?> pNames = p.propertyNames();
            while (pNames.hasMoreElements()) {
                final String pName = (String) pNames.nextElement();
                final String trimmed = pName.trim();
                if (!trimmed.startsWith("#")) {
                    result.add(pName);
                }
            }
            return result;
        }
        return new ArrayList<String>(0);
    }

    // Does not need to synchronize as the manager implements ContextualManager.
    protected WildflyUsersPropertiesFileLoader getUsersFileLoader() throws Exception {
        if (usersFileLoader == null) {
            this.usersFileLoader = buildFileLoader(getUsersFilePath());
        }
        return usersFileLoader;
    }

    protected synchronized WildflyGroupPropertiesManager getGroupsPropertiesManager() {
        try {
            return (WildflyGroupPropertiesManager) userSystemManager.groups();
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * An extension of the default Wildfly's users properties file loader,
     * but this one supports deleting users and using empty passwords.
     */
    public static final class WildflyUsersPropertiesFileLoader
            extends UserPropertiesFileLoader {

        private final PropertiesLineWriterPredicate lineWriterPredicate;

        public WildflyUsersPropertiesFileLoader(final String path) {
            this(path, null);
        }

        public WildflyUsersPropertiesFileLoader(final String path,
                                                final String relativeTo) {
            super(null, path, relativeTo);
            this.lineWriterPredicate = new PropertiesLineWriterPredicate(WildflyUsersPropertiesFileLoader.this::cleanKey,
                                                                         true);
        }

        public void start() throws StartException {
            super.start(null);
        }

        public void stop() {
            super.stop(null);
        }

        @Override
        protected void beginPersistence() throws IOException {
            lineWriterPredicate.begin(getProperties());
            super.beginPersistence();
        }

        @Override
        protected void endPersistence(final BufferedWriter writer) throws IOException {
            super.endPersistence(writer);
            lineWriterPredicate.end();
        }

        @Override
        protected void write(final BufferedWriter writer,
                             final String line,
                             final boolean newLine) throws IOException {
            if (lineWriterPredicate.test(line)) {
                super.write(writer, line, newLine);
            }
        }
    }

    /**
     * Validates the candidate user identifier by following same Wildfly's patterns for usernames in properties realms,
     * and by following the behavior for the <code>add-user.sh</code> script as well,
     * here is the actual username validation constraints:
     * <code>
     * WFLYDM0028: Username must be alphanumeric with the exception of
     * the following accepted symbols (",", "-", ".", "/", "=", "@", "\")
     * </code>
     *
     * @param identifier The identifier to validate.
     */
    private void validateUserIdentifier(String identifier) {
        if (!PropertiesFileLoader.PROPERTY_PATTERN
                .matcher(identifier + "=0")
                .matches()) {
            throw new InvalidEntityIdentifierException(identifier,
                                                       VALID_USERNAME_SYMBOLS);
        }
    }
}
