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


package org.kie.workbench.common.stunner.sw.client.shapes;

import org.kie.workbench.common.stunner.sw.definition.StateDataFilter;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_INPUT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_IS_NULL;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_OUTPUT;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.DATA_FILTER_PARAMETER;

public interface HasDataFilter extends HasTranslation, IsTruncatable {

    default String getStateDataFilter(StateDataFilter filter) {
        if (filter == null) {
            return getTranslation(DATA_FILTER_IS_NULL);
        }

        return getTranslation(DATA_FILTER_PARAMETER) + ":\r\n" + getTranslation(DATA_FILTER_INPUT) + ": " + truncate(filter.getInput())
                + "\r\n" + getTranslation(DATA_FILTER_OUTPUT) + ": " + truncate(filter.getOutput());
    }

}
