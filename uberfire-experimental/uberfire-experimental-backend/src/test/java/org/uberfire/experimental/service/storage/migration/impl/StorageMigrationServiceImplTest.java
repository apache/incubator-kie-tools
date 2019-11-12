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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.experimental.service.storage.migration.StorageMigration;
import org.uberfire.java.nio.file.FileSystem;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StorageMigrationServiceImplTest {

    @Mock
    private StorageMigration migration1;

    @Mock
    private StorageMigration migration2;

    @Mock
    private StorageMigration migration3;

    @Mock
    private FileSystem fileSystem;

    private List<StorageMigration> migrationList = new ArrayList<>();

    private StorageMigrationServiceImpl migrationService;

    @Before
    public void init() {
        when(migration1.getTargetVersion()).thenReturn(1);
        when(migration2.getTargetVersion()).thenReturn(2);
        when(migration3.getTargetVersion()).thenReturn(3);

        Instance<StorageMigration> migrations = mock(Instance.class);

        migrationList.add(migration3);
        migrationList.add(migration1);
        migrationList.add(migration2);

        when(migrations.iterator()).thenReturn(migrationList.iterator());

        migrationService = new StorageMigrationServiceImpl(migrations);
    }

    @Test
    public void testMigrateV1() {
        testMigrate(1);
    }

    @Test
    public void testMigrateV2() {
        testMigrate(2);
    }

    @Test
    public void testMigrateV3() {
        testMigrate(3);
    }

    private void testMigrate(int version) {
        migrationService.migrate(version, fileSystem);

        Collections.sort(migrationList, Comparator.comparingInt(StorageMigration::getTargetVersion));

        for (int i = 0; i < migrationList.size(); i++) {
            verify(migrationList.get(i), i + 1 <= version ? times(1) : never()).migrate(eq(fileSystem));
        }
    }
}
