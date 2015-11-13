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

import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.rpc.SessionInfo;

public abstract class BasePluginEvent {

    private String pluginName;
    private PluginType type;
    private SessionInfo sessionInfo;

    public BasePluginEvent() {
    }

    protected BasePluginEvent( final String pluginName,
                               final PluginType type,
                               final SessionInfo sessionInfo ) {
        this.pluginName = pluginName;
        this.type = type;
        this.sessionInfo = sessionInfo;
    }

    public String getPluginName() {
        return pluginName;
    }

    public PluginType getType() {
        return type;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
}
