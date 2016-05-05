package org.uberfire.ext.security.management.wildfly;

import org.jboss.errai.security.shared.api.Role;
import org.uberfire.ext.security.management.UberfireRoleManager;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.Set;

/**
 * <p>Custom implementation for Role Manager for the JBoss Wildfly / EAP provider.</p>
 * <p>Using the default <code>org.uberfire.ext.security.management.UberfireRoleManager</code> the registered application roles must be present in the realm,
 * but when using the Wildfly / EAP properties based realm, registered roles could not be present if not assigned,
 * so consider all available roles as  all the registered ones.</p>
 *
 * @since 0.8.0
 */
@Dependent
@Named("wildflyRoleManager")
public class WildflyRoleManager extends UberfireRoleManager {

    @Override
    protected Set<Role> getRegisteredRoles() {
        return SecurityManagementUtils.getRegisteredRoles();
    }

}
