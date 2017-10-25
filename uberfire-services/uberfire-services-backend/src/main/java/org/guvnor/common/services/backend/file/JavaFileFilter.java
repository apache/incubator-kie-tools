/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.backend.file;

import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Path;

/**
 * Filter to match Java source files
 */
public class JavaFileFilter implements DirectoryStream.Filter<Path> {

    @Override
    public boolean accept(final Path path) {
        final String fileName = path.getFileName().toString();
        return fileName.toLowerCase().endsWith(".java");
    }
}
