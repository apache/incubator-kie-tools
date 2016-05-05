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

package org.uberfire.ext.security.management;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.api.*;
import org.uberfire.ext.security.management.api.validation.EntityValidator;
import org.uberfire.ext.security.management.validation.GroupValidatorImpl;
import org.uberfire.ext.security.management.validation.RoleValidatorImpl;
import org.uberfire.ext.security.management.validation.UserValidatorImpl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * <p>The main backend manager for the user management stuff.</p>
 * 
 * Note: No full role management support yet.
 * @since 0.8.0
 */
@ApplicationScoped
public class BackendUserSystemManager implements UserSystemManager {

    private static final Logger LOG = LoggerFactory.getLogger(BackendUserSystemManager.class);
    
    public static final String ENV_USER_MANAGEMENT_PREFIX = "org.uberfire.ext.security.management";
    public static final String ENV_USER_MANAGEMENT_SERVICE = ENV_USER_MANAGEMENT_PREFIX + ".api.userManagementServices";
    public static final String SECURITY_MANAGEMENT_DESCRIPTOR = "security-management.properties";

    @Inject
    BeanManager beanManager;
    
    @Inject
    Instance<UserManagementService> userManagementServices;
    
    private UserManager usersManagementService;
    private GroupManager groupsManagementService;
    private RoleManager roleManagementService;
    private boolean isActive;

    @PostConstruct
    public void initialize() {

        // Load properties found in the descriptor.
        loadDescriptor();
        
        // Obtain the services with the given runtime, descriptor or default properties configuration.
        UserManagementService userManagementService = getService();
        
        boolean isUserManagerActive = false;
        boolean isGroupManagerActive = false;
        boolean isRoleManagerActive = false;
        
        if ( null != userManagementService ) {

            // Look for the Service Provider implementation class targeted for users management.
            usersManagementService = userManagementService.users();
            if (usersManagementService != null) {
                try {
                    ContextualManager m = (ContextualManager) usersManagementService;
                    m.initialize(this);
                    isUserManagerActive = true;
                } catch (ClassCastException e) {
                    // Manager is not contextual.
                } catch (Exception e) {
                    LOG.error("UsersManagementService initialization failure", e);
                }
            } else {
                LOG.warn("No management services for users available.");
            }

            // Look for the Service Provider implementation class  targeted for groups management.
            groupsManagementService = userManagementService.groups();
            if (groupsManagementService != null) {
                try {
                    ContextualManager m = (ContextualManager) groupsManagementService;
                    m.initialize(this);
                    isGroupManagerActive = true;
                } catch (ClassCastException e) {
                    // Manager is not contextual.
                } catch (Exception e) {
                    LOG.error("GroupManagementService initialization failure", e);
                }
            }else {
                LOG.warn("No management services for groups available.");
            }

            // Look for the Service Provider implementation class  targeted for role management.
            // NOTE: Not full role management support yet, so if no present, do not complain.
            roleManagementService = userManagementService.roles();
            if (roleManagementService != null) {
                try {
                    ContextualManager m = (ContextualManager) roleManagementService;
                    m.initialize(this);
                    isRoleManagerActive = true;
                } catch (ClassCastException e) {
                    // Manager is not contextual.
                } catch (Exception e) {
                    LOG.error("RoleManagementService initialization failure", e);
                }
            }
            
            this.isActive = isUserManagerActive && isGroupManagerActive && isRoleManagerActive;
            
        } else {
            LOG.warn("No user management services available.");
        }
        
    }
    
    public UserManager users() {
        return usersManagementService;
    }

    public GroupManager groups() {
        return groupsManagementService;
    }

    public RoleManager roles() {
        return roleManagementService;
    }

    @Override
    public EntityValidator<User> usersValidator() {
        return new UserValidatorImpl();
    }

    @Override
    public EntityValidator<Group> groupsValidator() {
        return new GroupValidatorImpl();
    }

    @Override
    public EntityValidator<Role> rolesValidator() {
        return new RoleValidatorImpl();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @PreDestroy
    public void onDestroy() {

        if (usersManagementService != null) {
            try {
                ContextualManager m = (ContextualManager) usersManagementService;
                m.destroy();
            } catch (ClassCastException e) {
                // Manager is not contextual.
            } catch (Exception e) {
                LOG.error("UserManagementService destroy failure", e);
            }
        }

        if (groupsManagementService != null) {
            try {
                ContextualManager m = (ContextualManager) groupsManagementService;
                m.destroy();
            } catch (ClassCastException e) {
                // Manager is not contextual.
            } catch (Exception e) {
                LOG.error("GroupManagementService destroy failure", e);
            }
        }

        if (roleManagementService != null) {
            try {
                ContextualManager m = (ContextualManager) roleManagementService;
                m.destroy();
            } catch (ClassCastException e) {
                // Manager is not contextual.
            } catch (Exception e) {
                LOG.error("RoleManagementService destroy failure", e);
            }
        }
    }

    private UserManagementService getService() {
        // Try to obtain the service impl from the system properties or from the descriptor file.
        String serviceName = System.getProperty(ENV_USER_MANAGEMENT_SERVICE);
        if (isEmpty(serviceName)) {
            LOG.warn("No user management services implementation specified neither at runtime or in the properties descriptor for security management.");
            return null;
        }

        // Obtain the beans for the concrete impl to use.
        Set<Bean<?>> beans =  beanManager.getBeans(serviceName);
        if (beans == null || beans.isEmpty()) {
            LOG.warn("No bean found for name '" + serviceName + "'. " + serviceName + " services will not work.");
            return null;
        }

        // Instantiate the service impl.
        LOG.info("Using the user management service named '" + serviceName + "'");
        Bean bean = (Bean) beans.iterator().next();
        if (beans.size() > 1) {
            LOG.warn("More than a single bean found for bean named '" + serviceName + "'. " +
                    "Using the first one found in the classpath with fully classified classname '" + bean.getBeanClass() + "'.");
        }
        CreationalContext context = beanManager.createCreationalContext(bean);
        return (UserManagementService) beanManager.getReference(bean, bean.getBeanClass(), context);
    }
    
    private void loadDescriptor() {
        InputStream is = BackendUserSystemManager.this.getClass().getClassLoader().getResourceAsStream(SECURITY_MANAGEMENT_DESCRIPTOR);
        if ( null != is ) {
            try {
                final Properties descriptorProperties = new Properties();
                descriptorProperties.load(is);
                if ( !descriptorProperties.isEmpty() ) {
                    final Enumeration<?> propNames = descriptorProperties.propertyNames();
                    if ( null != propNames && propNames.hasMoreElements()) {
                        while (propNames.hasMoreElements()) {
                            String propId = (String) propNames.nextElement();
                            // Check only properties with a given prefix, for security reasons.
                            if (propId.startsWith(ENV_USER_MANAGEMENT_PREFIX)) {
                                if ( isEmpty(System.getProperty(propId))) {
                                    System.setProperty( propId, descriptorProperties.getProperty(propId) );
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Error reading security management properties descriptor.", e);
            }
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
