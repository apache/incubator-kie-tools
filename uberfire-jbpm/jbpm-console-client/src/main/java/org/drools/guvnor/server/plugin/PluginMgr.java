package org.drools.guvnor.server.plugin;

import org.drools.guvnor.server.util.ServiceLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Loads plugins through the {@link org.jboss.bpm.console.server.util.ServiceLoader}.
 */
public class PluginMgr {

    //    private static final Log log = LogFactory.getLog(PluginMgr.class);
    private static List<String> failedToResolve = new CopyOnWriteArrayList<String>();

    /**
     * Load a plugin through the {@link org.jboss.bpm.console.server.util.ServiceLoader}.
     * The plugin interface name acts as the service key.
     *
     * @param type plugin interface
     * @return a plugin implementation of type T or null if the plugin is not available.
     */
    public static <T> T load(Class<T> type) {
        boolean failedBefore = failedToResolve.contains(type.getName());
        if (failedBefore) return null;

        T pluginImpl = (T) ServiceLoader.loadService(
                type.getName(), null
        );


        if (pluginImpl != null) {
//            log.info("Successfully loaded plugin '" +type.getName()+ "': "+pluginImpl.getClass());
            return pluginImpl;
        } else {
            //log.warn("Unable to load plugin: '" + type.getName() + "'");
            failedToResolve.add(type.getName());
            return null;
        }
    }
}
