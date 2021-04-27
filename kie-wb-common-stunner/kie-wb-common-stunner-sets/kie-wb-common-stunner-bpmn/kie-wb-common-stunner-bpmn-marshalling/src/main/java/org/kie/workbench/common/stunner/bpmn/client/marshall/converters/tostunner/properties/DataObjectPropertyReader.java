/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataObjectReference;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

public class DataObjectPropertyReader extends BasePropertyReader {

    private final DataObject element;

    public DataObjectPropertyReader(DataObjectReference ref, BPMNDiagram diagram,
                                    BPMNShape shape,
                                    double resolutionFactor) {
        super(ref.getDataObjectRef(), diagram, shape, resolutionFactor);
        this.element = ref.getDataObjectRef();
    }

    public String getName() {
        return CustomElement.name.of(element).get();
    }

    public String getType() {
        return element.getItemSubjectRef().getStructureRef();
    }
}

