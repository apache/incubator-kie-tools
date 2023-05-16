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

import org.kie.workbench.common.stunner.sw.definition.ForEachState;

import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.FOR_EACH_BATCH_SIZE;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.INPUT_COLLECTION;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.ITERATION_PARAMETER;
import static org.kie.workbench.common.stunner.sw.resources.i18n.SWConstants.OUTPUT_COLLECTION;

public interface HasCollections extends HasDataFilter {

    default String getCollections(ForEachState state) {
        StringBuilder result = new StringBuilder();
        result.append(getTranslation(INPUT_COLLECTION) + ": " + truncate(state.inputCollection) + "\r\n");

        if (state.outputCollection != null && !state.outputCollection.isEmpty()) {
            result.append(getTranslation(OUTPUT_COLLECTION) + ": " + truncate(state.outputCollection) + "\r\n");
        }

        if (state.iterationParam != null && !state.iterationParam.isEmpty()) {
            result.append(getTranslation(ITERATION_PARAMETER) + ": " + truncate(state.iterationParam) + "\r\n");
        }

        if (isDefaultMode(state.getMode())) {
            String batchSize = state.batchSize == null ? "∞" : state.batchSize.toString();
            result.append(getTranslation(FOR_EACH_BATCH_SIZE) + ": " + batchSize);
        }

        return result.toString();
    }

    default boolean isDefaultMode(String mode) {
        return mode == null || !mode.equals("sequential");
    }
}
