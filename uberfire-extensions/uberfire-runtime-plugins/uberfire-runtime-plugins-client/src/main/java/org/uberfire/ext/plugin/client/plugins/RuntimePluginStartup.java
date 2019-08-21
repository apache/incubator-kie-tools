package org.uberfire.ext.plugin.client.plugins;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.ScriptInjector;
import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.backend.plugin.PluginProcessor;
import org.uberfire.backend.plugin.RuntimePlugin;
import org.uberfire.backend.plugin.RuntimePluginService;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorGenerator;
import org.uberfire.workbench.events.UberfireJSAPIReadyEvent;

@EntryPoint
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class RuntimePluginStartup {

    @Inject
    PerspectiveEditorGenerator perspectiveEditorGenerator;
    @Inject
    private Workbench workbench;
    @Inject
    private Caller<RuntimePluginService> runtimePlugins;

    @PostConstruct
    public void init() {
        if (!BusToolsCli.isRemoteCommunicationEnabled()) {
            return;
        }

        workbench.addStartupBlocker(RuntimePluginStartup.class);
    }

    void startPlugins(@Observes UberfireJSAPIReadyEvent event) {
        if (!BusToolsCli.isRemoteCommunicationEnabled()) {
            return;
        }

        runtimePlugins.call(new RemoteCallback<List<RuntimePlugin>>() {
            @Override
            public void callback(List<RuntimePlugin> plugins) {
                try {
                    for (final RuntimePlugin p : plugins) {
                        if (isJSPlugin(p)) {
                            ScriptInjector.fromString(p.getPluginContent()).setWindow(ScriptInjector.TOP_WINDOW).inject();
                        } else if (isPerspectivePlugin(p)) {
                            perspectiveEditorGenerator.generatePerspective(p.getPluginContent());
                        }
                    }
                } finally {
                    workbench.removeStartupBlocker(RuntimePluginStartup.class);
                }
            }
        }).getRuntimePlugins();
    }

    private boolean isPerspectivePlugin(RuntimePlugin p) {
        return p.getType().name() == PluginProcessor.PluginProcessorType.PERSPECTIVE_EDITOR.name();
    }

    private boolean isJSPlugin(RuntimePlugin p) {
        return p.getType().name() == PluginProcessor.PluginProcessorType.JS.name();
    }
}
