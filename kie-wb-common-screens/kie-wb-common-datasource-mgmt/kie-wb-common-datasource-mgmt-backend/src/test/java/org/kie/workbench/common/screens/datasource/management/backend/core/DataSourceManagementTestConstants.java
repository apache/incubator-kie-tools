/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.backend.core;

public interface DataSourceManagementTestConstants {

    String SEPARATOR = "#";

    String RANDOM_UUID = "random_uuid";

    String DS1_UUID = "uuid1";

    String DS1_NAME = "name1";

    String DS1_CONNECTION_URL = "connectionURL1";

    String DS1_USER = "user1";

    String DS1_PASSWORD = "password1";

    String DS1_JNID = "jndi1";

    String DS1_DEPLOYMENT_ID = "kie" + SEPARATOR + DS1_UUID + SEPARATOR + "_" + RANDOM_UUID;

    String DS2_UUID = "uuid2";

    String DS2_NAME = "name2";

    String DS2_CONNECTION_URL = "connectionURL2";

    String DS2_USER = "user2";

    String DS2_PASSWORD = "password2";

    String DS2_JNID = "jndi2";

    String DS2_DEPLOYMENT_ID = "kie" + SEPARATOR + DS2_UUID + SEPARATOR + "_" + RANDOM_UUID;

    String DS3_UUID = "uuid3";

    String DS3_DEPLOYMENT_ID = "kie" + SEPARATOR + DS3_UUID + SEPARATOR + "_" + RANDOM_UUID;

    String DRIVER1_UUID = "driverUuid1";

    String DRIVER1_DEPLOYMENT_ID = "kie" + SEPARATOR + DRIVER1_UUID + SEPARATOR;

    String DRIVER1_NAME = "driver1";

    String DRIVER2_UUID = "uuid2";

    String GROUP_ID = "group-id";

    String ARTIFACT_ID = "artifact-id";

    String VERSION = "version";

    String DRIVER1_CLASS = "java.sql.MyDriver1";

    String DRIVER2_CLASS = "java.sql.MyDriver2";

    String METHOD_EXECUTION_OK = "METHOD_EXECUTION_OK";

    String METHOD_EXECUTION_FAILED = "METHOD_EXECUTION_FAILED";
}