/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.data.workingset;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Portable
public class WorkingSetSettings {

    private Map<Path, WorkingSetConfigData> configData;

    public WorkingSetSettings() {
    }

    public WorkingSetSettings( Map<Path, WorkingSetConfigData> configData ) {
        this.configData = configData;
    }

    public Set<Path> getResources() {
        return configData.keySet();
    }

    public Collection<WorkingSetConfigData> getConfigData() {
        return configData.values();
    }

    public void removeWorkingSet( final Path workingSet ) {
        configData.remove( workingSet );
    }
}
