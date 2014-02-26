package org.uberfire.commons.services.cdi;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Designates the target type as a server-side bean that should be created immediately when the application is deployed
 * within the server. Normally, CDI beans are instantiated lazily when first needed, but {@code @Startup} beans
 * have their PostConstruct methods called early in the server-side CDI application lifecycle.
 */
@Retention(RUNTIME)
@Documented
@Target({ TYPE })
public @interface Startup {

    /**
     * Specifies which cohort this {@Startup} bean is initialized in.
     */
    StartupType value() default StartupType.EAGER;

    int priority() default 0;

}
