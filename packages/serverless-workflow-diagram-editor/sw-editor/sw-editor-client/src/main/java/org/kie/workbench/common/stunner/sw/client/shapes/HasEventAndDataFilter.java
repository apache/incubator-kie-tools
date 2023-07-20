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

import org.kie.workbench.common.stunner.sw.definition.EventDataFilter;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_FILTER_DATA;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_FILTER_IS_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_FILTER_PARAMETER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_FILTER_TO_STATE_DATA;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.EVENT_FILTER_USE_DATA;

public interface HasEventAndDataFilter extends HasDataFilter {

    default String getEventFilter(EventDataFilter filter) {
        if (filter == null) {
            return getTranslation(EVENT_FILTER_IS_NULL);
        }

        return getTranslation(EVENT_FILTER_PARAMETER) + ":\r\n"
                + getTranslation(EVENT_FILTER_DATA) + ": " + truncate(filter.getData()) + "\r\n"
                + getTranslation(EVENT_FILTER_TO_STATE_DATA) + ": " + truncate(filter.getToStateData()) + "\r\n"
                + getTranslation(EVENT_FILTER_USE_DATA) + ": " + (filter.getUseData() == null ? "true" : filter.getUseData());
    }
}
