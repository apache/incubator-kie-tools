package org.uberfire.client;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSLockService;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.mvp.ParameterizedCommand;

@Alternative
public class VFSLockServiceProxyBackendImpl implements VFSLockServiceProxy {

    @Inject
    private Caller<VFSLockService> vfsLockService;

    @Override
    public void acquireLock( final Path path,
                             final ParameterizedCommand<LockResult> parameterizedCommand ) {

        vfsLockService.call( new RemoteCallback<LockResult>() {

            @Override
            public void callback( final LockResult result ) {
                parameterizedCommand.execute( result );

            }
        } ).acquireLock( path );

    }

    @Override
    public void releaseLock( final Path path,
                             final ParameterizedCommand<LockResult> parameterizedCommand ) {
        
        vfsLockService.call( new RemoteCallback<LockResult>() {

            @Override
            public void callback( final LockResult result ) {
                parameterizedCommand.execute( result );

            }
        } ).releaseLock( path );

    }

    @Override
    public void retrieveLockInfo( final Path path,
                                  final ParameterizedCommand<LockInfo> parameterizedCommand ) {
        
        vfsLockService.call( new RemoteCallback<LockInfo>() {

            @Override
            public void callback( final LockInfo lockInfo ) {
                parameterizedCommand.execute( lockInfo );

            }
        } ).retrieveLockInfo( path );

    }

}
