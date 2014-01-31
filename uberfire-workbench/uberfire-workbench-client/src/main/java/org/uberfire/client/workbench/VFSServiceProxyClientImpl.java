package org.uberfire.client.workbench;

import javax.enterprise.context.Dependent;

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class VFSServiceProxyClientImpl implements VFSServiceProxy {

    @Override
    public void get( final String path,
                     final ParameterizedCommand<Path> parameterizedCommand ) {
        parameterizedCommand.execute( null );
    }
}
