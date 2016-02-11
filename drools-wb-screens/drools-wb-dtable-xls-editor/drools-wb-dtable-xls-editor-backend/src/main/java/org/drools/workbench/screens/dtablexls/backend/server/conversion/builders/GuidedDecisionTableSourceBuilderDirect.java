/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

/**
 * Interface for additional responsibilities for Guided Decision Table builders
 */
public interface GuidedDecisionTableSourceBuilderDirect
        extends
        GuidedDecisionTableSourceBuilder {

    /**
     * Populate the given Decision Table with details of the parsed column.
     * @param dtable The target Guided Decision Table to populate
     * @param maxRowCount The maximum number of used rows detected in the XLS
     */
    void populateDecisionTable( final GuidedDecisionTable52 dtable,
                                final int maxRowCount );

}
