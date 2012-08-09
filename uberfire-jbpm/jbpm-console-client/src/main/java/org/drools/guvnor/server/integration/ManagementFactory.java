package org.drools.guvnor.server.integration;


import org.drools.guvnor.server.util.ServiceLoader;

/**
 * Construct management implementation.
 * It uses the {@link org.jboss.bpm.console.server.util.ServiceLoader} to load concrete
 * factory implementations.
 */
public abstract class ManagementFactory {
    public static ManagementFactory newInstance() {
        return (ManagementFactory)
                ServiceLoader.loadService(
                        ManagementFactory.class.getName(),
                        "org.jbpm.integration.console.ManagementFactoryImpl"
                );
    }

    public abstract ProcessManagement createProcessManagement();

    public abstract TaskManagement createTaskManagement();

    public abstract UserManagement createUserManagement();

}
