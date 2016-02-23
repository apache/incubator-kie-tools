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

package org.uberfire.ext.editor.commons.service.restrictor;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.restriction.PathOperationRestriction;

/**
 * Represents a restrictor to a path operation (copy, delete or rename).
 */
public interface PathOperationRestrictor {

    /**
     * Checks if there is a restriction to execute a operation on this path.
     * @param path Path to be checked.
     * @return The restriction to execute the operation, or null if there is not one.
     */
    PathOperationRestriction hasRestriction( Path path );
}
