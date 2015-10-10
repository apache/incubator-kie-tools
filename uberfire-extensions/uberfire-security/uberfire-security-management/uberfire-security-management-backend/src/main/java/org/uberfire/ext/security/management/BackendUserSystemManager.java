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
import java.util.Properties;
import java.util.Set;

/**
 * Note that role management services are not yet available. 
 */
@ApplicationScoped
public class BackendUserSystemManager implements UserSystemManager {

    private static final Logger LOG = LoggerFactory.getLogger(BackendUserSystemManager.class);
    
    public static final String ENV_USER_MANAGEMENT_SERVICE = "org.uberfire.ext.security.management.api.userManagementServices";
    public static final String ENV_DEFAULT_USER_MANAGER_SERVICE = "WildflyCLIUserManagementService";
    public static final String SECURITY_MANAGEMENT_DESCRIPTOR = "security-management.properties";

    @Inject
    BeanManager beanManager;
    
    @Inject
    Instance<UserManagementService> userManagementServices;
    
    private UserManager usersManagementService;
    private GroupManager groupsManagementService;

    
    @PostConstruct
    public void initialize() throws Exception {

        UserManagementService userManagementService = getService(ENV_DEFAULT_USER_MANAGER_SERVICE);
        
        if ( null != userManagementService ) {

            // Look for the Service Provider implementation class targeted for users management.
            usersManagementService = userManagementService.users();
            if (usersManagementService != null) {
                try {
                    ContextualManager m = (ContextualManager) usersManagementService;
                    m.initialize(this);
                } catch (ClassCastException e) {
                    // Manager is not contextual.
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
                } catch (ClassCastException e) {
                    // Manager is not contextual.
                }
            }else {
                LOG.warn("No management services for groups available.");
            }
            
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

    // Roles are not supported yet.
    public RoleManager roles() {
        return null;
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

    @PreDestroy
    public void onDestroy() throws Exception {
        
        if (usersManagementService != null) {
            try {
                ContextualManager m = (ContextualManager) usersManagementService;
                m.destroy();
            } catch (ClassCastException e) {
                // Manager is not contextual.
            }    
        }

        if (groupsManagementService != null) {
            try {
                ContextualManager m = (ContextualManager) groupsManagementService;
                m.destroy();
            } catch (ClassCastException e) {
                // Manager is not contextual.
            }
        }
        
    }

    private UserManagementService getService(String defaultServiceName) {
        // Try to obtain the service impl from the system properties.
        String serviceName = System.getProperty(ENV_USER_MANAGEMENT_SERVICE);
        if (isEmpty(serviceName)) {
            LOG.info("No user management services implementation specified at runtime. Checking the default one given by the properties file descriptor.");
            serviceName = getServiceFromDescriptor(ENV_USER_MANAGEMENT_SERVICE);
        }

        // Try to obtain the service impl from the properties file.
        if (isEmpty(serviceName)) {
            serviceName = defaultServiceName;
            LOG.warn("No user management services implementation specified neither at runtime or in the properties descriptor for security management. " +
                    "Using the default one named '" + serviceName + "'.");
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
    
    private String getServiceFromDescriptor(String envKey) {
        InputStream is = BackendUserSystemManager.this.getClass().getClassLoader().getResourceAsStream(SECURITY_MANAGEMENT_DESCRIPTOR);
        if ( null != is ) {
            Properties p = new Properties();
            try {
                p.load(is);
                return p.getProperty(envKey);
            } catch (IOException e) {
                LOG.error("Error reading security management properties descriptor.", e);
            }
        }
        return null;
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
