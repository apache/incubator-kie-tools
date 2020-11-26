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

package org.dashbuilder.backend;

import java.io.File;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.dashbuilder.external.service.ComponentLoader;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;

/**
 * When a runtime model is removed then we should remove components content as well.
 *
 */
@ApplicationScoped
public class ExternalComponentsContentListener {

    @Inject
    ComponentLoader loader;

    @Inject
    RuntimeOptions options;

    public void onRuntimeModelRemoved(@Observes RemovedRuntimeModelEvent event) {
        if (options.isComponentPartition()) {
            String componentsDir = loader.getExternalComponentsDir();
            String runtimeModelId = event.getRuntimeModelId();
            if (componentsDir != null && runtimeModelId != null) {
                File runtimeModelComponentsFile = Paths.get(componentsDir, runtimeModelId).toFile();
                FileUtils.deleteQuietly(runtimeModelComponentsFile);
            }
        }

    }

}