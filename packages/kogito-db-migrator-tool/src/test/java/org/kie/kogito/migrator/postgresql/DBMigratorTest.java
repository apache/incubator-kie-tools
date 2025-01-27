/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.kogito.migrator.postgresql;

import io.quarkus.test.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class DBMigratorTest {

    @Mock
    MigrationService migrationService;

    @Mock
    DBConnectionChecker dbConnectionChecker;

    DBMigrator dbMigrator = new DBMigrator();

    @BeforeEach
    void setupEach() {
        migrationService = mock(MigrationService.class);
        dbConnectionChecker = mock(DBConnectionChecker.class);
    }

    @Test
    void testMigratorWithNoMigrations() throws Exception {
        dbMigrator.migrateDataIndex = false;
        dbMigrator.migrateJobsService = false;

        dbMigrator.run();
        verify(dbConnectionChecker, times(0)).checkDataIndexDBConnection();
        verify(dbConnectionChecker, times(0)).checkJobsServiceDBConnection();
        verify(migrationService, times(0)).migrateDataIndex();
        verify(migrationService, times(0)).migrateJobsService();
    }

    @Test
    void testMigratorWithAllMigrations() throws Exception {
        dbMigrator.migrateDataIndex = true;
        dbMigrator.migrateJobsService = true;
        dbMigrator.dbConnectionChecker = dbConnectionChecker;
        dbMigrator.service = migrationService;

        dbMigrator.run();
        verify(dbConnectionChecker, times(1)).checkDataIndexDBConnection();
        verify(dbConnectionChecker, times(1)).checkJobsServiceDBConnection();
        verify(migrationService, times(1)).migrateDataIndex();
        verify(migrationService, times(1)).migrateJobsService();
    }

    @Test
    void testDataIndexMigrationWithException() throws Exception {
        dbMigrator.migrateDataIndex = true;
        dbMigrator.migrateJobsService = false;
        dbMigrator.dbConnectionChecker = dbConnectionChecker;
        dbMigrator.service = migrationService;

        doThrow(new SQLException()).when(dbConnectionChecker).checkDataIndexDBConnection();

        dbMigrator.run();
        verify(migrationService, times(0)).migrateDataIndex();
        verify(migrationService, times(0)).migrateJobsService();
    }

    @Test
    void testJobsServiceWithException() throws Exception {
        dbMigrator.migrateDataIndex = false;
        dbMigrator.migrateJobsService = true;
        dbMigrator.dbConnectionChecker = dbConnectionChecker;
        dbMigrator.service = migrationService;

        doThrow(new SQLException()).when(dbConnectionChecker).checkJobsServiceDBConnection();

        dbMigrator.run();
        verify(migrationService, times(0)).migrateDataIndex();
        verify(migrationService, times(0)).migrateJobsService();
    }
}
