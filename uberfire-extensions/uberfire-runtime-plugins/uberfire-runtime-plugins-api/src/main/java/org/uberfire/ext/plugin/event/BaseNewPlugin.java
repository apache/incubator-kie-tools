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
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.rpc.SessionInfo;

public abstract class BaseNewPlugin {

    private Plugin plugin;
    private SessionInfo sessionInfo;

    public BaseNewPlugin( @MapsTo("plugin") final Plugin plugin,
                          @MapsTo("sessionInfo") final SessionInfo sessionInfo ) {
        this.plugin = plugin;
        this.sessionInfo = sessionInfo;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

}
