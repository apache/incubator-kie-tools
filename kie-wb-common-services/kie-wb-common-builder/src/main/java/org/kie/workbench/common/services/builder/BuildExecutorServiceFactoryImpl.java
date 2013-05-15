package org.kie.workbench.common.services.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * Producer for Executor services so we can plug-in a different implementation in tests
 */
@ApplicationScoped
public class BuildExecutorServiceFactoryImpl implements BuildExecutorServiceFactory {

    private ExecutorService service;

    @PostConstruct
    public void setup() {
        final int cores = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool( cores );
    }

    @Override
    public ExecutorService getExecutorService() {
        return service;
    }

}
