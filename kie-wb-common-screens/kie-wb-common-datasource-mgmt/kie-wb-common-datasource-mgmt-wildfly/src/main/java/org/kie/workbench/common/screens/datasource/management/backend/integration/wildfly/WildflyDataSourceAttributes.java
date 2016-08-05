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

package org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly;

/**
 * Definition the data source attribute names used internally by the Wildfly server.
 */
public class WildflyDataSourceAttributes {

    /**
     * A String value.
     */
    public static final String JNDI_NAME = "jndi-name";

    /**
     * A String value.
     */
    public static final String DRIVER_NAME = "driver-name";

    /**
     * A String value.
     */
    public static final String DRIVER_CLASS = "driver-class";

    /**
     * A String value.
     */
    public static final String DATASOURCE_CLASS = "datasource-class";

    /**
     * A String value.
     */
    public static final String POOL_NAME = "pool-name";

    /**
     * A String value.
     */
    public static final String CONNECTION_URL = "connection-url";

    /**
     * A String value.
     */
    public static final String NEW_CONNECTION = "new-connection-sql";

    /**
     * A boolean value.
     */
    public static final String JTA = "jta";

    /**
     * A String value.
     */
    public static final String SECURITY_DOMAIN = "security-domain";

    /**
     * A String value.
     */
    public static final String USER_NAME = "user-name";

    /**
     * A String value.
     */
    public static final String PASSWORD = "password";

    /**
     * Should be a String value.
     * Set the java.sql.Connection transaction isolation level.
     * Valid values are: TRANSACTION_READ_UNCOMMITTED, TRANSACTION_READ_COMMITTED,
     * TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE and TRANSACTION_NONE
     */
    public static final String TRANSACTION_ISOLATION = "transaction-isolation";


    /**
     * Should be a String value.
     */
    public static final String VALID_CONNECTION_CHECKER_CLASS_NAME = "valid-connection-checker-class-name";

    /**
     * Should be a String value.
     */
    public static final String CHECK_VALID_CONNECTION_SQL = "check-valid-connection-sql";

    /**
     * Should be a boolean value.
     */
    public static final String BACKGROUND_VALIDATION = "background-validation";

    /**
     * Should be a Long value.
     */
    public static final String BACKGROUND_VALIDATION_MILLIS = "background-validation-millis";

    /**
     * Should be a boolean value.
     */
    public static final String VALIDATE_ON_MATCH = "validate-on-match";

    /**
     * Should be a String value.
     */
    public static final String STALE_CONNECTION_CHECKER_CLASS_NAME = "stale-connection-checker-class-name";

    /**
     * Should be a String value.
     */
    public static final String EXCEPTION_SORTER_CLASS_NAME = "exception-sorter-class-name";

    /**
     * Should be a Long value.
     */
    public static final String PREPARED_STATEMENTS_CACHE_SIZE = "prepared-statements-cache-size";

    /**
     * Should be a boolean value.
     */
    public static final String SHARE_PREPARED_STATEMENTS = "share-prepared-statements";

    /**
     * Should be boolean value.
     */
    public static final String USE_CCM = "use-ccm";

}