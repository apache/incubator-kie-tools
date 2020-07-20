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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.util;

import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

public class DataObjectUtils {

    private DataObjectUtils() {
    }

    public static void maybeAddDataObjectToItemDefinitions(List<ItemDefinition> itemDefinitions, Set<DataObject> dataObjects) {
        dataObjects.forEach(elm -> maybeAddDataObjectToItemDefinitions(itemDefinitions, elm));
    }

    public static void maybeAddDataObjectToItemDefinitions(List<ItemDefinition> itemDefinitions, DataObject data) {
        if (itemDefinitions.stream()
                .noneMatch(elm -> elm.getId().equals(Ids.item(data.getId())))) {
            data.getItemSubjectRef().setId(Ids.item(data.getId()));
            itemDefinitions.add(data.getItemSubjectRef());
        }
    }

    public static void maybeAddDataObjects(FlowElementsContainer process, Set<DataObject> dataObjects) {
        dataObjects.stream()
                .filter(elm -> !checkIfDataObjectExists(process.getFlowElements(), elm))
                .forEach(elm -> process.getFlowElements().add(elm));
    }

    public static boolean checkIfDataObjectExists(List<FlowElement> flowElements, DataObject dataObject) {
        return flowElements.stream()
                .filter(elm -> (elm instanceof DataObject))
                .anyMatch(elm -> elm.getId().equals(dataObject.getId()));
    }
}
