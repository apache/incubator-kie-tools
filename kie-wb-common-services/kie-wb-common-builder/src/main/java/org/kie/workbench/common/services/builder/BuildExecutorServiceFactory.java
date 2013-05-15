package org.kie.workbench.common.services.builder;

import java.util.concurrent.ExecutorService;

/**
 * Producer of ExecutorServices
 */
public interface BuildExecutorServiceFactory {

    /**
     * Return an ExecutorService
     * @return
     */
    ExecutorService getExecutorService();

}
