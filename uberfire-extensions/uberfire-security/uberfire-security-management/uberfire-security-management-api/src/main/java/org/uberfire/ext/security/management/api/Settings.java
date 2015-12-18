package org.uberfire.ext.security.management.api;

import java.util.Map;

/**
 * <p>The settings for a given entity manager.</p>
 *
 * @since 0.8.0
 */
public interface Settings {

    /**
     * <p>Obtain all available provider capabilities in a single call.</p>
     * @return All capabilities for the service and their status. 
     */
    Map<Capability, CapabilityStatus> getCapabilities();
    
}
