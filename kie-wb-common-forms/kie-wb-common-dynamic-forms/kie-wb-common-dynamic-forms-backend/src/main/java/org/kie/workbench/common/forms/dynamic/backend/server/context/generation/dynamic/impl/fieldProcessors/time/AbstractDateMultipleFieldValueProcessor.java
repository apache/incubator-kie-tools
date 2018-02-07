/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractDateMultipleFieldValueProcessor<F extends FieldDefinition> implements FieldValueProcessor<F, List<?>, List<Date>> {

    private LocalDateFieldValueProcessor localDateFieldValueProcessor;

    public AbstractDateMultipleFieldValueProcessor(LocalDateFieldValueProcessor localDateFieldValueProcessor) {
        this.localDateFieldValueProcessor = localDateFieldValueProcessor;
    }

    @Override
    public List<Date> toFlatValue(F field,
                                  List<?> rawValues,
                                  BackendFormRenderingContext context) {

        if(rawValues == null) {
            return new ArrayList<>();
        }


        if(field.getStandaloneClassName().equals(Date.class.getName())) {
            return (List<Date>) rawValues;
        }

        return rawValues.stream()
                .map(rawValue -> localDateFieldValueProcessor.toFlatValue(field.getStandaloneClassName(), rawValue))
                .collect(Collectors.toList());
    }

    @Override
    public List<?> toRawValue(F field,
                              List<Date> dates,
                              List<?> originalValue,
                              BackendFormRenderingContext context) {
        if(dates == null) {
            return new ArrayList<>();
        }

        if(field.getStandaloneClassName().equals(Date.class.getName())) {
            return dates;
        }

        return dates.stream()
                .map(rawValue -> localDateFieldValueProcessor.toRawValue(field.getStandaloneClassName(), rawValue))
                .collect(Collectors.toList());
    }


}
