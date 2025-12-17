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

 package org.kie.kogito.migrator.postgresql;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.logging.Log;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import io.quarkus.flyway.FlywayDataSource;

@ApplicationScoped
public class MigrationService {
    @Inject
    @FlywayDataSource("dataindex") 
    Flyway flywayDataIndex;

    @Inject
    @FlywayDataSource("jobsservice") 
    Flyway flywayJobsService;

    @ConfigProperty(name = "quarkus.flyway.dataindex.clean-at-start")
    Boolean cleanDataIndex;

    @ConfigProperty(name = "quarkus.flyway.jobsservice.clean-at-start")
    Boolean cleanJobsService;

    private void migrateDB(Flyway flywayService, Boolean clean, String serviceName) {
        Log.info("Migrating " + serviceName);
        if (clean) {
            Log.info("Cleaned the " + serviceName);
            flywayService.clean();
        }
        flywayService.migrate();
        if (flywayService.info() != null) {
            Log.info("Migrated to version " + flywayService.info().current().toString());
        }
    }

    public void migrateDataIndex() {
        migrateDB(flywayDataIndex, cleanDataIndex, "data-index");
    }

    public void migrateJobsService() {
        migrateDB(flywayJobsService, cleanJobsService, "jobs-service");
    }
}