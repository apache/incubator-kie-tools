package org.uberfire.ext.security.management.api;

/**
 * <p>The settings for a groups entity manager.</p>
 *
 * @since 0.8.0
 */
public interface GroupManagerSettings extends Settings {

    /**
     * <p>Specify if the provider allows groups with no users assigned.</p>
     * <p>Usually realm based on property files, such as the default ones for Wildfly or EAP, does not allow empty users 
     * as the username is the key for the property entry.</p>
     * @return Allows groups with any user assigned.
     */
    boolean allowEmpty();
    
}
