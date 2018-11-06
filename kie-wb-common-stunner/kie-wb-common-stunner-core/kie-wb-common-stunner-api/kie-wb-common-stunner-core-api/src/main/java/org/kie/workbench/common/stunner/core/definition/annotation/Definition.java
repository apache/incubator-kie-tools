/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.definition.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.definition.builder.VoidBuilder;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Definition {

    Class<? extends ElementFactory> graphFactory();

    @Deprecated
    Class<? extends Builder<?>> builder() default VoidBuilder.class;

    /**
     * Corresponds to the field that represents the <b>Name</b> of the annotated class.
     * The name field should be expressed with the namespace if applied.
     * </br>
     *
     * Example: nameField = "general.text"
     * "general" is the attribute name on the current Definition(annotated class) and "text" is the attribute name
     * contained in "general".     *
     * </br>
     *
     * Note: If nameField is not set, than the first attribute annotated with {@link PropertyMetaTypes#NAME}
     * will be used to return the name as a default behavior.
     */
    String nameField() default "";
}