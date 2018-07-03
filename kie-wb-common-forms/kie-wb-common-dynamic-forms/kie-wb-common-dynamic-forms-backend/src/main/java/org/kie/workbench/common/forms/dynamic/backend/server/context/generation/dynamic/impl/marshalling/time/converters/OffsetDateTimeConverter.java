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

import java.time.OffsetDateTime;
import java.util.Date;

import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.LocalDateFieldValueMarshaller;

public class OffsetDateTimeConverter implements LocalDateFieldValueMarshaller.TimeConverter<OffsetDateTime> {

    @Override
    public Class<OffsetDateTime> getType() {
        return OffsetDateTime.class;
    }

    @Override
    public Date toFlatValue(OffsetDateTime rawValue) {
        return Date.from(rawValue.toInstant());
    }

    @Override
    public OffsetDateTime toRawValue(Date flatValue) {
        return flatValue.toInstant().atOffset(OffsetDateTime.now().getOffset());
    }
}
