/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.profile.api.preferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Profile {
    
    PLANNER_AND_RULES("Planner and Rules",
            Arrays.asList(
                "wb_entry_pages",
                "wb_entry_process_definitions",
                "wb_entry_process_instances",
                "wb_entry_task_administration",
                "wb_entry_jobs",
                "wb_execution_errors",
                "wb_entry_tasks_list",
                "wb_entry_process_dashboard",
                "wb_entry_task_dashboard",
                // groups go last
                "wb_group_manage",
                "wb_group_track"
            )), 
    FULL("Full",
            Collections.emptyList());

    private String profileName;
    private List<String> menuBlackList;
    
    private Profile(String name, List<String> menuBlackList) {
        this.profileName = name;
        this.menuBlackList = menuBlackList;
    }
    
    public List<String> getMenuBlackList() {
        return menuBlackList;
    }
    
    @Override
    public String toString() {
        return this.profileName;
    }
    
    /**
     * A human readable profile
     * @return
     *  A String containing a human readable name for this profile.
     */
    public String getName() {
        return this.profileName;
    }
    
    public static Profile withName(String name) {
        return Arrays.stream(Profile.values())
                     .filter(p -> p.getName().equals(name))
                     .findFirst().orElse(Profile.FULL);
    }

}
