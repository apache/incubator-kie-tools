/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.external.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.dashbuilder.external.service.ComponentAssetProvider;
import org.dashbuilder.external.service.ComponentLoader;

/**
 * Looks for components assets.
 *
 */
@ApplicationScoped
public class ComponentAssetProviderImpl implements ComponentAssetProvider {

    @Inject
    ComponentLoader componentsLoader;

    @Override
    public InputStream openAsset(String componentAssetPath) {
        if (componentAssetPath != null) {
            String normalizedAssetPath = FilenameUtils.normalizeNoEndSeparator(componentAssetPath);
            if (normalizedAssetPath != null) {
                return getExternalComponentAsset(normalizedAssetPath);
            }
        }
        throw new IllegalArgumentException("Invalid Asset Path.");
    }

    String fixSlashes(String componentAssetPath) {
        return componentAssetPath == null ? "" : componentAssetPath.replaceAll("\\\\", "/");
    }

    private InputStream getExternalComponentAsset(String componentAssetPath) {
        if (!componentsLoader.isExternalComponentsEnabled()) {
            throw new IllegalArgumentException("External Components are not enabled");
        }

        Path baseDir = Paths.get(componentsLoader.getExternalComponentsDir());
        Path assetPath = baseDir.resolve(componentAssetPath);

        if (isFileInComponentsDir(baseDir, assetPath)) {
            return loadExternalComponentFile(assetPath);
        } else {
            throw new IllegalArgumentException("Not a component file.");

        }
    }

    private InputStream loadExternalComponentFile(Path assetPath) {
        try {
            return new FileInputStream(assetPath.toFile());
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Error opening component file: " + e.getMessage(), e);
        }
    }

    // Used to prevent path traversal attacks.
    // Reference https://portswigger.net/web-security/file-path-traversal
    private boolean isFileInComponentsDir(Path baseDir, Path assetPath) {
        return assetPath.toAbsolutePath().normalize().startsWith(baseDir);
    }

}