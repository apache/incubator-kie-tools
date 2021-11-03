/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.sql.util;

import org.dashbuilder.dataprovider.sql.DatabaseTestSettings;

public class DatabaseProvider {

    public static String fromDriverClassName(String driverClassName) {
        if (driverClassName == null || driverClassName.isEmpty()) {
            throw new IllegalArgumentException("Driver class name cannot be empty.");
        }

        String sanitizedDriverClassName = driverClassName.trim().toLowerCase();
        if (sanitizedDriverClassName.startsWith("com.ibm.db2")) {
            return DatabaseTestSettings.DB2;
        } else if (sanitizedDriverClassName.startsWith("org.h2")) {
            return DatabaseTestSettings.H2;
        } else if (sanitizedDriverClassName.startsWith("com.microsoft.sqlserver")) {
            return DatabaseTestSettings.SQLSERVER;
        } else if (sanitizedDriverClassName.startsWith("org.mariadb")) {
            return DatabaseTestSettings.MARIADB;
        } else if (sanitizedDriverClassName.startsWith("com.mysql")) {
            return DatabaseTestSettings.MYSQL;
        } else if (sanitizedDriverClassName.startsWith("oracle")) {
            return DatabaseTestSettings.ORACLE;
        } else if (sanitizedDriverClassName.startsWith("org.postgresql") ||
                   sanitizedDriverClassName.startsWith("com.edb")) {
            return DatabaseTestSettings.POSTGRES;
        } else if (sanitizedDriverClassName.startsWith("com.sybase")) {
            return DatabaseTestSettings.SYBASE;
        } else {
            throw new IllegalArgumentException("Unsupported database provider with a driver class:" + driverClassName);
        }
    }
}
