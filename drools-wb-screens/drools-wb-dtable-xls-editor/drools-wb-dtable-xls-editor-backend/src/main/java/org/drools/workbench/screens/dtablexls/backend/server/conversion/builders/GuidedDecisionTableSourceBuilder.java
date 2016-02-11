/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import org.drools.decisiontable.parser.SourceBuilder;

/**
 * Interface for additional responsibilities for Guided Decision Table builders
 */
public interface GuidedDecisionTableSourceBuilder
        extends
        SourceBuilder {

    /**
     * Get the number of rows processed by the SourceBuilder. POI returns
     * cells that contain empty values (if the User has set the value to, for
     * example, an empty String). This can mean not all columns contain the
     * same number of rows.
     * @return
     */
    int getRowCount();

}
