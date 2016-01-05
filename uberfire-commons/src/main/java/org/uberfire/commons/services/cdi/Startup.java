/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
