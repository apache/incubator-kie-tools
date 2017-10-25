/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.ui.model.ProviderType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

/**
 * Provisioning management related preferences.
 */
@WorkbenchPreference(identifier = "ProvisioningPreferences")
public class ProvisioningPreferences
        implements BasePreference<ProvisioningPreferences> {

    @Property
    private Map<ProviderType, Boolean> providerTypeEnablements;

    public Map<ProviderType, Boolean> getProviderTypeEnablements() {
        return providerTypeEnablements;
    }

    public void setProviderTypeEnablements(final Map<ProviderType, Boolean> providerTypeEnablements) {
        this.providerTypeEnablements = providerTypeEnablements;
    }

    @Override
    public ProvisioningPreferences defaultValue(final ProvisioningPreferences defaultValue) {
        defaultValue.setProviderTypeEnablements(new HashMap<>());
        return defaultValue;
    }
}
