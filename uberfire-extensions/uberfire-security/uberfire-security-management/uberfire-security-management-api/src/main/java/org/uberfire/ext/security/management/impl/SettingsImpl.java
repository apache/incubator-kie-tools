package org.uberfire.ext.security.management.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.Settings;

import java.util.Map;

/**
 * <p>Base provider settings implementation.</p>
 * 
 * @since 0.8.0
 */
@Portable
public class SettingsImpl implements Settings {

    final Map<Capability, CapabilityStatus> capabilities;

    public SettingsImpl(@MapsTo("capabilities") Map<Capability, CapabilityStatus> capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public Map<Capability, CapabilityStatus> getCapabilities() {
        return capabilities;
    }
}
