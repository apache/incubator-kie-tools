/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.workbench.configuration;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContextualView {

    public static final String BASIC_MODE = "Basic";
    public static final String ADVANCED_MODE = "Advanced";

    public static final String PROCESS_DEFINTIONS = "process_definitions";
    public static final String PROCESS_INSTANCES = "process_instances";
    public static final String TASK_LIST = "task_list";
    public static final String ALL_PERSPECTIVES = "all_prespectives";

    private Map<String, String> perspectiveViewMode = new HashMap<String, String>();

    {
        perspectiveViewMode.put( ALL_PERSPECTIVES,
                                 BASIC_MODE );
        perspectiveViewMode.put( PROCESS_DEFINTIONS,
                                 BASIC_MODE );
        perspectiveViewMode.put( PROCESS_INSTANCES,
                                 BASIC_MODE );
        perspectiveViewMode.put( TASK_LIST,
                                 BASIC_MODE );
    }

    public ContextualView() {

    }

    public String getViewMode( final String perspective ) {
        return perspectiveViewMode.get( perspective );
    }

    public void setViewMode( final String perspective,
                             final String ViewMode ) {
        perspectiveViewMode.put( perspective,
                                 ViewMode );
    }

}
