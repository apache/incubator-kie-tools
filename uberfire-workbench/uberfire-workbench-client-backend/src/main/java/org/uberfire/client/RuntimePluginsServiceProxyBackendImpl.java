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
