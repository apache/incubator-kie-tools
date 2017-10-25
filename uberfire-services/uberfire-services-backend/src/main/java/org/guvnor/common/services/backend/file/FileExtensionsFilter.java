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

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.file.Files;

/**
 * A Filter only accepting files with the given file extensions
 */
public class FileExtensionsFilter extends DotFileFilter {

    private String[] extensions;

    public FileExtensionsFilter(final String[] extensions) {
        this.extensions = PortablePreconditions.checkNotNull("extension",
                                                             extensions);
        for (int i = 0; i < extensions.length; i++) {
            if (!extensions[i].startsWith(".")) {
                extensions[i] = "." + extensions[i];
            }
        }
    }

    @Override
    public boolean accept(final org.uberfire.java.nio.file.Path path) {
        //Check with super class first
        boolean accept = super.accept(path);
        if (accept) {
            return false;
        }

        //Only match files
        if (!Files.isRegularFile(path)) {
            return false;
        }

        //Assume the Path does not match by default
        accept = false;
        final String uri = path.toUri().toString();
        for (String extension : extensions) {
            if (uri.substring(uri.length() - extension.length()).equals(extension)) {
                accept = true;
                break;
            }
        }
        return accept;
    }
}
