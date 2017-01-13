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
package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import java.util.Map;

import org.eclipse.bpmn2.BaseElement;

/**
 * A helper to marshall specific properties of the Process Designer models,
 * to translate them into BPMN 2 constraints.
 * @author Antoine Toulme
 */
public interface BpmnMarshallerHelper {

    /**
     * Applies the set of properties from the json model to the BPMN 2 element.
     * @param baseElement the base element to be customized.
     * @param properties the set of properties extracted from the json model.
     */
    public void applyProperties(BaseElement baseElement,
                                Map<String, String> properties);
}
