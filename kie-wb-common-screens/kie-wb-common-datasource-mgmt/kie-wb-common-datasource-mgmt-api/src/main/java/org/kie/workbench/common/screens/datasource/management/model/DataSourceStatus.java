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

package org.kie.workbench.common.screens.datasource.management.model;

public enum DataSourceStatus {

    /**
     * A data source that was just created but never looked up but the lookup operation.
     */
    NEW,

    /**
     * A data source that was looked up by at least one client.
     */
    REFERENCED,

    /**
     * A data source in stale mode may continue serving connections but it's not safe to do it. Components that uses
     * a data source may register themselves as listeners to be notified about data source status changes, and in this
     * way know when a data source enters in STALE mode. If this is the case, it's recommended to do a new lookup
     * of the data source to get a safe reference.
     *
     */
    STALE

}
