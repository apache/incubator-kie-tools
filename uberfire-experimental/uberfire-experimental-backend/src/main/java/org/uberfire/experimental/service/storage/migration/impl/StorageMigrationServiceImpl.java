/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.experimental.service.storage.migration.impl;

import org.uberfire.experimental.service.storage.migration.StorageMigration;
import org.uberfire.experimental.service.storage.migration.StorageMigrationService;
import org.uberfire.java.nio.file.FileSystem;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Dependent
public class StorageMigrationServiceImpl implements StorageMigrationService {

    private Set<StorageMigration> migrations = new TreeSet<>(Comparator.comparingInt(StorageMigration::getTargetVersion));

    @Inject
    public StorageMigrationServiceImpl(Instance<StorageMigration> instance) {
        for (StorageMigration migration : instance) {
            migrations.add(migration);
        }
    }

    @Override
    public void migrate(final Integer targetVersion, final FileSystem fileSystem) {
        migrations.iterator().forEachRemaining(migration -> {
            if (migration.getTargetVersion() <= targetVersion) {
                migration.migrate(fileSystem);
            }
        });
    }
}
