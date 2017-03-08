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
package org.uberfire.security.client.authz.tree;

import java.util.Collection;

/**
 * Interface defining several options for altering the way permission nodes are loaded
 */
public interface LoadOptions {

    /**
     * A string pattern used to match the nodes to load.
     * @return A string used to compare against the node name.
     * If null or empty then this option is ruled out.
     */
    String getNodeNamePattern();

    /**
     * The resource identifiers to take into account when loading the children nodes.
     * @return A collection of ids
     * If null or empty then this option is ruled out.
     */
    Collection<String> getResourceIds();

    /**
     * The maximum number of nodes to load.
     * <p>
     * <p>NOTE: It can only be used in combination with the {@link #getNodeNamePattern()} option.</p>
     * @return A positive integer. If 0 or negative integer then all nodes are loaded.
     */
    int getMaxNodes();
}