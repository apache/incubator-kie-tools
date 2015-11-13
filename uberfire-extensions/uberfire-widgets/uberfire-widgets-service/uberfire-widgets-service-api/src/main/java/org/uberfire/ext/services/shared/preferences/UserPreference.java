/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.services.shared.preferences;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserPreference {

    protected UserPreferencesType type;

    protected String preferenceKey;

    public UserPreferencesType getType() {
        return type;
    }

    public void setType( final UserPreferencesType type ) {
        this.type = type;
    }

    public void setPreferenceKey( final String preferenceKey ) {
        this.preferenceKey = preferenceKey;
    }

    public String getPreferenceKey() {
        return this.preferenceKey;
    }
}
