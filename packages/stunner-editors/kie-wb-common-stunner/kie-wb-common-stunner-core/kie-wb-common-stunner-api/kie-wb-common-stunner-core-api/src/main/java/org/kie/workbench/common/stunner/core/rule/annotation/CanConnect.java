/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for specifying a connection rule.
 * It allows the connection from domain objects that have the role specified in <code>startRole</code> to domain objects
 * that have the role specified in <code>endRole</code>.
 * It's only allowed to use on Definitions of type Edge
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AllowedConnections.class)
public @interface CanConnect {

    String role() default "";

    String startRole();

    String endRole();
}
