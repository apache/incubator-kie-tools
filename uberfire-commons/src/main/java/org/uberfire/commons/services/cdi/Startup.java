package org.uberfire.commons.services.cdi;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target({ TYPE })
public @interface Startup {

    StartupType value() default StartupType.EAGER;

    int priority() default 0;

}
