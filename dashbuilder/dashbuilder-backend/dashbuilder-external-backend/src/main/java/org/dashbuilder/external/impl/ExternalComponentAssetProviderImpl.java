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

import org.dashbuilder.external.service.ExternalComponentAssetProvider;
import org.dashbuilder.external.service.ExternalComponentLoader;

@ApplicationScoped
public class ExternalComponentAssetProviderImpl implements ExternalComponentAssetProvider {

    @Inject
    ExternalComponentLoader componentsLoader;

    @Override
    public InputStream openAsset(String componentAssetPath) {
        try {
            Path path = Paths.get(componentsLoader.getExternalComponentsDir(), componentAssetPath);
            return new FileInputStream(path.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}