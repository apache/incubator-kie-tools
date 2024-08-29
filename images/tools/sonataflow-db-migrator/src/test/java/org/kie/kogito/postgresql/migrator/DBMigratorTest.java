package org.kie.kogito.postgresql.migrator;

import io.quarkus.test.Mock;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class DBMigratorTest {
    @Rule
    public final ExpectedSystemExit exitRule = ExpectedSystemExit.none();

    @Mock
    MigrationService migrationService;

    @Mock
    DBConnectionChecker dbConnectionChecker;

    DBMigrator dbMigrator = new DBMigrator();

    @BeforeEach
    public void setupEach() {
        migrationService = mock(MigrationService.class);
        dbConnectionChecker = mock(DBConnectionChecker.class);
    }

    @Test
    public void testMigratorWithNoMigrations() throws Exception {
        dbMigrator.migrateDataIndex = false;
        dbMigrator.migrateJobsService = false;

        exitRule.expectSystemExitWithStatus(0);
        dbMigrator.run();
    }

    @Test
    public void testMigratorWithAllMigrations() throws Exception {
        dbMigrator.migrateDataIndex = true;
        dbMigrator.migrateJobsService = true;
        dbMigrator.dbConnectionChecker = dbConnectionChecker;
        dbMigrator.service = migrationService;

        exitRule.expectSystemExitWithStatus(0);
        dbMigrator.run();
    }

    @Test
    public void testDataIndexMigrationWithException() throws Exception {
        dbMigrator.migrateDataIndex = true;
        dbMigrator.migrateJobsService = false;
        dbMigrator.dbConnectionChecker = dbConnectionChecker;
        dbMigrator.service = migrationService;

        doThrow(new SQLException()).when(dbConnectionChecker).checkDataIndexDBConnection();

        exitRule.expectSystemExitWithStatus(-1);
        dbMigrator.run();
    }

    @Test
    public void testJobsServiceWithException() throws Exception {
        dbMigrator.migrateDataIndex = false;
        dbMigrator.migrateJobsService = true;
        dbMigrator.dbConnectionChecker = dbConnectionChecker;
        dbMigrator.service = migrationService;

        doThrow(new SQLException()).when(dbConnectionChecker).checkJobsServiceDBConnection();

        exitRule.expectSystemExitWithStatus(-2);
        dbMigrator.run();
    }
}
