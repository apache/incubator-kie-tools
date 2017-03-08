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

package org.uberfire.ext.editor.commons.backend.service.naming;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.PathNamingService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class PathNamingServiceImpl implements PathNamingService {

    @Inject
    private Instance<ResourceTypeDefinition> resourceTypeDefinitions;

    public Path buildTargetPath(Path originalPath,
                                String targetFileName) {
        final org.uberfire.java.nio.file.Path _originalPath = Paths.convert(originalPath);
        String extension = "";

        if (!Files.isDirectory(_originalPath)) {
            extension = getExtension(originalPath.getFileName());
        }

        return Paths.convert(_originalPath.resolveSibling(targetFileName + extension));
    }

    public Path buildTargetPath(Path originalPath,
                                Path targetParentDirectory,
                                String targetFileName) {
        final org.uberfire.java.nio.file.Path _originalPath = Paths.convert(originalPath);
        final org.uberfire.java.nio.file.Path _targetParentDirectory = Paths.convert(targetParentDirectory);
        String extension = "";

        if (!Files.isDirectory(_originalPath)) {
            extension = getExtension(originalPath.getFileName());
        }

        return Paths.convert(_targetParentDirectory.resolve(targetFileName + extension));
    }

    public String getExtension(final String fileName) {
        String extension = getResourceTypeExtension(fileName);

        if (extension == null) {
            extension = "";
            final int extensionIndex = fileName.lastIndexOf(".");

            if (extensionIndex >= 0) {
                extension = fileName.substring(extensionIndex);
            }
        }

        return extension;
    }

    private String getResourceTypeExtension(String fileName) {
        String extension = null;

        for (ResourceTypeDefinition resourceTypeDefinition : getResourceTypeDefinitions()) {
            if (resourceTypeDefinition.getSuffix() != null) {
                String resourceTypeExtension = "." + resourceTypeDefinition.getSuffix();
                if (fileName.endsWith(resourceTypeExtension)) {
                    if (extension == null || resourceTypeExtension.length() > extension.length()) {
                        extension = resourceTypeExtension;
                    }
                }
            }
        }

        return extension;
    }

    public Iterable<ResourceTypeDefinition> getResourceTypeDefinitions() {
        return resourceTypeDefinitions;
    }
}
