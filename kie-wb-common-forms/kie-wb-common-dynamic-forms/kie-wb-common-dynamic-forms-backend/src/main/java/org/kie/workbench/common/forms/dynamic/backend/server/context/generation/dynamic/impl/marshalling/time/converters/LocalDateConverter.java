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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.LocalDateFieldValueMarshaller;

public class LocalDateConverter implements LocalDateFieldValueMarshaller.TimeConverter<LocalDate> {

    @Override
    public Class<LocalDate> getType() {
        return LocalDate.class;
    }

    @Override
    public Date toFlatValue(LocalDate rawValue) {
        return Date.from(rawValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public LocalDate toRawValue(Date flatValue) {
        return flatValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
