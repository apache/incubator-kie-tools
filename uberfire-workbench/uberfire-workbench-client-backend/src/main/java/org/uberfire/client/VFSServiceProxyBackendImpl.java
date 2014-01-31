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
