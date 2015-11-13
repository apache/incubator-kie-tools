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

package org.uberfire.ext.plugin.client.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.uberfire.ext.plugin.service.PluginServices;

@ApplicationScoped
public class PluginConfigService {

    @Inject
    private Caller<PluginServices> pluginServices;

    private String mediaServletURI;

    @AfterInitialization
    public void init() {
        pluginServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( final String response ) {
                mediaServletURI = response;
            }
        } ).getMediaServletURI();
    }

    public String getMediaServletURI() {
        return mediaServletURI;
    }
}
