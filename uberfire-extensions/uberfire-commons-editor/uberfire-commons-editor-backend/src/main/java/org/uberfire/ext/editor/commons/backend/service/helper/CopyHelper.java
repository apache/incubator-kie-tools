/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
 * Helper for when a file is copied. Helpers are always invoked after the copy occurred.
 */
public interface CopyHelper {

    /**
     * This is invoked by parent code to provide the means for a Helper to signal whether it supports the given Path
     * @param destination Path
     * @return
     */
    boolean supports( final Path destination );

    /**
     * Perform post processing following the copy operation. This is always within a IOService batch operation.
     * @param source Path Source before copy
     * @param destination Path Destination after copy
     */
    void postProcess( final Path source,
                      final Path destination );

}
