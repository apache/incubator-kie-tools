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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;

public class CorrelationsEditorValidator {

    public static List<CorrelationsEditorValidationItem> validate(List<Correlation> correlations) {
        return correlations.stream()
                .map(correlation -> new CorrelationsEditorValidationItem(correlation,
                                                                         hasDuplicateID(correlation, correlations),
                                                                         hasDivergingName(correlation, correlations),
                                                                         hasDuplicatePropertyID(correlation, correlations)))
                .collect(Collectors.toList());
    }

    public static boolean hasInvalidCorrelation(List<CorrelationsEditorValidationItem> validationItems) {
        return validationItems.stream()
                .anyMatch(CorrelationsEditorValidator::isInvalidCorrelation);
    }

    public static Optional<String> getCorrelationName(List<CorrelationsEditorValidationItem> validationItems, String id) {
        return validationItems.stream()
                .filter(validationItem -> !validationItem.isEmptyName() &&
                        !validationItem.isDivergingName() &&
                        Objects.equals(validationItem.getCorrelation().getId(), id))
                .map(correlationsEditorValidationItem -> correlationsEditorValidationItem.getCorrelation().getName())
                .findAny();
    }

    private static boolean hasDuplicateID(Correlation correlation, List<Correlation> correlations) {
        return correlations.stream()
                .filter(c -> Objects.equals(c.getId(), correlation.getId()))
                .count() > 1;
    }

    private static boolean hasDivergingName(Correlation correlation, List<Correlation> correlations) {
        return correlations.stream()
                .filter(c -> Objects.equals(c.getId(), correlation.getId()))
                .filter(c -> !c.getName().isEmpty())
                .anyMatch(c -> !Objects.equals(c.getName(), correlation.getName()));
    }

    private static boolean hasDuplicatePropertyID(Correlation correlation, List<Correlation> correlations) {
        return correlations.stream()
                .filter(c -> Objects.equals(c.getPropertyId(), correlation.getPropertyId()))
                .count() > 1;
    }

    private static boolean isInvalidCorrelation(CorrelationsEditorValidationItem correlationsEditorValidationItem) {
        return correlationsEditorValidationItem.isEmptyID() ||
                correlationsEditorValidationItem.isEmptyName() ||
                correlationsEditorValidationItem.isEmptyPropertyID() ||
                correlationsEditorValidationItem.isEmptyPropertyName() ||
                correlationsEditorValidationItem.isEmptyPropertyType() ||
                correlationsEditorValidationItem.isDivergingName() ||
                correlationsEditorValidationItem.isDuplicatePropertyID();
    }
}
