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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.quarkus.logging.Log;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DBConnectionChecker {
    @ConfigProperty(name = "quarkus.datasource.dataindex.jdbc.url")
    String dataIndexDBURL;

    @ConfigProperty(name = "quarkus.datasource.dataindex.username")
    String dataIndexDBUserName;

    @ConfigProperty(name = "quarkus.datasource.dataindex.password")
    String dataIndexDBPassword;

    @ConfigProperty(name = "quarkus.datasource.jobsservice.jdbc.url")
    String jobsServiceDBURL;

    @ConfigProperty(name = "quarkus.datasource.jobsservice.username")
    String jobsServiceDBUserName;

    @ConfigProperty(name = "quarkus.datasource.jobsservice.password")
    String jobsServiceDBPassword;

    private void checkDBConnection(String dbURL, String dbUser, String dbPassword) throws SQLException {
        try (Connection db = DriverManager.getConnection(dbURL, dbUser, dbPassword)) {
            Log.infof("Checking DB connection: %s - success", dbURL);
        } catch (SQLException sqe) {
            Log.infof("Checking DB connection %s- failed", dbURL);
            throw sqe;
        }
    }

    public void checkDataIndexDBConnection() throws SQLException {
        checkDBConnection(dataIndexDBURL, dataIndexDBUserName, dataIndexDBPassword);
    }

    public void checkJobsServiceDBConnection() throws SQLException {
        checkDBConnection(jobsServiceDBURL, jobsServiceDBUserName, jobsServiceDBPassword);
    }
}
