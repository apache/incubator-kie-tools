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

package org.uberfire.ext.plugin.event;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.rpc.SessionInfo;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

@Portable
public class PluginRenamed extends BasePluginEvent {

    private String oldPluginName;

    public PluginRenamed(@MapsTo("oldPluginName") final String oldPluginName,
                         @MapsTo("plugin") final Plugin plugin,
                         @MapsTo("sessionInfo") final SessionInfo sessionInfo) {
        super(plugin,
              sessionInfo);
        this.oldPluginName = checkNotEmpty("oldPluginName",
                                           oldPluginName);
    }

    public String getOldPluginName() {
        return oldPluginName;
    }

    public PluginType getOldPluginType() {
        return getPlugin().getType();
    }
}