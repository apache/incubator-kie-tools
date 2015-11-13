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

package org.uberfire.client;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.workbench.VFSServiceProxy;
import org.uberfire.mvp.ParameterizedCommand;

@Alternative
public class VFSServiceProxyBackendImpl implements VFSServiceProxy {

    @Inject
    private Caller<VFSService> vfsService;

    @Override
    public void get( final String path,
                     final ParameterizedCommand<Path> parameterizedCommand ) {
        vfsService.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path o ) {
                parameterizedCommand.execute( o );
            }
        } ).get( path );

    }
}
