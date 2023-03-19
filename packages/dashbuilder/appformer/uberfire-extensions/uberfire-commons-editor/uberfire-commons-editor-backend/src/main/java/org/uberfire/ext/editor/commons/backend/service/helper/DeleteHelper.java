/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.editor.commons.backend.service.helper;

import org.uberfire.backend.vfs.Path;

/**
 * Helper for when a file is deleted. Helpers are always invoked before the deletion occurs.
 */
public interface DeleteHelper {

    /**
     * Helpers signal whether it supports the given Path
     * @param path The Path that was deleted.
     * @return
     */
    boolean supports(final Path path);

    /**
     * Performs post processing following the delete operation. This is always within a IOService batch operation.
     * @param path The Path that was deleted.
     */
    void postProcess(final Path path);
}
