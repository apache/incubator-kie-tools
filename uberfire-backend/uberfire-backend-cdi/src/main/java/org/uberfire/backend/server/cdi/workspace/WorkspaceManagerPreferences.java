/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.cdi.workspace;

import java.util.concurrent.TimeUnit;

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "WorkspaceManagerPreferences", bundleKey = "WorkspaceManagerPreferences.Label")
public class WorkspaceManagerPreferences implements BasePreference<WorkspaceManagerPreferences> {

    @Property(bundleKey = "WorkspaceManagerPreferences.CacheMaximumSize")
    private int cacheMaximumSize;

    @Property(bundleKey = "WorkspaceManagerPreferences.CacheExpirationTime")
    private int cacheExpirationTime;

    @Property(bundleKey = "WorkspaceManagerPreferences.CacheExpirationUnit")
    private String cacheExpirationUnit;

    public WorkspaceManagerPreferences() {
    }

    @Override
    public WorkspaceManagerPreferences defaultValue(final WorkspaceManagerPreferences defaultValue) {
        defaultValue.cacheMaximumSize = 50;
        defaultValue.cacheExpirationTime = 30;
        defaultValue.cacheExpirationUnit = TimeUnit.MINUTES.toString();
        return defaultValue;
    }

    public int getCacheMaximumSize() {
        return cacheMaximumSize;
    }

    public void setCacheMaximumSize(final int cacheMaximumSize) {
        this.cacheMaximumSize = cacheMaximumSize;
    }

    public int getCacheExpirationTime() {
        return cacheExpirationTime;
    }

    public void setCacheExpirationTime(final int cacheExpirationTime) {
        this.cacheExpirationTime = cacheExpirationTime;
    }

    public String getCacheExpirationUnit() {
        return this.cacheExpirationUnit;
    }

    public void setCacheExpirationUnit(final String cacheExpirationUnit) {
        this.cacheExpirationUnit = cacheExpirationUnit;
    }
}
