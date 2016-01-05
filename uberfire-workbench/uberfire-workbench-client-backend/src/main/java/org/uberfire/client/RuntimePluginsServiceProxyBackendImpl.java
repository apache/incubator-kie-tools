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

package org.uberfire.client;

import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.plugin.RuntimePluginsService;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
@Alternative
public class RuntimePluginsServiceProxyBackendImpl implements RuntimePluginsServiceProxy {

    @Inject
    private Caller<RuntimePluginsService> runtimePluginsService;

    @Override
    public void getTemplateContent( final String contentUrl,
                                    final ParameterizedCommand<String> command ) {
        runtimePluginsService.call( new RemoteCallback<String>() {
            @Override
            public void callback( String o ) {
                command.execute( o );
            }
        } ).getTemplateContent( contentUrl );
    }

    @Override
    public void listFrameworksContent( final ParameterizedCommand<Collection<String>> command ) {
        runtimePluginsService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> o ) {
                command.execute( o );
            }
        } ).listFramworksContent();
    }

    @Override
    public void listPluginsContent( final ParameterizedCommand<Collection<String>> command ) {
        runtimePluginsService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> o ) {
                command.execute( o );
            }
        } ).listPluginsContent();
    }
}
