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

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class UserWorkbenchPreferences extends UserPreference {

    private String language;
    private Map<String, String> perspectiveViewMode = new HashMap<String, String>();
    private boolean useWorkbenchInCompactMode;

    public UserWorkbenchPreferences() {
    }

    public UserWorkbenchPreferences( final String language ) {
        super();
        super.type = UserPreferencesType.WORKBENCHSETTINGS;
        super.preferenceKey = "settings";
        this.language = language;

    }

    public Map<String, String> getPerspectiveViewMode() {
        return perspectiveViewMode;
    }

    public void setPerspectiveViewMode( final Map<String, String> perspectiveViewMode ) {
        this.perspectiveViewMode = perspectiveViewMode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage( final String language ) {
        this.language = language;
    }

    public String getViewMode( final String perspective ) {
        return perspectiveViewMode.get( perspective );
    }

    public void setViewMode( final String perspective,
                             final String viewMode ) {
        perspectiveViewMode.put( perspective,
                                 viewMode);
    }

    public void setUseWorkbenchInCompactMode( boolean useWorkbenchInCompactMode ) {
        this.useWorkbenchInCompactMode = useWorkbenchInCompactMode;
    }

    public boolean isUseWorkbenchInCompactMode() {
        return useWorkbenchInCompactMode;
    }
}
