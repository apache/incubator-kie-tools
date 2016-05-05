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

package org.uberfire.ext.editor.commons.service.restriction;

import org.uberfire.backend.vfs.Path;

/**
 * Represents a restriction to a path operation (copy, delete or rename).
 */
public interface PathOperationRestriction {

    /**
     * Returns the message which describes the restriction.
     * @param path Path related to the restriction.
     * @return Message related to the restriction.
     */
    String getMessage( Path path );
}
