package org.uberfire.client;

import java.util.Collection;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.ScriptInjector;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.mvp.ParameterizedCommand;

import static com.google.gwt.core.client.ScriptInjector.*;

@EntryPoint
public class JSEntryPoint {

    @Inject
    private RuntimePluginsServiceProxy runtimePluginsService;

    @Inject
    private Event<ApplicationReadyEvent> appReady;

    @Inject
    private ClientMessageBus bus;

    @AfterInitialization
    public void setup() {
        runtimePluginsService.listFrameworksContent( new ParameterizedCommand<Collection<String>>() {
            @Override
            public void execute( final Collection<String> response ) {
                for ( final String s : response ) {
                    ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                }
                runtimePluginsService.listPluginsContent( new ParameterizedCommand<Collection<String>>() {
                    @Override
                    public void execute( final Collection<String> response ) {
                        try {
                            for ( final String s : response ) {
                                ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                            }
                        } finally {
                            appReady.fire( new ApplicationReadyEvent() );
                        }

                    }
                } );
            }
        } );
    }
}
