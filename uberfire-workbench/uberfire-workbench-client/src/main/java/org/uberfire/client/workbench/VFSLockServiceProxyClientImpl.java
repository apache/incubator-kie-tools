package org.uberfire.client.workbench;

import javax.enterprise.context.Dependent;

import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class VFSLockServiceProxyClientImpl implements VFSLockServiceProxy {

    @Override
    public void acquireLock( Path path,
                             ParameterizedCommand<LockResult> parameterizedCommand ) {
        parameterizedCommand.execute( new LockResult(true, new LockInfo(false, "", path)) );
        
    }

    @Override
    public void releaseLock( Path path,
                             ParameterizedCommand<LockResult> parameterizedCommand ) {
        parameterizedCommand.execute( new LockResult(true, new LockInfo(false, "", path)) );
        
    }

    @Override
    public void retrieveLockInfo( Path path,
                                  ParameterizedCommand<LockInfo> parameterizedCommand ) {
        parameterizedCommand.execute( new LockInfo(false, "", path) );
    }
    
}