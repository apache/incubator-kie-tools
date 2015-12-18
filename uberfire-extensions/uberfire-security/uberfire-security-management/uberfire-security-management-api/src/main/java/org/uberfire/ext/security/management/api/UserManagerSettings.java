package org.uberfire.ext.security.management.api;

import java.util.Collection;

/**
 * <p>The settings for a users entity manager.</p>
 *
 * @since 0.8.0
 */
public interface UserManagerSettings extends Settings {

    /**
     * <p>This method is used to provide all the supported user attributes supported by the specific manager implementation.</p>
     * @return The collection of supported attributes in the underlying security system.
     */
    Collection<UserManager.UserAttribute> getSupportedAttributes();
    
}
