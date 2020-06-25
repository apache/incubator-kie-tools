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

package org.dashbuilder.backend.services.impl;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.shared.service.ImportValidationService;

import static org.dashbuilder.shared.model.ImportDefinitions.DATASET_PREFIX;
import static org.dashbuilder.shared.model.ImportDefinitions.NAVIGATION_PREFIX;
import static org.dashbuilder.shared.model.ImportDefinitions.PERSPECTIVE_PREFIX;

/**
 * Simple Validation Service Implementation that checks if the ZIPs contains the necessary structure.
 *
 */
@ApplicationScoped
public class ImportValidationServiceImpl implements ImportValidationService {

    /**
     * Checks if the given file URL is valid for a Runtime Model.
     */
    @Override
    public boolean validate(final String file) {
        boolean hasDatasetDir = false;
        boolean hasPage = false;
        boolean hasNavigation = false;
        try (final ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {

                hasDatasetDir = hasDatasetDir || entryStartsWith(entry, DATASET_PREFIX);
                hasPage = hasPage || entryStartsWith(entry, PERSPECTIVE_PREFIX);
                hasNavigation = hasNavigation || entryStartsWith(entry, NAVIGATION_PREFIX);

                if (hasNavigation && hasDatasetDir && hasPage) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error validating file: " + file, e);
        }

        return false;
    }

    private boolean entryStartsWith(ZipEntry entry, String path) {
        return entry.getName().startsWith(path);
    }

}