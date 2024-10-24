/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.postgresql.migrator;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import io.quarkus.logging.Log;

import java.sql.SQLException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@QuarkusMain
public class DBMigrator implements QuarkusApplication {

    @Inject
    MigrationService service;

    @Inject
    DBConnectionChecker dbConnectionChecker;

    @ConfigProperty(name = "migrate.db.dataindex")
    Boolean migrateDataIndex;

    @ConfigProperty(name = "migrate.db.jobsservice")
    Boolean migrateJobsService;

    @Override
    public int run(String... args) {
        if (migrateDataIndex) {
            try {
                dbConnectionChecker.checkDataIndexDBConnection();
            } catch (SQLException e) {
                Log.error( "Error obtaining data index database connection. Cannot proceed, exiting.");
                Quarkus.asyncExit(-1);
                return -1;
            }
            service.migrateDataIndex();
        }

        if (migrateJobsService) {
            try {
                dbConnectionChecker.checkJobsServiceDBConnection();
            } catch (SQLException e) {
                Log.error( "Error obtaining jobs service database connection. Cannot proceed, exiting.");
                Quarkus.asyncExit(-2);
                return -2;
            }
            service.migrateJobsService();
        }

        Quarkus.asyncExit(0);
        return 0;
    }
}