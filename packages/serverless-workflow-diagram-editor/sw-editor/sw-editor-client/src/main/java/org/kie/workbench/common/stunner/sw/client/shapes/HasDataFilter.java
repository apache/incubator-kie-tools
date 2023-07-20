/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.sw.definition.StateDataFilter;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_INPUT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_IS_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_OUTPUT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_PARAMETER;

public interface HasDataFilter extends HasTranslation {

    int DEFAULT_MAX_LENGTH_SIZE = 30;

    default String getStateDataFilter(StateDataFilter filter) {
        if (filter == null) {
            return getTranslation(DATA_FILTER_IS_NULL);
        }

        return getTranslation(DATA_FILTER_PARAMETER) + ":\r\n" + getTranslation(DATA_FILTER_INPUT) + ": " + truncate(filter.getInput())
                + "\r\n" + getTranslation(DATA_FILTER_OUTPUT) + ": " + truncate(filter.getOutput());
    }

    default String truncate(String value) {
        return truncate(value, DEFAULT_MAX_LENGTH_SIZE);
    }

    default String truncate(String value, int size) {
        if (value == null) {
            return value;
        }

        String result = value.trim();
        if (result.length() <= size) {
            return result;
        }

        return result.substring(0, size) + "...";
    }
}
