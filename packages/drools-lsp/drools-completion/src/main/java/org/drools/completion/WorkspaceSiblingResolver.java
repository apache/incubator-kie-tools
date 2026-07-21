/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.nio.file.Path;
import java.util.List;

/**
 * Resolves the set of DRL files that should be considered "siblings" of a
 * given document — files in the same logical group whose declared types are
 * in scope for completion and navigation.
 *
 * <p>The default resolver (see {@link WorkspaceSiblingResolvers}) groups by
 * directory: every other {@code .drl} file beside the current one. Hosts
 * with an explicit grouping model (build configuration, rule sets, …) can
 * install their own implementation via
 * {@link WorkspaceSiblingResolvers#setActive}.
 */
public interface WorkspaceSiblingResolver {

    /**
     * Returns the absolute paths of the files grouped with
     * {@code currentFile} (excluding the file itself), or an empty list when
     * no grouping applies.
     */
    List<Path> resolveSiblings(Path currentFile);
}
