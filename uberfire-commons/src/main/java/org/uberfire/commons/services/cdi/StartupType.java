package org.uberfire.commons.services.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.spi.AfterDeploymentValidation;

/**
 * Argument type for the {@link Startup} annotation.
 */
public enum StartupType {

    /**
     * The target bean's {@link PostConstruct} method will be invoked immediately after the all BOOTSTRAP beans
     * have had their PostConstruct methods called.
     * <p>
     * Among EAGER startup beans, the order they are called is not specified, but it is guaranteed that all BOOTSTRAP
     * startup beans are processed before any EAGER startup beans are processed.
     */
    EAGER,

    /**
     * The target bean's {@link PostConstruct} method will be invoked immediately after the CDI container fires the
     * {@code AfterDeploymentValidation} event.
     * See {@link AfterDeploymentValidation} in the CDI documentation for details.
     * <p>
     * Among BOOSTRAP startup beans, the order they are called is not specified, but it is guaranteed that all BOOTSTRAP
     * startup beans are processed before any EAGER startup beans are processed.
     */
    BOOTSTRAP

}
