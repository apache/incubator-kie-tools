/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Plugin;

import static org.guvnor.common.services.project.backend.server.utils.NullSafeEquals.areValuesEqual;

public class MavenPluginUpdater {

    private final List<Plugin> pluginsDeclaredInPOM;

    public MavenPluginUpdater(List<Plugin> pluginsDeclaredInPOM) {
        this.pluginsDeclaredInPOM = pluginsDeclaredInPOM;
    }

    public List<Plugin> update(final List<org.guvnor.common.services.project.model.Plugin> from) {

        List<Plugin> result = new ArrayList<>();

        for (org.guvnor.common.services.project.model.Plugin plugin : from) {
            if (plugin.getArtifactId() != null && plugin.getGroupId() != null) {
                result.add(update(plugin,
                                  findPlugin(plugin.getGroupId(),
                                             plugin.getArtifactId())));
            }
        }

        return result;
    }

    private Plugin findPlugin(final String groupId,
                              final String artifactId) {
        for (final Plugin plugin : pluginsDeclaredInPOM) {
            if (areValuesEqual(groupId,
                               plugin.getGroupId())
                    && areValuesEqual(artifactId,
                                      plugin.getArtifactId())) {
                return plugin;
            }
        }
        return new Plugin();
    }

    private Plugin update(final org.guvnor.common.services.project.model.Plugin from,
                          final Plugin to) {

        to.setGroupId(from.getGroupId());
        to.setArtifactId(from.getArtifactId());
        to.setVersion(from.getVersion());

        // false is the default value, so we only set it if value is true
        if (from.isExtensions()) {
            to.setExtensions(from.isExtensions());
        } else {
            to.setExtensions(null);
        }

        new DependencyUpdater(to.getDependencies()).updateDependencies(from.getDependencies());

        return to;
    }
}
