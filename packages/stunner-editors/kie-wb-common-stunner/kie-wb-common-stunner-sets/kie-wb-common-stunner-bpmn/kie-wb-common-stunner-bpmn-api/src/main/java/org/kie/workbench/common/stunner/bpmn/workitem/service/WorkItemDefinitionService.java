/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.workitem.service;

import java.util.Collection;

import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

/**
 * Describes a generic service for work item definitions.
 * <p>
 * This kind of services consider:
 * - a reasonable set of work item definitions
 * - reasonable sizes for the descriptors and resources (eg: icons)
 * - all resulting items must be processed before consuming a process (due to potential dependencies with the process definition itself)
 * This way, services do not consider lazy processing neither pagination (cursor) capabilities.
 */
public interface WorkItemDefinitionService<T> {

    Collection<WorkItemDefinition> execute(T input);
}
