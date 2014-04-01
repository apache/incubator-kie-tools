package org.uberfire.security.server.auth.source.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.errai.security.shared.api.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.impl.RoleImpl;

public class WebSphereRolesAdapter implements RolesAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WebSphereRolesAdapter.class);
    private Object registry;

    public WebSphereRolesAdapter() {
        try {
            this.registry = InitialContext.doLookup("UserRegistry");
        } catch (NamingException e) {
            logger.warn("Unable to look up UserRegistry in JNDI under key 'UserRegistry', disabling websphere adapter");
        }
    }

    @Override
    public List<Role> getRoles(String username) {
        List<Role> roles = new ArrayList<Role>();
        if (registry == null) {
            return roles;
        }
        try {
            Method method = registry.getClass().getMethod("getGroupsForUser", new Class[]{String.class});
            List rolesIn = (List) method.invoke(registry, new Object[]{username});
            if (rolesIn != null) {
                for (Object o : rolesIn) {
                    roles.add( new RoleImpl( o.toString() ) );
                }
            }
        } catch (Exception e) {
            logger.error("Unable to get roles from registry due to {}", e.getMessage(), e);
        }

        return roles;
    }
}
