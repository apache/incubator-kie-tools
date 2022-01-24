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

package org.uberfire.security;

import java.util.Collections;
import java.util.List;

import jsinterop.annotations.JsType;

/**
 * A generic interface for modelling resources, like UI assets: perspectives, screens or
 * editors or even backend resources like repositories, projects, data objects, etc...
 */
@JsType
public interface Resource {

    /**
     * An identifier that is unique among all the resources of the same type
     * (see {@link Resource#getResourceType()}).
     */
    String getIdentifier();

    /**
     * Get the resource type classifier
     */
    default ResourceType getResourceType() {
        return ResourceType.UNKNOWN;
    }

    /**
     * A list of dependent resources.
     * <p>
     * <p>
     * The dependency list is used for instance to determine if a user can
     * access a given resource. Should the access to all its dependencies is
     * denied, it is denied for this instance as well.
     * </p>
     * @return A list of resources, never null.
     */
    default List<Resource> getDependencies() {
        return Collections.emptyList();
    }

    /**
     * Check if this resource is of the provided type. The type name is used
     * here so this method can be used on instances from external (GWT-compiled)
     * scripts (enum equals and instanceof doesn't work across script boundaries).
     * @param typeName the resource type's name
     * @return true if the resource has the provided type, otherwise false.
     */
    default boolean isType(String typeName) {
        return getResourceType().getName().equalsIgnoreCase(typeName);
    }
}
