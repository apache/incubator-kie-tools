/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.bpmn.resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;

/**
 * This resource override the extension used for bpmn files in this workbench project's showcase to the use of "bpmn2"
 * instead of the default "bpmn" one, due to the default jbpm example repositories for this workbench are
 * using "bpmn2" as extension.
 */
@ApplicationScoped
@Specializes
public class ShowcaseBPMNDefinitionSetResourceType extends BPMNDefinitionSetResourceType {

    public static final String BPMN2_EXTENSION = "bpmn2";

    @Override
    public String getSuffix() {
        return BPMN2_EXTENSION;
    }
}