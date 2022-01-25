/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import jsinterop.annotations.JsType;

/**
 * An action represents something that someone can do over a resource.
 * Can vary from a complex UI feature to a low-level action.
 * <p>
 * <p>This interface is intended to be extended by the different {@link Resource} types. It is up to every
 * resource type implementation to define the list of available actions.</p>
 */
@JsType
public interface ResourceAction {

    /**
     * The read action is common to all resource types.
     * <p>Basically, it refers to the ability to access (view, read, ...) a resource</p>
     */
    ResourceAction READ = () -> "read";

    /**
     * An string representation of the action.
     */
    String getName();
}
