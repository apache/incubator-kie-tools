/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.backend.service;

import java.sql.Connection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSource;
import org.kie.workbench.common.screens.datasource.management.backend.core.DataSourceRuntimeManager;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.model.TestResult;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceRuntimeManagerClientService;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class DataSourceRuntimeManagerClientServiceImpl
        implements DataSourceRuntimeManagerClientService {

    @Inject
    private DataSourceRuntimeManager runtimeManager;

    public DataSourceRuntimeManagerClientServiceImpl() {
    }

    @Override
    public DataSourceDeploymentInfo getDataSourceDeploymentInfo(String uuid) {
        try {
            return runtimeManager.getDataSourceDeploymentInfo(uuid);
        } catch (Exception e) {
            throw new GenericPortableException(e.getMessage(),
                                               e);
        }
    }

    @Override
    public DriverDeploymentInfo getDriverDeploymentInfo(String uuid) {
        try {
            return runtimeManager.getDriverDeploymentInfo(uuid);
        } catch (Exception e) {
            throw new GenericPortableException(e.getMessage(),
                                               e);
        }
    }

    @Override
    public TestResult testDataSource(final String uuid) {
        try {
            DataSource dataSource = runtimeManager.lookupDataSource(uuid);
            return test(dataSource);
        } catch (Exception e) {
            StringBuilder strBuilder = new StringBuilder();
            TestResult testResult = new TestResult(false);
            strBuilder.append("Reference to datasource ds: " + uuid + " couldn't be obtained ");
            strBuilder.append("\n");
            strBuilder.append("Test Failed");
            testResult.setMessage(strBuilder.toString());
            return testResult;
        }
    }

    private TestResult test(final DataSource dataSource) {
        TestResult testResult = new TestResult(false);
        StringBuilder stringBuilder = new StringBuilder();
        try {

            checkNotNull("dataSource",
                         dataSource);

            stringBuilder.append("Reference to datasource was successfully obtained: " + dataSource);
            stringBuilder.append("\n");

            Connection conn = dataSource.getConnection();

            if (conn == null) {
                stringBuilder.append("It was not possible to get connection from the datasoure.");
                stringBuilder.append("\n");
                stringBuilder.append("Test Failed");
            } else {
                stringBuilder.append("Connection was successfully obtained: " + conn);
                stringBuilder.append("\n");
                stringBuilder.append("*** DatabaseProductName: " + conn.getMetaData().getDatabaseProductName());
                stringBuilder.append("\n");
                stringBuilder.append("*** DatabaseProductVersion: " + conn.getMetaData().getDatabaseProductVersion());
                stringBuilder.append("\n");
                stringBuilder.append("*** DriverName: " + conn.getMetaData().getDriverName());
                stringBuilder.append("\n");
                stringBuilder.append("*** DriverVersion: " + conn.getMetaData().getDriverVersion());
                stringBuilder.append("\n");
                conn.close();
                stringBuilder.append("Connection was successfully released.");
                stringBuilder.append("\n");
                stringBuilder.append("Test Successful");
                testResult.setTestPassed(true);
            }
        } catch (Exception e) {
            stringBuilder.append(e.getMessage());
            stringBuilder.append("\n");
            stringBuilder.append("Test Failed");
        }
        testResult.setMessage(stringBuilder.toString());
        return testResult;
    }
}