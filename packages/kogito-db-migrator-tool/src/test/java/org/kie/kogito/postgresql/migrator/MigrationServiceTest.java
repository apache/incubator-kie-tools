/*
 * Copyright 2024 Apache Software Foundation (ASF)
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

package org.kie.kogito.postgresql.migrator;

import io.quarkus.test.Mock;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.CleanResult;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class MigrationServiceTest {
    @Mock
    Flyway flyway;

    MigrationService migrationService = new MigrationService();

    @BeforeEach
    public void setupEach() {
        flyway = mock(Flyway.class);
        when(flyway.migrate()).thenReturn(new MigrateResult("flywayVersion", "db", "schema", "postgres"));
        when(flyway.clean()).thenReturn(new CleanResult("flywayVersion", "db"));
    }

    @Test
    public void testMigrateDataIndexWithNoClean() {
        migrationService.cleanDataIndex = false;
        migrationService.flywayDataIndex = flyway;
        migrationService.migrateDataIndex();
    }

    @Test
    public void testMigrateDataIndexWithClean() {
        migrationService.cleanDataIndex = true;
        migrationService.flywayDataIndex = flyway;
        migrationService.migrateDataIndex();
    }

    @Test
    public void testMigrateJobsServiceWithNoClean() {
        migrationService.cleanJobsService = false;
        migrationService.flywayJobsService = flyway;
        migrationService.migrateJobsService();
    }

    @Test
    public void testMigrateJobsServiceWithClean() {
        migrationService.cleanJobsService = true;
        migrationService.flywayJobsService = flyway;
        migrationService.migrateJobsService();
    }
}
