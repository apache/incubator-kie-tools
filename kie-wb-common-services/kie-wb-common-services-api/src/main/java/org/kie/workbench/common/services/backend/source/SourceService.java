/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.backend.source;

import org.uberfire.java.nio.file.Path;

public interface SourceService<T> {

    boolean accepts( final Path path );

    /**
     * @param path path to the file
     * @param model the current model. Originally loaded from the file, but the content might have changed.
     * @return Source generated from the model, not from the file that the path points to.
     */
    String getSource( final Path path,
                      final T model );
    /**
     * @param path path to the file
     * @return Source generated from the model, that the path points to.
     */
    String getSource(Path path);

    String getPattern();

}
