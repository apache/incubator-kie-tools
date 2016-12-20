/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.backend.marhsall.json.oryx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.BaseOryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagement;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;

@Dependent
@CaseManagement
public class CaseManagementOryxIdMappings extends BaseOryxIdMappings {

    @Inject
    public CaseManagementOryxIdMappings( final DefinitionManager definitionManager ) {
        super( definitionManager );
    }

    @Override
    public Map<Class<?>, String> getCustomMappings() {
        final Map<Class<?>, String> customMappings = new HashMap<Class<?>, String>() {{
            put( CaseManagementDiagram.class, "BPMNDiagram" );
        }};

        return customMappings;
    }

    @Override
    public Map<Class<?>, Set<String>> getSkippedProperties() {
        final Map<Class<?>, Set<String>> skippedProperties = new HashMap<Class<?>, Set<String>>() {{
            put( CaseManagementDiagram.class,
                 new HashSet<String>() {{
                     add( "name" );
                 }} );
        }};

        return skippedProperties;
    }

    @Override
    public Map<Class<?>, Map<Class<?>, String>> getDefinitionMappings() {
        final Map<Class<?>, Map<Class<?>, String>> definitionMappings = new HashMap<Class<?>, Map<Class<?>, String>>() {{
            final Map<Class<?>, String> diagramPropertiesMap = new HashMap<>();
            put( CaseManagementDiagram.class,
                 diagramPropertiesMap );
            diagramPropertiesMap.put( Name.class,
                                      "processn" );
            diagramPropertiesMap.put( ProcessVariables.class,
                                      "vardefs" );

            final Map<Class<?>, String> userTaskPropertiesMap = new HashMap<>();
            put( UserTask.class,
                 userTaskPropertiesMap );
            userTaskPropertiesMap.put( AssignmentsInfo.class,
                                       "assignmentsinfo" );
            userTaskPropertiesMap.put( TaskName.class,
                                       "taskname" );

            final Map<Class<?>, String> exclusiveDatabasedGatewayPropertiesMap = new HashMap<>();
            put( ExclusiveDatabasedGateway.class,
                 exclusiveDatabasedGatewayPropertiesMap );
            exclusiveDatabasedGatewayPropertiesMap.put( DefaultRoute.class,
                                                        "defaultgate" );
        }};

        return definitionMappings;
    }

}
