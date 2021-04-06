/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.config.rebind;

import java.util.Collection;

import org.jboss.errai.codegen.meta.MetaClass;

/**
 * An <tt>ExposedTypesProvider</tt> is an environment extension component that can be automatically discovered
 * at runtime (when annotated with {@link EnvironmentConfigExtension}) to provide types to the environment
 * config that application components want made available to the marshalling framework.
 *
 * @author Mike Brock
 */
public interface ExposedTypesProvider {

    /**
     * Returns the list of types that should be exposed as portable. The returned
     * list may include any kind of MetaClass: primitive types, interfaces,
     * abstract and concrete classes, enums, annotation types, and so on.
     *
     * @return a collection of types that will need to be marshallable, based on
     *         the type of inspection performed by the underlying implementation.
     */
    public Collection<MetaClass> provideTypesToExpose();
}
