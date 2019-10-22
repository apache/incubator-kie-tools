/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class DescriptionPropertyConverter {

    public static Description wbFromDMN(final String description) {
        if (Objects.isNull(description)) {
            return new Description("");
        } else {
            return new Description(description);
        }
    }

    public static String dmnFromWB(final Description description) {
        if (Objects.isNull(description)) {
            return null;
        } else if (StringUtils.isEmpty(description.getValue())) {
            return null;
        } else {
            return description.getValue();
        }
    }
}