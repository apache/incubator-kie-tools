/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.workbench.models.datamodel.rule.ActionFieldList;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;

/**
 * Utilities for ModelFields
 */
public final class ModelFieldUtil {

    /**
     * Returns an array of ModelFields not consumed by the ActionFieldList; i.e. an array of available ModelFields.
     *
     * @param fieldCompletions The complete collection of ModelFields
     * @param afl              The model of ModelFields already used
     * @return An array of unused ModelFields
     */
    public static ModelField[] getAvailableFieldCompletions(final ModelField[] fieldCompletions,
                                                            final ActionFieldList afl) {
        if (fieldCompletions == null || fieldCompletions.length == 0) {
            return fieldCompletions;
        }
        if (afl == null || afl.getFieldValues().length == 0) {
            return fieldCompletions;
        }

        final List<ModelField> availableModelFields = new ArrayList<>();
        availableModelFields.addAll(Arrays.asList(fieldCompletions));
        for (ActionFieldValue afv : afl.getFieldValues()) {
            final List<ModelField> usedModelFields = availableModelFields.stream().filter(m -> m.getName().equals(afv.getField())).collect(Collectors.toList());
            availableModelFields.removeAll(usedModelFields);
        }
        return availableModelFields.toArray(new ModelField[availableModelFields.size()]);
    }

    /**
     * Constructs model field with given name and data type
     *
     * Other three values are hardcoded as
     *  - ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
     *  - ModelField.FIELD_ORIGIN.DECLARED,
     *  - FieldAccessorsAndMutators.BOTH,
     * @param fieldName name of the field
     * @param fieldDataType data type of the field
     * @return constructed new model field
     */
    public static ModelField modelField(final String fieldName,
                                        final String fieldDataType) {
        return new ModelField(fieldName,
                              fieldDataType,
                              ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                              ModelField.FIELD_ORIGIN.DECLARED,
                              FieldAccessorsAndMutators.BOTH,
                              fieldDataType);
    }
}
