/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.migration.cli;


public interface MigrationConstants {

    // TODO Find where these constants come from

    String SYSTEM_GIT = "system.git";

    static final String[] systemRepos = {
                                         SYSTEM_GIT,
                                         "datasets.git",
                                         "datasources.git",
                                         "plugins.git",
                                         "preferences.git",
                                         "security.git"
    };

    static String MIGRATION_TOOL_NAME = "migration-tool";

    String SYSTEM_SPACE = "system";

}
