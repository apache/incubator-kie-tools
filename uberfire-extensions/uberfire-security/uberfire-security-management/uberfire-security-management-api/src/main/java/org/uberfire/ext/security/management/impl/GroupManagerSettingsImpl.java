package org.uberfire.ext.security.management.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.GroupManagerSettings;

import java.util.Map;

/**
 * <p>Default settings for a group manager.</p>
 * 
 * @since 0.8.0
 */
@Portable
public class GroupManagerSettingsImpl extends SettingsImpl implements GroupManagerSettings {

    final boolean allowEmpty;

    public GroupManagerSettingsImpl(@MapsTo("capabilities") Map<Capability, CapabilityStatus> capabilities,
                                    @MapsTo("allowEmpty") boolean allowEmpty) {
        super(capabilities);
        this.allowEmpty = allowEmpty;
    }


    @Override
    public boolean allowEmpty() {
        return allowEmpty;
    }
}
