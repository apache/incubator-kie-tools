package org.uberfire.ext.security.management.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.RoleManagerSettings;

import java.util.Map;

/**
 * <p>Default settings for a role manager.</p>
 * 
 * @since 0.8.0
 */
@Portable
public class RoleManagerSettingsImpl extends SettingsImpl implements RoleManagerSettings {

    public RoleManagerSettingsImpl(@MapsTo("capabilities") Map<Capability, CapabilityStatus> capabilities) {
        super(capabilities);
    }
}
