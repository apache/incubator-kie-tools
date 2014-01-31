package org.uberfire.client;

import java.util.Collection;
import java.util.Collections;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.uberfire.mvp.ParameterizedCommand;

@Dependent
@Alternative
public class RuntimePluginsServiceProxyClientImpl implements RuntimePluginsServiceProxy {

    @Override
    public void getTemplateContent( String contentUrl,
                                    ParameterizedCommand<String> command ) {
        command.execute( "" );
    }

    @Override
    public void listFramworksContent( ParameterizedCommand<Collection<String>> command ) {
        command.execute( Collections.<String>emptyList() );
    }

    @Override
    public void listPluginsContent( ParameterizedCommand<Collection<String>> command ) {
        command.execute( Collections.<String>emptyList() );
    }
}
